Простой OcspServer.

Алгоритм работы не совсем корректен, так как работает по чёрному списку, то есть на входе у него CRL,
а не по белому списку всех серийных номер данного УЦ.


1) Замечание по алгоритму работы
https://tools.ietf.org/html/rfc6960#section-2.1

   _The "good" state indicates a positive response to the status inquiry.
   At a minimum, this positive response indicates that no certificate
   with the requested certificate serial number currently within its
   validity interval is revoked.  This state does not necessarily mean
   that the certificate was ever issued or that the time at which the
   response was produced is within the certificate's validity interval.
   Response extensions may be used to convey additional information on
   assertions made by the responder regarding the status of the
   certificate, such as a positive statement about issuance, validity,
   etc._

То есть OCSP server не может проверить сам сертификат, он только сообщает есть ли данный серийный номер в отозванных и валидна ли мета информация об УЦ, которая присылается вместе с ним.

Он может вернуть в ответ три статуса:

1) **GOOD** - хороший серийный номер, он не отозван И сертификат уц в запросе тот, который ожидался
2) **RevokedStatus** - сертификат найден в списке и он отозван
3) **UnknownStatus** - сертификат уц в запросе  НЕ тот, который ожидался, или серийный номер не найден в базе (так как у нас чёрный список, то тут будет **GOOD**, а не **UnknownStatus**)

Итого клиент спрошивает сервер передавая сертификат УЦ и серийный номер, проверить сертификат сервер никак не может вообще!


Как пользоваться данным сервером:
настроить конфиг файл: ocspConfig.json, запустить

Запускать на сервере, логи появляются в папке logs, в дериктории программы, папку эту надо создать

    {
      "serverPort" : 8082,
      "servletInstances" : [ {
        "serverPath" : "/ocsp",
        "caCertPath" : "certForCA/output/etalon/ca2000000000002.cer",
        "ocspSingingCertPath" : "certForCA/output/etalon/ocsp.cer",
        "ocspSingingPkeyPath" : "certForCA/output/etalon/ocsp.cer.pkey",
        "crlPath" : "certForCA/output/etalon/kreker2000000000002.crl"
      }, {
        "serverPath" : "/ocsp2",
        "caCertPath" : "certForCA/output/etalon/ca2000000000002.cer",
        "ocspSingingCertPath" : "certForCA/output/etalon/ocsp.cer",
        "ocspSingingPkeyPath" : "certForCA/output/etalon/ocsp.cer.pkey",
        "crlPath" : "certForCA/output/etalon/kreker2000000000002.crl"
      } ]
    }


Скрипт для запуска как сервис, нужно заменить /usr/share/nginx/KrekerCa/OcspServer.jar и создать этот файл в 
    /usr/local/bin/OcspServer.sh , сделать исполняемым
    
    Запускать
        /usr/local/bin/OcspServer.sh start
        /usr/local/bin/OcspServer.sh stop

-----

    #!/bin/sh 
    SERVICE_NAME=OcspServer
    PATH_TO_JAR=/usr/share/nginx/KrekerCa/OcspServer.jar
    PID_PATH_NAME=/tmp/OcspServer-pid 
    case $1 in 
    start)
           echo "Starting $SERVICE_NAME ..."
      if [ ! -f $PID_PATH_NAME ]; then 
           nohup java -jar $PATH_TO_JAR /tmp 2>> /dev/null >>/dev/null &      
                       echo $! > $PID_PATH_NAME  
           echo "$SERVICE_NAME started ..."         
      else 
           echo "$SERVICE_NAME is already running ..."
      fi
    ;;
    stop)
      if [ -f $PID_PATH_NAME ]; then
             PID=$(cat $PID_PATH_NAME);
             echo "$SERVICE_NAME stoping ..." 
             kill $PID;         
             echo "$SERVICE_NAME stopped ..." 
             rm $PID_PATH_NAME       
      else          
             echo "$SERVICE_NAME is not running ..."   
      fi    
    ;;    
    restart)  
      if [ -f $PID_PATH_NAME ]; then 
          PID=$(cat $PID_PATH_NAME);    
          echo "$SERVICE_NAME stopping ..."; 
          kill $PID;           
          echo "$SERVICE_NAME stopped ...";  
          rm $PID_PATH_NAME     
          echo "$SERVICE_NAME starting ..."  
          nohup java -jar $PATH_TO_JAR /tmp 2>> /dev/null >> /dev/null &            
          echo $! > $PID_PATH_NAME  
          echo "$SERVICE_NAME started ..."    
      else           
          echo "$SERVICE_NAME is not running ..."    
         fi     ;;
     esac

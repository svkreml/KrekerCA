ростая утилита которая перевыпускает CRL и кладёт их в заданое место (эту папку должен хостить HTTP server типа NGINX)

Настройка: получить сертификат УЦ. 
Редактировать файл CrlRecreatorConfig.json
Запускать на сервере, логи появляются в папке logs, в дериктории программы, папку эту надо создать

В конфиге может одновременно выпускаться несколько CRL, в него же кладутся отзываемые сертификаты, для применения изменений требуется перезагруть программу

    {
      "recreateTimerSeconds" : 10,
      "crlGenerateTimeoffset" : -1000,
      "crlTTLseconds" : 18000,
      "crlConfigList" : [ {
        "certificatePath" : "certForCA\\output\\etalon\\ca2000000000002.cer",
        "privateKeyPath" : "certForCA\\output\\etalon\\ca2000000000002.cer.pkey",
        "crlPath" : "certForCA\\output\\ca2000000000002.crl",
        "revokedDtoLisconfig" : [ {
          "serialHex" : "1234567890",
          "crlReason" : 0,
          "addDate" : "11/12/2015 12:34:56"
        } ]
      } ]
    }




Скрипт для запуска на линуксе в фоне, создать скрипт, заменить
 PATH_TO_JAR=/usr/share/nginx/html/crlRecreator/CrlRecreator.jar на нужный
 
Запускать

    /usr/local/bin/CrlRecreator.sh start
    /usr/local/bin/CrlRecreator.sh stop

    `#!/bin/sh 
     SERVICE_NAME=CrlRecreator
     PATH_TO_JAR=/usr/share/nginx/html/crlRecreator/CrlRecreator.jar
     PID_PATH_NAME=/tmp/CrlRecreator-pid 
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
      esac`

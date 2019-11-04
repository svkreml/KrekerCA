Данная программа позволяет строить цепочки сертификатов, беря за основу уже существующие сертификаты.

Утилита запускается следующей командой: 

-Dfile.encoding=UTF8 

    chcp 1251
    java -jar CertificateDublicator.jar <параметры>
Для получения справки запустить с параметром "-h"

Алгоритм работы:
1) Берется корневой сертификат, редактируется с помощью ASN1Editor, после чего подаётся на вход программы, 
На выходе получаем два файла: новый корневой сертификат и его закрытый ключ
2) Далее аналогично можно создать подчинённый УЦ, но при генерации уже указать файлы из предыдущего пункта, при выборе сертификата для клонирования тут уже требуется редактировать как минимум расширения, где указаны URL адреса (CRL, OCSP, CRT)
3) Создание конечного сертификата: похоже на предыдущий пункт, но можно дополнительно указать ещё и заявку на сертификат или уже сущёствующий сертификат, тогда открытый ключ будет не сгенерирован новый, а использован из этого параметра.
4) С помощью утилиты CrlRecreator и Nginx поднять сервер CRL и CRT
5) Выпустить OCSP сертификат Поднять OCSP сервер (OcspServer)

Пример параметров запуска:

    -c ..\\certForCA\\output\\etalon\\ca2000000000002.cer -p ..\\certForCA\\output\\etalon\\ca2000000000002.cer.pkey -d ..\\certForCA\\output\\etalon\\client20000002copy.cer -pub ..\\certForCA\\output\\etalon\\client20000002copy.cer -s eeeeeeeeeeeeeeeeeeee -o ..\\certForCA\\output\\client20000002copy_copy.cer" 


    D:\projects\certcreator2\CertificateDublicator\target>chcp 1251
    Текущая кодовая страница: 1251
    
    D:\projects\certcreator2\CertificateDublicator\target>java -jar CertificateDublicator.jar -h
    Usage: CertDublicator [-hV] [-a=<notAfter>] [-alg=<pKeyAlg>] [-b=<notBefore>]
                          [-c=<caCertPath>] [-d=<inputCertPath>]
                          [-n=<donorSubject>] [-o=<outputNewCertPath>]
                          [-p=<caPkeyPath>] [-pub=<donorPublicKeyPath>]
                          [-req=<donorCertReq>] [-reqSub=<reqSub>] [-s=<serial>]
    Создаёт новый сертификат, похожий на введённый
      -a, --notAfter=<notAfter>
                              Опционально, Действителен по, dd/MM/yyyy HH:mm:ss
          -alg, --pKeyAlg=<pKeyAlg>
                              Опционально, переопределение алгоритма приватного
                                ключа: 2001, 2012, 2012str, rsa2048, rsa4096
      -b, --notBefore=<notBefore>
                              Опционально, Действителен с, dd/MM/yyyy HH:mm:ss
      -c, --caCert=<caCertPath>
                              Опционально, Вышестоящий сертификат
      -d, --donorCert=<inputCertPath>
                              Сертификат для копирования
      -h, --help              Show this help message and exit.
      -n, --donorSubject=<donorSubject>
                              Опционально, Сертификат для копирования полей субъекта
      -o, --output=<outputNewCertPath>
                              Файл, куда будет сохранён новый сертификат
      -p, --caCertPkey=<caPkeyPath>
                              Обязателен, если есть -с, --caCert, Закрытый ключ,
                                которым сертификат будет подписан в формате pkcs8
          -pub, --donorPublicKey=<donorPublicKeyPath>
                              Опционально, Сертификат для копирования публичного
                                ключа
          -req, --certificateRequest=<donorCertReq>
                              Опционально, запрос на сертификат, будет
                                использоваться только открытый ключ из него
          -reqSub, --certificateRequestSub=<reqSub>
                              Опционально, если значение данного параметра "true",
                                то имя субъекта возьмётся из заявки
      -s, --serial=<serial>   Опционально, Серийный номер генерируемого
                                сертификата, hex
      -V, --version           Print version information and exit.
    
    
    


Пример команды на создания сертификата по сгенерированной заявке:

`java -jar CertificateDublicator.jar -req "D:\projects\certcreator2\certForCA\output\etalon\request.req" -d "D:\projects\certcreator2\certForCA\output\etalon\client20000002copy.cer" -c "D:\projects\certcreator2\certForCA\output\etalon\ca2000000000002.cer" -p "D:\projects\certcreator2\certForCA\output\etalon\ca2000000000002.cer.pkey"`

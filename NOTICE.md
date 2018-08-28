Certificate-Validation
======================
FROM https://github.com/jeewamp/Certificate-Validation

This is the OCSP/CRL Certificate Validation Feature I made for Apache Synapse. But this can be used by any other 
project at the Certificate Validation phase of SSL Handshake.

Please see the contribution to Apache Synapse in this JIRA location
https://issues.apache.org/jira/browse/SYNAPSE-954

Check the code in Synapse trunk
http://svn.apache.org/viewvc/synapse/trunk/java/modules/transports/core/nhttp/src/main/java/org/apache/synapse/transport/utils/sslcert/

Jeewantha Dharmaparakrama


### Changes:

From:
        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcprov-jdk16</artifactId>
            <version>1.46</version>
        </dependency>      
To:
        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcprov-jdk15on</artifactId>
            <version>1.60</version>
        </dependency>       
(For this, some changes have occurred in the code)

Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

http://aws.amazon.com/apache2.0

or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.


Time Stamping Authority (TSA) Client [RFC 3161].
======================
https://svn.apache.org/repos/asf/pdfbox/trunk/examples/src/main/java/org/apache/pdfbox/examples/signature/TsaClient.java

author Vakhtang Koroghlishvili

author John Hewson

Licensed under the Apache License, Version 2.0 (the "License")

### Changes:

new hash algorithm

some new methods

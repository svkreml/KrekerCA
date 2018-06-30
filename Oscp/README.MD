FROM https://github.com/jeewamp/Certificate-Validation

Changes:

From

        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcprov-jdk16</artifactId>
            <version>1.46</version>
        </dependency>
        
To

        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcprov-jdk15on</artifactId>
            <version>1.59</version>
        </dependency>
        
(For this, some changes have occurred in the code)
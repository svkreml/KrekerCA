import caJava.fileManagement.FileManager;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class Main {
    byte[] openkey;

    public static void main(String[] args) throws IOException {
        String containerPath = "D:\\te-7b66c.000";
        String password = "";
        Mask mask = new Mask(new File(containerPath, "masks.key"));
        System.out.println(Hex.toHexString(mask.mask));
        System.out.println(Hex.toHexString(mask.salt));
        System.out.println(Hex.toHexString(mask.hash));

        Primary primary = new Primary(new File(containerPath,"primary.key"));
        System.out.println(Hex.toHexString(primary.primary));
    }

    public static ASN1Sequence getASN1(byte[] enc) throws IOException {
        ASN1InputStream in = new ASN1InputStream(new ByteArrayInputStream(enc));
        return (ASN1Sequence) in.readObject();
    }
    //make_pwd_key(pwd_key, salt12, 12, passw);
    /*int make_pwd_key(char *result_key, char *start12, int start12_len, char *passw)
{
	int result;
	int i;
	char pincode4[1024];
	int pin_len;
	char current[32];
	char material36[32];
	char material5C[32];
	char hash_result[32];
	gost_hash_ctx ctx;
	init_gost_hash_ctx(&ctx, &GostR3411_94_CryptoProParamSet);
	memset(pincode4, 0, sizeof(pincode4));
	pin_len = strlen(passw);
	if (pin_len*4 > sizeof(pincode4)) {	result = 1;	goto err; }
	for(i = 0; i < pin_len; i++)
		pincode4[i*4] = passw[i];

	start_hash(&ctx);
	hash_block(&ctx, start12, start12_len);
	if (pin_len)
		hash_block(&ctx, pincode4, pin_len * 4);
	finish_hash(&ctx, hash_result);

	memcpy(current, (char*)"DENEFH028.760246785.IUEFHWUIO.EF", 32);

	for(i = 0; i < (pin_len?2000:2); i++)
	{
		xor_material(material36, material5C, current);
		start_hash(&ctx);
		hash_block(&ctx, material36, 32);
		hash_block(&ctx, hash_result, 32);
		hash_block(&ctx, material5C, 32);
		hash_block(&ctx, hash_result, 32);
		finish_hash(&ctx, current);
	}

	xor_material(material36, material5C, current);

	start_hash(&ctx);
	hash_block(&ctx, material36, 32);
	hash_block(&ctx, start12, start12_len);
	hash_block(&ctx, material5C, 32);
	if (pin_len)
		hash_block(&ctx, pincode4, pin_len * 4);
	finish_hash(&ctx, current);

	start_hash(&ctx);
	hash_block(&ctx, current, 32);
	finish_hash(&ctx, result_key);

	result = 0; //ok
err:
	return result;
}*/
}

class Mask {
    byte[] mask;
    byte[] salt;
    byte[] hash;

    public Mask(File file) throws IOException {
        ASN1Sequence asn1 = Main.getASN1(FileManager.read(file));
        mask = ((DEROctetString) asn1.getObjectAt(0)).getOctets();
        salt = ((DEROctetString) asn1.getObjectAt(1)).getOctets();
        hash = ((DEROctetString) asn1.getObjectAt(2)).getOctets();
    }
}

class Primary {
    byte[] primary;

    public Primary(File file) throws IOException {
        ASN1Sequence asn1 = Main.getASN1(FileManager.read(file));
        primary = ((DEROctetString) asn1.getObjectAt(0)).getOctets();
    }
}
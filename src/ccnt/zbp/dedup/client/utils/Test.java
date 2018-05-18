package ccnt.zbp.dedup.client.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import com.sun.corba.se.spi.ior.MakeImmutable;

public class Test {

	public static void main(String[] args) throws Exception {
//		String str = "0";
//		System.out.println(str.length());
//		System.out.println(str.substring(str.length()-3));
//		HttpClientUtil.doPost("http://127.0.0.1:8080/DedupServer/user/request", "C:/Users/zbp/Desktop", "tmp-2.txt");
//		for(long i = 0; i<10; i++){
//			System.out.println((int)(i%3));
//		}
//		
//		long now = 100;
//		System.out.println(100/21.0);
		
		String str = "abblskjdfljssdf54sdf6 asdfasdfa abblskjdfljssdf54sdf6 asdfasdfaabblskjdfljssdf54sdf6 asdfasdfaabblskjdfljssdf54sdf6 asdfasdfaabblskjdfljssdf54sdf6 asdfasdfa";
		System.out.println(str.hashCode());
		String result = "";
		try {
			result = new Test().makeSHA1Hash(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println(result);
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		String hex = (new HexBinaryAdapter()).marshal(md5.digest(str.getBytes()));
		System.out.println(hex);
		
		String original = str;
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(original.getBytes());
		byte[] digest = md.digest();
		StringBuffer sb = new StringBuffer();
		for (byte b : digest) {
			sb.append(String.format("%02x", b & 0xff));
		}

		System.out.println("original:" + original);
		System.out.println("digested(hex):" + sb.toString());
	}
	
	public String makeSHA1Hash(String input)
            throws NoSuchAlgorithmException, UnsupportedEncodingException
        {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.reset();
            byte[] buffer = input.getBytes("UTF-8");
            md.update(buffer);
            byte[] digest = md.digest();
            

            String hexStr = "";
            for (int i = 0; i < digest.length; i++) {
                hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
            }
            return hexStr;
        }

}

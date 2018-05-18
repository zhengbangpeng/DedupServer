package ccnt.zbp.dedup.client.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apache.commons.io.FileUtils;

import com.sun.corba.se.spi.ior.MakeImmutable;

public class TestT {

	public static void main(String[] args) throws Exception {
	
//		String oriPath = "C:/Users/zbp/Desktop/test/ori.txt";
//		String copyPath = "C:/Users/zbp/Desktop/copy.txt";
//		FileUtil.copyFileUsingFileChannels(new File(oriPath), new File(copyPath));
		
		//HttpClientUtil.doPost("http://"+"127.0.0.1"+":8080/DedupServer/user/request", "test", "123","R");
//		char c;
//		for(c='0' ;c<='9';c++){
//			System.out.print("'"+c+"',");
//		}
//		for(c='a' ;c<='z';c++){
//			System.out.print("'"+c+"',");
//		}
		
		String str = "abcdef";
		System.out.println(str.substring(0,3));
	}

}

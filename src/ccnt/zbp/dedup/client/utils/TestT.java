package ccnt.zbp.dedup.client.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Set;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import redis.clients.jedis.Jedis;

import com.sun.corba.se.spi.ior.MakeImmutable;

public class TestT {

	public  static void main(String[] args) throws Exception {
		
		String fileName = "021";
		String url = "http://192.168.1.65:8080/DedupServer/user/request";
		String fileHash = "021test021";
		//String filePath = "C:/Users/zbp/Desktop/source-archive.zip"; 
		String filePath = "C:/Users/zbp/Desktop/tmp.txt"; 
		//String filePath = "C:/Users/zbp/Desktop/filestore/tmp-2.txt"; 
		HttpEntity entity = MultipartEntityBuilder.create()
			.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
			.addBinaryBody("file", new File(filePath), ContentType.DEFAULT_BINARY, fileName)
			.build();

	    HttpUriRequest request = RequestBuilder
                .post(url)
                .setEntity(entity)
                .addParameter("WoR", "W")
                .addParameter("fileName",fileName)
                .addParameter("fileHash", fileHash)
                .build();

	    HttpClient client = HttpClientBuilder.create().build();
	    HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (Exception e) {
			e.printStackTrace();
		} 
        System.out.println(response.toString());
	}

}

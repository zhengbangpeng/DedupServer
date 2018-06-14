package ccnt.zbp.dedup.client.utils;

import java.io.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import redis.clients.jedis.Jedis;

@MultipartConfig
public class ChunkRequest extends HttpServlet {
	static String dataDir = "/media/ubuntu/mec-data";
	static String[] ips = new String[]{"192.168.1.131","192.168.1.132","192.168.1.144"};
	static String chunkDir = "/media/ubuntu/mec-data/chunkstore";
	static Jedis chunkJedis = ChunkRedisUtil.getJedis();
	//static Jedis jedis = RedisUtil.getJedis();
	public ChunkRequest() {
        super();
    }

    public void destroy() {
        super.destroy(); 
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response){
    	
    }
    // dispathch
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
    	
    	//String[] ips = new String[]{"127.0.0.1","127.0.0.1","127.0.0.1"};
    	System.out.println("get chunk: "+new Date().getTime()/1000);
    	String chunkHash = request.getParameter("chunkHash");
    	String chunkPath = chunkDir+File.separator+chunkHash.substring(0, 3)+File.separator+chunkHash;
    	
    	File chunkFile = new File(chunkPath);
    	FileInputStream fis = new FileInputStream(chunkFile);
    	byte[] data = new byte[(int) chunkFile.length()];
    	fis.read(data);
    	fis.close();
    	
		//write chunk to response
		OutputStream out = response.getOutputStream();
		out.write(data);
		
    	
    }
}

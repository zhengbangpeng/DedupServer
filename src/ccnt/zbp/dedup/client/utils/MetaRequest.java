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
public class MetaRequest extends HttpServlet {
	static String dataDir = "/media/ubuntu/mec-data";
	static String[] ips = new String[]{"192.168.1.131","192.168.1.132","192.168.1.144"};
	static String chunkDir = "/media/ubuntu/mec-data/chunkstore";
	static Jedis chunkJedis = ChunkRedisUtil.getJedis();
	static Jedis localJedis = LocalRedisUtil.getJedis();
	static String newLine = System.getProperty("line.separator");
	//static Jedis jedis = RedisUtil.getJedis();
	public MetaRequest() {
        super();
    }

    public void destroy() {
        super.destroy(); 
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response){
    	
    }
    // dispathch
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
    	
    	String fShortHash = request.getParameter("fileHash");
    	String metaFilePath = localJedis.get(fShortHash);
    	OutputStream out = response.getOutputStream();
    	
    	try (BufferedReader br = new BufferedReader(new FileReader(metaFilePath))){
    		String line = null;
    		while((line = br.readLine()) != null){
    			String partServerId = chunkJedis.get(line);
    			String nextLine = line+" "+ newLine;
    			out.write(nextLine.getBytes());
        	}
    	}
    	out.flush();
    	out.close();
    }
}

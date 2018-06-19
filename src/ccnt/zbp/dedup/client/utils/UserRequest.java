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
public class UserRequest extends HttpServlet {
	static String dataDir = "/media/ubuntu/mec-data";
	static String[] ips = new String[]{"192.168.1.131","192.168.1.132","192.168.1.144"};
	//static Jedis jedis = RedisUtil.getJedis();
	public UserRequest() {
        super();
    }

    public void destroy() {
        super.destroy(); 
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response){
    	
    }
    // dispathch
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
    	
    	
    	// simulate remote call
    	// 30 ms average
    	try {
			Thread.sleep(30);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	//String[] ips = new String[]{"127.0.0.1","127.0.0.1","127.0.0.1"};
    	
    	String fileName = request.getParameter("fileName");
    	String serverIp = ips[(int) (Long.parseLong(fileName)%3)];
    	String url = "http://"+serverIp+":8080/DedupServer/file/request";
    	String fileHash = request.getParameter("fileHash");
    	
    	String WoR = request.getParameter("WoR");
    	
    	if(WoR.equals("W")){
    		Part part = new ArrayList<Part>(request.getParts()).get(0);
        	
        	HttpEntity entity = MultipartEntityBuilder.create()
        			.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
        			//.addPart((FormBodyPart) part)
        			.addBinaryBody(fileName, part.getInputStream())
        			.build();

    	    HttpUriRequest dRequest = RequestBuilder
                    .post(url)
                    .setEntity(entity)
                    .addParameter("WoR", WoR)
                    .addParameter("fileName", fileName)
                    .addParameter("fileHash", fileHash)
                    .build();

    	    HttpClient client = HttpClientBuilder.create().build();
    	    HttpResponse dResponse = null;
    		try {
    			dResponse = client.execute(dRequest);
    		} catch (Exception e) {
    			e.printStackTrace();
    		} 
            System.out.println(dResponse.toString());
    	}else{
    		//read file
    	    HttpUriRequest dRequest = RequestBuilder
                    .post(url)
                    .addParameter("fileName", fileName)
                    .addParameter("WoR",WoR)
                    .addParameter("fileHash", fileHash)
                    .build();

    	    HttpClient client = HttpClientBuilder.create().build();
    	    HttpResponse dResponse = null;
    		try {
    			dResponse = client.execute(dRequest);
    			HttpEntity entity = dResponse.getEntity();
    			InputStream in = entity.getContent();
    			OutputStream out = response.getOutputStream();
    	    	BufferedOutputStream buff = new BufferedOutputStream(out);
    	    	
    	    	byte[] buffer = new byte[1024*4];
    	    	int bytesRead = -1;
    	    	while ((bytesRead = in.read(buffer)) != -1) {
    				buff.write(buffer, 0, bytesRead);
    			}
    	    	buff.flush();
    	    	buff.close();
    		} catch (Exception e) {
    			e.printStackTrace();
    		} 
            System.out.println(dResponse.toString());
    	}
    	
    }
}

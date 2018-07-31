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

import ccnt.zbp.dedup.client.cache.FileLRU.CacheNode;
import redis.clients.jedis.Jedis;

@MultipartConfig
public class CacheRequest extends HttpServlet {
	static String dataDir = "/media/ubuntu/mec-data";
	static String cacheDir = "/media/ubuntu/fileCache";
	static String[] ips = new String[]{"192.168.1.131","192.168.1.132","192.168.1.144"};
	static String serverIp = "192.168.1.65";
	//static Jedis jedis = RedisUtil.getJedis();
	public CacheRequest() {
        super();
    }

    public void destroy() {
        super.destroy(); 
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response){
    	
    }
    // read 
    // if cache exist return
    // else trans to serverIp
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
    	
    	
    	// simulate remote call
    	// 30 ms average
//    	try {
//			Thread.sleep(30);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
    	//String[] ips = new String[]{"127.0.0.1","127.0.0.1","127.0.0.1"};
    	String fileName = request.getParameter("fileName");
    	String url = "http://"+serverIp+":8080/DedupServer/file/request";
    	String fileHash = request.getParameter("fileHash");
    	long fSize = Long.parseLong(request.getParameter("fSize"));
    	
    	CacheNode node = DataHelper.getFileCache().get(fileHash);
    	if(node == null){
    		// file not exist
    		HttpUriRequest dRequest = RequestBuilder
                    .post(url)
                    .addParameter("fileName", fileName)
                    .addParameter("WoR","R")
                    .addParameter("fileHash", fileHash)
                    .build();

    	    HttpClient client = HttpClientBuilder.create().build();
    	    HttpResponse dResponse = null;
    		try {
    			dResponse = client.execute(dRequest);
    			
    			InputStream stream = dResponse.getEntity().getContent();
     			if (stream.markSupported() == false) {

     		        // lets replace the stream object
     		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
     		      
         		   byte[] buffer = new byte[4*1024];  
         		   int len;  
         		   while ((len = stream.read(buffer)) > -1 ) {  
         		       baos.write(buffer, 0, len);  
         		   }  
         		   baos.flush();  
     		       stream = new ByteArrayInputStream(baos.toByteArray());
     		    }
     			//store file cache
     			FileOutputStream out = new FileOutputStream(DataHelper.getDataDir() + File.separator + "filecache"
     					+ File.separator + fileHash.substring(fileHash.length() - 3)
     					+ File.separator + fileHash);
    	    	BufferedOutputStream buff = new BufferedOutputStream(out);
    	    	
    	    	byte[] buffer = new byte[4*1024];
    	    	int bytesRead = -1;
    	    	while ((bytesRead = stream.read(buffer)) != -1) {
    				buff.write(buffer, 0, bytesRead);
    			}
    	    	buff.flush();
    	    	buff.close();
    	    	out.close();
    	    	
    	    	DataHelper.getFileCache().put(fileHash, fSize);
     			
    	    	stream.reset();
     			
     			//return file
    			OutputStream outs = response.getOutputStream();
    	    	buff = new BufferedOutputStream(outs);
    	    	
    	    	buffer = new byte[1024*4];
    	    	bytesRead = -1;
    	    	while ((bytesRead = stream.read(buffer)) != -1) {
    				buff.write(buffer, 0, bytesRead);
    			}
    	    	buff.flush();
    	    	buff.close();
    	    	outs.close();
    	    	
    		} catch (Exception e) {
    			e.printStackTrace();
    		} 
    	}else{
    		//return file
    		FileInputStream in = new FileInputStream(DataHelper.getDataDir() + File.separator + "filecache"
 					+ File.separator + fileHash.substring(fileHash.length() - 3)
 					+ File.separator + fileHash);
	    	
    		OutputStream outs = response.getOutputStream();
	    	
	    	byte[] buffer = new byte[1024*4];
	    	int bytesRead = -1;
	    	while ((bytesRead = in.read(buffer)) != -1) {
	    		outs.write(buffer, 0, bytesRead);
			}
	    	outs.flush();
	    	outs.close();
    	}
    	
    	/*String WoR = request.getParameter("WoR");
    	
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
    	}*/
    	
    }
}

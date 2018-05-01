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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import redis.clients.jedis.Jedis;

@MultipartConfig
public class FileRequest extends HttpServlet {
	static String dataDir = "/media/ubuntu/mec-data";
	static Jedis jedis = RedisUtil.getJedis();
	public FileRequest() {
        super();
    }

    public void destroy() {
        super.destroy(); 
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response){
    	
    }
    //store file
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
    	
//    	String[] ips = new String[]{"192.168.1.131","192.168.1.132","192.168.1.144"};
//    	System.out.println(request.getParts().size());
    	Part part = new ArrayList<Part>(request.getParts()).get(0);
    	String fileName = part.getName();
    	
    	System.out.println("get file:  "+fileName);
    	
//    	String serverIp = ips[(int) (Long.parseLong(fileName)%3)];
    	
    	//InputStream in = part.getInputStream();
    	
    	//获取文件hash
    	//String fHash = jedis.get(fileName);
    	
//    	FileOutputStream out = new FileOutputStream(new File("C:/Users/zbp/Desktop/tmp-3.txt"));
//    	BufferedOutputStream buff = new BufferedOutputStream(out);
//    	
//    	byte[] buffer = new byte[1024*4];
//    	int bytesRead = -1;
//    	while ((bytesRead = in.read(buffer)) != -1) {
//			buff.write(buffer, 0, bytesRead);
//		}
//    	buff.flush();
//    	buff.close();
    	System.out.println("success");
    	
    }
}

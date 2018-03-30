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

@MultipartConfig
public class UserRequest extends HttpServlet {
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
    
    	String[] ips = new String[]{"192.168.1.131","192.168.1.132","192.168.1.144"};
//    	System.out.println(request.getParts().size());
    	Part part = new ArrayList<Part>(request.getParts()).get(0);
    	String fileName = part.getName();
//    	System.out.println(fileName);
    	InputStream in = part.getInputStream();
    	FileOutputStream out = new FileOutputStream(new File("C:/Users/zbp/Desktop/tmp-3.txt"));
    	BufferedOutputStream buff = new BufferedOutputStream(out);
    	
    	byte[] buffer = new byte[1024*4];
    	int bytesRead = -1;
    	while ((bytesRead = in.read(buffer)) != -1) {
			buff.write(buffer, 0, bytesRead);
		}
    	buff.flush();
    	buff.close();
//    	System.out.println("success");
    	
    }
}

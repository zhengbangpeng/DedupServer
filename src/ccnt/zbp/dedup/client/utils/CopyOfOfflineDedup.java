package ccnt.zbp.dedup.client.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.Part;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import redis.clients.jedis.Jedis;

public class CopyOfOfflineDedup {
	
/*	static String[] ips = new String[]{"192.168.1.131","192.168.1.132","192.168.1.144"};
	
	static Jedis localJedis = LocalRedisUtil.getJedis();
	
	public static void main(String[] args) {
		TimerTask task = new TimerTask() {
            @Override
            public void run() {
            	//get file list from servers
                System.out.println("offline dedup");
                for(String ip : ips){
                	String url = "http://"+ip+":8080/DedupServer/offline/request";
                	HttpUriRequest request = RequestBuilder
                             .post(url)
                             .addParameter("method", "getchunkset")
                             .build();
             	    HttpClient client = HttpClientBuilder.create().build();
             	    HttpResponse response = null;
             		try {
             			response = client.execute(request);
             			HttpEntity entity = response.getEntity();
//             			InputStream in = entity.getContent();
//             			BufferedReader br = new BufferedReader (new InputStreamReader(in));
//             	    	String line = null;
//             	    	while ((line = br.readLine()) != null) {
             	    		//deal with each file
//             	    		String fileName = line;
             	    		//get file metadata
             	    		//String metaurl = "http://"+ip+":8080/DedupServer/offline";
             	    		HttpUriRequest metarequest = RequestBuilder
                                    .post(url)
                                    .addParameter("method", "getfilemeta")
                                    .addParameter("fileName",fileName)
                                    .build();
             	    		HttpResponse metaresponse = client.execute(metarequest);
             	    		
             	    		for(String otherip : ips){
             	    			if(otherip.equals(ip)){
             	    				continue;
             	    			}
             	    			//String otherurl = "http://"+ip+":8080/DedupServer/offline/getfilediff";
             	    			HttpUriRequest otherrequest = RequestBuilder
                                        .post(url)
                                        .addParameter("method", "getchunkdiff")
                                        .setEntity(metaresponse.getEntity())
                                        .build();
                 	    		HttpResponse otherresponse = client.execute(otherrequest);
             	    		}
             	    		
             			}
             			
             		} catch (Exception e) {
             			e.printStackTrace();
             		} 
                }
                
        };
        
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 1000*3600, 1000 * 3600);
	}*/
}

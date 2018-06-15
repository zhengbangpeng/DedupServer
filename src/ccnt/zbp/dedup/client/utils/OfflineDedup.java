package ccnt.zbp.dedup.client.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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

public class OfflineDedup {
	
	static String[] ips = new String[]{"192.168.1.131","192.168.1.132","192.168.1.144"};
	
	//static Jedis localJedis = LocalRedisUtil.getJedis();
	
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
             			
             			InputStream is = new BufferedInputStream(response.getEntity().getContent());
             			
             			ByteArrayOutputStream baos = new ByteArrayOutputStream();
             			org.apache.commons.io.IOUtils.copy(is, baos);
             			byte[] bytes = baos.toByteArray();
             	    		
         	    		for(String otherip : ips){
         	    			if(otherip.equals(ip)){
         	    				continue;
         	    			}
         	    			String otherurl = "http://"+otherip+":8080/DedupServer/offline/request";
         	    			
         	    			HttpEntity chunkset = MultipartEntityBuilder.create()
             	           			.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
             	           			.addBinaryBody("chunkset", bytes)
             	           			.build();
         	    			
         	    			HttpUriRequest otherrequest = RequestBuilder
                                    .post(otherurl)
                                    .addParameter("method", "getchunkdiff")
                                    .setEntity(chunkset)
                                    .build();
         	    			HttpClient otherclient = HttpClientBuilder.create().build();
             	    		HttpResponse otherresponse = otherclient.execute(otherrequest);
             	    		
             	    		HttpEntity chunkdiff = MultipartEntityBuilder.create()
             	           			.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
             	           			.addBinaryBody("chunkdiff", otherresponse.getEntity().getContent())
             	           			.build();
             	    		//HttpEntity diffEntity = otherresponse.getEntity();
             	    		HttpUriRequest updateRequest = RequestBuilder
                                    .post(url)
                                    .addParameter("method", "updatechunk")
                                    .setEntity(chunkdiff)
                                    .build();
             	    		HttpClient diffclient = HttpClientBuilder.create().build();
             	    		HttpResponse diffResponse = diffclient.execute(updateRequest);
         	    		}
             		}catch(Exception e){
             			e.printStackTrace();
             		}
                }
                System.out.println("offline dedup finish!");
            }
            
                
        };
        
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 1000*5, 1000 * 3600);
	}
}

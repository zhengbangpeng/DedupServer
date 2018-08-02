package ccnt.zbp.dedup.client.utils;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
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

import ccnt.zbp.dedup.client.cache.MetaLRU.CacheNode;
import redis.clients.jedis.Jedis;

@MultipartConfig
public class MetaLRUCacheRequest extends HttpServlet {
	static String dataDir = "/media/ubuntu/mec-data";
	static String cacheDir = "/media/ubuntu/fileCache";
	static String[] ips = new String[]{"192.168.1.131","192.168.1.132","192.168.1.144"};
	static String serverIp = "192.168.1.65";
	//static Jedis jedis = RedisUtil.getJedis();
	public MetaLRUCacheRequest() {
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
    	
    	String fileName = request.getParameter("fileName");
    	String url = "http://"+serverIp+":8080/DedupServer/file/request";
    	String fileHash = request.getParameter("fileHash");
    	long fSize = Long.parseLong(request.getParameter("fSize"));
    	
    	CacheNode node = DataHelper.getMetaCache().get(fileHash);
    	if(node == null){
    		// meta file not exist
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
     			
    			//return file
    			OutputStream outs = response.getOutputStream();
    	    	
    	    	byte[]  buffer = new byte[1024*4];
    	    	int bytesRead = -1;
    	    	while ((bytesRead = stream.read(buffer)) != -1) {
    				outs.write(buffer, 0, bytesRead);
    			}
    	    	outs.flush();
    	    	outs.close();
    			
     			//get and store meta file cache
    	    	String serverIp = ips[(int) (Long.parseLong(fileName)%3)];
    	    	url = "http://"+serverIp+":8080/DedupServer/meta/getmeta";
    	    	HttpUriRequest mRequest = RequestBuilder
                        .post(url)
                        .addParameter("fileName", fileName)
                        .addParameter("WoR","R")
                        .addParameter("fileHash", fileHash)
                        .build();
    	    	
    	    	HttpClient mClient = HttpClientBuilder.create().build();
    	    	HttpResponse mResponse = null;
    	    	mResponse = mClient.execute(mRequest);
    	    	InputStream mStream = mResponse.getEntity().getContent();
    	    	
    	    	
     			FileOutputStream out = new FileOutputStream(DataHelper.getDataDir() + File.separator + "metacache"
     					+ File.separator + fileHash.substring(fileHash.length() - 3)
     					+ File.separator + fileHash);
    	
    	    	bytesRead = -1;
    	    	while ((bytesRead = mStream.read(buffer)) != -1) {
    	    		out.write(buffer, 0, bytesRead);
    	    		
    			}
    	    	out.flush();
    	    	out.close();
    	    	
    	    	DataHelper.getMetaCache().put(fileHash, fSize);
     			
    	    	
    		} catch (Exception e) {
    			e.printStackTrace();
    		} 
    	}else{
    		//meta file exist
    		String filePath = DataHelper.getDataDir() + File.separator + "metacache"
 					+ File.separator + fileHash.substring(fileHash.length() - 3)
 					+ File.separator + fileHash;
    		BufferedReader br = new BufferedReader(new FileReader(filePath));
    		String line = null;
    		int threadNum = 9;
    		if(fSize/8 < 9){
    			threadNum = (int)fSize/8;
    		}
    		ExecutorService service = Executors.newFixedThreadPool(threadNum);
    		List<Future<String>> futureList = new ArrayList<Future<String>>();
    		
    		while((line = br.readLine()) != null){
    			System.out.println(line);
    			String[] array = line.split(" ");
    			String partHash = array[0];
                String partServerId = array[1];
                
                
                Future<String> f = service.submit(getJob(partServerId,partHash));
                futureList.add(f);
    		}
    		service.shutdown();
    		
    		OutputStream outs = response.getOutputStream();
    		
    		ExecutorService s = Executors.newSingleThreadExecutor();
            s.execute(getCollectJob(futureList,outs));
            s.shutdown();
	    	
	    	outs.flush();
	    	outs.close();
    	}
    }
	public Callable<String> getJob(final String partServerId, final String partHash) {
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
            	String url = "http://"+ips[Integer.valueOf(partServerId)]+":8080/DedupServer/chunk/request";
                
                HttpUriRequest cRequest = RequestBuilder
                        .post(url)
                        .addParameter("chunkHash", partHash)
                        .build();
            	
            	HttpClient cClient = HttpClientBuilder.create().build();
            	HttpResponse cResponse = null;
            	InputStream mStream = null;
            	try {
            		cResponse = cClient.execute(cRequest);
        			mStream = cResponse.getEntity().getContent();
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
                return IOUtils.toString(mStream);
            }
        };
    }
    public Runnable getCollectJob(final List<Future<String>> fList, OutputStream outs) {
        return new Runnable() {
            public void run() {
                for (Future<String> future : fList) {
                    try {
                        while (true) {
                            if (future.isDone() && !future.isCancelled()) {
                                outs.write(future.get().getBytes());
                                break;
                            } else {
                                Thread.sleep(3);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }
}

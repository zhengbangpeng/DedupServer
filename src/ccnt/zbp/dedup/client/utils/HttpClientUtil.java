package ccnt.zbp.dedup.client.utils;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.sun.xml.internal.ws.util.StreamUtils;

public class HttpClientUtil {
	
	static String tmpDir = "/media/ubuntu/tmp";
	
	public static void doPost(String url, String dataDir, String fileNmae,String fileHash,
			String WoR) {
		if(WoR.equals("R")){
			doPostR(url,dataDir,fileNmae,fileHash);
		}else{
			doPostW(url,dataDir,fileNmae,fileHash);
		}
		
	}

	public static String doPostR(String url,String dataDir, String fileName, String fileHash) {
		
	    HttpUriRequest request = RequestBuilder
                .post(url)
                .addParameter("WoR", "R")
                .addParameter("fileName",fileName)
                .addParameter("fileHash",fileHash)
                .build();

	    HttpClient client = HttpClientBuilder.create().build();
	    HttpResponse response = null;
		try {
			response = client.execute(request);
			
			HttpEntity entity = response.getEntity();
			InputStream in = entity.getContent();
	    	//FileOutputStream out = new FileOutputStream(new File("C:/Users/zbp/Desktop/mec-data/copy.txt"));
	    	FileOutputStream out = new FileOutputStream(tmpDir+File.separator+fileName);
	    	BufferedOutputStream buff = new BufferedOutputStream(out);
	    	
	    	byte[] buffer = new byte[8*1024];
	    	int bytesRead = -1;
	    	while ((bytesRead = in.read(buffer)) != -1) {
				buff.write(buffer, 0, bytesRead);
			}
	    	buff.flush();
	    	buff.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
        return response.toString();
	}

	public static String doPostW(String url,String dataDir, String fileName,String fileHash) {
	
		String ts = "00" + fileName;
		
		String filePath = new File(dataDir).getParent()+File.separator+"filestore"+File.separator+ts.substring(ts.length()-3)+File.separator+fileName; 
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
        return response.toString();
	}
    
    public static String doGet(String url, Map<String, String> param,String token) {

        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
            
            
            if(token != null){
            	httpGet.setHeader("Authorization", "Bearer "+token);
            }
            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public static String doGet(String url) {
        return doGet(url,null,null);
    }
    
    public static String doGet(String url,String token) {
        return doGet(url, null,token);
    }
    
    public static String doGet(String url, Map<String, String> param) {
    	 return doGet(url,param,null);
    }
    
    public static String doPost(String url, Map<String, String> param,String path,String token) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            
            if(token != null){
            	httpPost.setHeader("Authorization", "Bearer "+token);
            }
            
            FileBody bin = new FileBody(new File(path));
            MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create()
            		.addPart("file",bin);
            for(String key : param.keySet()){
            	reqEntity.addPart(key, new StringBody(param.get(key),ContentType.create("text/plain", Consts.UTF_8)));
            }
            httpPost.setEntity(reqEntity.build());
            
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return resultString;
    }
    
    public static String doPost(String url, Map<String, String> param,String token) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            
            if(token != null){
            	httpPost.setHeader("Authorization", "Bearer "+token);
            }
            
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return resultString;
    }

    public static String doPost(String url) {
    	Map<String,String> map = null;
        return doPost(url, map,null);
    }
    
    public static String doPost(String url, Map<String, String> param) {
        return doPost(url, param,null);
    }
    
    public static String doPut(String url, Map<String, String> param,String token) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
//            HttpPost httpPost = new HttpPost(url);
            
            HttpPut httpPut = new HttpPut(url);
            
            if(token != null){
//            	httpPost.setHeader("Authorization", "Bearer "+token);
            	httpPut.setHeader("Authorization", "Bearer "+token);
            }
            
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
//                httpPost.setEntity(entity);
                httpPut.setEntity(entity);
            }
            // 执行http请求
//            response = httpClient.execute(httpPost);
            response = httpClient.execute(httpPut);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return resultString;
    }

    public static String doPut(String url) {
        return doPut(url, null,null);
    }
    
    public static String doPut(String url,String token) {
        return doPut(url, null,token);
    }
    
    public static String doPut(String url, Map<String, String> param) {
        return doPut(url, param,null);
    }
    
    
    public static String doPostJson(String url, String json) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return resultString;
    }
    
    public static void main(String[] args) {
    	long start = System.nanoTime();
    	for (int i=0;i<100;i++){
    		doGet("http://www.baidu.com?i="+i);
    		//doGet("http://i.firefoxchina.cn/?i="+i);
    	}
		long end = System.nanoTime();
		System.out.println((end-start)/100.0/1000000.0);
	}

	
}

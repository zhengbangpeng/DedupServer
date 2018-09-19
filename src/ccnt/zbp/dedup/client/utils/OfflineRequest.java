package ccnt.zbp.dedup.client.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

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
public class OfflineRequest extends HttpServlet {
	static String dataDir = "/media/ubuntu/mec-data";
	static String chunkDir = "/media/ubuntu/mec-data/chunkstore";
	
	//serviceId 0 1 2
	static String[] ips = DataHelper.getServerIps();
	
	static String tmpDir = "/media/ubuntu/tmp";
	
	static String newLine = System.getProperty("line.separator");
	//static String serverId = "0";

	public OfflineRequest() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {

	}

	// write or read file
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String method = request.getParameter("method");
		if (method.equals("getchunkset")) {
			// get new chunk set 
//			Set<String> set = DataHelper.getChunkSet();
			PrintWriter pw = response.getWriter();
			Iterator<String> it = DataHelper.getChunkSet().iterator();
//			for (String chunk : set){
//				pw.write(chunk+" "+chunkJedis.get(chunk));
//				pw.write(newLine);
//			}
			Jedis chunkJedis = ChunkRedisUtil.getJedis();
			while(it.hasNext()){
				String chunk = it.next();
				pw.write(chunk+" "+chunkJedis.get(chunk));
				pw.write(newLine);
				it.remove();
			}
			ChunkRedisUtil.returnResource(chunkJedis);
			pw.flush();
//			set.clear();
			return;
		} else if(method.equals("getchunkdiff")){
			
			PrintWriter pw = response.getWriter();
			
			// get chunk difference
			Part part = new ArrayList<Part>(request.getParts()).get(0);
			InputStream is = part.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while((line=br.readLine())!=null){
				String[] array = line.split(" ");
				String chunkHash = array[0];
				String chunkRemoteServerId = array[1];
				Jedis chunkJedis = ChunkRedisUtil.getJedis();
				if(chunkJedis.exists(chunkHash)){
					String chunkLocalServerId = chunkJedis.get(chunkHash);
					if(chunkRemoteServerId.equals(chunkLocalServerId)){
						continue;
					}
					if(DataHelper.getChunkSet().contains(chunkHash)){
						chunkJedis.set(chunkHash, chunkRemoteServerId);
						DataHelper.getChangedSet().add(chunkHash);
					}else{
						pw.write(chunkHash+" "+chunkLocalServerId);
						pw.write(newLine);
					}
				}
				ChunkRedisUtil.returnResource(chunkJedis);
				
			}
			pw.flush();
			return;
		}else if(method.equals("updatechunk")){
			//update chunk
			Part part = new ArrayList<Part>(request.getParts()).get(0);
			InputStream is = part.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while((line=br.readLine())!=null){
				String[] array = line.split(" ");
				String chunkHash = array[0];
				String chunkNewServerId = array[1];
				Jedis chunkJedis = ChunkRedisUtil.getJedis();
				if(chunkJedis.exists(chunkHash)){
					chunkJedis.set(chunkHash, chunkNewServerId);
					DataHelper.getChangedSet().add(chunkHash);
				}
				ChunkRedisUtil.returnResource(chunkJedis);
			}
			
		}
	}
}

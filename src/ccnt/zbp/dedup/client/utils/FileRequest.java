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
public class FileRequest extends HttpServlet {
	static String dataDir = "/media/ubuntu/mec-data";
	static String chunkDir = "/media/ubuntu/mec-data/chunkstore";
	static Jedis remoteJedis = RedisUtil.getJedis();
	static Jedis localJedis = LocalRedisUtil.getJedis();
	static Jedis chunkJedis = ChunkRedisUtil.getJedis();
	
	//serviceId 0 1 2
	static String[] ips = new String[]{"192.168.1.131","192.168.1.132","192.168.1.144"};
	
	static String tmpDir = "/media/ubuntu/tmp";
	
	static String newLine = System.getProperty("line.separator");
	static String serverId = "0";

	public FileRequest() {
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
		
		// 
		
		String WoR = request.getParameter("WoR");
		String fileName = request.getParameter("fileName");
		String fShortHash = request.getParameter("fileHash");
		if (WoR.equals("R")) {
			// read file
			String metaFilePath = localJedis.get(fShortHash);
			readFileFromMetaFile(metaFilePath,fileName);
			File file = new File(tmpDir+File.separator+fileName);

			// String filePath = "C:/Users/zbp/Desktop/mec-data/test.txt";
			// File file = new File(filePath);

			OutputStream outStream = null;
			FileInputStream inputStream = null;
			outStream = response.getOutputStream();
			inputStream = new FileInputStream(file);
			byte[] buffer = new byte[4 * 1024];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			inputStream.close();
			outStream.flush();
			outStream.close();
			file.delete();
			return;
		} else {
			// write file
			Part part = new ArrayList<Part>(request.getParts()).get(0);
			System.out.println("get file: " + fileName);
			
			
			
			// metadata file path
			String ts = "00" + fileName;
			String metaFilePath = dataDir + File.separator + "metastore"
					+ File.separator + fShortHash.substring(0,3)
					+ File.separator + fShortHash;
			
			File metaFile = new File(metaFilePath);
			if(!metaFile.exists()){
				metaFile.createNewFile();
			}

			// get file hash from remote jedis
			String fLongHash = remoteJedis.get(fileName);

/*			MessageDigest md5 = null;
			try {
				md5 = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			String fShortHash = (new HexBinaryAdapter()).marshal(md5
					.digest(fLongHash.getBytes()));*/
			
			// compare with local jedis
			// if file exist
			if (localJedis.exists(fShortHash)) {
				return;
				/*String existfilePath = localJedis.get(fShortHash);
				localJedis.set(fileName, existfilePath);*/
			} else {
				//add file to the fileset
				//DataHelper.getFileSet().add(fileName);
				
				// store file
				dedupFileByChunk(part, metaFilePath,fLongHash);
				localJedis.set(fileName, metaFilePath);
				localJedis.set(fShortHash, metaFilePath);
			}
		}
	}

	private void dedupFileByChunk(Part part, String metaFilePath, String fLongHash) throws FileNotFoundException, IOException {
		
		int partCounter = 0;

		int sizeOfFiles = 4 * 1024;// 4k chunk
		byte[] buffer = new byte[sizeOfFiles];

		String[] chunkHash = fLongHash.split(",");
		
		FileOutputStream mout = new FileOutputStream(metaFilePath);

		// try-with-resources to ensure closing stream
		try (BufferedInputStream bis = new BufferedInputStream(part.getInputStream())) {

			int bytesAmount = 0;
			while ((bytesAmount = bis.read(buffer)) > 0) {
				//partName = partHash (32 Bytes)
				String partHash = chunkHash[partCounter];
				partCounter++;
				// if chunk exists
				if(chunkJedis.exists(partHash)) {
					//mout.write((partHash+" "+serverId).getBytes());
					mout.write((partHash).getBytes());
					mout.write(newLine.getBytes());
					continue;
				}else{
					String partPath = chunkDir+File.separator+partHash.substring(0, 3)+File.separator+partHash;
					//chunkJedis.set(partHash, partPath);
					chunkJedis.set(partHash, serverId);
					
					// store new chunk
					DataHelper.getChunkSet().add(partHash);
					
					//mout.write((partHash+" "+serverId).getBytes());
					mout.write((partHash).getBytes());
					mout.write(newLine.getBytes());
					
					try (FileOutputStream out = new FileOutputStream(partPath)) {
						out.write(buffer, 0, bytesAmount);
					}
				}
			}
		}

	}

	private void readFileFromMetaFile(String metaFilePath, String fileName) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(metaFilePath));
				FileOutputStream fos = new FileOutputStream(tmpDir+File.separator+fileName);
                BufferedOutputStream mergingStream = new BufferedOutputStream(fos)) {
				String line = null;
				while((line = br.readLine()) != null){
		            //String[] array = line.split(" ");
		            //String partHash = array[0];
		            String partHash = line;
		            //String partServerId = array[1];
		            String partServerId = chunkJedis.get(partHash);
		            if(partServerId.equals(serverId)){
		            	Files.copy(Paths.get(chunkDir+File.separator+partHash.substring(0, 3)+File.separator+partHash), mergingStream);
		            }else{
		            	String ip = ips[Integer.parseInt(partServerId)];
		            	InputStream in = readChunkFileFromServer(ip,partHash);
		            	
		            	//write chunk
		            	byte[] buffer = new byte[1024*4];
		    	    	int bytesRead = -1;
		    	    	while ((bytesRead = in.read(buffer)) != -1) {
		    	    		mergingStream.write(buffer, 0, bytesRead);
		    			}
		            }
		            /*fos.flush();
		            fos.close();*/
				}
                
		}
		return;
	}

	private InputStream readChunkFileFromServer(String ip, String partHash) {
		String url = "http://"+ip+":8080/DedupServer/chunk/request";
		//read file
	    HttpUriRequest dRequest = RequestBuilder
                .post(url)
                .addParameter("chunkHash", partHash)
                .build();

	    HttpClient client = HttpClientBuilder.create().build();
	    HttpResponse dResponse = null;
	    InputStream in = null;
		try {
			dResponse = client.execute(dRequest);
			HttpEntity entity = dResponse.getEntity();
			in = entity.getContent();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return in;
	}
}

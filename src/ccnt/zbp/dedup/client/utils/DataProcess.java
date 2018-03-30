package ccnt.zbp.dedup.client.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import redis.clients.jedis.Jedis;

public class DataProcess {
	
	public static long count = 0;

	public static void main(String[] args) {
		//String dataDir = "/home/ubuntu/Desktop/mec-data/data-file";
		String dataDir = "C:/Users/zbp/Desktop/mec-data/web-vm/data-file";
		//DataProcess.extractFileTrace(dataDir);
		DataProcess.createAllFile(dataDir);
	}
	private static void createAllFile(String dataDir) {
		init(dataDir);
		
		File dir = new File(dataDir);
		File[] files =dir.listFiles();
		for(File file:files){
			System.out.println(file.getAbsolutePath());
			createFile(file);
		}
	}
	private static void createFile(File file) {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	String[] row = line.split(" ");
		    	String fTimestamp = row[0];
		    	int fSize = Integer.parseInt(row[1]);
				String fWoR = row[2];
				
				String ts = "00" + fTimestamp;
				
				String newFilePath = file.getParentFile().getParent()+File.separator+"filestore"+File.separator+ts.substring(ts.length()-3)+File.separator+fTimestamp;
				System.out.println(count++ + "  " + newFilePath);
				try {
					//count = count +fSize;
					//System.out.println(count);
					FileGenerator.create(new File(newFilePath), fSize*512);
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }
		    
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	//生成1000个文件夹 000-999
	private static void init(String dataDir) {
		File dir = new File(dataDir);
		File filestoreDir = new File(dir.getParent()+File.separator+"filestore");
		if(!filestoreDir.exists()){
			filestoreDir.mkdir();
		}
		for(int i=0; i<10; i++){
			for(int j=0; j<10; j++){
				for(int k=0; k<10; k++){
					File newDir = new File(filestoreDir.getAbsolutePath()+File.separator+i+j+k);
					if(!newDir.exists()){
						newDir.mkdir();
					}
				}
			}
		}
	}
	public static void extractFileTrace(String dataDir) {  
		File dir = new File(dataDir);
		File[] files =dir.listFiles();
		for(File file:files){
			System.out.println(file.getAbsolutePath());
			mergeFile(file);
		}
		
	}
	public static void mergeFile(File chunkFileName){
		Jedis jedis = RedisUtil.getJedis();
		File newDir = new File(chunkFileName.getParent()+File.separator+"data-file");
		if(!newDir.exists()){
			newDir.mkdir();
		}
		File newFile;
		FileWriter writer = null;
		
		int lLba = 0;
		int lSize = 0;
		int fSize = 0;
		String fTimestamp = "";
		String fWoR = "";
		String fHash = "";
		
		try {
			newFile = File.createTempFile(chunkFileName.getName(), ".file", newDir);
			writer = new FileWriter(newFile);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try (BufferedReader br = new BufferedReader(new FileReader(chunkFileName))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	String[] row = line.split(" ");
		    	
		    	String timestamp = row[0];
		    	int lba = Integer.parseInt(row[3]);
		    	int bSize = Integer.parseInt(row[4]);
		    	String WoR = row[5];
		    	String bHash = row[8];
		    	
		    	// a new file
		    	if(lLba+lSize!=lba){
		    		// end last file && insert the file
		    		if(fSize!=0){
		    			jedis.set(fTimestamp, fHash);
		    			
		    			//insert a file
		    			writer.write(fTimestamp+" "+fSize+" "+fWoR);
		    			System.out.println(fTimestamp+" "+fSize+" "+fWoR);
		    			writer.write("\n");
		    		}
		    		
		    		// new file info
	    			lLba = lba;
	    			lSize = bSize;
	    			fTimestamp = timestamp;
	    			
	    			fWoR = WoR;
	    			fSize = bSize;
	    			fHash = bHash;
		    	}else{
		    		// a block
		    		lLba = lba;
		    		lSize = bSize;
		    		fSize = fSize + bSize;
		    		fHash = fHash +","+bHash;
		    	}
		    }
		    // end last file && insert the file
	    	jedis.set(fTimestamp, fHash);
	    	
	    	//insert a file 
	    	writer.write(fTimestamp+" "+fSize+" "+fWoR);
	    	System.out.println(fTimestamp+" "+fSize+" "+fWoR);
			writer.write("\n");	
		    writer.close();
		    
		} catch (Exception e) {
			e.printStackTrace();
		} 
		RedisUtil.returnResource(jedis);
	}
}

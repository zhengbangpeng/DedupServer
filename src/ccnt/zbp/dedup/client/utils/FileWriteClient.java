package ccnt.zbp.dedup.client.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import redis.clients.jedis.Jedis;

public class FileWriteClient {
	
	public static void main(String[] args) {
		//String dataDir = "/media/ubuntu/mec-data/data-file";
		String dataDir = "C:/Users/zbp/Desktop/mec-data/web-vm/data-file";
		FileWriteClient.start(dataDir);
	}

	private static void start(String dataDir) {
		File dir = new File(dataDir);
		/*File[] files =dir.listFiles();
		for(File f : files){
			System.out.println(f.getName());
		}*/
		List<String> names =Arrays.asList(dir.list());
		Collections.sort(names, new Comparator<String>(){

			@Override
			public int compare(String o1, String o2) {
				if(o1.length()<o2.length()){
					return -1;
				}else if(o1.length()==o2.length()){
					return o1.compareTo(o2);
				}else{
					return 1;
				}
			}
			
		});
		long start = System.nanoTime();
		//servier ips
		//coordinator ip
		String serverIp = DataHelper.getCoordinatorIp();
		//edge ip
		String cacheIp = DataHelper.getEdgeIp();
		for(String n : names){
			//System.out.println(n);
			try (BufferedReader br = new BufferedReader(new FileReader(dataDir+File.separator+n))) {
			    String line;
			    while ((line = br.readLine()) != null) {
			    	String[] row = line.split(" ");
			    	
			    	long timestamp = Long.parseLong(row[0]);
			    	String fileNmae = row[0];
			    	int fSize = Integer.parseInt(row[1]);
			    	String WoR = row[2];
			    	if(WoR.equals("W")){
			    		continue;
			    	}
			    	//System.out.println(line);
			    	Jedis localJedis = LocalRedisUtil.getJedis();
			    	String fLongHash = localJedis.get(fileNmae);
			    	LocalRedisUtil.returnResource(localJedis);
			    	
			    	MessageDigest md5 = null;
					try {
						md5 = MessageDigest.getInstance("MD5");
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
					String fShortHash = (new HexBinaryAdapter()).marshal(md5
							.digest(fLongHash.getBytes())).toLowerCase();
			    	
			    	//String serverIp = ips[(int) (timestamp%3)];
			    	
			    	while(true){
			    		// expriment data 21 days to /84 = 6 hours
			    		long now = (System.nanoTime()-start)*84;
			    		if(now > timestamp){
			    			long requestStart = System.nanoTime();
			    			HttpClientUtil.doPost("http://"+serverIp+":8080/DedupServer/user/request", dataDir, fileNmae,fShortHash,"W");
			    			// time ns -> ms
			    			double requestTime = (System.nanoTime()-requestStart)/1000000.0;
			    			System.out.println("finish time: "+String.format("%.1f", requestTime)+" sendRequest: "+line);
			    			break;
			    		}
			    	}
			    }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return;
	}
}

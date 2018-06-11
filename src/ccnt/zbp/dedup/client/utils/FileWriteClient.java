package ccnt.zbp.dedup.client.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileWriteClient {
	public static void main(String[] args) {
		String dataDir = "/media/ubuntu/mec-data/data-file";
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
				}
				return o1.compareTo(o2);
			}
			
		});
		long start = System.nanoTime();
		//servier ips
		//String[] ips = new String[]{"192.168.1.131","192.168.1.132","192.168.1.144"};
		//coordinator ip
		String serverIp = "192.168.1.65";
		//edge ip
		String cacheIp = "192.168.1.130";
		for(String n : names){
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
			    	//String serverIp = ips[(int) (timestamp%3)];
			    	
			    	while(true){
			    		// expriment data 21 days to /84 = 6 hours
			    		long now = (System.nanoTime()-start)/84;
			    		if(now > timestamp){
			    			HttpClientUtil.doPost("http://"+serverIp+":8080/DedupServer/user/request", dataDir, fileNmae,"W");
			    			System.out.println("time:  "+now+"  sendRequest:  "+line);
			    			break;
			    		}
			    	}
			    }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}

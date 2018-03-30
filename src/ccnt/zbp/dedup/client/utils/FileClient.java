package ccnt.zbp.dedup.client.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileClient {
	public static void main(String[] args) {
		String dataDir = "/media/ubuntu/mec-data/data-file";
		FileClient.start(dataDir);
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
		for(String n : names){
			try (BufferedReader br = new BufferedReader(new FileReader(dataDir+File.separator+n))) {
			    String line;
			    while ((line = br.readLine()) != null) {
			    	String[] row = line.split(" ");
			    	
			    	long timestamp = Long.parseLong(row[0]);
			    	String fileNmae = row[0];
			    	int fSize = Integer.parseInt(row[1]);
			    	String WoR = row[2];
			    	
			    	while(true){
			    		long now = System.nanoTime();
			    		if((now - start)/21 > timestamp){
			    			HttpClientUtil.doPost("http://192.168.1.131:8080/DedupServer/user/request", dataDir, fileNmae);
			    			System.out.println("sendRequest: "+line);
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

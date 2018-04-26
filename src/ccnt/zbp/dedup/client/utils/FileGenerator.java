package ccnt.zbp.dedup.client.utils;

import java.io.*;

public class FileGenerator {

	public static void main(String[] args) {
		try {
			FileGenerator.create(new File("1.txt"), 100000*1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void create(File file, long length) throws IOException{  
        //long start = System.currentTimeMillis();  
        RandomAccessFile r = null;  
        try {  
            r = new RandomAccessFile(file, "rw");  
            r.setLength(length);
        } finally{  
            if (r != null) {  
                try {  
                    r.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        //long end = System.currentTimeMillis();  
        //System.out.println(end-start);  
          
    } 

}

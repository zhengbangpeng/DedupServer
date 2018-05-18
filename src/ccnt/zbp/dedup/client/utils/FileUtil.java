package ccnt.zbp.dedup.client.utils;

import java.io.*;
import java.nio.channels.FileChannel;

public class FileUtil {

	public static void main(String[] args) {
		try {
			FileUtil.create(new File("1.txt"), 100000*1024);
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
	
	public static void copyFileUsingFileChannels(File source, File dest) throws IOException {    
        FileChannel inputChannel = null;    
        FileChannel outputChannel = null;    
	    try {
	        inputChannel = new FileInputStream(source).getChannel();
	        outputChannel = new FileOutputStream(dest).getChannel();
	        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
	    } finally {
	        inputChannel.close();
	        outputChannel.close();
	    }
	}

}

package ccnt.zbp.dedup.client.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Set;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apache.commons.io.FileUtils;

import redis.clients.jedis.Jedis;

import com.sun.corba.se.spi.ior.MakeImmutable;

public class TestH {

	public  static void main(String[] args) throws Exception {
	
//		String oriPath = "C:/Users/zbp/Desktop/test/ori.txt";
//		String copyPath = "C:/Users/zbp/Desktop/copy.txt";
//		FileUtil.copyFileUsingFileChannels(new File(oriPath), new File(copyPath));
		
		//HttpClientUtil.doPost("http://"+"127.0.0.1"+":8080/DedupServer/user/request", "test", "123","R");
//		char c;
//		for(c='0' ;c<='9';c++){
//			System.out.print("'"+c+"',");
//		}
//		for(c='a' ;c<='z';c++){
//			System.out.print("'"+c+"',");
//		}
		
/*		String str = "abcdef";
		System.out.println(str.substring(0,3));*/
		
//		DataHelper.getChunkSet().add("aaa");
//		DataHelper.getChunkSet().add("bbb");
//		
//		System.out.println(DataHelper.getChunkSet().size());
//		
//		Set<String> set = DataHelper.getChunkSet();
//		set.clear();
//		System.out.println(DataHelper.getChunkSet().size());
/*		long size = 100000l;
		int init = (int) (size * 3 / 32);
		System.out.println(init);*/
/*		double d = 1.3643;
		System.out.println(String.format("%.1f", d));
		
		System.out.println(new Date().getTime()/1000);*/
		
		Jedis chunkJedis = ChunkRedisUtil.getJedis();
		Set<String> keys = chunkJedis.keys("*");
		int i = 0;
		int j = 0;
		System.out.println(keys.size());
		for(String key : keys){
			/*if(i > 10){
				break;
			}
			i++;*/
			String value = chunkJedis.get(key);
			if(!value.equals("2")){
				j++;
			}
			System.out.println(value);
			
		}
		System.out.println("dedup count:" +j);
		return;
	}

}

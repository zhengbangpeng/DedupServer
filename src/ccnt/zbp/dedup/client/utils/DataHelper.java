package ccnt.zbp.dedup.client.utils;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import ccnt.zbp.dedup.client.cache.FileLRU;
import ccnt.zbp.dedup.client.cache.MetaLRU;

public class DataHelper {
	
	static String dataDir = "/media/ubuntu/mec-data";
	
	//public static Set<String> FileSet = Collections.synchronizedSet(new HashSet<String>());
	public static Set<String> FileSet = ConcurrentHashMap.newKeySet();
	
	
	//public static Set<String> ChunkSet = Collections.synchronizedSet(new HashSet<String>());
	
	public static Set<String> ChunkSet = ConcurrentHashMap.newKeySet();
	//public static Set<String> ChunkSet = new ConcurrentHashSet<>();
	
	//chunk changed in this cycle
	//public static Set<String> ChangedSet = Collections.synchronizedSet(new HashSet<String>());
	public static Set<String> ChangedSet = ConcurrentHashMap.newKeySet();
	
	//param size
	//file size 8 = 4k
	//4G = 4 * 1024 * 1024 * 2 = 8388608
	public static FileLRU fileCache = new FileLRU(1140000);
	
	//4G assume *50 
	public static MetaLRU metaCache = new MetaLRU(1140000*50);
	
	//param size
	//public static Map<String, String> metaCache = new MetaLRU<>(5);
	
	public static Set<String> getFileSet() {
		return FileSet;
	}


	public static MetaLRU getMetaCache() {
		return metaCache;
	}


	public static void setMetaCache(MetaLRU metaCache) {
		DataHelper.metaCache = metaCache;
	}


	public static FileLRU getFileCache() {
		return fileCache;
	}


	public static void setFileCache(FileLRU fileCache) {
		DataHelper.fileCache = fileCache;
	}


	public static void setFileSet(Set<String> fileSet) {
		FileSet = fileSet;
	}


	public static Set<String> getChunkSet() {
		return ChunkSet;
	}


	public static void setChunkSet(Set<String> chunkSet) {
		ChunkSet = chunkSet;
	}


	public static Set<String> getChangedSet() {
		return ChangedSet;
	}


	public static void setChangedSet(Set<String> changedSet) {
		ChangedSet = changedSet;
	}


	public static String getDataDir() {
		return dataDir;
	}


	public static void setDataDir(String dataDir) {
		DataHelper.dataDir = dataDir;
	}
	public static void main(String[] args) {
		ChunkSet.add("test");
		System.out.println(ChunkSet);
		ChunkSet.clear();
		System.out.println(ChunkSet);
	}
}

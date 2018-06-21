package ccnt.zbp.dedup.client.utils;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import ccnt.zbp.dedup.client.cache.FileLRU;
import ccnt.zbp.dedup.client.cache.MetaLRU;

public class DataHelper {
	
	static String dataDir = "/media/ubuntu/mec-data";
	
	public static Set<String> FileSet = Collections.synchronizedSet(new HashSet<String>());
	//public static Set<String> FileSet = ConcurrentHashMap.newKeySet();
	
	
	public static Set<String> ChunkSet = Collections.synchronizedSet(new HashSet<String>());
	
	//public static Set<String> ChunkSet = ConcurrentHashMap.newKeySet();
	//public static Set<String> ChunkSet = new ConcurrentHashSet<>();
	
	//chunk changed in this cycle
	public static Set<String> ChangedSet = Collections.synchronizedSet(new HashSet<String>());
	//public static Set<String> ChangedSet = ConcurrentHashMap.newKeySet();
	
	//param size
	//public static Map<String, String> fileCache = new FileLRU<>(5);
	
	//param size
	//public static Map<String, String> metaCache = new MetaLRU<>(5);

	public static Set<String> getFileSet() {
		return FileSet;
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

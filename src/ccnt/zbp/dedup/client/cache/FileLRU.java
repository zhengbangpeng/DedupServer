package ccnt.zbp.dedup.client.cache;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import ccnt.zbp.dedup.client.utils.DataHelper;


public class FileLRU {

    private long currentCacheSize;
    private long CacheCapcity;
    private HashMap<String,CacheNode> caches;
    private CacheNode first;
    private CacheNode last;
    
    

    public FileLRU(long size){
        currentCacheSize = 0;
        this.CacheCapcity = size;
        caches = new HashMap<String,CacheNode>((int)(size*3/32));
    }

    public void put(String fileHash,long fileSize){
        CacheNode node = caches.get(fileHash);
        if(node == null){
        	while(currentCacheSize + fileSize > CacheCapcity){
        		caches.remove(last.fileHash);
                removeLast();
        	}
            node = new CacheNode();
            node.fileHash = fileHash;
            node.fileSize = fileSize;
        }
        moveToFirst(node);
        caches.put(fileHash, node);
    }

    public CacheNode get(String fielHash){
        CacheNode node = caches.get(fielHash);
        if(node == null){
            return null;
        }
        moveToFirst(node);
        return node;
    }

    public Object remove(String fileHash){
        CacheNode node = caches.get(fileHash);
        if(node != null){
            if(node.pre != null){
                node.pre.next=node.next;
            }
            if(node.next != null){
                node.next.pre=node.pre;
            }
            if(node == first){
                first = node.next;
            }
            if(node == last){
                last = node.pre;
            }
        }

        return caches.remove(fileHash);
    }

    public void clear(){
        first = null;
        last = null;
        caches.clear();
    }



    private void moveToFirst(CacheNode node){
        if(first == node){
            return;
        }
        if(node.next != null){
            node.next.pre = node.pre;
        }
        if(node.pre != null){
            node.pre.next = node.next;
        }
        if(node == last){
            last= last.pre;
        }
        if(first == null || last == null){
            first = last = node;
            return;
        }

        node.next=first;
        first.pre = node;
        first = node;
        first.pre=null;

    }

    private void removeLast(){
        if(last != null){
            last = last.pre;
            if(last == null){
                first = null;
            }else{
                last.next = null;
            }
         removeCacheFile(last.fileHash);
        }
    }
    private void removeCacheFile(String fileHash) {
		String filePath =  DataHelper.getDataDir() + File.separator + "filecache"
				+ File.separator + fileHash.substring(fileHash.length() - 3)
				+ File.separator + fileHash;
		File file = new File(filePath);
		file.delete();
	}

	@Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        CacheNode node = first;
        while(node != null){
            sb.append(String.format("%s:%s ", node.fileHash,node.fileSize));
            node = node.next;
        }

        return sb.toString();
    }

    public class CacheNode{
        CacheNode pre;
        CacheNode next;
        String fileHash;
        //file size
        long fileSize;
        public CacheNode(){

        }
    }

    public static void main(String[] args) {

/*        FileLRU<Integer,String> lru = new FileLRU<Integer,String>(3);

        lru.put(1, "a");    // 1:a
        System.out.println(lru.toString());
        lru.put(2, "b");    // 2:b 1:a 
        System.out.println(lru.toString());
        lru.put(3, "c");    // 3:c 2:b 1:a 
        System.out.println(lru.toString());
        lru.put(4, "d");    // 4:d 3:c 2:b  
        System.out.println(lru.toString());
        lru.put(1, "aa");   // 1:aa 4:d 3:c  
        System.out.println(lru.toString());
        lru.put(2, "bb");   // 2:bb 1:aa 4:d
        System.out.println(lru.toString());
        lru.put(5, "e");    // 5:e 2:bb 1:aa
        System.out.println(lru.toString());
        System.out.println(lru.get(1));         // 1:aa 5:e 2:bb
        System.out.println(lru.toString());
        lru.remove(11);     // 1:aa 5:e 2:bb
        System.out.println(lru.toString());
        lru.remove(1);      //5:e 2:bb
        System.out.println(lru.toString());
        lru.put(1, "aaa");  //1:aaa 5:e 2:bb
        System.out.println(lru.toString());*/
    }

}

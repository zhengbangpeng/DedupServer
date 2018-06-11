package ccnt.zbp.dedup.client.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 20170220b on 2017/11/27.
 */
public class LFU<k, v> {
  private final int capcity;

  private Map<k, v> cache = new HashMap<>();

  private Map<k, HitRate> count = new HashMap<>();

  public LFU(int capcity) {
    this.capcity = capcity;
  }

  public void put(k key, v value) {
    v v = cache.get(key);
    if (v == null) {
      if (cache.size() == capcity) {
        removeElement();
      }
      count.put(key, new HitRate(key, 1, System.nanoTime()));
    } else {
      addHitCount(key);
    }
    cache.put(key, value);
  }

  public v get(k key) {
    v value = cache.get(key);
    if (value != null) {
      addHitCount(key);
      return value;
    }
    return null;
  }

  //remove element
  private void removeElement() {
    HitRate hr = Collections.min(count.values());
    cache.remove(hr.key);
    count.remove(hr.key);
  }

  //update 
  private void addHitCount(k key) {
    HitRate hitRate = count.get(key);
    hitRate.hitCount = hitRate.hitCount + 1;
    hitRate.lastTime = System.nanoTime();
  }

  //inner class
  class HitRate implements Comparable<HitRate> {
    private k key;
    private int hitCount;
    private long lastTime;

    private HitRate(k key, int hitCount, long lastTime) {
      this.key = key;
      this.hitCount = hitCount;
      this.lastTime = lastTime;
    }

    @Override
    public int compareTo(HitRate o) {
      int compare = Integer.compare(this.hitCount, o.hitCount);
      return compare == 0 ? Long.compare(this.lastTime, o.lastTime) : compare;
    }
  }


  public static void main(String[] args) {
    LFU<Integer, Integer> cache = new LFU<>(3);
    cache.put(2, 2);
    cache.put(1, 1);

    System.out.println(cache.get(2));
    System.out.println(cache.get(1));
    System.out.println(cache.get(2));

    cache.put(3, 3);
    cache.put(4, 4);

    //1、2 hit number, add 3, add 4 remove 3
    System.out.println(cache.get(3));
    System.out.println(cache.get(2));
    //System.out.println(cache.get(1));
    System.out.println(cache.get(4));

    cache.put(5, 5);
    //2 hit number:2 1:1 4:1 but 4 last access time is newer, so add 5 remove 1
    System.out.println("-=-=-=-");
    cache.cache.entrySet().forEach(entry -> {
      System.out.println(entry.getValue());
    });

  }
}

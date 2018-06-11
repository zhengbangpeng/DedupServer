package ccnt.zbp.dedup.client.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 20170220b on 2017/11/27.
 */
public class LRU<k, v> extends LinkedHashMap<k, v> {

  private final int MAX_SIZE;

  public LRU(int capcity) {
    super(16, 0.75f, true);
    this.MAX_SIZE = capcity;
  }

  @Override
  public boolean removeEldestEntry(Map.Entry<k, v> eldest) {
    if (size() > MAX_SIZE) {
      System.out.println("entry remove: " + eldest.getValue());
    }
    return size() > MAX_SIZE;
  }

  public static void main(String[] args) {
    Map<Integer, Integer> map = new LRU<>(5);
    for (int i = 1; i <= 11; i++) {
      map.put(i, i);
      System.out.println("cache volume: " + map.size());
      if (i == 4) {
        map.get(1);
      }
    }

    System.out.println("=-=-=-=-=-=-=-map entry:");
    map.entrySet().forEach(integerIntegerEntry -> System.out.println(integerIntegerEntry.getValue()));

  }

}

package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Performs reservoir sampling
 */

public class ReservoirSampling {
  // A function to randomly select k items from stream[0..n-1].
  public static List<String> selectKItems(List<String> stream, int k) {
    Random rand = new Random();
    List<String> givenList = new ArrayList<String>();
    givenList.addAll(stream);
    List<String> result = new ArrayList<String>();
    for (int i = 0; i < k; i++) {
      int randomIndex = rand.nextInt(givenList.size());
      String randomElement = givenList.get(randomIndex);
      result.add(randomElement);
      givenList.remove(randomIndex);
    }
    return result;
  }
}

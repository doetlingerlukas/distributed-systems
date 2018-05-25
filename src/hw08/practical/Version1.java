package hw08.practical;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lukas Dötlinger.
 */
public class Version1 {

  private static final double failureRate = 0.95;
  private static final int N = 2;
  private static final int T = 10000;

  public static void main(String[] args) {

    Map<Integer, Integer> values = IntStream.rangeClosed(1, T)
      .mapToObj(i -> new HashMap.SimpleEntry<>(i, 0))
      .collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue));

    IntStream.rangeClosed(1, N)
      .forEach(i -> {
        values
          .forEach((k, v) -> {
            if (Utils.randomWithProbability(failureRate)) values.replace(k, v, 1);
          });
      });

    System.out.println("Availability: "+values.values().stream()
      .mapToInt(i -> i)
      .sum() / (double) T);
  }
}

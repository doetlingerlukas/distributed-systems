package hw08.practical;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lukas Dötlinger.
 */
public class Version2 {

  private static final double failureRate = 0.95;
  private static final double targetAvailability = 0.999;
  private static final int T = 10000;

  public static void main(String[] args) {

    Map<Integer, Integer> values = IntStream.rangeClosed(1, T)
      .mapToObj(i -> new HashMap.SimpleEntry<>(i, 0))
      .collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue));

    int N = 0;
    double availability = 0;
    while (availability < targetAvailability) {
      N++;
      values
        .forEach((k, v) -> values.replace(k, 0));
      IntStream.rangeClosed(1, N)
        .forEach(i -> {
          values
            .forEach((k, v) -> {
              if (Utils.randomWithProbability(failureRate)) values.replace(k, v, 1);
            });
        });
      availability = values.values().stream()
        .mapToInt(i -> i)
        .sum() / (double) T;
    }

    System.out.println("Availability "+targetAvailability+" is reached with N >= "+N);
  }
}

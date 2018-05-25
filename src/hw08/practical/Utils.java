package hw08.practical;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Lukas Dötlinger.
 */
public class Utils {

  public static boolean randomWithProbability(double probability) {
    return ThreadLocalRandom.current().nextDouble(0, 1) < probability;
  }
}

package hw06;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
public class Utils {

  public static String getRandomString(int size) {
    String alphabet = "abcdefghijklmnopqrstuvwxyz123456789";
    return ThreadLocalRandom.current()
      .ints(0, alphabet.length())
      .limit(size)
      .mapToObj(i -> alphabet.charAt(i))
      .map(Object::toString)
      .collect(Collectors.joining());
  }
}

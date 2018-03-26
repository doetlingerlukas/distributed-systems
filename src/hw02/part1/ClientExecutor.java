package hw02.part1;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lukas Dötlinger.
 */
public class ClientExecutor {

  /**
   * Method to generate a random String of a given size.
   */
  public static String getRandomString(int size) {
    String alphabet = "abcdefghijklmnopqrstuvwxyz";
    return ThreadLocalRandom.current()
      .ints(0, alphabet.length()-1)
      .limit(size)
      .mapToObj(i -> alphabet.charAt(i))
      .map(Object::toString)
      .collect(Collectors.joining());
  }

  /**
   * Starts 3 clients, which will submit a random String to sort to the server.
   */
  public static void main(String[] args) {
    IntStream.rangeClosed(1,3)
      .mapToObj(i -> new SimpleClient(8888, "localhost", getRandomString(10)))
      .forEach(client -> new Thread(client).start());

  }
}

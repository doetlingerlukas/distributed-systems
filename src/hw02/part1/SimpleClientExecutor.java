package hw02.part1;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lukas Dötlinger.
 */
public class SimpleClientExecutor {

  /**
   * Method to generate a random String of a given size.
   */
  public static String getRandomString(int size) {
    String alphabet = "abcdefghijklmnopqrstuvwxyz123456789";
    return ThreadLocalRandom.current()
      .ints(0, alphabet.length())
      .limit(size)
      .mapToObj(i -> alphabet.charAt(i))
      .map(Object::toString)
      .collect(Collectors.joining());
  }

  /**
   * Starts 6 clients, which will submit a random String to sort to the server.
   * Starts a thread to remotely shutdown the server.
   */
  public static void main(String[] args) {
    IntStream.rangeClosed(1,6)
      .mapToObj(i -> new SimpleClient(8888, "localhost", getRandomString(10)))
      .forEach(client -> new Thread(client).start());

    //Start the shutdown thread.
    new Thread(() -> {
      try {
        Socket socket = new Socket("localhost", 8888);
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        output.writeUTF("-shutdown-");
        socket.close();
      } catch (Exception e) {
        System.err.println("Shutdown failed!");
      }
    }).start();
  }
}

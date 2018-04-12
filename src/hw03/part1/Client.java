package hw03.part1;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lukas Dötlinger.
 */
public class Client implements Runnable {

  private int id;
  private String string;

  public Client(int id, String string) {
    this.id = id;
    this.string = string;
  }

  @Override
  public void run() {
    try {
      Registry registry = LocateRegistry.getRegistry(null);

      RemoteService stub = (RemoteService) registry.lookup("remoteService");
      if (string.equals("-compute-")) {
        System.out.println("Server starts computing for client "+id);
        stub.compute(5000L);
        System.out.println("Server ended computing for client "+id);
      } else {
        System.out.println(stub.sort(string)+" for client "+id);
      }

    } catch (RemoteException e) {
      System.err.println("Failed to get registry!");
      e.printStackTrace();
    } catch (NotBoundException e) {
      System.err.println("Failed to retrieve stub!");
      e.printStackTrace();
    }
  }

  public static String getRandomString(int size) {
    String alphabet = "abcdefghijklmnopqrstuvwxyz123456789";
    return ThreadLocalRandom.current()
      .ints(0, alphabet.length())
      .limit(size)
      .mapToObj(i -> alphabet.charAt(i))
      .map(Object::toString)
      .collect(Collectors.joining());
  }

  public static void main(String[] args) {
    IntStream.rangeClosed(1,6)
      .mapToObj(i -> new Client(i, i <= 3 ? "-compute-" : getRandomString(10)))
      .forEach(client -> new Thread(client).start());
  }
}

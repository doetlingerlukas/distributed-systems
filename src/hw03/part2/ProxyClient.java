package hw03.part2;

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
public class ProxyClient implements Runnable {

  private int id;
  private String access;
  private String string;

  public ProxyClient(int id, String access, String string) {
    this.id = id;
    this.access = access;
    this.string = string;
  }

  @Override
  public void run() {
    try {
      Registry registry = LocateRegistry.getRegistry(null);

      ProxyService stub = (ProxyService) registry.lookup("proxyServer");
      stub.accessService(access, string, id);

    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (NotBoundException e) {
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
      .mapToObj(i -> new ProxyClient(i, i <= 3 ? "-compute-" : "-sort-", getRandomString(10)))
      .forEach(client -> new Thread(client).start());
  }
}

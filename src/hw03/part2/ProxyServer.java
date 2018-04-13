package hw03.part2;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Created by Lukas Dötlinger.
 */
public class ProxyServer extends ProxyServiceImpl {

  public static void main(String[] args) {

    IntStream.rangeClosed(1,2)
      .mapToObj(i -> new ComputeServer(i))
      .forEach(computeServer -> new Thread(computeServer).start());

    IntStream.rangeClosed(1,2)
      .mapToObj(i -> new SortServer(i))
      .forEach(sortServer -> new Thread(sortServer).start());

    try {
      ProxyServiceImpl proxyService = new ProxyServiceImpl();
      ProxyService stub = (ProxyService) UnicastRemoteObject.exportObject(proxyService, 0);

      Registry registry = LocateRegistry.getRegistry();
      registry.bind("proxyServer", stub);

      System.out.println("Successfully registered proxy server!");
    } catch (RemoteException e) {
      System.err.println("Failed to generate stub!");
      e.printStackTrace();
    } catch (AlreadyBoundException e) {
      System.err.println("Failed to bind stub to registry!");
    }
  }

}

package hw03.part1;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Lukas Dötlinger.
 */
public class Server extends RemoteServiceImpl {

  public Server() {}

  public static void main(String[] args) {
    try {
      RemoteServiceImpl remoteService = new RemoteServiceImpl();
      RemoteService stub = (RemoteService) UnicastRemoteObject.exportObject(remoteService, 0);

      Registry registry = LocateRegistry.getRegistry();
      registry.bind("remoteService", stub);

      System.out.println("Successfully registered remote service!");
    } catch (RemoteException e) {
      System.err.println("Failed to generate stub!");
      e.printStackTrace();
    } catch (AlreadyBoundException e) {
      System.err.println("Failed to bind stub to registry!");
      e.printStackTrace();
    }

  }

}

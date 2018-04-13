package hw03.part2;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Lukas Dötlinger.
 */
public class ComputeServer extends ComputeServiceImpl implements Runnable {

  private int id;

  public ComputeServer(int id) {
    this.id = id;
  }

  @Override
  public void run() {
    try {
      ComputeServiceImpl computeService = new ComputeServiceImpl();
      ComputeService stub = (ComputeService) UnicastRemoteObject.exportObject(computeService, 0);

      Registry registry = LocateRegistry.getRegistry(null);
      registry.bind("computeService"+id, stub);

      System.out.println("Successfully registered compute service "+id);
    } catch (RemoteException e) {
      System.err.println("Failed to generate stub at compute service "+id);
    } catch (AlreadyBoundException e) {
      System.err.println("Failed to bind stub to registry at compute service "+id);
    }
  }

  public int getId() {
    return this.id;
  }
}

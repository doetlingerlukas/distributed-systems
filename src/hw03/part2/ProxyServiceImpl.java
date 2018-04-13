package hw03.part2;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Lukas Dötlinger.
 */
public class ProxyServiceImpl implements ProxyService {

  @Override
  public void accessService(String access, String inputString, int id) {
    String[] sorters = {"sortService1", "sortService2"};
    String[] computers = {"computeService1", "computeService2"};
    try {
      Registry registry = LocateRegistry.getRegistry(null);

      if (access.equals("-sort-")) {
        String service = sorters[ThreadLocalRandom.current().nextInt(0, 2)];
        SortService stub = (SortService) registry.lookup(service);

        System.out.println(stub.sort(inputString)+" sorted by "+service+" for client "+id);
      } else if (access.equals("-compute-")) {
        String service = computers[ThreadLocalRandom.current().nextInt(0, 2)];
        ComputeService stub = (ComputeService) registry.lookup(service);

        System.out.println(service+" starts computing for client "+id);
        stub.compute(5000L);
        System.out.println(service+" ended computing for client "+id);
      } else {
        System.out.println("No rights to access service!");
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (NotBoundException e) {
      e.printStackTrace();
    }
  }
}

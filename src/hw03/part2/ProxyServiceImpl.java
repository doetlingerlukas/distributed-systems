package hw03.part2;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Lukas Dötlinger.
 */
public class ProxyServiceImpl implements ProxyService {

  @Override
  public String accessService(String access, String inputString, int id) {
    List<String> sorters = new ArrayList<>();
    List<String> computers = new ArrayList<>();
    try {
      Registry registry = LocateRegistry.getRegistry(null);
      Arrays.stream(registry.list())
        .forEach(service -> {
          if (service.startsWith("sort")) {sorters.add(service);}
          else if (service.startsWith("compute")) {computers.add(service);}
        });

      if (access.equals("-sort-")) {
        String service = sorters.get(ThreadLocalRandom.current().nextInt(0, 2));
        SortService stub = (SortService) registry.lookup(service);

        return stub.sort(inputString)+" sorted by "+service+" for client "+id;
      } else if (access.equals("-compute-")) {
        String service = computers.get(ThreadLocalRandom.current().nextInt(0, 2));
        ComputeService stub = (ComputeService) registry.lookup(service);

        System.out.println(service+" starts computing for client "+id);
        stub.compute(5000L);
        return service+" ended computing for client "+id;
      } else {
        return "No rights to access service!";
      }
    } catch (RemoteException e) {
      e.printStackTrace();
      return "No rights to access service!";
    } catch (NotBoundException e) {
      e.printStackTrace();
      return "No rights to access service!";
    }
  }
}

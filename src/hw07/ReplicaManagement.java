package hw07;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lukas Dötlinger.
 */
public class ReplicaManagement {

  private final static String matNumber = "01518316";
  private final static double replicaCost = 30;

  private static List<Integer> getInitialLatency(int amount) {
    List<Integer> matDigits = Arrays.stream(matNumber.split(""))
      .map(n -> n.equals("0") ? 10 : Integer.parseInt(n))
      .collect(Collectors.toList());
    return IntStream.range(0, amount)
      .mapToObj(i -> matDigits.size() > i ? matDigits.get(i) : ThreadLocalRandom.current().nextInt(1, 10))
      .collect(Collectors.toList());
  }

  private static List<Connection> setupConnections(List<String> centers) {
    List<Connection> connections = new ArrayList<>();
    centers.stream()
      .forEach(center -> {
        centers.stream()
          .filter(c -> !c.equals(center))
          .map(c -> new Connection(0, center, c))
          .forEach(connection -> {
            if (!connection.containedIn(connections)) connections.add(connection);
          });
      });
    return  connections;
  }

  private static Map<String, Integer> getRequestsForDataCenter(String from, List<String> to) {
    int count = 5;
    List<Integer> chosen = new ArrayList<>();
    return to.stream()
      .filter(t -> !t.equals(from))
      .map(t -> {
        int i = ThreadLocalRandom.current().nextInt(0, count+1-chosen.stream()
          .mapToInt(n -> n)
          .sum());
        chosen.add(i);
        return new HashMap.SimpleEntry<>(t, i);
      })
      .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
  }

  private static Map<String, Map<String, Integer>> getAllRequests(List<String> dataCenters) {
    return dataCenters.stream()
      .map(d -> new HashMap.SimpleEntry<>(d, getRequestsForDataCenter(d, dataCenters).entrySet().stream()
        .filter(e -> e.getValue() != 0)
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()))))
      .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
  }

  private static Map<String, Integer> getTotalLatencies(Map<String, Map<String, Integer>> requests,
                                                                   List<Connection> connections) {
    return requests.entrySet().stream()
      .map(e1 -> new HashMap.SimpleEntry<>(e1.getKey(), e1.getValue().entrySet().stream()
        .mapToInt(e2 -> (Connection.findFromList(connections, e1.getKey(), e2.getKey()).getLatency()+1) * e2.getValue())
        .sum()))
      .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
  }

  private static Map<String, Map<String, Integer>> getLatenciesPerConnection(Map<String, Map<String, Integer>> requests,
                                                        List<Connection> connections) {
    return requests.entrySet().stream()
      .map(e1 -> new HashMap.SimpleEntry<>(e1.getKey(), e1.getValue().entrySet().stream()
        .map(e2 -> new HashMap.SimpleEntry<>(e2.getKey(), e2.getValue() *
          (Connection.findFromList(connections, e1.getKey(), e2.getKey()).getLatency()+1)))
        .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()))))
      .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
  }

  private static Map<String, Integer> getInitialStorageCost(Map<String, Map<String, Integer>> requests) {
    return requests.entrySet().stream()
      .map(e1 -> new HashMap.SimpleEntry<>(e1.getKey(), e1.getValue().entrySet().size() * 30))
      .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
  }

  private static double totalCost(double w1, double w2) {
    return w1 + w2;
  }

  public static void main(String[] args) {
    List<String> dataCenters = new ArrayList<>();
    dataCenters.add("A");
    dataCenters.add("B");
    dataCenters.add("C");
    dataCenters.add("D");

    // calculate connections and latencies
    List<Connection> connections = setupConnections(dataCenters);
    List<Integer> latencies = getInitialLatency(connections.size());

    IntStream.range(0, connections.size())
      .forEach(i -> connections.get(i).setLatency(latencies.get(i)));

    connections.stream()
      .forEach(c -> System.out.println(c.getFrom()+" and "+c.getTo()+" with "+c.getLatency()));

    System.out.println("----- Initial costs -----");

    Map<String, Map<String, Integer>> requests = getAllRequests(dataCenters);
    Map<String, Integer> totalLatencies = getTotalLatencies(requests, connections);
    Map<String, Integer> totalInitialStorage = getInitialStorageCost(requests);

    getLatenciesPerConnection(requests, connections).entrySet().stream()
      .forEach(e1 -> e1.getValue().entrySet().stream()
        .forEach(e2 -> System.out.println(e1.getKey()+" to "+e2.getKey()+" latency: "+e2.getValue())));
    totalLatencies.entrySet().stream()
      .forEach(e -> System.out.println(e.getKey()+" latency cost: "+e.getValue()));
    System.out.println("Total latency cost: "+totalLatencies.values().stream()
      .mapToInt(i -> i)
      .sum());

    System.out.println("There is no initial storage cost!");

    System.out.println("----- Optimized costs -----");

  }
}

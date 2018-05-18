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
  private final static double w1 = 0.75;
  private final static double w2 = 0.25;
  private final static double replicaCost = 30*w2;

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
    centers.forEach(center -> {
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
      .collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue));
  }

  private static Map<String, Map<String, Integer>> getAllRequests(List<String> dataCenters) {
    return dataCenters.stream()
      .map(d -> new HashMap.SimpleEntry<>(d, getRequestsForDataCenter(d, dataCenters).entrySet().stream()
        .filter(e -> e.getValue() != 0)
        .collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue))))
      .collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue));
  }

  private static Map<String, Map<String, Double>> getLatenciesPerConnection(Map<String, Map<String, Integer>> requests,
                                                        List<Connection> connections, Map<String, String> replicas) {
    return requests.entrySet().stream()
      .map(e1 -> new HashMap.SimpleEntry<>(e1.getKey(), e1.getValue().entrySet().stream()
        .map(e2 -> {
          double latency = Connection.findFromList(connections, e1.getKey(), e2.getKey()).getLatency();
          if (containsReplica(replicas, e1.getKey(), e2.getKey())) {
            return new HashMap.SimpleEntry<>(e2.getKey(), w1 * (e2.getValue() + latency));
          }
          return new HashMap.SimpleEntry<>(e2.getKey(), w1 * (e2.getValue() * (latency+1)));
        })
        .collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue))))
      .collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue));
  }

  private static Map<String, Double> getTotalLatencies(Map<String, Map<String, Integer>> requests,
                                                       List<Connection> connections, Map<String, String> replicas) {
    return getLatenciesPerConnection(requests, connections, replicas).entrySet().stream()
      .map(e1 -> new HashMap.SimpleEntry<>(e1.getKey(), e1.getValue().values().stream()
        .mapToDouble(d -> d)
        .sum()))
      .collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue));
  }

  private static boolean containsReplica(Map<String, String> replicas, String key, String val) {
    return replicas.entrySet().stream()
      .anyMatch(e -> e.getKey().equals(key) && e.getValue().equals(val));
  }

  private static Map<String, String> calculateOptimalReplicas(Map<String, Map<String, Double>> latencies,
                                                              List<Connection> connections) {
    Map<String, String> replicas = new HashMap<>();
    latencies
      .forEach((k1, v1) -> v1
        .forEach((k2, v2) -> {
          double latency = Connection.findFromList(connections, k1, k2).getLatency();
          double requestAmount = v2 / (latency+1);
          if ((((requestAmount+latency)*w1)+replicaCost) < v2) {
            replicas.put(k1, k2);
          }
        }));
    return replicas;
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

    System.out.println("----- Initial latencies -----");

    connections.forEach(c -> System.out.println(c.getFrom()+" and "+c.getTo()+" with "+c.getLatency()*w1));

    System.out.println("----- Initial costs -----");

    Map<String, Map<String, Integer>> requests = getAllRequests(dataCenters);
    Map<String, Double> totalLatencies = getTotalLatencies(requests, connections, new HashMap<>());
    Map<String, Map<String, Double>> latenciesPerConnection = getLatenciesPerConnection(requests, connections, new HashMap<>());

    latenciesPerConnection
      .forEach((k1, v1) -> v1
        .forEach((k2, v2) -> System.out.println(k1+" to "+k2+" latency: "+v2)));
    totalLatencies
      .forEach((k, v) -> System.out.println(k+" latency cost: "+v));
    System.out.println("Total latency cost: "+totalLatencies.values().stream()
      .mapToDouble(d -> d)
      .sum());

    System.out.println("There is no initial storage cost!");

    System.out.println("----- Optimized costs -----");

    Map<String, String> optimalReplicas = calculateOptimalReplicas(latenciesPerConnection, connections);
    Map<String, Double> optimalTotalLatencies = getTotalLatencies(requests, connections, optimalReplicas);

    optimalReplicas
      .forEach((k, v) -> System.out.println(k+" created replica of "+v));

    getLatenciesPerConnection(requests, connections, optimalReplicas)
      .forEach((k1, v1) -> v1
        .forEach((k2, v2) -> System.out.println(k1+" to "+k2+" latency: "+v2)));
    optimalTotalLatencies
      .forEach((k, v) -> System.out.println(k+" latency cost: "+v));
    System.out.println("Total optimal latency cost: "+optimalTotalLatencies.values().stream()
      .mapToDouble(d -> d)
      .sum());
    System.out.println("Total optimal storage cost: "+optimalReplicas.size()*replicaCost);
  }
}

package hw07;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lukas Dötlinger.
 */
public class ReplicaManagement {

  public final static String matNumber = "01518316";
  public final static double replicaCost = 30;

  private static List<Integer> getInitialLatency(int amount) {
    List<Integer> matDigits = Arrays.stream(matNumber.split(""))
      .map(n -> n == "0" ? 10 : Integer.parseInt(n))
      .collect(Collectors.toList());
    return IntStream.rangeClosed(1, amount)
      .mapToObj(i -> matDigits.size() < i ? matDigits.get(i) : ThreadLocalRandom.current().nextInt(1, 10))
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
      .filter(t -> t != from)
      .map(t -> {
        int i = ThreadLocalRandom.current().nextInt(0, count+1-chosen.stream()
          .mapToInt(n -> n)
          .sum());
        chosen.add(i);
        return new HashMap.SimpleEntry<>(t, i);
      })
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

    System.out.println("------------------------------------");

    dataCenters.stream()
      .forEach(d -> getRequestsForDataCenter(d, dataCenters).entrySet().stream()
        .forEach(e -> System.out.println(d+": "+e.getValue()+" requests to "+e.getKey())));
  }
}

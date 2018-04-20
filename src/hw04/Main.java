package hw04;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lukas Dötlinger.
 */
public class Main {

  private final static int networkSize = 8;

  public static TableEntry getRandomInitNode(List<TableEntry> nodes, int port) {
    return nodes.stream()
      .filter(n -> n.getPort() != port)
      .collect(Collectors.toList())
      .get(ThreadLocalRandom.current().nextInt(0, nodes.size()-1));
  }

  public static void main(String[] args) {
    List<TableEntry> nodes = IntStream.rangeClosed(1, networkSize)
      .mapToObj(i -> new TableEntry("localhost", 8000+i))
      .collect(Collectors.toList());

    IntStream.rangeClosed(1,networkSize)
      .mapToObj(i -> new Node(Integer.toString(i), 8000+i, getRandomInitNode(nodes, 8000+i)))
      .forEach(n -> new Thread(n).start());
  }
}

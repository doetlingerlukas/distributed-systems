package hw05.practical;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
public class Main {

  private final static int chordSize = 5;

  public static void main(String[] args) {
    // initialize chord
    List<Node> chord = Utils.getNodePositions("1518316").stream()
      .map(i -> new Node(i, "localhost:800"+i, "node"+i))
      .collect(Collectors.toList())
      .subList(0, chordSize);

    // initialize finger-tables for nodes in chord
    chord.stream()
      .forEach(node -> node.setFingerTableAndSuccessor(chord.stream()
        .sorted(Comparator.comparing(Node::getId))
        .collect(Collectors.toList())));

    chord.stream()
      .forEach(node -> Utils.printTable(node.getFingerTable(), node));
  }
}

package hw05.practical;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lukas Dötlinger.
 */
public class Node {

  private int id;
  private String address;
  private String name;

  private List<Node> fingerTable;
  private Node predecessor;
  private Node successor;

  public Node(int id, String address, String name) {
    this.id = id;
    this.address = address;
    this.name = name;
  }

  public void setFingerTableAndSuccessor(List<Node> table) {
    this.fingerTable = table.stream()
      .filter(n -> !(n.getId() == this.id))
      .collect(Collectors.toList());
    int tableIndex = table.indexOf(table.stream()
      .filter(n -> n.getId() == this.id)
      .findFirst()
      .get());
    this.successor = tableIndex == table.size()-1 ?
      table.get(0) : table.get(tableIndex+1);
    this.predecessor = tableIndex == 0 ?
      table.get(table.size()-1) : table.get(tableIndex-1);
  }

  public Node findSuccessor(List<Node> table, int toGet) {
    return table.stream()
      .filter(n -> n.getId() >= toGet)
      .collect(Collectors.toList())
      .size() != 0 ?
        table.stream()
          .filter(n -> n.getId() >= toGet)
          .min(Comparator.comparing(Node::getId))
          .get() : table.stream()
            .min(Comparator.comparing(Node::getId))
            .get();
  }

  public List<Node> findFingers(List<Node> table) {
    return IntStream.rangeClosed(2, Main.fingerTableSize)
      .mapToObj(i -> {
        int toGet = (int) (this.id+Math.pow(2, i-1)) % Main.chordCapacity;
        return findSuccessor(table, toGet);
      })
      .collect(Collectors.toList());
  }

  public void update(List<Node> table) {
    setFingerTableAndSuccessor(table);
    this.fingerTable.clear();
    this.fingerTable = findFingers(table);
    // add successor and predecessor to finger table
    Collections.reverse(fingerTable);
    fingerTable.add(successor);
    Collections.reverse(fingerTable);
    fingerTable.add(predecessor);
    this.fingerTable = this.fingerTable.stream()
      .collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
        new TreeSet<>(Comparator.comparingInt(Node::getId))), ArrayList::new));
  }

  public int getId() {
    return id;
  }

  public String getAddress() {
    return address;
  }

  public String getName() {
    return name;
  }

  public Node getPredecessor() {
    return predecessor;
  }

  public Node getSuccessor() {
    return successor;
  }

  public void setPredecessor(Node predecessor) {
    this.predecessor = predecessor;
  }

  public void setSuccessor(Node successor) {
    this.successor = successor;
  }

  public List<Node> getFingerTable() {
    return fingerTable;
  }

  public void setFingerTable(List<Node> fingerTable) {
    this.fingerTable = fingerTable;
  }
}

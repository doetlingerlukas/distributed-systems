package hw05.practical;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

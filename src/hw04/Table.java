package hw04;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
public class Table {

  private String owner;
  private List<TableEntry> list = new ArrayList<>();

  public Table(String owner) {
    this.owner = owner;
  }

  public boolean containsEntry(List<TableEntry> list, TableEntry entry) {
    return list.stream()
      .anyMatch(e -> e.getIp().equals(entry.getIp()) && (e.getPort() == entry.getPort()));
  }

  public List<TableEntry> getList() {
    synchronized (this.list) {
      return this.list;
    }
  }

  public String getListAsString() {
    String toReturn = "[";
    synchronized (this.list) {
      toReturn += this.list.stream()
        .mapToInt(e -> e.getPort())
        .sorted()
        .mapToObj(p -> Integer.toString(p))
        .map(Object::toString)
        .collect(Collectors.joining(", "));
    }
    return toReturn+"]";
  }

  public boolean isEmpty() {
    synchronized (this.list) {
      return this.list.isEmpty();
    }
  }

  public void mergeList(List<TableEntry> toMerge) {
    synchronized (this.list) {
      toMerge.stream()
        .filter(e -> !containsEntry(this.list, e))
        .forEach(e -> this.list.add(e));
    }
  }

  public void removeEntry(TableEntry entry) {
    synchronized (this.list) {
      this.list.remove(entry);
    }
  }

  public void addEntry(TableEntry entry) {
    synchronized (this.list) {
      this.list.add(entry);
    }
  }

  public int size() {
    synchronized (this.list) {
      return this.list.size();
    }
  }
}

package hw04.part1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
public class Table implements Serializable {

  private int maxTableSize;
  private String owner;
  private List<TableEntry> list = new ArrayList<>();
  private List<TableEntry> removed = new ArrayList<>();
  private ReentrantLock lock = new ReentrantLock();

  public Table(String owner, int maxTableSize) {
    this.owner = owner;
    this.maxTableSize = maxTableSize;
  }

  public static boolean containsEntry(List<TableEntry> list, TableEntry entry) {
    return list.stream()
      .anyMatch(e -> e.getIp().equals(entry.getIp()) && (e.getPort() == entry.getPort()));
  }

  public List<TableEntry> getList() {
    lock.lock();
    try {
      return this.list;
    } finally {
      lock.unlock();
    }
  }

  public String getListAsString() {
    String toReturn = "[";
    synchronized (this) {
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
    synchronized (this) {
      return this.list.isEmpty();
    }
  }

  public void mergeList(List<TableEntry> toMerge) {
    lock.lock();
    try {
      toMerge.stream()
        .filter(e -> !containsEntry(this.list, e) && !containsEntry(this.removed, e))
        .forEach(e -> this.list.add(e));
      reduceTable();
    } finally {
      lock.unlock();
    }
  }

  public void removeEntry(TableEntry entry) {
    lock.lock();
    try {
      this.list.remove(entry);
      this.removed.add(entry);
    } finally {
      lock.unlock();
    }
  }

  public void addEntry(TableEntry entry) {
    lock.lock();
    try {
      if (!containsEntry(this.list, entry) && !containsEntry(this.removed, entry)) {
        this.list.add(entry);
        reduceTable();
      }
    } finally {
      lock.unlock();
    }
  }

  public void reduceTable() {
    lock.lock();
    try {
      if (list.size() > maxTableSize) {
        Collections.shuffle(this.list);
        this.list = new ArrayList<TableEntry>(this.list.subList(0, maxTableSize));
      }
    } finally {
      lock.unlock();
    }
  }

  public int size() {
    synchronized (this) {
      return this.list.size();
    }
  }
}

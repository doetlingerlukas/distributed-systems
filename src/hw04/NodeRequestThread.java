package hw04;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Lukas Dötlinger.
 */
public class NodeRequestThread implements Runnable {

  private String node;
  private Table table;
  private TableEntry me;

  public NodeRequestThread(String node, Table table, TableEntry me) {
    this.node = node;
    this.table = table;
    this.me = me;
  }

  public TableEntry getRandomNode(List<TableEntry> list) {
    return list.get(ThreadLocalRandom.current().nextInt(0, list.size()));
  }

  @Override
  public void run() {
    while (true) {
      if (!table.isEmpty()) {
        TableEntry randomEntry = getRandomNode(table.getList());
        try {
          Thread.sleep(5000);
          Socket socket = new Socket(randomEntry.getIp(), randomEntry.getPort());
          ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
          output.writeObject(me);

          ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
          List<TableEntry> newList = new ArrayList<>();
          try {
            newList = (List<TableEntry>) input.readObject();
          } catch (EOFException e) {}
          this.table.mergeList(newList);
          socket.close();
          System.out.println("Updated table for " + node + " to: "+table.getListAsString());
        } catch (SocketException e) {
          table.removeEntry(randomEntry);
          System.err.println("Updated table for " + node + ", removed: "+randomEntry.getPort());
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}

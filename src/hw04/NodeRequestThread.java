package hw04;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Lukas Dötlinger.
 */
public class NodeRequestThread implements Runnable {

  private String node;
  private Table table;

  public NodeRequestThread(String node, Table table) {
    this.node = node;
    this.table = table;
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
          Socket socket = new Socket(randomEntry.getIp(), randomEntry.getPort());
          ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
          List<TableEntry> newList = new ArrayList<>();
          try {
            newList = (List<TableEntry>) input.readObject();
          } catch (EOFException e) {}
          this.table.mergeList(newList);
          socket.close();
          System.out.println("Updated table for " + node + " to: "+table.getListAsString());
          Thread.sleep(5000);
        } catch (SocketException e) {
          table.removeEntry(randomEntry);
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

package hw04;

import java.io.*;
import java.net.Socket;
import java.util.ConcurrentModificationException;

/**
 * Created by Lukas Dötlinger.
 */
public class NodeRequestHandler implements Runnable {

  private String node;
  private Table table;
  private Socket client;
  private TableEntry me;

  public NodeRequestHandler(String node, Table table, Socket client, TableEntry me) {
    this.node = node;
    this.table = table;
    this.client = client;
    this.me = me;
  }

  @Override
  public void run() {
    try {
      ObjectInputStream input = new ObjectInputStream(client.getInputStream());
      TableEntry requestingNode = me;
      try {
        requestingNode = (TableEntry) input.readObject();
      } catch (EOFException e) {}
      table.addEntry(requestingNode);

      ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
      output.writeObject(table.getList());
      client.close();
    } catch (ConcurrentModificationException e) {
      try {
        client.close();
      } catch (IOException e1) {
        System.err.println(node+" failed to reply!");
        e1.printStackTrace();
      }
    } catch (Exception e) {
      System.err.println(node+" failed to reply!");
      e.printStackTrace();
    }
  }
}

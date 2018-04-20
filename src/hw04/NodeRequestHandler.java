package hw04;

import java.io.*;
import java.net.Socket;

/**
 * Created by Lukas Dötlinger.
 */
public class NodeRequestHandler implements Runnable {

  private String node;
  private Table table;
  private Socket client;

  public NodeRequestHandler(String node, Table table, Socket client) {
    this.node = node;
    this.table = table;
    this.client = client;
  }

  @Override
  public void run() {
    try {
      ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
      output.writeObject(table.getList());
      client.close();
    } catch (IOException e) {
      System.err.println(node+" failed to reply!");
      e.printStackTrace();
    }
  }
}

package hw04;

import hw04.part2.Gossip;
import hw04.part2.NodeGossipHandler;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Lukas Dötlinger.
 */
public class Node implements Runnable {

  private final static String ip = "localhost";
  private String name;
  private int port;
  private ServerSocket serverSocket;
  private Table table;
  private String mode;

  public Node(String name, int port, List<TableEntry> init, int tableSize, String mode) {
    this.name = name;
    this.port = port;
    try {
      this.serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      System.err.println("Failed to start Node on port "+port+"!");
    }
    this.table = new Table(name, tableSize);
    this.table.mergeList(init);
    this.mode = mode;
  }

  @Override
  public void run() {
    System.out.println("Started Node "+name+" on port "+port+"!");
    ExecutorService es = Executors.newFixedThreadPool(4);

    if (mode.equals("normal")) {
      // start thread to update table
      es.submit(new NodeRequestThread(name, table, new TableEntry(ip, port, name)));

      try {
        while (true) {
          Socket socket = serverSocket.accept();
          System.out.println("Node " + name + " accepted new request!");

          es.submit(new NodeRequestHandler(name, table, socket, serverSocket, new TableEntry(ip, port, name)));
        }
      } catch (SocketException se) {
        es.shutdown();
      } catch (Exception e) {
        System.err.println("Node " + name + " failed!");
      } finally {
        System.err.println("Node " + name + " left network!");
      }
    } else if (mode.equals("gossip")) {
      try {
        Gossip latestGossip = new Gossip(0,"null", new ArrayList<>());
        Gossip newGossip = latestGossip;

        while (true) {
          Socket socket = serverSocket.accept();

          try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            newGossip = (Gossip) input.readObject();
          } catch (EOFException e) {}

          es.submit(new NodeGossipHandler(name, table, serverSocket, new TableEntry(ip, port, name),
            newGossip, latestGossip));
          latestGossip = newGossip;
          socket.close();
        }
      } catch (SocketException e) {
        es.shutdown();
      } catch (Exception e) {
        System.err.println("Node " + name + " failed!");
      }
    }
  }
}

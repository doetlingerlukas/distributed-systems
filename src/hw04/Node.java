package hw04;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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

  public Node(String name, int port, TableEntry init) {
    this.name = name;
    this.port = port;
    try {
      this.serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      System.err.println("Failed to start Node on port "+port+"!");
    }
    this.table = new Table(name);
    this.table.addEntry(init);
  }

  @Override
  public void run() {
    System.out.println("Started Node "+name+" on port "+port+"!");

    // start thread to update table
    new Thread(new NodeRequestThread(name, table, new TableEntry(ip, port))).start();

    // request handling
    ExecutorService es = Executors.newFixedThreadPool(4);
    try {
      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("Node "+name+" accepted new request!");

        es.submit(new NodeRequestHandler(name, table, socket, new TableEntry(ip, port)));
      }
    } catch (Exception e) {
      System.err.println("Node "+name+" failed!");
    }
  }
}

package hw02.part2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Created by Lukas Dötlinger.
 */
public class ProxyServer implements Runnable{

  private int port;
  private String server;
  private int serverId;
  private Socket clientSocket;
  private ServerSocket proxySocket;

  public ProxyServer(int port, int serverId, Socket clientSocket, ServerSocket proxySocket) {
    this.port = port;
    this.serverId = serverId;
    this.server = "localhost";
    this.clientSocket = clientSocket;
    this.proxySocket = proxySocket;
  }

  public void reply() {
    try {
      DataInputStream clientInput = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());
      String toSort = clientInput.readUTF();

      Socket serverSocket = new Socket(server, port);
      DataInputStream serverInput = new DataInputStream(serverSocket.getInputStream());
      DataOutputStream serverOutput = new DataOutputStream(serverSocket.getOutputStream());

      serverOutput.writeUTF(toSort);
      String sorted = serverInput.readUTF();

      clientOutput.writeUTF(sorted+" with end-point server "+serverId);
      serverSocket.close();
      clientSocket.close();
    } catch (IOException e) {
      System.err.println("Proxy failed to reply!");
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    List<Integer> serverPorts = new ArrayList<>();
    ExecutorService es = Executors.newFixedThreadPool(4);
    try {
      //Start the end-point servers.
      IntStream.rangeClosed(1,4)
        .mapToObj(i -> new EndPointSortingServer(8000+i, i))
        .forEach(server -> {
          new Thread(server).start();
          serverPorts.add(8000+server.getId());
        });

      ServerSocket proxySocket = new ServerSocket(8888);

      while (true) {
        Socket clientSocket = proxySocket.accept();
        System.out.println("Proxy accepted new Client!");

        int port = serverPorts.get(ThreadLocalRandom.current().nextInt(0, serverPorts.size()));
        es.submit(new ProxyServer(port, serverPorts.indexOf(port)+1, clientSocket, proxySocket));
      }
    } catch (IOException e) {
      System.err.println("IOException occurred!");
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    reply();
  }
}

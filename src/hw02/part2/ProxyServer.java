package hw02.part2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
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

  private ServerData serverData;
  private List<ServerData> servers;
  private Socket clientSocket;
  private ServerSocket proxySocket;

  public ProxyServer(List<ServerData> servers, Socket clientSocket, ServerSocket proxySocket) {
    this.servers = servers;
    this.clientSocket = clientSocket;
    this.proxySocket = proxySocket;
  }

  public void reply() {
    try {
      this.serverData = servers.get(ThreadLocalRandom.current().nextInt(0, servers.size()));

      Socket serverSocket = new Socket(serverData.getName(), serverData.getPort());
      DataInputStream clientInput = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());
      DataInputStream serverInput = new DataInputStream(serverSocket.getInputStream());
      DataOutputStream serverOutput = new DataOutputStream(serverSocket.getOutputStream());

      String toSort = clientInput.readUTF();
      serverOutput.writeUTF(toSort);
      String sorted = serverInput.readUTF();

      clientOutput.writeUTF(sorted + " with end-point server " + serverData.getId());
      serverSocket.close();
      clientSocket.close();

    } catch (ConnectException e) {
      //Connection to server was not possible. Therefore redirect to other server if possible.
      System.out.println("Server "+serverData.getId()+" not available! Redirecting..");
      servers.remove(serverData);
      if (servers.isEmpty()) {
        System.err.println("No servers are available!");
        return;
      }
      reply();

    } catch (IOException e) {
      System.err.println("Proxy failed to reply!");
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    List<ServerData> servers = new ArrayList<>();
    ExecutorService es = Executors.newFixedThreadPool(4);
    try {
      //Start the end-point servers.
      IntStream.rangeClosed(1,4)
        .mapToObj(i -> new EndPointSortingServer(8000+i, i))
        .forEach(server -> {
          new Thread(server).start();
          servers.add(new ServerData(server.getId(), "localhost", server.getPort()));
        });

      ServerSocket proxySocket = new ServerSocket(8888);

      while (true) {
        Socket clientSocket = proxySocket.accept();
        System.out.println("Proxy accepted new Client!");

        es.submit(new ProxyServer(servers, clientSocket, proxySocket));
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

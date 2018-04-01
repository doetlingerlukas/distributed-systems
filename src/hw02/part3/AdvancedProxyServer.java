package hw02.part3;

import hw02.part2.ServerData;

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
public class AdvancedProxyServer implements Runnable{

  private ServerData serverData;
  private List<ServerData> servers;
  private Socket clientSocket;
  private String toSort;

  public AdvancedProxyServer(List<ServerData> servers, Socket clientSocket, String toSort) {
    this.servers = servers;
    this.clientSocket = clientSocket;
    this.toSort = toSort;
  }

  /**
   * Method to send a request to a server and to reply it to a client.
   */
  public void reply() {
    try {
      this.serverData = servers.get(ThreadLocalRandom.current().nextInt(0, servers.size()));

      Socket serverSocket = new Socket(serverData.getName(), serverData.getPort());
      DataInputStream clientInput = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());
      DataInputStream serverInput = new DataInputStream(serverSocket.getInputStream());
      DataOutputStream serverOutput = new DataOutputStream(serverSocket.getOutputStream());

      serverOutput.writeUTF(this.toSort);
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

  @Override
  public void run() {
    reply();
  }

  public static void main(String[] args) {
    List<ServerData> servers = new ArrayList<>();
    ExecutorService es = Executors.newFixedThreadPool(4);
    try {
      ServerSocket proxyServerSocket = new ServerSocket(8888);

      //Start the end-point servers.
      IntStream.rangeClosed(1,4)
        .mapToObj(i -> new RegisteringEndPointServer(i, 8888))
        .forEach(server -> {
          new Thread(server).start();
        });

      while (true) {
        Socket clientSocket = proxyServerSocket.accept();

        DataInputStream input = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
        String inputString = input.readUTF();

        //Checks whether a server wants to register or a client wants to send a request.
        if (inputString.equals("-registration-")) {
          output.writeUTF("-OK-");
          int port = input.readInt();
          servers.add(new ServerData(port-8000, "localhost", port));
          System.out.println("Proxy accepted new Server on port "+port+"!");
          clientSocket.close();
        } else {
          System.out.println("Proxy accepted new Client!");
          es.submit(new AdvancedProxyServer(servers, clientSocket, inputString));
        }
      }
    } catch (IOException e) {
      System.err.println("IOException occurred!");
      e.printStackTrace();
    }
  }
}


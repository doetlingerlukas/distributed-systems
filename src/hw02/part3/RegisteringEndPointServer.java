package hw02.part3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Lukas Dötlinger.
 */
public class RegisteringEndPointServer implements Runnable {

  private int id;
  private int port;
  private int proxyPort;
  private ServerSocket serverSocket;

  public RegisteringEndPointServer(int id, int proxyPort) {
    this.id = id;
    this.port = 8000 + id;
    this.proxyPort = proxyPort;
    try {
      this.serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      System.err.println("Failed to open EndPointSortingServer on port "+port+"!");
    }
  }

  /**
   * Method to sort a string.
   */
  public String sortString(String toSort) {
    return Stream.of(toSort.split(""))
      .sorted()
      .collect(Collectors.joining());
  }

  /**
   * Method to reply to the proxy.
   */
  public void reply(Socket clientSocket) {
    try {
      DataInputStream input = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
      String toSort = input.readUTF();

      if(toSort.equals("-shutdown-")) {
        serverSocket.close();
      } else {
        output.writeUTF(sortString(toSort));
      }
      clientSocket.close();
    } catch (IOException e) {
      System.err.println("EndPointServer "+id+" failed to reply!");
    }
  }

  @Override
  public void run() {
    //Register to the proxy.
    try {
      Socket proxySocket = new Socket("localhost", proxyPort);

      DataInputStream input = new DataInputStream(proxySocket.getInputStream());
      DataOutputStream output = new DataOutputStream(proxySocket.getOutputStream());
      output.writeUTF("-registration-");
      if (input.readUTF().equals("-OK-")) {
        output.writeInt(this.port);
      }
      proxySocket.close();
    } catch (IOException e) {
      System.out.println("Failed while registering to the proxy!");
    }
    System.out.println("Started EndPointServer "+id+"!");

    //Main server actions.
    ExecutorService es = Executors.newFixedThreadPool(4);
    try {
      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("EndPointServer "+id+" accepted new task!");

        es.submit(() -> {
          reply(socket);
        });
      }
    } catch (SocketException e) {
      System.out.println("EndPointServer "+id+" shutting down!");
      es.shutdown();
    } catch (IOException e) {
      System.err.println("IOException occurred at EndPointServer "+id+"!");
    } finally {
      System.out.println("EndPointServer "+id+" closed!");
    }
  }
}

package hw02.part2;

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
public class EndPointSortingServer implements Runnable {

  private int id;
  private int port;
  private ServerSocket serverSocket;

  public EndPointSortingServer(int port, int id) {
    this.id = id;
    this.port = port;
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

  public void reply(Socket clientSocket) {
    try {
      DataInputStream input = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
      String toSort = input.readUTF();

      if(toSort.equals("-shutdown-")) {
        serverSocket.close();
      }
      output.writeUTF(sortString(toSort));
      clientSocket.close();
    } catch (IOException e) {
      System.err.println("EndPointServer "+id+" failed to reply!");
    }
  }

  @Override
  public void run() {
    System.out.println("Started EndPointServer "+id+"!");
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

  public int getId() {
    return id;
  }

  public int getPort() {
    return port;
  }
}

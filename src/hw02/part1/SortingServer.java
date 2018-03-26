package hw02.part1;

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
public class SortingServer implements Runnable {

  private Socket socket;
  private ServerSocket serverSocket;

  public SortingServer(Socket socket, ServerSocket serverSocket) {
    this.socket = socket;
    this.serverSocket = serverSocket;
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
   * Method to handle a clients request and reply to it.
   */
  public void reply() {
    try {
      DataInputStream input = new DataInputStream(socket.getInputStream());
      DataOutputStream output = new DataOutputStream(socket.getOutputStream());

      String toSort = input.readUTF();

      if(toSort.equals("-shutdown-")) {
        serverSocket.close();
      }
      output.writeUTF(sortString(toSort));
      socket.close();

    } catch (IOException e) {
      System.err.println("Failed to reply!");
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    ExecutorService es = Executors.newFixedThreadPool(5);
    try {
      ServerSocket serverSocket = new ServerSocket(8888);

      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("Accepted new Client!");
        //Server starts a new request handler.
        es.submit(new SortingServer(socket, serverSocket));
      }
    } catch (SocketException se) {
      System.out.println("Shutting down server!");
      es.shutdown();
    } catch (IOException ioe) {
      System.err.println("IOException occurred!");
      ioe.printStackTrace();
    } finally {
      System.out.println("Shutdown successful!");
    }
  }

  @Override
  public void run() {
    reply();
  }
}

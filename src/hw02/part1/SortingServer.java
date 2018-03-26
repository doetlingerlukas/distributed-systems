package hw02.part1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Lukas Dötlinger.
 */
public class SortingServer implements Runnable {

  private Socket socket;

  public SortingServer(Socket socket) {
    this.socket = socket;
  }

  /**
   * Method to sort a string.
   */
  public String sortString(String toSort) {
    return Stream.of(toSort.split(""))
            .sorted()
            .collect(Collectors.joining());
  }

  public void reply() {
    try {
      DataInputStream input = new DataInputStream(socket.getInputStream());
      DataOutputStream output = new DataOutputStream(socket.getOutputStream());

      output.writeUTF(sortString(input.readUTF()));
      socket.close();

    } catch (IOException e) {
      System.err.println("Failed to reply!");
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(8888);

    while (true) {
      Socket socket = serverSocket.accept();
      System.out.println("Accepted new Client!");
      //Server starts a new request handler.
      new Thread(new SortingServer(socket)).start();
    }
  }

  @Override
  public void run() {
    reply();
  }
}

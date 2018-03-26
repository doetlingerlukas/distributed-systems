package hw02.part1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Lukas Dötlinger.
 */
public class SimpleClient implements Runnable {

  private int port;
  private String server;
  private String toSort;

  public SimpleClient(int port, String server, String toSort) {
    this.port = port;
    this.server = server;
    this.toSort = toSort;
  }

  /**
   * Method to send a request to the server.
   */
  public void request() {
    try {
      Socket socket = new Socket(server, port);
      System.out.println("Connected to server on port "+port+".");

      DataInputStream input = new DataInputStream(socket.getInputStream());
      DataOutputStream output = new DataOutputStream(socket.getOutputStream());

      output.writeUTF(toSort);
      System.out.println("Server changed "+toSort+" to "+input.readUTF());
      socket.close();
    } catch (IOException e) {
      System.err.println("Failed to request!");
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    request();
  }
}

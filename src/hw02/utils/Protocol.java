package hw02.utils;

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
public class Protocol {

  public final static int serverPort = 8888;
  public final static String serverName = "localhost";


  /**
   * Method to sort a string.
   */
  public static String sortString(String toSort) {
    return Stream.of(toSort.split(""))
      .sorted()
      .collect(Collectors.joining());
  }

  /**
   * Method to send a request to the server.
   */
  public static void request(String toSort) {
    try {
      Socket socket = new Socket(serverName, serverPort);
      System.out.println("Connected to server on port "+serverPort+".");

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

  /**
   * Method to handle a clients request and reply to it.
   */
  public static void reply(Socket clientSocket, ServerSocket serverSocket) {
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
      System.err.println("Failed to reply!");
      e.printStackTrace();
    }
  }

  public static int getServerPort() {
    return serverPort;
  }

  public static String getServerName() {
    return serverName;
  }
}

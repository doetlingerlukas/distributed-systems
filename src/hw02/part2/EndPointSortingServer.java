package hw02.part2;

import hw02.utils.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

  @Override
  public void run() {
    System.out.println("Started EndPointServer "+id+"!");
    ExecutorService es = Executors.newFixedThreadPool(4);
    try {
      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("EndPointServer "+id+" accepted new task!");

        es.submit(() -> {
          Protocol.reply(socket, serverSocket);
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

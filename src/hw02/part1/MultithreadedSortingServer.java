package hw02.part1;

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
public class MultithreadedSortingServer implements Runnable {

  private Socket clientSocket;
  private ServerSocket serverSocket;

  public MultithreadedSortingServer(Socket clientSocket, ServerSocket serverSocket) {
    this.clientSocket = clientSocket;
    this.serverSocket = serverSocket;
  }

  @Override
  public void run() {
    Protocol.reply(clientSocket, serverSocket);
  }

  public static void main(String[] args) {
    ExecutorService es = Executors.newFixedThreadPool(5);
    try {
      ServerSocket serverSocket = new ServerSocket(Protocol.getServerPort());

      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("Accepted new Client!");
        //Server starts a new request handler.
        es.submit(new MultithreadedSortingServer(socket, serverSocket));
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
}

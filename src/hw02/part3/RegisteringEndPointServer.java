package hw02.part3;

import hw02.utils.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Lukas Dötlinger.
 */
public class RegisteringEndPointServer implements Runnable {

  private int id;
  private int port;
  private ServerSocket serverSocket;

  public RegisteringEndPointServer(int id) {
    this.id = id;
    this.port = 8000 + id;
    try {
      this.serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      System.err.println("Failed to open EndPointSortingServer on port "+port+"!");
    }
  }

  @Override
  public void run() {
    //Register to the proxy.
    try {
      Socket proxySocket = new Socket(Protocol.getServerName(), Protocol.getServerPort());

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
}

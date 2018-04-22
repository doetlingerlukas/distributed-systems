package hw04.part3;

import hw04.part1.TableEntry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Lukas Dötlinger.
 */
public class NameServer implements Runnable {

  private List<TableEntry> nodes;

  public NameServer(List<TableEntry> list) {
    this.nodes = list;
  }

  public void run() {
    ExecutorService es = Executors.newFixedThreadPool(4);
    try {
      ServerSocket serverSocket = new ServerSocket(NameResolutionProtocol.nameServerIp);
      System.out.println("Started name-server.");
      while (true) {
        Socket client = serverSocket.accept();

        es.submit(new Thread(() -> {
          NameResolutionProtocol.resolveName(client, nodes);
        }));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

package hw04.part1;

import hw04.part3.NameServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lukas Dötlinger.
 */
public class Main {

  public final static int networkSize = 15;
  public final static boolean withNameServer = false;

  public static TableEntry getRandomInitNode(List<TableEntry> nodes, int port) {
    return nodes.stream()
      .filter(n -> n.getPort() != port)
      .collect(Collectors.toList())
      .get(ThreadLocalRandom.current().nextInt(0, nodes.size()-1));
  }

  public static List<TableEntry> getInitAsList(List<TableEntry> nodes, int port, int amount) {
    return IntStream.rangeClosed(1, amount)
      .mapToObj(i -> getRandomInitNode(nodes, port))
      .collect(Collectors.toList());
  }

  public static void main(String[] args) {
    List<TableEntry> nodes = IntStream.rangeClosed(1, networkSize)
      .mapToObj(i -> new TableEntry("localhost", 8000+i, "node"+Integer.toString(i)))
      .collect(Collectors.toList());

    if (withNameServer) {
      new Thread(new NameServer(nodes)).start();
    }

    IntStream.rangeClosed(1, networkSize)
      .mapToObj(i -> new Node("node"+Integer.toString(i), 8000+i,
        getInitAsList(nodes, 8000+i, 1), networkSize/3, "normal"))
      .forEach(n -> new Thread(n).start());

    try {
      Thread.sleep(15000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    IntStream.rangeClosed(networkSize+1, networkSize+3)
      .mapToObj(i -> new Node("node"+Integer.toString(i), 8000+i,
        getInitAsList(nodes, 8000+i, 1), 4, "normal"))
      .forEach(n -> new Thread(n).start());

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    IntStream.rangeClosed(1, 3)
      .mapToObj(i -> new Thread(() -> {
        TableEntry toShutdown = getRandomInitNode(nodes, 8000);
        try {
          Socket socket = new Socket(toShutdown.getIp(), toShutdown.getPort());
          ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
          output.writeObject(new TableEntry("-shutdown-", 8888, "-shutdown-"));
          socket.close();
        } catch (Exception e) {
          // Node already shut down.
        }
      }))
      .forEach(t -> t.start());

  }
}

package hw04.part2;

import hw04.Main;
import hw04.Node;
import hw04.TableEntry;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lukas Dötlinger.
 */
public class Gossiping {

  private final static int networkSize = 60;

  public static List<TableEntry> getInitAsList(List<TableEntry> nodes, int port, int amount) {
    List<TableEntry> localCopy = nodes.stream()
      .filter(n -> n.getPort() != port)
      .collect(Collectors.toList());
    Collections.shuffle(localCopy);
    return new ArrayList<>(localCopy.subList(0, amount));
  }

  public static void main(String[] args) {

    System.out.println("Start gossiping in a Network with "+networkSize+" nodes!");

    List<TableEntry> nodes = IntStream.rangeClosed(1, networkSize)
      .mapToObj(i -> new TableEntry("localhost", 8000+i, "node"+Integer.toString(i)))
      .collect(Collectors.toList());

    IntStream.rangeClosed(1, networkSize)
      .mapToObj(i -> new Node("node"+Integer.toString(i), 8000+i,
        getInitAsList(nodes, 8000+i, (networkSize/3)), (networkSize/3), "gossip"))
      .forEach(n -> new Thread(n).start());

    new Thread(() -> {
      TableEntry gossipStartNode = Main.getRandomInitNode(nodes, 8000);
      List<TableEntry> startRecipients = new ArrayList<>();
      startRecipients.add(gossipStartNode);
      try {
        Socket socket = new Socket(gossipStartNode.getIp(), gossipStartNode.getPort());
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        output.writeObject(new Gossip(1,"I am gossip!", startRecipients));
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();

    try {
      Thread.sleep(2500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    int reachedNodes = 0;
    for (TableEntry n : nodes) {
      try {
        Socket socket = new Socket(n.getIp(), n.getPort());
        socket.close();
      } catch (SocketException e) {
        reachedNodes = reachedNodes+1;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    System.out.println(reachedNodes+"/"+networkSize+" nodes received the gossip!");

  }
}

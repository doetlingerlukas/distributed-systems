package hw04.part2;

import hw04.part1.Table;
import hw04.part1.TableEntry;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
public class NodeGossipHandler implements Runnable {

  private String node;
  private Table table;
  private ServerSocket serverSocket;
  private TableEntry me;
  private Gossip gossip;
  private Gossip oldGossip;

  public NodeGossipHandler(String node, Table table, ServerSocket serverSocket, TableEntry me, Gossip g, Gossip og) {
    this.node = node;
    this.table = table;
    this.serverSocket = serverSocket;
    this.me = me;
    this.gossip = g;
    this.oldGossip = og;
  }

  @Override
  public void run() {
    try {
      if (gossip.getMessage().equals(oldGossip.getMessage())) {
        serverSocket.close();
      } else {
        List<TableEntry> oldRecipients = gossip.getRecipients();
        String toDistribute = gossip.getMessage();
        int newIteration = gossip.getIteration()+1;
        System.out.println("Step "+(newIteration-1)+": Node "+node+" received gossip "+toDistribute);
        List<TableEntry> newRecipients = table.getList().stream()
          .filter(e -> !(Table.containsEntry(oldRecipients, e)))
          .collect(Collectors.toList());
        newRecipients.stream()
          .forEach(e -> distributeGossip(e, new Gossip(newIteration, toDistribute, newRecipients)));
        serverSocket.close();
      }
    } catch (Exception e) {
      System.err.println(node+" failed to reply!");
      e.printStackTrace();
    }
  }

  public void distributeGossip(TableEntry entry, Gossip gossip) {
    try {
      Socket socket = new Socket(entry.getIp(), entry.getPort());
      ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
      output.writeObject(gossip);
      socket.close();
    } catch (SocketException e) {
      // Node has already terminated, since it received gossip.
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

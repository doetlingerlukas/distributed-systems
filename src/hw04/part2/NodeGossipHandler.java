package hw04.part2;

import hw04.Table;
import hw04.TableEntry;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
public class NodeGossipHandler implements Runnable {

  private String node;
  private Table table;
  private Socket client;
  private ServerSocket serverSocket;
  private TableEntry me;
  private Gossip latestGossip;

  public NodeGossipHandler(String node, Table table, Socket client, ServerSocket serverSocket, TableEntry me) {
    this.node = node;
    this.table = table;
    this.client = client;
    this.serverSocket = serverSocket;
    this.me = me;
    this.latestGossip = new Gossip("null", new ArrayList<>());
  }

  @Override
  public void run() {
    try {
      ObjectInputStream input = new ObjectInputStream(client.getInputStream());
      Gossip newGossip = latestGossip;
      try {
        newGossip = (Gossip) input.readObject();
      } catch (EOFException e) {}
      if (newGossip.getMessage().equals(latestGossip.getMessage())) {
        client.close();
      } else {
        List<TableEntry> oldRecipients = newGossip.getRecipients();
        String toDistribute = newGossip.getMessage();
        System.out.println("Node "+node+" received gossip "+toDistribute);
        List<TableEntry> newRecipients = table.getList().stream()
          .filter(e -> !(Table.containsEntry(oldRecipients, e)))
          .collect(Collectors.toList());
        newRecipients.stream()
          .forEach(e -> distributeGossip(e, new Gossip(toDistribute, newRecipients)));
        client.close();
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
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

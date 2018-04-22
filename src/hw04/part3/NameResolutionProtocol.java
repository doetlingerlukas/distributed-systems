package hw04.part3;

import hw04.TableEntry;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Created by Lukas Dötlinger.
 */
public class NameResolutionProtocol {

  public final static int nameServerIp = 7000;

  public static void resolveName(Socket client, List<TableEntry> nodes) {
    ObjectOutputStream output = null;
    try {
      DataInputStream input = new DataInputStream(client.getInputStream());
      String name = input.readUTF();

      output = new ObjectOutputStream(client.getOutputStream());
      output.writeObject(nodes.stream()
        .filter(n -> n.getName().equals(name))
        .findFirst()
        .get()
      );
    } catch (Exception e) {
      try {
        output.writeObject(new TableEntry("-wrong-", 0, "-wrong-"));
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
  }

  public static TableEntry resolveName(String name) {
    TableEntry toReturn = null;
    try {
      Socket socket = new Socket("localhost", nameServerIp);
      DataOutputStream output = new DataOutputStream(socket.getOutputStream());
      output.writeUTF(name);
      try {
        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        toReturn = (TableEntry) input.readObject();
      } catch (EOFException e) {}
    } catch (Exception e) {
      e.printStackTrace();
    }
    return toReturn;
  }

  /*
  public static TableEntry registerClient(Socket client) {
    String ip = null;
    int port = 0;
    String name = null;
    try {
      DataInputStream input = new DataInputStream(client.getInputStream());
      DataOutputStream output = new DataOutputStream(client.getOutputStream());
      output.writeUTF("-ok-");
      ip = input.readUTF();
      output.writeUTF("-ok-");
      port = input.readInt();
      output.writeUTF("-ok-");
      name = input.readUTF();
      client.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Server registered "+name);
    return new TableEntry(ip, port, name);
  }

  public static void registerToServer(String ip, int port, String name) {
    try {
      Socket socket = new Socket("localhost", nameServerIp);
      DataInputStream input = new DataInputStream(socket.getInputStream());
      DataOutputStream output = new DataOutputStream(socket.getOutputStream());
      output.writeUTF("-register-");
      if (input.readUTF().equals("-ok-")) {
        output.writeUTF(ip);
      } else {
        throw new Exception();
      }
      if (input.readUTF().equals("-ok-")) {
        output.writeInt(port);
      } else {
        throw new Exception();
      }
      if (input.readUTF().equals("-ok-")) {
        output.writeUTF(name);
      } else {
        throw new Exception();
      }
      socket.close();
    } catch (Exception e) {
      System.err.println("Failed to register "+name);
    }
  } */
}

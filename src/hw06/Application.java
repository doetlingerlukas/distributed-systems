package hw06;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Lukas Dötlinger.
 */
public class Application implements Runnable {

  public static final String color_reset = "\u001B[0m";
  private String color;
  private final static String ip = "localhost";
  private int id;
  private ServerSocket applicationSocket;
  private int port;
  private int middlewarePort;
  private boolean automated;
  private List<Message> messages = new ArrayList<>();

  public Application(String color, int id, int port, boolean automated) {
    this.color = color;
    this.id = id;
    this.port = port;
    this.middlewarePort = port+1;
    this.automated = automated;
    try {
      this.applicationSocket = new ServerSocket(port);
    } catch (IOException e) {
      System.err.println(color+"Failed to start Application "+id+" on port "+port+"!"+color_reset);
    }
  }

  public Message getNewestMessage() {
    return messages.stream()
      .sorted(Comparator.comparing(Message::getTimestamp))
      .findFirst()
      .get();
  }

  public void sendMessage() {
    String msg;
    try {
      if (automated) {
        Thread.sleep(2000);
        msg = Integer.toString(id);
      } else {
        System.out.print(color+"Enter message to send: "+color_reset);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        msg = reader.readLine();
        reader.close();
      }

      Socket middleware = new Socket(ip, middlewarePort);
      ObjectOutputStream output = new ObjectOutputStream(middleware.getOutputStream());

      output.writeObject(new Message(msg, "-send-", id));
      middleware.close();
      System.out.println(color+"Successfully sent message."+color_reset);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    System.out.println(color+"Started Application "+id+" on port "+port+"!"+color_reset);
    ExecutorService es = Executors.newFixedThreadPool(4);

    if (id == 1) {
      es.submit(new Thread(() -> {
        sendMessage();
      }));
      System.out.println(color+"Application "+id+" sent message!"+color_reset);
    }

    try {
      while (true) {
        Socket socket = applicationSocket.accept();

        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        Message msg = new Message("-error-", "-receive-", id);
        try {
          msg = (Message) input.readObject();
        } catch (EOFException e) {}
        socket.close();
        Message retrieved = msg;

        if (!msg.getMessage().equals("-error-") && !messages.stream()
          .anyMatch(m -> m.getMessage().equals(retrieved.getMessage()))) {
          messages.add(retrieved);
          if (messages.size() > 1) {
            messages.stream()
              .sorted(Comparator.comparing(Message::getTimestamp))
              .forEach(m -> System.out.println(color + "Application " + id + " received new message: " + m.getMessage() +
                " with time "+m.getTimestamp()+"!" + color_reset));
          }
          if (retrieved.getSenderId() == id-1) {
            sendMessage();
            System.out.println(color+"Application "+id+" sent message!"+color_reset);
          }
        }
      }
    } catch (SocketException se) {
      es.shutdown();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(color+"Application "+id+" failed!"+color_reset);
    }
  }
}

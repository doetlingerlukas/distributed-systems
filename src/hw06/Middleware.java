package hw06;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Lukas Dötlinger.
 */
public class Middleware implements Runnable {

  public static final int maxMessages = 2;
  public static final String color_reset = "\u001B[0m";
  private String color;
  private final static String ip = "localhost";
  private int id;
  private ServerSocket middlewareSocket;
  private int port;
  private int applicationPort;
  private Map<Integer, Integer> otherMiddleware;
  private List<Message> buffer;

  public Middleware(String color, int id, int applicationPort) {
    this.color = color;
    this.id = id;
    this.port = applicationPort+1;
    this.applicationPort = applicationPort;
    this.buffer = new ArrayList<>();
    try {
      this.middlewareSocket = new ServerSocket(port);
    } catch (IOException e) {
      System.err.println(color+"Failed to start Middleware "+id+" on port "+port+"!"+color_reset);
    }
  }

  public void sendMessage(int port, Message message) {
    try {
      try {
        Thread.sleep(AutomatedStart.getSleepTime(message.getSenderId(), (port-8001)/100));
      } catch (Exception e1) {e1.printStackTrace();}
      Socket socket = new Socket(ip, port);
      ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

      output.writeObject(message);

      socket.close();
      System.out.println(color+"Middleware "+id+" sent message to middleware on port "+port+"!"+color_reset);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void sendToClient(Message message) {
    try {
      Socket application = new Socket(ip, applicationPort);
      ObjectOutputStream appOut = new ObjectOutputStream(application.getOutputStream());
      appOut.writeObject(message);
      application.close();
      System.out.println(color + "Middleware " + id + " sent message to it's application!" + color_reset);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void forwardMessage(Socket socket) {
    try {
      ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

      Message msg = new Message("-error-", "-receive-", id);
      try {
        msg = (Message) input.readObject();
      } catch (EOFException e) {}

      Message toSend = msg;
      if (toSend.getMode().equals("-send-")) {
        toSend.setMode("-receive-");
        toSend.setTimestamp(new Date());
        otherMiddleware.entrySet().stream()
          .forEach(e -> sendMessage(e.getKey(), toSend));
      } else {
        if (!buffer.stream().anyMatch(m -> m.getMessage().equals(toSend.getMessage()))) {
          buffer.add(toSend);
          sendToClient(toSend);
        }
      }
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    System.out.println(color+"Started Middleware "+id+" on port "+port+"!"+color_reset);
    ExecutorService es = Executors.newFixedThreadPool(4);

    try {
      while (true) {
        Socket socket = middlewareSocket.accept();
        es.submit(new Thread(() -> {forwardMessage(socket);}));

        System.out.println(color+"Middleware "+id+" accepted new message!"+color_reset);
      }
    } catch (SocketException se) {
      es.shutdown();
    } catch (Exception e) {
      System.err.println(color+"Middleware "+id+" failed!"+color_reset);
    }
  }

  public int getPort() {
    return port;
  }

  public Map<Integer, Integer> getOtherMiddleware() {
    return otherMiddleware;
  }

  public void setOtherMiddleware(Map<Integer, Integer> otherMiddleware) {
    this.otherMiddleware = otherMiddleware;
  }
}

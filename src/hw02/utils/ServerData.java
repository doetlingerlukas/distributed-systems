package hw02.utils;

/**
 * Created by Lukas Dötlinger.
 */
public class ServerData {

  private int id;
  private String name;
  private int port;

  public ServerData(int id, String name, int port) {
    this.id = id;
    this.name = name;
    this.port = port;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getPort() {
    return port;
  }
}

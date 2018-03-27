package hw02.part2;

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

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }
}

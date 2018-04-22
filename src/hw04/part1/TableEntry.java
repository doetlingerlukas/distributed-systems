package hw04.part1;

import java.io.Serializable;

/**
 * Created by Lukas Dötlinger.
 */
public class TableEntry implements Serializable {

  private String ip;
  private int port;
  private String name;

  public TableEntry(String ip, int port, String name) {
    this.ip = ip;
    this.port = port;
    this.name = name;
  }

  public boolean equals(TableEntry toCompare) {
    if (this.ip.equals(toCompare.ip) && (this.port == toCompare.port)) {
      return true;
    }
    return false;
  }

  public String getIp() {
    return ip;
  }

  public int getPort() {
    return port;
  }

  public String getName() {
    return name;
  }
}

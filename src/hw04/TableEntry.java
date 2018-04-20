package hw04;

import java.io.Serializable;

/**
 * Created by Lukas Dötlinger.
 */
public class TableEntry implements Serializable {

  private String ip;
  private int port;

  public TableEntry(String ip, int port) {
    this.ip = ip;
    this.port = port;
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
}

package hw07;

import java.util.List;

/**
 * Created by Lukas Dötlinger.
 */
public class Connection {

  private int latency;
  private String from;
  private String to;

  public Connection(int latency, String from, String to) {
    this.latency = latency;
    this.from = from;
    this.to = to;
  }

  public boolean equals(Connection c) {
    if (from.equals(c.from) && to.equals(c.to)) {
      return true;
    } else if (from.equals(c.to) && to.equals(c.from)) {
      return true;
    } else {
      return false;
    }
  }

  public boolean containedIn(List<Connection> list) {
    return list.stream()
      .anyMatch(connection -> this.equals(connection));
  }

  public int getLatency() {
    return latency;
  }

  public void setLatency(int latency) {
    this.latency = latency;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }
}

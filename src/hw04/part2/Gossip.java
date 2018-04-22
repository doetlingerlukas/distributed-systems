package hw04.part2;

import hw04.TableEntry;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lukas Dötlinger.
 */
public class Gossip implements Serializable {

  private String message;
  private List<TableEntry> recipients;

  public Gossip(String msg, List<TableEntry> recipients) {
    this.message = msg;
    this.recipients = recipients;
  }

  public String getMessage() {
    return message;
  }

  public List<TableEntry> getRecipients() {
    return recipients;
  }
}

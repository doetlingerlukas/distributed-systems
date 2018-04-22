package hw04.part2;

import hw04.TableEntry;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lukas Dötlinger.
 */
public class Gossip implements Serializable {

  private int iteration;
  private String message;
  private List<TableEntry> recipients;

  public Gossip(int iteration, String msg, List<TableEntry> recipients) {
    this.iteration = iteration;
    this.message = msg;
    this.recipients = recipients;
  }

  public int getIteration() {
    return iteration;
  }

  public String getMessage() {
    return message;
  }

  public List<TableEntry> getRecipients() {
    return recipients;
  }
}

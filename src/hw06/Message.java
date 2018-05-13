package hw06;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Lukas Dötlinger.
 */
public class Message implements Serializable {

  private String message;
  private Date timestamp;
  private String mode;
  private int senderId;

  public Message(String message, String mode, int senderId) {
    this.message = message;
    this.mode = mode;
    this.timestamp = new Date();
    this.senderId = senderId;
  }

  public boolean olderThan(Message message) {
    return this.timestamp.before(message.getTimestamp());
  }

  public boolean newerThan(Message message) {
    return this.timestamp.after(message.getTimestamp());
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public int getSenderId() {
    return senderId;
  }

  public void setSenderId(int senderId) {
    this.senderId = senderId;
  }
}

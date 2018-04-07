package hw02.part1;

import hw02.utils.Protocol;

/**
 * Created by Lukas Dötlinger.
 */
public class SimpleClient implements Runnable {

  private String toSort;

  public SimpleClient(String toSort) {
    this.toSort = toSort;
  }

  @Override
  public void run() {
    Protocol.request(toSort);
  }
}
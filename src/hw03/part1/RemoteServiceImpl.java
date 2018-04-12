package hw03.part1;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Lukas Dötlinger.
 */
public class RemoteServiceImpl implements RemoteService {

  @Override
  public String sort(String inputString) {
    return Stream.of(inputString.split(""))
      .sorted()
      .collect(Collectors.joining());
  }

  @Override
  public void compute(Long inputTime) {
    try {
      Thread.sleep(inputTime);
    } catch (InterruptedException e) {
      System.err.println("Failed to compute for "+inputTime+" milliseconds!");
    }
  }
}

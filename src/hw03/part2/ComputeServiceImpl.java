package hw03.part2;

/**
 * Created by Lukas Dötlinger.
 */
public class ComputeServiceImpl implements ComputeService {

  @Override
  public void compute(Long inputTime) {
    try {
      Thread.sleep(inputTime);
    } catch (InterruptedException e) {
      System.err.println("Failed to compute for "+inputTime+" milliseconds!");
    }
  }

}

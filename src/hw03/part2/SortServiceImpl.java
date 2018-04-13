package hw03.part2;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Lukas Dötlinger.
 */
public class SortServiceImpl implements SortService {

  @Override
  public String sort(String inputString) {
    return Stream.of(inputString.split(""))
      .sorted()
      .collect(Collectors.joining());
  }

}

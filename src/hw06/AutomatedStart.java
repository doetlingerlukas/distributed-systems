package hw06;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lukas Dötlinger.
 */
public class AutomatedStart {

  public static final String CYAN_BACKGROUND = "\u001B[46m";
  public static final String WHITE_BACKGROUND = "\u001B[47m";

  private static Map<Integer, Integer> generateMap(List<Middleware> ms, int port) {
    Map<Integer, Integer> map = new HashMap<>();
    ms.stream()
      .filter(m -> m.getPort() != port)
      .forEach(m -> map.put(m.getPort(), (int) (port-8001)/100));
    return map;
  }

  public static int getSleepTime(int from, int to) {
    if ((from == 1 && to == 2) || (from == 2 && to == 1)) {
      return 1000;
    } else if ((from == 1 && to == 3) || (from == 3 && to == 1)) {
      return 20000;
    } else {
      return 3000;
    }
  }

  public static void main(String[] args) {
    List<Middleware> ms = IntStream.rangeClosed(1, 3)
      .mapToObj(i -> new Middleware(WHITE_BACKGROUND, i, 8000+(100*i)))
      .collect(Collectors.toList());

    ms.stream()
      .forEach(m -> m.setOtherMiddleware(generateMap(ms, m.getPort())));

    ms.stream()
      .forEach(m -> new Thread(m).start());

    IntStream.rangeClosed(1, 3)
      .mapToObj(i-> new Application(CYAN_BACKGROUND, i, 8000+(100*i), true))
      .forEach(a -> new Thread(a).start());
  }
}

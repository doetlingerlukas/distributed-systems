package hw05.practical;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lukas Dötlinger.
 */
public class Utils {

  public static List<Integer> getNodePositions(String matrNumber) {
    List<Integer> matrDigits = Arrays.stream(matrNumber.split(""))
      .map(n -> Integer.parseInt(n))
      .collect(Collectors.toList());
    return IntStream.range(0, matrDigits.size())
      .mapToObj(i -> matrDigits.subList(i, matrDigits.size()-1).stream()
        .mapToInt(x -> x)
        .sum() % 16)
      .collect(Collectors.toList());
  }

  public static void printTable(List<Node> table, Node node) {
    System.out.println("-----"+node.getName()+"-----");
    System.out.println("Successor | Id: "+node.getSuccessor().getId()+" | Name: "+node.getSuccessor().getName()+
      " | Address: "+node.getSuccessor().getAddress());
    System.out.println("Predecessor | Id: "+node.getPredecessor().getId()+" | Name: "+node.getPredecessor().getName()+
      " | Address: "+node.getPredecessor().getAddress());
    table.stream()
      .forEach(n -> System.out.println("Id: "+n.getId()+" | Name: "+n.getName()+" | Address: "+n.getAddress()));
    System.out.println("---------------");
  }
}

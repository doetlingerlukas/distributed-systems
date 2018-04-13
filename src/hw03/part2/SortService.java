package hw03.part2;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Lukas Dötlinger.
 */
public interface SortService extends Remote {

  public String sort(String inputString) throws RemoteException;

}

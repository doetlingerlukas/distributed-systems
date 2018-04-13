package hw03.part2;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Lukas Dötlinger.
 */
public interface ProxyService extends Remote {

  public void accessService(String access, String inputString, int id) throws RemoteException;

}

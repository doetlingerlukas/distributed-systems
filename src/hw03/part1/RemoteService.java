package hw03.part1;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Lukas Dötlinger.
 */
public interface RemoteService extends Remote {

  public String sort(String inputString) throws RemoteException;

  public void compute(Long inputTime) throws RemoteException;
}

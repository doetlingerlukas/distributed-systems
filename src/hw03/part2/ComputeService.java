package hw03.part2;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Lukas Dötlinger.
 */
public interface ComputeService extends Remote {

  public void compute(Long inputTime) throws RemoteException;

}

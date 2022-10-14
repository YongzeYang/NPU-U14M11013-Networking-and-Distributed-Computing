package exam2.Interface;

import exam2.Bean.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Interface extends Remote {
    public boolean login(User user) throws RemoteException;
    public boolean register(User user) throws RemoteException;

}

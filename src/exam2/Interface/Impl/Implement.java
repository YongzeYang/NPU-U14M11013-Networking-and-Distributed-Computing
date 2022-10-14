package exam2.Interface.Impl;

import exam2.Bean.User;
import exam2.Interface.Interface;

import java.rmi.RemoteException;

public class Implement implements Interface {

    @Override
    public boolean login(User user) throws RemoteException {
        return false;
    }

    @Override
    public boolean register(User user) throws RemoteException {
        return false;
    }
}

package exec3.Interface.Impl;

import exec3.Bean.Meeting;
import exec3.Bean.User;
import exec3.Interface.MeetingSystem;

import java.rmi.RemoteException;
import java.util.Date;

public class MeetingSystemImplement implements MeetingSystem {

    @Override
    public boolean register(User user) throws RemoteException {
        return false;
    }

    @Override
    public boolean add(Meeting meeting) throws RemoteException {
        return false;
    }

    @Override
    public boolean login(User user) throws RemoteException {
        return false;
    }

    @Override
    public Meeting query(User user, Date begin, Date end) throws RemoteException {
        return null;
    }

    @Override
    public boolean delete(User user, String id) throws RemoteException {
        return false;
    }

    @Override
    public Boolean clear(User user) throws RemoteException {
        return null;
    }
}

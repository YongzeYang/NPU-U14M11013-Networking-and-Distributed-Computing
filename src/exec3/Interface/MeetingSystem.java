package exec3.Interface;

import exec3.Bean.Meeting;
import exec3.Bean.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface MeetingSystem extends Remote {
        public boolean register(User user)
            throws RemoteException;
        public boolean add(Meeting meeting)
            throws RemoteException;
        public boolean login(User user)
            throws RemoteException;
        public Meeting query(User user, Date begin, Date end)
            throws RemoteException;
        public boolean delete(User user, String id)
            throws RemoteException;
        public Boolean clear(User user)
            throws RemoteException;

}

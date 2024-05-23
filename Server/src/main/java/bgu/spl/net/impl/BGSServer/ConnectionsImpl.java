package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.NonBlockingConnectionHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {
    private static ConnectionsImpl instance = null;
    private ConcurrentHashMap<Integer, ConnectionHandler> connectionsList;

    public static ConnectionsImpl getInstance() {
        if (instance == null){
            synchronized (ConnectionsImpl.class){
                if (instance == null){
                    instance = new ConnectionsImpl();
                }
            }
        }
        return instance;
    }

    private ConnectionsImpl(){
        connectionsList = new ConcurrentHashMap<>();
    }

    public void addConnection(Integer id , NonBlockingConnectionHandler handler){
        handler.getProtocol().start(id,this);
        connectionsList.put(id, handler);
    }

    public void addConnection(Integer id , BlockingConnectionHandler handler){
        handler.getProtocol().start(id,this);
        connectionsList.put(id, handler);
    }

    @Override
    public boolean send(int connectionId, Object msg) {
        if (msg == null){
            return false;
        }
        try {
            connectionsList.get(connectionId).send(msg);
            return true;
        }catch (IOException ex){
            ex.printStackTrace();
            return false;
        }
        // used by connectionHandler via send(T msg) function
        //how to return the boolean
    }

    @Override
    public void broadcast(Object msg) {
        try {
            for (ConnectionHandler c : connectionsList.values()) {
                c.send(msg);
            }
        } catch (IOException ex){ ex.printStackTrace();}
    }

    @Override
    public void disconnect(int connectionId) {
        connectionsList.remove(connectionId);
    }

    public void changeID(int oldId, int newID){

    }

}

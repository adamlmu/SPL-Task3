package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.BGSServer.messages.NotificationMessage;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataBase {
    private ConcurrentHashMap<String,User> users;
    private ConcurrentHashMap<Integer,String> id2username;
    private ConcurrentHashMap<Integer, User> loggedInUsers;
    private ConcurrentLinkedQueue<String> forbiddenWords;
    private ConcurrentHashMap<Integer, ConcurrentLinkedQueue<User>> blockingLists;
    private static DataBase instance = null;
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<NotificationMessage>> notificationQueues;
    private ConcurrentHashMap<User, Integer> knownIDs;
    private ConnectionsImpl connections;

    private DataBase(){
        users = new ConcurrentHashMap<>();
        id2username = new ConcurrentHashMap<>();
        forbiddenWords = new ConcurrentLinkedQueue<>();
        forbiddenWords.add("shit");
        forbiddenWords.add("banana");
        forbiddenWords.add("glass");
        blockingLists = new ConcurrentHashMap<>();
        loggedInUsers = new ConcurrentHashMap<>();
        notificationQueues = new ConcurrentHashMap<>();
        knownIDs = new ConcurrentHashMap<>();
        connections = ConnectionsImpl.getInstance();
    }

    public void addBlocking(Integer blockingID, User blockedUser){
        if (!(blockingLists.containsKey(blockingID))) {
            blockingLists.put(blockingID, new ConcurrentLinkedQueue<>());
        }
        (blockingLists.get(blockingID)).add(blockedUser);
    }

    public boolean checkBlocked(Integer id, User user){
        if (blockingLists.containsKey(id)){
            return blockingLists.get(id).contains(user);
        }
        return false;
    }

    public ConcurrentHashMap<String, User> getUsers() {
        return users;
    }

    public void setForbiddenWords(List<String> _forbiddenWords) {
        forbiddenWords.addAll(_forbiddenWords);
    }

    public ConcurrentLinkedQueue<String> getForbiddenWords() {
        return forbiddenWords;
    }

    public User getUserByName(String name){
        if (name!=null) {
            if (users.containsKey(name))
                return users.get(name);
            else return null;
        }
        else return null;
    }

    public User getUserByID(Integer id){
        if (id2username.containsKey(id))
            return users.get(id2username.get(id));
        else return null;
    }

    public static DataBase getInstance() {
        if (instance == null){
            synchronized (DataBase.class){
                if (instance == null){
                    instance = new DataBase();
                }
            }
        }
        return instance;
    }

    public void addUser(String username,int id,User user){
        users.put(username,user);
        id2username.put(id,username);
    }

    public boolean loginUser(User user, int connectionID){
        loggedInUsers.put(connectionID, user);
        if (knownIDs.containsKey(user)){
            connections.changeID(user.getConnectionID(),connectionID);
        }
        if (notificationQueues.containsKey(user.getUsername())) {
            return (!(notificationQueues.get(user.getUsername()).isEmpty()));
        }
        else return false;
    }

    public ConcurrentLinkedQueue getNotifications(String user){
        return notificationQueues.get(user);
    }

    public void logoutUser(Integer connectionID){
//        if (!knownIDs.containsKey(user)){
//            knownIDs.put(user, user.getConnectionID());
//        }
        loggedInUsers.remove(connectionID);
    }

    public boolean isUserLoggedIn(User user){
        return loggedInUsers.contains(user);
    }

    public void addNotification(User user, NotificationMessage msg){
        if (!notificationQueues.containsKey(user.getUsername())){
            notificationQueues.put(user.getUsername(),new ConcurrentLinkedQueue<>());
        }
        notificationQueues.get(user.getUsername()).add(msg);
    }

    public ConcurrentHashMap<Integer,User> getLoggedInUsers() {
        return loggedInUsers;
    }
}

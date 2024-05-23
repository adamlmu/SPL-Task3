package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.messages.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BGSProtocol implements BidiMessagingProtocol<Message> {
    private int connectionId;
    private Connections<Message> connections;
    private DataBase db;
    private User myUser;

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.connectionId = connectionId;
        this.connections = connections;
        db = DataBase.getInstance();
    }

    @Override
    public Message process(Message msg) {
        //REGISTER
        if (msg.getOpcode().equals(1)){
            RegisterMessage register = (RegisterMessage)msg;
            if (db.getUserByName(register.getUsername())==null){                                                  //checks if user does not exists
                try {
                    User newUser = new User(register.getUsername(), register.getPassword(), register.getBirthday(), connectionId);
                    db.addUser(register.getUsername(),connectionId,newUser);
                    AckMessage ack = new AckMessage(10,1,null);
                    myUser = null;
                    return ack;
                } catch (ParseException pe){ pe.printStackTrace();}
            }
            else return new ErrorMessage(11,1);
        }
        //LOGIN
        if (msg.getOpcode().equals(2)) {
            LoginMessage login = (LoginMessage) msg;
            if ((db.getUserByName(login.getUsername())!=null)                                               //checks if user exists
                    && (login.getPassword().equals((db.getUserByName(login.getUsername())).getPass()) )     //checks if password is correct
                    && (login.getCaptcha())                                                                 //checks if captcha is 1
                    && !(db.isUserLoggedIn(db.getUserByName(login.getUsername())))){                          //checks if user is not logged in already
                myUser = db.getUserByName(login.getUsername());
                if (db.loginUser(db.getUserByName(login.getUsername()), connectionId)){
                    connections.send(connectionId,new AckMessage(10,2,null));
                    ConcurrentLinkedQueue<NotificationMessage> queue = db.getNotifications(myUser.getUsername());
                    for (int i=1; i<=queue.size() ; i++){
                        NotificationMessage nm = queue.poll();
                        if (nm!=null) {
                            if (!queue.isEmpty())
                                connections.send(connectionId, nm);
                            else return nm;
                        }
                    }
                }
                else return new AckMessage(10,2,null);
            }
            else return new ErrorMessage(11,2);
        }
        //LOGOUT
        if (msg.getOpcode().equals(3)) {
            LogoutMessage logout = (LogoutMessage) msg;
            if ((myUser != null) && (db.isUserLoggedIn(myUser))){
                db.logoutUser(connectionId);
                connections.send(connectionId, new AckMessage(10, 3, null));
                connections.disconnect(connectionId);
                myUser = null;
            }
            else return new ErrorMessage(11, 3);
        }
        //FOLLOW
        if (msg.getOpcode().equals(4)){
            FollowMessage follow = (FollowMessage)msg;
            if ((myUser != null)
                    && (db.isUserLoggedIn(myUser)
                    && db.getUserByName(follow.getUsername())!=null)) {
                User user = db.getUserByName(follow.getUsername());
                if (!isBlocked(user)) {                                   //checks if blocked or blocking
                    if (follow.getFollow()) {
                        if (!(user.doesHeFollowMe(myUser))) {
                            user.addFollower(myUser);
                            myUser.increaseFollowing();
                            return new AckMessage(10, 4, null);
                        } else return new ErrorMessage(11, 4);
                    }
                    else {
                        if (user.doesHeFollowMe(myUser)) {
                            user.removeFollower(myUser);
                            myUser.decreaseFollowing();
                            return new AckMessage(10, 4, null);
                        } else return new ErrorMessage(11, 4);
                    }
                }
                else return new ErrorMessage(11, 4);
            }
            else return new ErrorMessage(11,4);
        }
        //POST
        if (msg.getOpcode().equals(5)){
            PostMessage post = (PostMessage)msg;
            if ((myUser != null) && (db.isUserLoggedIn(myUser))){
                String filteredContent = filterContent(post.getContent());
                User taggedUser = db.getUserByName(tag(filteredContent));
                NotificationMessage notification = new NotificationMessage(9,connectionId,(short)1,myUser.getUsername(),filteredContent);
                ConcurrentLinkedQueue<User> myFollowers = myUser.getFollowers();
                for (User follower : myFollowers){
                    if (!isBlocked(follower)) {                                   //checks if blocked or blocking
                        if (db.isUserLoggedIn(follower))
                            connections.send(follower.getConnectionID(), notification);
                        else
                            db.addNotification(follower,notification);
                    }
                }
                if (taggedUser!=null){
                    if ( !(isBlocked(taggedUser)) && !(myUser.doesHeFollowMe(taggedUser))) {                                   //checks if blocked or blocking
                        if (db.isUserLoggedIn(taggedUser))
                            connections.send(taggedUser.getConnectionID(), notification);
                        else
                            db.addNotification(taggedUser,notification);
                    }
                }
                myUser.increasePosts();
                return new AckMessage(10,5,null);
            }
            else return new ErrorMessage(11,5);
        }
        //PM
        if (msg.getOpcode().equals(6)){
            PMMessage pm = (PMMessage) msg;
            if ((myUser != null) && (db.getUserByName(pm.getAddressee())!=null) && (db.isUserLoggedIn(myUser))){
                User addressee = db.getUserByName(pm.getAddressee());
                if (!isBlocked(addressee)) {                                   //checks if blocked or blocking
                    String filteredContent = filterContent(pm.getContent());
                    NotificationMessage notification = new NotificationMessage(9,connectionId, (short)0, myUser.getUsername(), filteredContent);
                    if (db.isUserLoggedIn(addressee))
                        connections.send(addressee.getConnectionID(), notification);
                    else
                        db.addNotification(addressee,notification);
                    return new AckMessage(10, 6, null);
                }
                else return new ErrorMessage(11, 6);
            }
            else return new ErrorMessage(11, 6);
        }
        //LOG STAT
        if (msg.getOpcode().equals(7)){
            if ((myUser != null) && (db.isUserLoggedIn(myUser))){
                ConcurrentHashMap<Integer,User> loggedInUsers = db.getLoggedInUsers();
                int size = loggedInUsers.size();
                int i = 1;
                for (User user : loggedInUsers.values()){
                    if (!isBlocked(user)) {                                   //checks if blocked or blocking
                        String stat = userStat(user);
                        if (i == size) {
                            return new AckMessage(10, 7, stat);
                        } else {
                            connections.send(connectionId, new AckMessage(10, 7, stat));
                        }
                    }
                    i++;
                }
            }
            else return new ErrorMessage(11,7);
        }
        //STAT
        if (msg.getOpcode().equals(8)){
            StatMessage stat = (StatMessage) msg;
            if ((myUser != null) && (db.isUserLoggedIn(myUser))) {
                LinkedList<String> users = stat.getUsernames();
                int size = users.size();
                int i = 1;
                for (String username : users) {
                    User user = db.getUserByName(username);
                    if (user != null){
                        if (!isBlocked(user)) {                                   //checks if blocked or blocking
                            String statStr = userStat(user);
                            AckMessage ack = new AckMessage(10, 8, statStr);
                            if (i == size) {
                                return ack;
                            } else connections.send(myUser.getConnectionID(), ack);
                        }
                    }
                    i++;
                }
            }
            else return new ErrorMessage(11,8);
        }
        //BLOCK
        if (msg.getOpcode().equals(12)){
            BlockMessage block = (BlockMessage) msg;
            if ((myUser != null) && (db.isUserLoggedIn(myUser))) {
                User blocking = db.getUserByName(block.getBlockingUsername());
                if (!(db.checkBlocked(connectionId,blocking)) && !((blocking.getUsername()).equals(myUser.getUsername()))){
                    db.addBlocking(connectionId,blocking);
                    myUser.removeFollower(blocking);
                    blocking.removeFollower(myUser);
                    return new AckMessage(10,12,null);
                }
                else return new ErrorMessage(11,12);
            }
            else return new ErrorMessage(11,12);
        }
        return null;
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }

    private String filterContent(String content){
        String[] contentWords = content.split(" ");
        for (int i=0;i<contentWords.length;i++){
            for (String forbidden : db.getForbiddenWords()){
                if (contentWords[i].equals(forbidden)) {
                    contentWords[i] = "<filtered>";
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String word : contentWords)
            sb.append(word.toString()).append(" ");
        String filteredContent =  sb.substring(0, sb.length() - 1);
        return filteredContent;
    }

    private String tag(String content){
        String[] contentWords = content.split(" ");
        for (String contentWord : contentWords) {
            char symbol = content.charAt(0);
            if (symbol == '@') {
                return contentWord.substring(1);
            }
        }
        return null;
    }

    private String userStat(User user){
        String stat = user.getAge()+" ";
        stat += (user.getNumOfPosts()+" ");
        stat += (user.getFollowers().size()+" ");
        stat += (user.getFollowing());
        return stat;
    }

    private boolean isBlocked(User user){
        return (db.checkBlocked(connectionId,user)
                | db.checkBlocked(user.getConnectionID(), myUser));
    }
}

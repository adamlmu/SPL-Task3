package bgu.spl.net.impl.BGSServer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {
    final private String username;
    final private String pass;
    final private int connectionID;
    private ConcurrentLinkedQueue<User> followers;
    private int following;
    private int numOfPosts;
    private int age;

    public User(String _username, String password, String date, int id) throws ParseException {
        username = _username;
        pass = password;
        connectionID = id;
        followers = new ConcurrentLinkedQueue<>();
        following = 0;
        numOfPosts = 0;
        age = calAge(date);
    }

    private int calAge(String date) throws ParseException {
        LocalDate today = LocalDate.now();
        Date birthday = new SimpleDateFormat("dd-MM-yyyy").parse(date);
        LocalDate birthday1 = birthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period p = Period.between(birthday1, today);
        return p.getYears();
    }

    public ConcurrentLinkedQueue<User> getFollowers() {
        return followers;
    }

    public void increasePosts(){
        numOfPosts++;
    }

    public int getFollowing() {
        return following;
    }

    public int getNumOfPosts() {
        return numOfPosts;
    }

    public int getAge() {
        return age;
    }

    public int getConnectionID() {
        return connectionID;
    }

    public String getPass() {
        return pass;
    }

    public String getUsername() {
        return username;
    }

    public void addFollower(User follower){
        followers.add(follower);
    }

    public void removeFollower(User follower){
        if (followers.contains(follower)) {
            followers.remove(follower);
        }
    }



    public boolean doesHeFollowMe(User user){
        return followers.contains(user);
    }

    public void increaseFollowing(){
        following++;
    }

    public void decreaseFollowing(){
        following--;
    }

}

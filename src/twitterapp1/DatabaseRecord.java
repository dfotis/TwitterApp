import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.*;
import java.util.ArrayList;
/**
 * Created by manos on 9/1/2017.
 */
public class DatabaseRecord {
    String user_id;
    String user_name;
    long timestamp;
    boolean retwitte;
    ArrayList<String> url_list;
    ArrayList<String> expanded_url_list;
    ArrayList<String> mention_name_list;
    ArrayList<String> mention_id_list;
    ArrayList<String> hashtags_list;
    String tweet_id;
    String text;
    String retweet_id;

    DatabaseRecord(){
        url_list = new ArrayList<>();
        expanded_url_list = new ArrayList<>();
        retwitte = false;
        mention_name_list = new ArrayList<>();
        mention_id_list = new ArrayList<>();
        hashtags_list = new ArrayList<>();

    }

    void setUser(String user_id, String user_name){
        this.user_id = user_id;
        this.user_name = user_name;
    }

    void setTimestamp(long timestamp){
        this.timestamp = timestamp;
    }

    void addUrl(String url, String expanded_url){
        url_list.add(url);
        expanded_url_list.add(expanded_url);
    }

    void addMentions(String name, String id ){
        mention_id_list.add(id);
        mention_name_list.add(name);
    }

    void setText(String text, String tweet_id){
        this.text = text;
        this.tweet_id = tweet_id;
    }

    void setRetwitte(String retweet_id){
        retwitte = true;
        this.retweet_id = retweet_id;
    }

    void addHashtag(String hashtag){
        hashtags_list.add(hashtag);
    }

    void insertUser(String table, Connection con) throws SQLException {
        String query = "INSERT IGNORE INTO " + table + " SET user_id = \"" + user_id + "\", name = \"" + user_name + "\"";
        Statement stmt = con.createStatement();
        stmt.executeUpdate(query);
    }

    void insertTweet(String table, Connection con) throws SQLException {
        String query = "INSERT INTO " + table + "( user_id, timestamp ,text, tweet_id) VALUES(?,?,?,?)";
        PreparedStatement preparedStat = con.prepareStatement(query);
        preparedStat.setString(1,user_id);
        preparedStat.setTimestamp(2, Timestamp.valueOf(timestampConverter()));
        preparedStat.setString(3,text);
        preparedStat.setString(4,tweet_id);
        preparedStat.execute();
    }

    void insertHashtags(String table, Connection con) throws SQLException {
        String query = "INSERT INTO " + table + "(user_id, timestamp, hashtag) VALUES(?,?,?)";
        for (String hashtag : hashtags_list){
            PreparedStatement preparedStat = con.prepareStatement(query);
            preparedStat.setString(1,user_id);
            preparedStat.setTimestamp(2, Timestamp.valueOf(timestampConverter()));
            preparedStat.setString(3,hashtag);
            preparedStat.execute();
        }
    }

    void  insertMentions(String table, Connection con) throws SQLException {
        String query = "INSERT INTO " + table + "(user_id,timestamp,mentioned_user) VALUES(?,?,?)";
        for (String mention : mention_id_list){
            PreparedStatement preparedStat = con.prepareStatement(query);
            preparedStat.setString(1,user_id);
            preparedStat.setTimestamp(2, Timestamp.valueOf(timestampConverter()));
            preparedStat.setString(3,mention);
            preparedStat.execute();
        }
    }

    void insertUrls(String table, Connection con) throws SQLException {
        String query = "INSERT INTO " + table + "(user_id, timestamp, url, extended_url) VALUES(?,?,?,?)";
        for (int i = 0; i < url_list.size(); i++){
            PreparedStatement preparedStat = con.prepareStatement(query);
            preparedStat.setString(1,user_id);
            preparedStat.setTimestamp(2, Timestamp.valueOf(timestampConverter()));
            preparedStat.setString(3,url_list.get(i));
            preparedStat.setString(4,expanded_url_list.get(i));
            preparedStat.execute();

        }
    }

    void insertRetweet(String table, Connection con) throws SQLException {
        String query = "INSERT INTO " + table + "(user_id, timestamp ,tweet_user, retweet_id ) VALUES(?,?,?,?)";
        if(retwitte){
            PreparedStatement preparedStat = con.prepareStatement(query);
            preparedStat.setString(1,user_id);
            preparedStat.setTimestamp(2, Timestamp.valueOf(timestampConverter()));
            preparedStat.setString(3,mention_id_list.get(0));
            preparedStat.setString(4,retweet_id);
        }
    }



    String timestampConverter(){
        DateTime current_time = new DateTime(timestamp, DateTimeZone.forTimeZone(java.util.TimeZone.getTimeZone("GR/Central")));
        DateTimeFormatter parser1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return  parser1.print(current_time);

    }
}

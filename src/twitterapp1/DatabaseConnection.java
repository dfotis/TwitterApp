import java.sql.*;
import java.util.ArrayList;

/**
 * Created by manos on 9/1/2017.
 */
public class DatabaseConnection {
    String url = "jdbc:mysql://localhost:3306/javabase";
    String username = "java";
    String password = "password";

    DatabaseConnection(){

    }

    void insertRecord(DatabaseRecord record)  {


        System.out.println("Connecting database...");

        try ( Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Database connected!");

            record.insertUser("users",connection);
            record.insertHashtags("hashtags",connection);
            record.insertMentions("mentions",connection);
            record.insertRetweet("retweet",connection);
            record.insertTweet("tweet",connection);
            record.insertUrls("urls",connection);

//
//           // String query_user = record.insertUser("users");
//            //System.out.println("Users query: " + query_user);
//           // Statement stmt = connection.createStatement();
//            //stmt.executeUpdate(query_user);
//
//            String query_tweet = record.insertTweet("tweet");
//            System.out.println("Tweet query: " + query_tweet);
//            stmt.executeUpdate(query_tweet);
//
//            String query_retweet = record.insertRetweet("retweet");
//            System.out.println("ReTweet query: " + query_retweet);
//            if(query_retweet != ""){
//                stmt.executeUpdate(query_retweet);
//            }
//
//
//            ArrayList<String> query_hashtags = record.insertHashtags("hashtags");
//            for(String hashtag : query_hashtags){
//                System.out.println("Hashtag query: " + hashtag);
//                stmt.executeUpdate(hashtag);
//            }
//
//            ArrayList<String> query_mentions = record.insertMentions("mentions");
//            for (String mention : query_mentions){
//                System.out.println("Mention query: " + mention);
//                stmt.executeUpdate(mention);
//            }
//
//            ArrayList<String> query_urls = record.insertUrls("urls");
//            for (String url : query_urls){
//                System.out.println("URLS query: " + url);
//                stmt.executeUpdate(url);
//            }

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }


}

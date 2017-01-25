import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by manos on 9/1/2017.
 */
public class DatabaseConnection {
    String url = "jdbc:mysql://localhost:3306/javabase";
    String username = "java";
    String password = "password";

    double[][] hashtags_similarity;
    double[][] tweet_similarity;
    double[][] retweet_similarity;
    double[][] mentions_similarity;
    double[][] urls_similarity;

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


        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }

     public String getAllUsers(){

        System.out.println("Connecting database...");
        ArrayList<String> users_ids = new ArrayList<>();
        ArrayList<String> all_hashtags = new ArrayList<>();
        try ( Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Database connected!");

            Statement st = connection.createStatement();
            String select_users = ("SELECT user_id FROM users;");
            ResultSet rs_selected_users = st.executeQuery(select_users);
            while (rs_selected_users.next()) {
                users_ids.add(rs_selected_users.getString("user_id"));
                //System.out.println("User_id: " + rs.getString("user_id"));
            }
           // System.out.println("Size of users_id" + users_ids.size());
            hashtags_similarity = new double[users_ids.size()][users_ids.size()];
            tweet_similarity = new double[users_ids.size()][users_ids.size()];
            retweet_similarity = new double[users_ids.size()][users_ids.size()];
            urls_similarity = new double[users_ids.size()][users_ids.size()];
            mentions_similarity = new double[users_ids.size()][users_ids.size()];
            for (int i = 0; i < users_ids.size(); i++) {

                String current_hashtags = getCurrentsUserHashtags(users_ids.get(i),connection);
                String current_mentions = getCurrentsUserMentions(users_ids.get(i),connection);



                //System.out.println("Hashtags from user_1: " + current_hashtags);
                hashtags_similarity[i][i] = 1;
                for (int j = i+1; j < users_ids.size(); j++) {
                    String current_hashtags_2 = "";
                    String sql_3 = ("SELECT hashtag FROM hashtags WHERE user_id = '" + users_ids.get(j) + "' ORDER BY hashtag ASC;");
                    ResultSet rs_3 = st.executeQuery(sql_3);
                    while (rs_3.next()) {
                        String temp_current_hashtags_2 = rs_3.getString("hashtag");
                        if(temp_current_hashtags_2 != null){
                            current_hashtags_2 = current_hashtags_2 + " " +temp_current_hashtags_2;
                        }
                    }
                    System.out.println("Hashtags from user_2: " + current_hashtags_2);
                    hashtags_similarity[i][j] = hashtags_similarity[j][i] = Similarity.cosineSimilarity(current_hashtags, current_hashtags_2);

                }
                System.out.println("|");



            }

            printArray();



        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
        return "";

    }

    String getCurrentsUserHashtags(String user_id, Connection connection) throws SQLException {
        //===== Get hashtags from current user =====
        Statement st = connection.createStatement();
        String current_hashtags = "";
        String sql_select_hashtags = ("SELECT hashtag FROM hashtags WHERE user_id = '" + user_id+ " ' ORDER BY hashtag ASC;");
        //System.out.println(sql_2);
        ResultSet rs_select_hashtags = st.executeQuery(sql_select_hashtags);
        while (rs_select_hashtags.next()) {
            String temp_current_hashtags =  rs_select_hashtags.getString("hashtag");
            if(temp_current_hashtags != null){
                current_hashtags = current_hashtags + " " + temp_current_hashtags;
            }
        }
        return current_hashtags;

    }

    String getCurrentsUserMentions(String user_id, Connection connection) throws SQLException {
        //===== Get Mentions from current user =====
        Statement st = connection.createStatement();
        String current_mentions = "";
        String sql_select_mentions = ("SELECT mentioned_user FROM mentions WHERE user_id = '" + user_id+ "';");
        //System.out.println(sql_2);
        ResultSet rs_select_mentions = st.executeQuery(sql_select_mentions);
        while (rs_select_mentions.next()) {
            String temp_current_mention =  rs_select_mentions.getString("mentioned_id");
            if(temp_current_mention != null){
                current_mentions = current_mentions + " " + temp_current_mention;
            }
        }
        return current_mentions;

    }


    String getCurrentsUserUrls(String user_id, Connection connection) throws SQLException {
        //===== Get Mentions from current user =====
        Statement st = connection.createStatement();
        String current_urls = "";
        String sql_select_urls = ("SELECT url FROM urls WHERE user_id = '" + user_id+ "';");
        //System.out.println(sql_2);
        ResultSet rs_select_urls = st.executeQuery(sql_select_urls);
        while (rs_select_urls.next()) {
            String temp_current_urls =  rs_select_urls.getString("url");
            if(temp_current_urls!= null){
                current_urls = current_urls + " " + temp_current_urls;
            }
        }
        return current_urls;
    }

    void printArray(){
                for(int i= 0; i < hashtags_similarity.length; i++) {
                    for (int j = 0; j < hashtags_similarity[i].length; j++) {
                        System.out.printf("%.2f ", hashtags_similarity[i][j]);
                    }
                    System.out.println("|");
                }
    }



}

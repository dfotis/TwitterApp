import java.net.URL;
import java.util.ArrayList;
import java.util.StringJoiner;

/**
 * Created by manos on 9/1/2017.
 */
public class DatabaseRecord {
    int user_id;
    String user_name;
    long timestamp;
    boolean retwitte;
    ArrayList<String> url_list;
    ArrayList<String> expanded_url_list;
    ArrayList<String> mention_name_list;
    ArrayList<Long> mention_id_list;
    ArrayList<String> hashtags_list;
    String text;

    DatabaseRecord(){
        url_list = new ArrayList<>();
        expanded_url_list = new ArrayList<>();
        retwitte = false;
        mention_name_list = new ArrayList<>();
        mention_id_list = new ArrayList<>();
        hashtags_list = new ArrayList<>();

    }

    void setUser(int user_id, String user_name){
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

    void addMentions(String name, long id ){
        mention_id_list.add(id);
        mention_name_list.add(name);
    }

    void setText(String text){
        this.text = text;
    }

    void setRetwitte(){
        retwitte = true;
    }

    void addHashtag(String hashtag){
        hashtags_list.add(hashtag);
    }
}

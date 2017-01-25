
import com.mongodb.*;
import com.mongodb.util.JSON;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dilaris Fotis
 */
public class DataStreamAndStore {
    public void streamAndStore(String keyword, DBCollection collection) {
        //listener, when it tracks a tweet with the keyword executes onStatus()
        StatusListener listener = new StatusListener() {
            public void onStatus(Status status) {
                System.out.println(status.getUser().getName()+":"+status.getText());
                String json = DataObjectFactory.getRawJSON(status);
                DBObject temp = (DBObject)JSON.parse(json);



                collection.insert(temp);
            }
            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }


            @Override
            public void onDeletionNotice(StatusDeletionNotice sdn) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onTrackLimitationNotice(int i) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onScrubGeo(long l, long l1) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onStallWarning(StallWarning sw) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };


        //configuration of the twitter stream
        ConfigurationBuilder confBuilder = new ConfigurationBuilder();
        confBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey("YDIYvIRyQH9wJIotPr861TDHE")
                .setOAuthConsumerSecret("xiZNutAgVVkWPxzPUGXFj4RpINd4bWb3YksWNX53cBnDPNCELi")
                .setOAuthAccessToken("1511979295-2pnC4Fvm7VmiBY5VhPfBshdUUsCV1nFmWXML9lp")
                .setOAuthAccessTokenSecret("pnCJFBPbNvoovY7w8duqmWOSh1FyiBl43LGe7CxFHRVRc");
        // get the full JSON of tweets
        confBuilder.setJSONStoreEnabled(true);

        // connection with Twitter Streaming API
        TwitterStream twitterStream = new TwitterStreamFactory(confBuilder.build()).getInstance();
        twitterStream.addListener(listener);

        FilterQuery f = new FilterQuery();
        f.track(keyword);
        twitterStream.filter(f);



        //twitterStream.sample(); //creates a thread


        final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.schedule(new Runnable(){
            @Override
            public void run(){
                twitterStream.shutdown();
                System.out.println("Twitter streaming api shuted down");
                FileWriter fstream = null;
                try {
                    fstream = new FileWriter("./data.json", true);
                    BufferedWriter out = new BufferedWriter(fstream);

                    DBCursor cursor = collection.find();
                    while (cursor.hasNext()) {
                        DBObject next_object = cursor.next();
                        System.out.println((String)next_object.get("text"));
                        out.write(next_object.toString());
                        out.newLine();


                    }
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 5, TimeUnit.MINUTES);

    }

    public void analyzeData(DBCollection collection) throws IOException {
        BasicDBObject query = new BasicDBObject();
        //query.append("text:", 1);

        DBCursor cursor = collection.find();
        DatabaseConnection connection = new DatabaseConnection();
        DatabaseRecord record = null;
        while(cursor.hasNext()) {
            System.out.println("================================");
            DBObject next_object = cursor.next();
            record = new DatabaseRecord();

            record.setText((String)next_object.get("text"),(String)next_object.get("id_str"));
            System.out.println((String)next_object.get("text"));

            DBObject user_object = (DBObject)next_object.get("user");
            //System.out.println("user_object.get(\"id\")" + (String) user_object.get("id_str"));
            record.setUser((String) user_object.get("id_str"), (String)user_object.get("name"));

            record.setTimestamp(Long.parseLong((String) next_object.get("timestamp_ms")));
            //System.out.println("Timestamp: " + timestampConverter(Long.valueOf((String)next_object.get("timestamp_ms"))));
            DBObject entities_object = (DBObject)next_object.get("entities");
            //System.out.println("Entities:" + entities_object);
            DBObject hashtags_objects = (DBObject)entities_object.get("hashtags");
            JSONObject entities_json = null;
            try {
                entities_json = new JSONObject(entities_object.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            //String pageName = entities_json.getJSONObject("pageInfo").getString("pageName");

            JSONArray hash_tag_array_json = null;
            try {
                hash_tag_array_json = entities_json.getJSONArray("hashtags");

                for (int i = 0; i < hash_tag_array_json.length(); i++)
                {
                    String hash_text = hash_tag_array_json.getJSONObject(i).getString("text");
                    //System.out.println("Hashtags:" + hash_text);
                    record.addHashtag(hash_text);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray uls_array_json = null;
            try {
                uls_array_json = entities_json.getJSONArray("urls");
                for (int i = 0; i < uls_array_json.length(); i++)
                {
                    String expanded_url = uls_array_json.getJSONObject(i).getString("expanded_url");
                    String url = uls_array_json.getJSONObject(i).getString("url");

                    record.addUrl(url,expanded_url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONArray media_array_json = null;
            try {
                media_array_json = entities_json.getJSONArray("media");
                for (int i = 0; i < media_array_json.length(); i++)
                {
                    String display_url = media_array_json.getJSONObject(i).getString("display_url");
                    String expanded_url = media_array_json.getJSONObject(i).getString("expanded_url");
                    String media_url = media_array_json.getJSONObject(i).getString("media_url");
                    record.addUrl(display_url,expanded_url);
                    //System.out.println("media_url: " + media_url);

                }
            } catch (JSONException e) {
                //System.out.println("NO media");
                //e.printStackTrace();
            }

            JSONArray user_mentions_array_json = null;
            try {
                user_mentions_array_json = entities_json.getJSONArray("user_mentions");
                for (int i = 0; i < user_mentions_array_json.length(); i++)
                {
                    String id = user_mentions_array_json.getJSONObject(i).getString("id_str");
                    String name = user_mentions_array_json.getJSONObject(i).getString("name");
                    record.addMentions(name,id);
                }
            } catch (JSONException e) {
                //System.out.println("NO mentions");
                //e.printStackTrace();
            }
            DBObject retweet_object = (DBObject)next_object.get("retweeted_status");
            if ( retweet_object != null){
                System.out.println("Retweeted" );
                record.setRetwitte((String)retweet_object.get("id_str"));
            }

            connection.insertRecord(record);
            System.out.println("================================");
        }

    }



    String timestampConverter(long timestamp){
        DateTime current_time = new DateTime(timestamp, DateTimeZone.forTimeZone(java.util.TimeZone.getTimeZone("GR/Central")));
        DateTimeFormatter parser1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return  parser1.print(current_time);

    }

}
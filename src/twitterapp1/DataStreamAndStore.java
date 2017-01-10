package twitterapp1;


import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
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
               
               DBCursor cursor = collection.find();               
               while(cursor.hasNext()) {
                    System.out.println(cursor.next());
               }
            }
        }, 2, TimeUnit.MINUTES);
        
    }
    
    public void tokenizeAndStoreData(DBCollection collection, DBCollection tokenizedCollection) {
        DBObject tokens = new BasicDBObject("_id","tokens");
                                  /*.append("hashtags", "n" )
                                    .append("mentios", null)
                                    .append("URLs", null)
                                    .append("retweets", null)
                                    .append("connections", new BasicDBObject("user",null)
                                                                     .append("timestamp",null)
                                                                     .append("hashtags", null)
                                                                     .append("mentios", null)
                                                                     .append("URLs", null)
                                                                     .append("retweets", null));*/
        
        tokenizedCollection.insert(tokens);
        //tokenizedCollection.c
        
        
        
        
        DBCursor cursor = collection.find();               
               while(cursor.hasNext()) {                    
                    DBObject c = cursor.next();
                    String text = c.get("text").toString();
                    //System.out.println(text);
                    String userScreenName = ((BasicDBObject) c.get("user")).get("screen_name").toString();
                    String timestamp = c.get("timestamp_ms").toString();
                    //System.out.println("-->"+userScreenName);
                    
                    DBObject modifiedObject =new BasicDBObject();
                    DBObject searchObject =new BasicDBObject("_id", "tokens");
                    searchObject.put("_id", "tokens");
                    
                    
                    StringTokenizer st = new StringTokenizer(text);
                    while (st.hasMoreTokens()) {
                        String temp = st.nextToken();
                        if(temp.startsWith("#")) {
                            //System.out.println(temp); 
                            modifiedObject.put("$push", new BasicDBObject().append("hashtags", temp));
                            tokenizedCollection.update(searchObject, modifiedObject);
                            
                            modifiedObject.put("$push", new BasicDBObject().append("connections", 
                                                                    new BasicDBObject("user",userScreenName)
                                                                        .append("timestamp",timestamp)
                                                                        .append("hashtags", temp)));
                            tokenizedCollection.update(searchObject, modifiedObject);
                        } 
                        else if(temp.startsWith("@")) {
                            //System.out.println(temp);
                            modifiedObject.put("$push", new BasicDBObject().append("mentions", temp));
                            tokenizedCollection.update(searchObject, modifiedObject);
                            
                            modifiedObject.put("$push", new BasicDBObject().append("connections", 
                                                                    new BasicDBObject("user",userScreenName)
                                                                        .append("timestamp",timestamp)
                                                                        .append("mentions", temp)));
                            tokenizedCollection.update(searchObject, modifiedObject);
                        }
                        else if(temp.startsWith("https://")) {
                            //System.out.println(temp);
                            modifiedObject.put("$push", new BasicDBObject().append("URLs", temp));
                            tokenizedCollection.update(searchObject, modifiedObject);
                            
                            modifiedObject.put("$push", new BasicDBObject().append("connections", 
                                                                    new BasicDBObject("user",userScreenName)
                                                                        .append("timestamp",timestamp)
                                                                        .append("URLs", temp)));
                            tokenizedCollection.update(searchObject, modifiedObject);
                        }
                        
                        //tokenizedCollection.update(tokens, push);
                    }
                    if(c.get("retweeted_status") != null) {
                        String rtId = ((BasicDBObject) c.get("retweeted_status")).get("id_str").toString();
                        //System.out.println(rtId);
                        modifiedObject.put("$push", new BasicDBObject().append("retweets", rtId));
                        tokenizedCollection.update(searchObject, modifiedObject);
                            
                        modifiedObject.put("$push", new BasicDBObject().append("connections", 
                                                                new BasicDBObject("user",userScreenName)
                                                                        .append("timestamp",timestamp)
                                                                        .append("retweets", rtId)));
                        tokenizedCollection.update(searchObject, modifiedObject);
                    }
                    
               }
               
    }
    
}

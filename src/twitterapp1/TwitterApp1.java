/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitterapp1;


import com.mongodb.BasicDBObject;
import java.util.List;
import twitter4j.*;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

import java.io.IOException;


public class TwitterApp1 {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws TwitterException, IOException {
        //initialization of mongodb database                
        Mongo mongo = new Mongo("localhost"); // connection with Mongodb server           
        
        DB database = mongo.getDB("tweetsDataBase");// connection with database
        System.out.println("Connect to database successfully");            
        
        final DBCollection collection = database.getCollection("tweetsGrigoropoulos");//creating a collection
        //collection.remove(new BasicDBObject());
            
        
        DataStreamAndStore sas = new DataStreamAndStore();
        //sas.streamAndStore("#grigoropoulos", collection);   
        
        sas.analyzeData(collection);
    }

    
}

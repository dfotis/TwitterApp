/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.mongodb.BasicDBObject;
import twitter4j.*;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

import java.io.IOException;


public class TwitterApp1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws TwitterException, IOException, JSONException {
        //initialization of mongodb database
        System.out.println("EEEEEEEEEEEEEEEEEEEEEEE");
        Mongo mongo = new Mongo("localhost"); // connection with Mongodb server

        DB database = mongo.getDB("tweets_database");// connection with database
        if(database != null){
            System.out.println("Connect to database successfully ");
            System.out.println("Connect to database successfully ");

        }


        final DBCollection collection = database.getCollection("new_collection");//creating a collection

        DataStreamAndStore sas = new DataStreamAndStore();
        //sas.streamAndStore("resist", collection);

        sas.analyzeData(collection);

        DatabaseConnection connection = new DatabaseConnection();
        connection.getAllUsers();



    }


}
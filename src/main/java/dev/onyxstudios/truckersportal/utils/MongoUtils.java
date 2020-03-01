package dev.onyxstudios.truckersportal.utils;

import com.mongodb.MongoClientURI;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

public class MongoUtils {

    private MongoClient mongoClient;
    private String databaseName;

    /**
     * Note: Constructor to instantiate the class
     * @param connectionURL - The URL for MongoDB to connect to
     * @param database - The name of the database that will be used
     */
    public MongoUtils(String connectionURL, String database) {
        this.mongoClient = new MongoClient(new MongoClientURI(connectionURL));
        this.databaseName = database;
    }

    /**
     * Note: Find a document and it's data from a Collection (Table)
     * @param table - The table to search
     * @param filter - The document to use as a filter
     * @return - The found document, null will be used if none are found matching the filter
     */
    public Document getDocument(String table, Document filter) {
        return this.getDatabase().getCollection(table).find(filter).first();
    }

    /**
     * Note: Get the first available document in a Collection (Table)
     * @param table - The table to search
     * @return - The first document found, null will be passed if none are available
     */
    public Document getFirstDocument(String table) {
        return this.getDatabase().getCollection(table).find().first();
    }

    /**
     * Note: Delete a document and it's data from a collection
     * @param table
     * @param dataSelected
     */
    public void deleteDocument(String table, Document dataSelected) {
        this.getDatabase().getCollection(table).deleteOne(dataSelected);
    }

    /**
     * Note: Update the data selected in a table
     * @param table - The Collection to work in
     * @param original - The original data that is written
     * @param updated - The new data we wish to override the original data with
     */
    public void updateDocument(String table, Document original, Document updated) {
        this.getDatabase().getCollection(table).findOneAndReplace(original, updated);
    }

    /**
     * Note: Safer to use than looping through MongoCollection<Document>#find()
     * Prevents any cursor leakage if done incorrectly with MongoCollection<Document>#find()
     * @param collectionName - The Collection that the data will be retrieved from
     * @return - The data iterator from the Colletion
     */
    public MongoCursor<Document> getTableData(String collectionName) {
        return this.getDatabase().getCollection(collectionName).find().iterator();
    }

    /**
     * Note: Insert a Document and data into a collection
     * @param table - The table that the data will be inserted into
     * @param document - The Document data that will be inserted into the collection
     */
    public void insertDocument(String table, Document document) {
        this.getDatabase().getCollection(table).insertOne(document);
    }

    /**
     * Note: Insert multiple documents and data into a collection
     * @param table - The table that the data will be inserted into
     * @param documents - The list of documents and data that will be inserted into the collections
     */
    public void insertDocuments(String table, List<Document> documents) {
        this.getDatabase().getCollection(table).insertMany(documents);
    }

    /**
     * Note: Insert multiple documents and data into a collection
     * @param table - The table that the data will be inserted into
     * @param documents - The list of documents and data that will be inserted into the collections
     */
    public void insertDocuments(String table, Document[] documents) {
        this.getDatabase().getCollection(table).insertMany(Arrays.asList(documents));
    }

    /**
     * Note: Grab a Collection (Table), from the database
     * @param collectionName - The name of the Collection (Table), to grab and read from
     * @return
     */
    public MongoCollection<Document> getTable(String collectionName) {
        return this.getDatabase().getCollection(collectionName);
    }

    /**
     * Note: Create a Collection (Table) in the database
     * @param collectionName - The name of the Collection to be created
     */
    public void createMongoCollection(String collectionName) {
        this.getDatabase().createCollection(collectionName);
    }

    /**
     * Note: Deletes a Collection (Table) in the database
     * @param collectionName - The name of the Collection to be deleted
     */
    public void deleteMongoCollection(String collectionName) {
        this.getDatabase().getCollection(collectionName).drop();
    }

    /**
     * Note: Checks if a collection exists since the method had been removed from MongoDB
     * @param collectionName - The name of the Collection (Table) to search for
     * @return - True if it exists, False if it does not
     */
    public boolean collectionExists(String collectionName) {
        MongoIterable<String> collectionNames = getDatabase().listCollectionNames();
        for (final String name : collectionNames) {
            if (name.equalsIgnoreCase(collectionName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Note: Grab the database from it's name
     * @return - The database that has been instantiated
     */
    public MongoDatabase getDatabase() {
        return this.getMongoClient().getDatabase(databaseName);
    }

    /**
     * Note: Grab the MongoClient private field above
     * @return - The connection to the client
     */
    public MongoClient getMongoClient() {
        return mongoClient;
    }
}

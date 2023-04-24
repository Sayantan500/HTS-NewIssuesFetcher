package helpdesk_ticketing_system.hts_new_issues_fetcher;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MongoDb {
    private final MongoCollection<Document> mongoCollection;
    private final ObjectMapper objectMapper;

    public MongoDb() {
        String connectionUri = System.getenv("mongodb_connection_uri");
        String username = System.getenv("mongodb_username");
        String password = System.getenv("mongodb_password");
        String database = System.getenv("mongodb_database");
        String collection = System.getenv("mongodb_collection");

        String connectionString = String.format(connectionUri, username, password);
        MongoClient mongoClient = MongoClients.create(
                MongoClientSettings.builder()
                        .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
                        .applyConnectionString(new ConnectionString(connectionString))
                        .build()
        );

        mongoCollection = mongoClient.getDatabase(database).getCollection(collection);
        objectMapper = new ObjectMapper();
    }
    List<Issue> getIssues(Long postedOnStartRange, Long postedOnEndRange, Integer limit, Context context)
    {
        List<Issue> records = new LinkedList<>();
        List<Bson> queryFiltersList = new ArrayList<>();

        if(postedOnEndRange==null || postedOnEndRange.equals(postedOnStartRange))
            queryFiltersList.add(Filters.gt("posted_on",postedOnStartRange));
        else{
            queryFiltersList.add(Filters.gte("posted_on",postedOnStartRange));
            queryFiltersList.add(Filters.lte("posted_on",postedOnEndRange));
        }

        List<String> fieldsToExclude = new ArrayList<>();
        fieldsToExclude.add("ticket_id");
        fieldsToExclude.add("status");

        Bson queryFilter = Filters.and(queryFiltersList);
        Bson projectionConfig = Projections.exclude(fieldsToExclude);

        FindIterable<Document> findIterable =
                mongoCollection.find(queryFilter).projection(projectionConfig).limit(limit);

        try (MongoCursor<Document> cursor = findIterable.cursor()) {
            cursor.forEachRemaining(document -> {
                try {
                    records.add(objectMapper.readValue(document.toJson(), Issue.class));
                } catch (JsonProcessingException e) {
                    context.getLogger().log(
                            "Exception Class : " + e.getClass().getName() + "\tMessage : " + e.getMessage() + "\n"
                    );
                    throw new RuntimeException(e.getMessage());
                }
            });
        } catch (Exception e){
            context.getLogger().log(
                    "Exception Class : " + e.getClass().getName() + "\tMessage : " + e.getMessage() + "\n"
            );
            throw e;
        }
        return records;
    }
}

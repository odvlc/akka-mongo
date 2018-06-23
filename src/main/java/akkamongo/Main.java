package akkamongo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.DeadLetterActorRef;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import akkamongo.actors.CollectorActor;
import akkamongo.actors.StarterActor;
import akkamongo.actors.StopperActor;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static akka.japi.Util.classTag;

public class Main {

    private final static int NUMBER_OF_DOCUMENTS = 1000;

    public static void main(String[] args) throws Exception {
        // Connect to MongoDB instance running on localhost
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("db");

        populateData(database);
        queryDataByActors(database);

        mongoClient.close();
    }

    private static void populateData(MongoDatabase database) {
        // Access database

        // Access collection
        MongoCollection<Document> collection = database.getCollection("random-people2");

        // Insert random data
        List<Document> documents = new ArrayList<>();
        for (int i = 1; i <= NUMBER_OF_DOCUMENTS; i++) {
            documents.add(new Document("name", "name-" + i)
                    .append("age", 20 + i % 10)
                    .append("height", 100 + i % 50)
                    .append("grade", i % 5)
                    .append("marker", "marker")//trick to get all one by one
                    .append("gender", i % 3 == 0 ? "Male" : "Female"));
        }

        collection.insertMany(documents);

        // Query
        collection = database.getCollection("random-people2");
        List<Document> results = collection.find().into(new ArrayList<>());

        System.out.println(results.size());
    }

    private static void queryDataByActors(MongoDatabase database) throws Exception {
        ActorSystem actorSystem = ActorSystem.create("mongo-system");

        ActorRef collectorActor = actorSystem.actorOf(Props.create(CollectorActor.class), "collector-actor");
        ActorRef starterActor = actorSystem.actorOf(Props.create(StarterActor.class, database), "starter-actor");
        ActorRef stopperActor = actorSystem.actorOf(Props.create(StopperActor.class), "stopper-actor");

        starterActor.tell(new Messages.StartSystem(), DeadLetterActorRef.noSender());

        //wait for some time before reading result
        Thread.currentThread().sleep(1000 * 10);

        Patterns.ask(stopperActor, new Messages.StopSystem(), new Timeout(Duration.create(1, TimeUnit.SECONDS)));

        Messages.AllResult result = Await.result(Patterns.ask(collectorActor, new Messages.PrintResult(),
                new Timeout(Duration.create(1, TimeUnit.SECONDS))).mapTo(classTag(Messages.AllResult.class)),
                Duration.create(1, TimeUnit.SECONDS));
        System.out.println("Got result: " + result.getJson());
        actorSystem.terminate();
    }
}

package akkamongo.actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorSelection;
import akkamongo.Constants;
import akkamongo.Messages;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class LooperActor extends AbstractLoggingActor {

    MongoDatabase database;

    public LooperActor(MongoDatabase database) {
        this.database = database;
    }

    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.StartLoop.class, startLoop -> {
                    log().info("loop-started");

                    Document document = getOne();
                    //this loop here is not good because it blocks the actor from receiving any other
                    //message when while loop is running
                    while (document != null) {
                        ActorSelection selection = getContext().actorSelection("/user/collector-actor");
                        Map<String, Object> result = new HashMap<>();
                        result.put("name", document.get("name"));
                        result.put("age", document.get("age"));
                        selection.tell(new Messages.OneResult(result), getSelf());
                        document = getOne();
                    }
                })
                .match(Messages.StopLoop.class, stopLoop -> {
                    log().info("loop-stopped");
                    getContext().stop(self());
                })
                .build();
    }

    private Document getOne() {
        MongoCollection<Document> collection = database.getCollection(Constants.COLLECTION_NAME);
        //using marker field as a hack to get one record easily
        //also here deleting the found record, which is good for testing but not in production
        return collection.findOneAndDelete(Filters.eq("marker", "marker"));
    }
}

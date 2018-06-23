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
                .match(Messages.StartLoop.class, s -> {
                    log().info("loop-started");

                    Document document = getOne();
                    while (document != null) {
                        ActorSelection selection = getContext().actorSelection("/user/collector-actor");
                        Map<String, Object> result = new HashMap<>();
                        result.put("name", document.get("name"));
                        result.put("age", document.get("age"));
                        selection.tell(new Messages.OneResult(result), getSelf());
                        document = getOne();
                    }

                })
                .match(Messages.StopLoop.class, s -> {
                    log().info("loop-stopped");
                    getContext().stop(self());
                })
                .build();
    }

    private Document getOne() {
        MongoCollection<Document> collection = database.getCollection(Constants.COLLECTION_NAME);
        return collection.findOneAndDelete(Filters.eq("marker", "marker"));
    }
}

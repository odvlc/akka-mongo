package akkamongo.actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akkamongo.Messages;
import com.mongodb.client.MongoDatabase;

public class StarterActor extends AbstractLoggingActor {

    MongoDatabase database;
    private final static int NUMBER_OF_LOOPER_ACTORS = 10;

    public StarterActor(MongoDatabase database) {
        this.database = database;
    }

    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.StartSystem.class, s -> {
                    log().info("started");

                    for (int i = 1; i <= NUMBER_OF_LOOPER_ACTORS; i++) {
                        ActorRef looperActor = getContext().actorOf(Props.create(LooperActor.class, database), "looper-actor" + i);
                        looperActor.tell(new Messages.StartLoop(), self());
                    }
                })
                .build();
    }
}

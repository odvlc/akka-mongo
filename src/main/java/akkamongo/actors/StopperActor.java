package akkamongo.actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorSelection;
import akkamongo.Messages;

public class StopperActor extends AbstractLoggingActor {

    public Receive createReceive() {

        return receiveBuilder()
                .match(Messages.StopSystem.class, s -> {
                    log().info("stopped");
                    ActorSelection selection = getContext().actorSelection("/user/starter-actor/*");
                    selection.tell(new Messages.StopLoop(), getSelf());
                })
                .build();
    }
}

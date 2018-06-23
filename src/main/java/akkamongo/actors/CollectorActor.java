package akkamongo.actors;

import akka.actor.AbstractLoggingActor;
import akkamongo.Messages;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectorActor extends AbstractLoggingActor {

    List<Map<String, Object>> list = new ArrayList<>();

    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.OneResult.class, oneResult -> {
                    log().info(getClass() + " one result received");
                    list.add(oneResult.getResult());
                })
                .match(Messages.PrintResult.class, printResult -> {
                    log().info("printing");
                    String json = new Gson().toJson(list);
                    sender().tell(new Messages.AllResult(json), self());
                })
                .build();
    }

}

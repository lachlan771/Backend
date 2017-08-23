package actors;

import java.util.Optional;

import akka.actor.AbstractActor;
import akka.actor.AbstractActor.Receive;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by burnish on 30/07/17.
 */
public class Ranker extends AbstractActor{
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public static Props props() {
        return Props.create(Ranker.class);
    }



    @Override
    public void preStart() {
        log.info("Device actor {}-{} started");
    }

    @Override
    public void postStop() {
        log.info("Device actor {}-{} stopped");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, r -> {
                    log.info("Recorded temperature reading {} with {}");

                    getSender().tell(r+"2", getSelf());
                    System.out.println(r);
                })
                .build();
    }

}


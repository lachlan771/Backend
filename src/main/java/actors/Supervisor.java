package actors;
import akka.actor.AbstractActor;
import akka.actor.ActorLogging;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
/**
 * Created by burnish on 30/07/17.
 */
public class Supervisor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    //Creation of the children actors
    final ActorRef QAGen  = getContext().actorOf(Props.create(QAGen.class), "QAGen-Actor");
    final ActorRef ranker = getContext().actorOf(Props.create(Ranker.class), "Ranker-Actor");
    final ActorRef writer = getContext().actorOf(Props.create(Writer.class), "Writer-Actor");




    public static Props props() {
        return Props.create(Supervisor.class);
    }

    @Override
    public void preStart() {
        log.info("IoT Application started");
    }

    @Override
    public void postStop() {
        log.info("IoT Application stopped");
    }

    // No need to handle any messages
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .build();
    }
}

package ActorSystems.QAGen;

import akka.actor.AbstractActor;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * Created by burnish on 9/08/17.
 */
public class AGenSupervisor extends AbstractLoggingActor{
    //incoming protocol
    private static class SentenceQs{
        String sentence;
        String[] questions;
    }
    //outgoing protocol
    private static class SentenceQsRef{
        String sentence;
        String[] questions;
        ActorRef destination;

        public SentenceQsRef(String sentence, String[] questions, ActorRef destination) {
            this.sentence = sentence;
            this.questions = questions;
            this.destination = destination;
        }
    }
    //"It is always preferable to communicate with other actors using their ActorRef instead of relying upon
    // ActorSelection" http://doc.akka.io/docs/akka/current/java/actors.html (messages and immutability)
    //Holds the val of AGenSupervisor,the destination after it gets back from the cloud.
    // Needs the actor ref of toQAGenStreamSupervisor
    private final ActorRef destination;
    public AGenSupervisor(ActorRef destination) {
        this.destination= destination;
    }
    public static Props props(ActorRef destination) {
        return Props.create(AGenSupervisor.class,() -> new AGenSupervisor(destination));
    }



    //Logging
    @Override
    public void preStart() {
        log().info("AGen-Supervisor Started");
    }

    @Override
    public void postStop() {
        log().info("AGen-Supervisor Stopped");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SentenceQs.class,this::onMessage)
                .build();
    }

    private void onMessage(SentenceQs p) {
        log().info("sentence : "+  p.sentence  +" made it to AGenSupervisor");
        newAGenActor(p);
    }

    private void newAGenActor(SentenceQs p) {
        final ActorRef aGenActor = getContext().actorOf(Props.create(AGenActor.class));
        aGenActor.tell(new SentenceQsRef(p.sentence,p.questions,destination),self());

    }
}

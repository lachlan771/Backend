package ActorSystems.QAGen;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * Created by burnish on 9/08/17.
 */
public class ToQAGenStreamSupervisor extends AbstractLoggingActor{
    //Incoming protocol
    private static class SentenceQsAs {
        String sentence;
        String[] questions;
        String[] answers;
    }
    //Outgoing protocol
    public static class ProcessedSentence{
        String sentence;
        String userId;
        String deckId;
        String[] answers;
        String[] questions;

        public ProcessedSentence(String sentence, String userId, String deckId, String[] answers, String[] questions) {
            this.sentence = sentence;
            this.userId = userId;
            this.deckId = deckId;
            this.answers = answers;
            this.questions = questions;
        }
    }
    //private final ActorRef destination;
    private final String userId;
    private final String deckId;
    private ToQAGenStreamSupervisor(String userId,String deckId) {
        //this.destination= destination;
        this.userId = userId;
        this.deckId = deckId;
    }
    //"It is always preferable to communicate with other actors using their ActorRef instead of relying upon
    // ActorSelection" http://doc.akka.io/docs/akka/current/java/actors.html (messages and immutability)
    //Holds the val of AGenSupervisor,the destination after it gets back from the cloud.

    public static Props props(String userId,String deckId) {
        return Props.create(ToQAGenStreamSupervisor.class,()-> new ToQAGenStreamSupervisor(userId,deckId));
    }
    //Logging
    @Override
    public void preStart() {
        log().info("To-QAGen-Supervisor Started");
    }

    @Override
    public void postStop() {
        log().info("To-QAGen-Supervisor Stopped");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SentenceQsAs.class,this::onMessage)
                .build();
    }

    private  void onMessage(SentenceQsAs p) {
        log().info("Messaged recieved: " + p.sentence +" made it to toQAGenStreanSupervisor");
        newToQAGenStreamActor(p);
    }

    private void newToQAGenStreamActor(SentenceQsAs p) {
        final ActorRef toQAGenStreamActor = getContext().actorOf(Props.create(ToQAGenStreamActor.class));
        toQAGenStreamActor.tell(new ProcessedSentence(p.sentence,userId,deckId,p.answers,p.questions),self());
    }
}

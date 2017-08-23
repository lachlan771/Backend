package ActorSystems.QAGen;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;

/**
 * Created by burnish on 19/08/17.
 */
public class ToQAGenStreamActor extends AbstractLoggingActor {


    //Incoming and outgoing Protocol
    public static class ProcessedSentence {
        String sentence;
        String userId;
        String deckId;
        String[] answers;
        String[] questions;
    }

    //Handles the sending of the message to the cloud


    public static Props props(){
        return Props.create(AGenActor.class);
    }
    //Logging
    @Override
    public void preStart() {
        log().info("AGen-Actor Started");
    }

    @Override
    public void postStop() {
        log().info("AGen-Actor Stopped");
    }
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ProcessedSentence.class,this::onMessage)
                .build();
    }

    private  void onMessage(ProcessedSentence p) {
        //Dont know what to do yet
    }


}


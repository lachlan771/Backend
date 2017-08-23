package ActorSystems.QAGen;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class AGenActor extends AbstractLoggingActor {


    //Incoming Protocol
    private static class SentenceQsRef{
        String sentence;
        String[] questions;
        ActorRef destination;
    }
    //Outgoing Protocol
    private static class SentenceQsAs{
        String sentence;
        String[] questions;
        String[] Answers;

        public SentenceQsAs(String sentence, String[] questions, String[] answers) {
            this.sentence = sentence;
            this.questions = questions;
            Answers = answers;
        }
    }
    //Handles the sending of the message to the cloud
    private void sendCloud(SentenceQsRef actorData) {
        //After everything works will make this usable to the cloud. In testing a arbitrary list of Answers is generated
        String[] testAs = {"answer for question 1","answer for question 2","answer for question 3"};
        actorData.destination.tell((new SentenceQsAs(actorData.sentence,actorData.questions,testAs)),self());
    }

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
                .match(SentenceQsRef.class,this::sendCloud)
                .build();
    }



}


package ActorSystems.QAGen;

import akka.actor.AbstractActor;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * Created by burnish on 9/08/17.
 */
public class QGenActor extends AbstractLoggingActor{


    //Incoming Protocol
    private static class SentenceRef{
        String sentence;
        ActorRef aGenSupervisor;
    }
    //Outgoing Protocol
    private static class SentenceQs{
        String sentence;
        String[] questions;

        public SentenceQs(String sentence, String[] questions) {
            this.sentence = sentence;
            this.questions = questions;
        }
    }
    //When the actor is created send the message to the cloud. Not having any local variables as that would change the state of the actor;
    public QGenActor(SentenceRef actorData) {
        //Calling the send to cloud method
        sendCloud(actorData);
    }
    //Handles the sending of the message to the cloud
    private void sendCloud(SentenceRef actorData) {
        //After everything works will make this usable to the cloud. In testing a arbitrary list of questions is generated
        String[] testQs = {"question generated 1","question generated 2","question generated 3"};
        actorData.aGenSupervisor.tell((new SentenceQs(actorData.sentence,testQs)),self());
    }

    public static Props props(){
        return Props.create(QGenActor.class);
    }
    //Logging
    @Override
    public void preStart() {
        log().info("QGen-Actor Started");
    }

    @Override
    public void postStop() {
        log().info("QGen-Actor Stopped");
    }
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SentenceRef.class,this::sendCloud)
                .build();
    }



}

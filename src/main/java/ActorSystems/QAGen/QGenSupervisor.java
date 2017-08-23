package ActorSystems.QAGen;

import akka.actor.AbstractActor;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * Created by burnish on 9/08/17.
 */
public class QGenSupervisor extends AbstractLoggingActor {
    //Incoming protocol
    private static class SBatch{
        String[] sentences;
    }
    //Outgoing protocol
    private static class SentenceRef{
        String sentence;
        ActorRef aGenSupervisor;

        public SentenceRef(String sentence, ActorRef aGenSupervisor) {
            this.sentence = sentence;
            this.aGenSupervisor = aGenSupervisor;
        }
    }

    //Props. Needs the actorRef to AGenSupervisor
    private final ActorRef aGenSupervisor;
    public QGenSupervisor(ActorRef aGenSupervisor) {
        this.aGenSupervisor = aGenSupervisor;
    }
    public static Props props(ActorRef aGenSupervisor){
        return Props.create(QGenSupervisor.class, () -> new QGenSupervisor(aGenSupervisor));
    }

    //Logging
    @Override
    public void preStart() {
        log().info("QGen-Supervisor Started");
    }

    @Override
    public void postStop() {
        log().info("QGen-Supervisor Stopped");
    }



    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String[].class,this::onBatch)
                .build();
    }

    private  void onBatch(String[] sBatch) {
        log().info("Sentence recieved " + sBatch[0]);
        for(int i =0;i<sBatch.length;i++){
            newQGenActor(sBatch[i]);


        }
    }
    //Method that starts up a QGenActor instance and gives it a sentence and the reference to the AGenSupervisor
    private void newQGenActor(String sentence){
        final ActorRef QGenActor = getContext().actorOf(Props.create(QGenActor.class));
        QGenActor.tell(new SentenceRef(sentence,aGenSupervisor),self());



    }


}

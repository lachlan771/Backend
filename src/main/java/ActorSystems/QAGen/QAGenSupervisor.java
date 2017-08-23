package ActorSystems.QAGen;


import ActorSystems.MessageTypes.SBatchUserInfo;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;


/**
 * Created by burnish on 9/08/17.
 */
public class QAGenSupervisor extends AbstractLoggingActor {
    //incoming messages protocol
    //private static class SBatchUserInfo {
      //  String[] sentences;
        //String userEmail;
        //String deckName;
   // }
    //outgoing messages protocol

     ActorRef toQAStreamS;
     ActorRef AGenS=null;
     ActorRef QGenS=null;
    public QAGenSupervisor(String userEmail,String deckName) {
        //toQAStreamS needs user info

        this.toQAStreamS = getContext().actorOf(ToQAGenStreamSupervisor.props(userEmail,deckName), "ToQAGenStream-Supervisor");
        //AGens needs actor ref of toQAStreamS

        this.AGenS = getContext().actorOf(AGenSupervisor.props(toQAStreamS), "AGen-Supervisor");
        //QGenS needs actor ref of AGenS

        this.QGenS = getContext().actorOf(QGenSupervisor.props(AGenS), "QGen-Supervisor");

    }

    public static Props props(String userEmail,String deckName) {
        return Props.create(QAGenSupervisor.class,()->new QAGenSupervisor(userEmail,deckName));
    }

    @Override
    public void preStart() {
        log().info("QAGen-Supervisor Started");
    }

    @Override
    public void postStop() {
        log().info("QAGen-Supervisor Stopped");
    }
    @Override
    public Receive createReceive() {
        //Two options are available to where the children can send their messaged. They can either route through this actor or be given direct acces to each actor supervisor
        log().info("the message is received");
        return receiveBuilder()
                .match(SBatchUserInfo.class,this::onBatch)
                .build();

    }

    private void onBatch(SBatchUserInfo sBatch) {
        //Splitting the Sentence Batch into single sentences and sending that sentence to the newly created actor
        QGenS.tell(sBatch.sentences,self());



    }




}




































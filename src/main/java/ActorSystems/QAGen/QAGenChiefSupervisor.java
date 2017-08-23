package ActorSystems.QAGen;

import ActorSystems.MessageTypes.SBatchUserInfo;
import actors.Supervisor;
import akka.actor.AbstractActor;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * Created by burnish on 9/08/17.
 */
public class QAGenChiefSupervisor extends AbstractLoggingActor {
    //Protocol
    //private static class SBatchUserInfo {
        //String[] sentences;
       // String userEmail;
       // String deckName;
   // }
    public static Props props() {
        return Props.create(QAGenChiefSupervisor.class);
    }

    @Override
    public void preStart() {
        log().info("QAGen-Chief-Supervisor Started");
    }

    @Override
    public void postStop() {
        log().info("QAGen-Chief-Supervisor Stopped");
    }
    @Override
    public Receive createReceive() {
        log().info("sends the message properly to QAGenChief. Does the mess match eachother :");
        return receiveBuilder()
                .match(SBatchUserInfo.class,this::onBatch)
                .build();

    }

    private  void onBatch(SBatchUserInfo sBatchUserInfo) {
        log().info("makes ito to onBatch in QAGenChiefSupervisor");
        //Creating the QABGenSupervisor to control this job
        final ActorRef QAGenS = getContext().actorOf(QAGenSupervisor.props(sBatchUserInfo.userId,sBatchUserInfo.deckId));
        //Giving the sentence to QAGens
        QAGenS.tell(sBatchUserInfo,getSelf());

    }


}

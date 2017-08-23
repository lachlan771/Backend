package ActorSystems;

import ActorSystems.MessageTypes.ProcessedSentence;
import ActorSystems.MessageTypes.SBatchUserInfo;
import ActorSystems.QAGen.QAGenChiefSupervisor;
import ActorSystems.QAGen.QAGenSupervisor;
import ActorSystems.QAGen.QGenSupervisor;
import actors.QAGen;
import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.*;
import akka.stream.*;
import sbt.complete.Not;


import java.util.Arrays;
import java.util.concurrent.CompletionStage;

import static akka.pattern.PatternsCS.ask;

/**
 * Created by burnish on 9/08/17.
 */
public class Test {
    public static void main(String[] args){
       //Creating the test data of 3 dummy messages in the form of SBatchUserInfo
        SBatchUserInfo[] testData = new SBatchUserInfo[1];
        String[] testSentences1 = {"Test sentence A1","Test sentence A2","Test sentence A3"};
        String[] testSentences2 = {"Test sentence B1","Test sentence B2","Test sentence B3"};
        String[] testSentences3 = {"Test sentence C1","Test sentence C2","Test sentence C3"};
        testData[0] = new SBatchUserInfo(testSentences1,"testEmailA@gmail.com","testDeckA");
       // testData[1] = new SBatchUserInfo(testSentences2,"testEmailB@gmail.com","testDeckB");
        //testData[2] = new SBatchUserInfo(testSentences3,"testEmailC@gmail.com","testDeckC");
        //Creating of the system
        final ActorSystem QAGenSystem = ActorSystem.create("QA-Gen-System");
        final Materializer materializer = ActorMaterializer.create(QAGenSystem);


        //createing QGenSupervisor ACtor
        final ActorRef qAGenChiefSupervisor =  QAGenSystem.actorOf(QAGenChiefSupervisor.props(),"QAGen-ChiefSupervisor");

        //Creating the stream structure


        //Third is the Picker Stream
        final Flow<String,String,NotUsed> PickerStream =
                Flow.of(String.class)
                        .map(elem -> elem + " c ")
                        .named("PickerStream");
        //Fourth is the WriterStream
        final Sink<String,NotUsed> WriterStream =
                PickerStream.to(Sink.foreach(System.out::println));
        //Second is the ranker stream.
        final Flow<String, String, NotUsed> RankerStream =
                Flow.of(String.class) // an atomic processing stage
                        .map(elem -> elem +" b ") // another atomic processing stage
                        .named("RankerStream"); // wraps up the Flow, and gives it a name
                        //.via(PickerStream);

        //First is the QASream which is a source getting information form kafka.In prototyping it gets it from a predefined source
        //It is a Source made up of three parts
        //The first part is a source that consumes from a kafka stream.
            //Input:A batch of sentences with user info(email(or uuid) and deck name (or uuid))
            //Output: Same as input
        final Source<SBatchUserInfo,NotUsed> QAGenConsumerSource =
                Source.from(Arrays.asList(testData))
                        .map(elem -> elem )
                        .named("QAGen-ConsumerSource");
        //The second part sends the SBatchUserInfo to the actor system. It is a sink
            //Input: A batch of sentences with user info(email(or uuid) and deck name (or uuid))
            //Output: No output
        final Sink<SBatchUserInfo,NotUsed> QAGenSenderSink =
                //This is where the amount of QAGenSupervisors running at a time is determined. Will change this to actorRefwithAck so it has back pressure
                Sink.<SBatchUserInfo> actorRef(qAGenChiefSupervisor,true)
                .named("QAGen-SenderSink");
        //The third part recieves the messages from the actor system. It is a source
            //Input:No real input but it recieves messages from the QAActor system in the form: sentence, questions, answers, UserInfo:deckid, userid
            //Output: sentence, questions, answers, UserInfo:deckid, userid, all as strings
        final Source<ProcessedSentence,SourceQueueWithComplete<ProcessedSentence>> QAGenReceiverSource =
                //This is where the buffer size is found
                Source.queue(10,OverflowStrategy.dropTail());
        //Combine all the stages in two stages;
        //The first is putting the QAGenSenderSink and QAGenReceiverSource together
        final Flow<SBatchUserInfo,ProcessedSentence,NotUsed> QAGenNestedStream = Flow.fromSinkAndSource(QAGenSenderSink,QAGenReceiverSource)
                ;
        //The second is combining them all into the QAGenStream Source
        final Source<ProcessedSentence,NotUsed> QAGenStream = QAGenConsumerSource.via(QAGenNestedStream);

        //Testing sink to make QAGenStream runnable,
        final RunnableGraph<NotUsed> TestSink =
                QAGenStream.to(Sink.foreach(System.out::println));
        TestSink.run(materializer);
               







        final Source<String , NotUsed>  QAStream =
                Source.from(Arrays.asList("testing","testing2"))
                        .map(elem -> elem +" a ")
                        .via(RankerStream)
                        .named("QAStream");

        //Creating a runnable graph
        final RunnableGraph<NotUsed> runnableGraph = QAStream.to(WriterStream);
       // runnableGraph.run(materializer);
    }


}

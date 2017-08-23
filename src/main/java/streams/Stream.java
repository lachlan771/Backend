package streams;
import static akka.pattern.PatternsCS.ask;
import static akka.pattern.PatternsCS.pipe;
import actors.QAGen;
import actors.Ranker;
import actors.Supervisor;
import actors.Writer;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscription;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.*;
import akka.stream.javadsl.*;
import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.util.ByteString;

import java.nio.file.Paths;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

//import jdocs.AbstractJavaTest;
import akka.util.Timeout;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import scala.concurrent.duration.Duration;
/**
 * Created by burnish on 30/07/17.
 */
public class Stream {
    //The creation of the sink
    static public Sink<String, CompletionStage<IOResult>> lineSink(String filename) {
        return Flow.of(String.class)
                .map(s -> ByteString.fromString(s.toString() + "\n"))
                .toMat(FileIO.toPath(Paths.get(filename)), Keep.right());
    }




    //The creation of the flows

    public static void main(String[] argv) {

        // The materializer needs an actorsystem //
        final ActorSystem system = ActorSystem.create("QuickStart");
        final Materializer materializer = ActorMaterializer.create(system);
        //code for the reciver of kafka messages
        /*final ConsumerSettings<byte[], String> consumerSettings =
                ConsumerSettings.create(system, new ByteArrayDeserializer(), new StringDeserializer())
                        .withBootstrapServers("localhost:9092")
                        .withGroupId("group1")
                        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
*/

        //Creating the supervisor of actor system
        ActorRef supervisor = system.actorOf(Props.create(Supervisor.class), "Supervisor-Actor");
        //Creating the actors necessary for the pipeline
        ActorRef QAGen = system.actorOf(Props.create(QAGen.class), "QAGen-Actor");
        ActorRef ranker = system.actorOf(Props.create(Ranker.class), "Ranker-Actor");
        ActorRef writer = system.actorOf(Props.create(Writer.class), "Writer-Actor");
        //Timeout used in the communication with actors
        Timeout askTimeout = Timeout.apply(5, TimeUnit.SECONDS);
        //Nested Source (Input and QA)

        final Flow<String, String, NotUsed> QANestedFlow =
                Flow.of(String.class) // an atomic processing stage
                        .mapAsync(2, elem -> ask(QAGen, elem, askTimeout))
                        .map(elem -> elem +"B") // another atomic processing stage
                        .named("QANestedFlow"); // wraps up the Flow, and gives it a name




       /* Consumer.committableSource(consumerSettings, (Subscription) Subscriptions.topics("topic1"))
                .mapAsync(1, msg -> db.update((String) msg.record().value())
                        .thenApply(done -> msg))
                .mapAsync(1, msg -> msg.committableOffset().commitJavadsl())

                .runWith(Sink.ignore(), materializer);
                */





        final Source<String, NotUsed> nestedSource =
                Source.from(Arrays.asList("Origin I","Origin II","Origin III"))
                        .map(elem -> elem +"A") // an atomic processing stage
                        .named("nestedSource")// wraps the current Source and gives it a name
                        .via(QANestedFlow);


        final Flow<String, String, NotUsed> RankerNestedFlow =
                Flow.of(String.class) // an atomic processing stage
                        .mapAsync(2, elem -> ask(ranker, elem, askTimeout))
                        .map(elem -> elem +"C") // another atomic processing stage
                        .named("nestedFlow"); // wraps up the Flow, and gives it a name

        final Sink<String, NotUsed> nestedSink =
                ///RankerNestedFlow.to(Sink.actorRef(writer,true)) // wire an atomic sink to the nestedFlow

                RankerNestedFlow.to(Sink.fold("",(acc , i)->acc +i)
                        .actorRef(writer,true)
                ) // wire an atomic sink to the nestedFlow

                        .named("nestedSink"); // wrap it up


// Create a RunnableGraph
        final RunnableGraph<NotUsed> runnableGraph = nestedSource.to(nestedSink);
        runnableGraph.run(materializer);
        //final CompletionStage<Done> done =
              // runnableGraph.run(i -> System.out.println(i), materializer);


    }

}

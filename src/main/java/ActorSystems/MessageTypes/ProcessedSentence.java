package ActorSystems.MessageTypes;

/**
 * Created by burnish on 19/08/17.
 */
public class ProcessedSentence {
    String sentence;
    String userId;
    String deckId;
    String[] answers;
    String[] questions;

    public <T> int printToScreen() {
        System.out.print(sentence);
        return 0;
    }
}

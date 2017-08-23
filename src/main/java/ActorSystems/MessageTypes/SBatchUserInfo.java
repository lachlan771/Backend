package ActorSystems.MessageTypes;

/**
 * Created by burnish on 19/08/17.
 */
public class SBatchUserInfo {
    public String[] sentences;
    public String userId;
    public String deckId;

    public SBatchUserInfo(String[] sentences, String userId, String deckId) {
        this.sentences = sentences;
        this.userId = userId;
        this.deckId = deckId;
    }
}

package ee.ttu.idk0071.ajukraanid.database;


import ee.ttu.idk0071.ajukraanid.database.internal.Answers;
import ee.ttu.idk0071.ajukraanid.database.sync.Entry;
import lombok.Getter;

public class Answer extends Entry {
    // inaccessible
    private Question question;
    private Answers answer;
    // accessible
    @Getter private final Player player;
    @Getter private final String text;

    /**
     * From existing database entry.
     */
    Answer(Question question, Answers answer) {
        this.question = question;
        question.getAnswers().add(this);
        this.answer = answer;
        this.player = question.getGame().getPlayers().stream().filter(player -> player.getPlayer().getPlayer_id()
                .equals(answer.getPlayerId())).findAny().orElseThrow(() -> new RuntimeException("Player with id " +
                answer.getPlayerId() + " not found, database is broken ;(")); // TODO WTF
        this.text = question.getText();
    }

    /**
     * Completely new.
     */
    public Answer(Question question, Player player, String text) {
        this.question = question;
        question.getAnswers().add(this);
        answer = new Answers(question.getQuestion().getId(), player.getPlayer().getPlayer_id(), text);
        this.player = player;
        this.text = text;
        answer = getDatabase().getAnswersRepository().save(answer);
    }

    @Override
    protected Database getDatabase() {
        return question.getDatabase();
    }
}

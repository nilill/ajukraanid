package ee.ttu.idk0071.ajukraanid.database;


import ee.ttu.idk0071.ajukraanid.database.internal.Games;
import ee.ttu.idk0071.ajukraanid.database.sync.Entry;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Game extends Entry {
    // inaccessible
    private final Database database;
    private Games game;

    // accessible
    @Getter private final int gameCode;
    @Getter @Setter private String gameState = "Lobby"; // TODO Override setter to update database.
    @Getter @Setter private int questionNumber = 0;

    // referenced by
    @Getter private ArrayList<Player> players = new ArrayList<>();
    @Getter private ArrayList<Question> questions = new ArrayList<>();

    /**
     * From an existing database entry.
     */
    Game(Database database, Games game) {
        this.database = database;
        database.getGames().add(this);
        this.game = game;
        this.gameCode = game.getGame_code();
        this.gameState = game.getGame_state();
        this.questionNumber = game.getQuestion_number();
    }

    /**
     * Completely new.
     */
    public Game(Database database, int code) {
        this.database = database;
        database.getGames().add(this);
        this.questions.add(new Question(this, "LEMME SMASH?")); // TODO Remove
        this.questions.add(new Question(this, "LEMME SMASH?")); // TODO Remove
        this.gameCode = code;
        game = new Games(code);
        game.setGame_state(gameState);
        game.setQuestion_number(questionNumber);
        game = database.getGamesRepository().save(game);
    }

    /**
     * Allow creation of unrelated objects temporarily.
     * @deprecated TODO Remove this constructor.
     */
    public Game(int code) {
        this.database = null;
        this.gameCode = code;
        game = new Games(code);
        game.setGame_state(gameState);
        game.setQuestion_number(questionNumber);
    }

    Games getGame() {
        return game;
    }

    @Override
    protected Database getDatabase() {
        return database;
    }
}

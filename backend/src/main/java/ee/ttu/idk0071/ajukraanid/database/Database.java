package ee.ttu.idk0071.ajukraanid.database;

import ee.ttu.idk0071.ajukraanid.database.internal.*;
import ee.ttu.idk0071.ajukraanid.database.sync.Entry;
import ee.ttu.idk0071.ajukraanid.util.StringUtilities;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class Database extends Entry {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    // inaccessible internal repositories
    private final GamesRepository gamesRepository;
    private final PlayersRepository playersRepository;
    private final PlainQuestionsRepository plainQuestionsRepository;
    private final QuestionsRepository questionsRepository;
    private final AnswersRepository answersRepository;
    private final EvaluationsRepository evaluationsRepository;

    // accessible members
    @Getter private final ArrayList<Game> games = new ArrayList<>(); // all the game sessions
    @Getter private final ArrayList<PlainQuestion> plainQuestions = new ArrayList<>(); // all questions

    @Autowired
    private Database(GamesRepository gamesRepository, PlayersRepository playersRepository,
                     PlainQuestionsRepository plainQuestionsRepository, QuestionsRepository questionsRepository,
                     AnswersRepository answersRepository, EvaluationsRepository evaluationsRepository) {
        this.gamesRepository = gamesRepository;
        this.playersRepository = playersRepository;
        this.plainQuestionsRepository = plainQuestionsRepository;
        this.questionsRepository = questionsRepository;
        this.answersRepository = answersRepository;
        this.evaluationsRepository = evaluationsRepository;
        loadDatabaseEntries();
        log.info("Finished loading existing database entries");
        printDatabaseEntries();
    }

    private void loadDatabaseEntries() {
        loadGames();
        loadPlainQuestions();
    }

    private void loadGames() {
        gamesRepository.findAll()
                .forEach(game -> {
                    if (!game.getState().equals(Game.State.INACTIVE.toString())) {
                        new Game(this, game);
                    }
                });
        games.forEach(this::loadPlayers);
        games.forEach(this::loadQuestions);
    }

    private void loadPlayers(Game game) {
        playersRepository.findByGameId(game.getGame().getId())
                .forEach(player -> {
                    if (player.isValid()) {
                        new Player(game, player);
                    }
                });
    }

    private void loadQuestions(Game game) {
        questionsRepository.findByGameId(game.getGame().getId())
                .forEach(question -> new Question(game, question));
        game.getQuestions().forEach(this::loadAnswers);
        game.getQuestions().forEach(this::loadEvaluations);
    }

    private void loadAnswers(Question question) {
        answersRepository.findByQuestionId(question.getQuestion().getId())
                .forEach(answer -> new Answer(question, answer));
    }

    private void loadEvaluations(Question question) {
        evaluationsRepository.findByQuestionId(question.getQuestion().getId())
                .forEach(evaluation -> new Evaluation(question, evaluation));
    }

    private void loadPlainQuestions() {
        plainQuestionsRepository.findAll()
                .forEach(question -> new PlainQuestion(this, question));
    }

    private void printDatabaseEntries() {
        StringBuilder stringBuilder = new StringBuilder("All loaded database entries in memory:\n");
        appendTo(stringBuilder, 1);

        System.out.println(stringBuilder.toString());
    }

    public GamesRepository getGamesRepository() {
        return gamesRepository;
    }

    public PlayersRepository getPlayersRepository() {
        return playersRepository;
    }

    public PlainQuestionsRepository getPlainQuestionsRepository() {
        return plainQuestionsRepository;
    }

    public QuestionsRepository getQuestionsRepository() {
        return questionsRepository;
    }

    public AnswersRepository getAnswersRepository() {
        return answersRepository;
    }

    public EvaluationsRepository getEvaluationsRepository() {
        return evaluationsRepository;
    }

    @Override
    protected void appendTo(StringBuilder stringBuilder, int indentSize) {
        StringUtilities.addIndent(indentSize + 1, stringBuilder);
        stringBuilder.append("Plain Questions (").append(plainQuestions.size()).append(")").append("\n");
        plainQuestions.forEach(question -> question.appendTo(stringBuilder, indentSize + 2));

        StringUtilities.addIndent(indentSize + 1, stringBuilder);
        stringBuilder.append("Games (").append(games.size()).append(")").append("\n");
        games.forEach(game -> game.appendTo(stringBuilder, indentSize + 2));
    }

    @Override
    protected Database getDatabase() {
        return this;
    }
}

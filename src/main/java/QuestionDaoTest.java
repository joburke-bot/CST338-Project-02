package main.java;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Optional;

/**
 * Course: CST338 - Software Design
 * Project: Trivia and Study Application
 * Component: Slice 2 (Question Bank) - tests for QuestionDao
 *
 * runs the dao against a throwaway in memory database so the real
 * trivia.db never gets touched.
 * Author: William Delgado
 * Date: August 4, 2026
 */
public class QuestionDaoTest {

    private Connection connection;
    private QuestionDao questionDao;

    @BeforeEach
    public void setUp() throws Exception {
        // in memory db, brand new for every test. load the driver
        // the same way MainApp does
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        questionDao = new QuestionDao(connection);
    }

    @Test
    public void createGivesTheQuestionAnId() {
        Question question = new Question(0, 1, "What is a class", "A blueprint",
                "A variable", "A loop", "A file");

        Question saved = questionDao.create(question);

        assertTrue(saved.getId() > 0);
        assertEquals("What is a class", saved.getPrompt());
    }

    @Test
    public void readFindsWhatWasCreated() {
        Question saved = questionDao.create(new Question(0, 1, "What is java", "A language",
                "A drink", "An island", "A framework"));

        Optional<Question> found = questionDao.read(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("What is java", found.get().getPrompt());
        assertEquals("A language", found.get().getAnswer());
    }

    @Test
    public void readMissingIdComesBackEmpty() {
        Optional<Question> found = questionDao.read(999);

        assertTrue(found.isEmpty());
    }

    @Test
    public void readAllByUserOnlyGivesThatUsersQuestions() {
        questionDao.create(new Question(0, 1, "mine", "yes", "a", "b", "c"));
        questionDao.create(new Question(0, 1, "also mine", "yes", "a", "b", "c"));
        questionDao.create(new Question(0, 2, "someone elses", "no", "a", "b", "c"));

        List<Question> userOnes = questionDao.readAllByUser(1);

        assertEquals(2, userOnes.size());
    }

    @Test
    public void updateChangesTheRow() {
        Question saved = questionDao.create(new Question(0, 1, "old prompt", "old answer",
                "a", "b", "c"));

        Question changed = new Question(saved.getId(), 1, "new prompt", "new answer",
                "a", "b", "c");
        boolean worked = questionDao.update(changed);

        assertTrue(worked);
        assertEquals("new prompt", questionDao.read(saved.getId()).get().getPrompt());
    }

    @Test
    public void deleteRemovesTheRow() {
        Question saved = questionDao.create(new Question(0, 1, "goes away", "bye",
                "a", "b", "c"));

        boolean worked = questionDao.delete(saved.getId());

        assertTrue(worked);
        assertTrue(questionDao.read(saved.getId()).isEmpty());
    }
}

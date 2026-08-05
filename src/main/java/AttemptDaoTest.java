/**
 * CST 338
 * Project 2
 * Slice 3 (Quiz Engine) -
 * AttempDao test
 *
 * Author: Allan Orozco
 * Date August 4, 2026
 */
package main.java;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AttemptDaoTest {
    private Connection connection;
    private AttemptDao attemptDao;

    @BeforeEach
    public void setUp() throws Exception {
        Class.forName("org.sqlite.JDBC");

        connection = DriverManager.getConnection(
                "jdbc:sqlite::memory:"
        );

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");

            statement.execute(
                    "CREATE TABLE users (" +
                            "id INTEGER PRIMARY KEY" +
                            ")"
            );

            statement.execute(
                    "INSERT INTO users (id) VALUES (1)"
            );
        }

        attemptDao = new AttemptDao(connection);
    }

    @AfterEach
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void createGivesAttemptAnId() {
        Attempt attempt = new Attempt(
                0,
                1,
                8,
                10,
                "2026-08-04 21:30:00"
        );

        Attempt saved = attemptDao.create(attempt);

        assertTrue(saved.getId() > 0);
        assertEquals(8, saved.getScore());
        assertEquals(10, saved.getTotalQuestions());
    }

    @Test
    public void readFindsCreatedAttempt() {
        Attempt saved = attemptDao.create(new Attempt(
                0,
                1,
                7,
                10,
                "2026-08-04 21:30:00"
        ));

        Optional<Attempt> found =
                attemptDao.read(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(7, found.get().getScore());
        assertEquals(1, found.get().getUserId());
    }

    @Test
    public void readMissingAttemptReturnsEmpty() {
        Optional<Attempt> found =
                attemptDao.read(999);

        assertTrue(found.isEmpty());
    }

    @Test
    public void updateChangesAttemptScore() {
        Attempt saved = attemptDao.create(new Attempt(
                0,
                1,
                5,
                10,
                "2026-08-04 21:30:00"
        ));

        Attempt changed = new Attempt(
                saved.getId(),
                saved.getUserId(),
                9,
                saved.getTotalQuestions(),
                saved.getCompletedAt()
        );

        boolean updated = attemptDao.update(changed);

        assertTrue(updated);
        assertEquals(
                9,
                attemptDao.read(saved.getId())
                        .orElseThrow()
                        .getScore()
        );
    }

    @Test
    public void deleteRemovesAttempt() {
        Attempt saved = attemptDao.create(new Attempt(
                0,
                1,
                6,
                10,
                "2026-08-04 21:30:00"
        ));

        boolean deleted =
                attemptDao.delete(saved.getId());

        assertTrue(deleted);
        assertTrue(
                attemptDao.read(saved.getId()).isEmpty()
        );
    }
}

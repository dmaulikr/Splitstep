package com.genenakagaki.splitstep.exercise.data.entity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.genenakagaki.splitstep.exercise.data.ExerciseDatabase;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by gene on 9/4/17.
 */

@RunWith(AndroidJUnit4.class)
public class ReactionExerciseTest {

    private static final ReactionExercise REACTION_EXERCISE = new ReactionExercise(1, 1, 1, 1, 1, 1);

    @Before
    public void setUp() throws Exception {
        Context mContext = InstrumentationRegistry.getTargetContext();

        FlowManager.init(FlowConfig.builder(mContext)
                .addDatabaseConfig(DatabaseConfig.inMemoryBuilder(ExerciseDatabase.class)
                        .databaseName("ExerciseDatabase")
                        .build())
                .build());
    }

    @After
    public void tearDown() throws Exception {
        FlowManager.destroy();
    }

    @Test
    public void testGetReactionExercise_WithNoExerciseInserted_ShouldReturnEmptyList() {
        List<ReactionExercise> exercises = SQLite.select()
                .from(ReactionExercise.class)
                .queryList();

        assertEquals(0, exercises.size());
    }

    @Test
    public void testGetReactionExerciseAsync_WithNoExerciseInserted_ShouldReturnEmptyList() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        SQLite.select()
                .from(ReactionExercise.class)
                .async()
                .queryListResultCallback(new QueryTransaction.QueryResultListCallback<ReactionExercise>() {
                    @Override
                    public void onListQueryResult(QueryTransaction transaction, @NonNull List<ReactionExercise> tResult) {
                        assertEquals(0, tResult.size());
                        countDownLatch.countDown();
                    }
                }).execute();

        countDownLatch.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void testInsertAndGetReactionExercise_ShouldReturnInsertedExercise() {
        REACTION_EXERCISE.insert();

        List<ReactionExercise> exercises = SQLite.select()
                .from(ReactionExercise.class)
                .queryList();

        assertTrue(isExerciseEqual(exercises.get(0), REACTION_EXERCISE));
    }

    @Test
    public void testInsertAndGetReactionExerciseAsync_ShouldReturnInsertedExercise() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        FlowManager.getDatabase(ExerciseDatabase.class).beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                REACTION_EXERCISE.insert();
            }
        }).success(new Transaction.Success() {
            @Override
            public void onSuccess(@NonNull Transaction transaction) {
                SQLite.select()
                        .from(ReactionExercise.class)
                        .async()
                        .queryListResultCallback(new QueryTransaction.QueryResultListCallback<ReactionExercise>() {
                            @Override
                            public void onListQueryResult(QueryTransaction transaction, @NonNull List<ReactionExercise> tResult) {
                                assertTrue(isExerciseEqual(tResult.get(0), REACTION_EXERCISE));
                                countDownLatch.countDown();
                            }
                        }).execute();
            }
        }).build().execute();

        countDownLatch.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void testUpdateReactionExercise_ShouldBeUpdated() {
        final ReactionExercise exerciseToUpdate = new ReactionExercise(1, 1, 1, 1, 1, 1);

        exerciseToUpdate.insert();
        exerciseToUpdate.reps = 2;
        exerciseToUpdate.sets = 2;
        exerciseToUpdate.cones = 2;
        exerciseToUpdate.repDuration = 2;
        exerciseToUpdate.restDuration = 2;
        exerciseToUpdate.update();

        List<ReactionExercise> exercises = SQLite.select()
                .from(ReactionExercise.class)
                .queryList();

        assertTrue(isExerciseEqual(exerciseToUpdate, exercises.get(0)));
    }

    @Test
    public void testUpdateReactionExerciseAsync_ShouldBeUpdated() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final ReactionExercise exerciseToUpdate = new ReactionExercise(1, 1, 1, 1, 1, 1);

        FlowManager.getDatabase(ExerciseDatabase.class).beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                exerciseToUpdate.insert();
                exerciseToUpdate.reps = 2;
                exerciseToUpdate.sets = 2;
                exerciseToUpdate.cones = 2;
                exerciseToUpdate.repDuration = 2;
                exerciseToUpdate.restDuration = 2;
                exerciseToUpdate.update();
            }
        }).success(new Transaction.Success() {
            @Override
            public void onSuccess(@NonNull Transaction transaction) {
                SQLite.select()
                        .from(ReactionExercise.class)
                        .async()
                        .queryListResultCallback(new QueryTransaction.QueryResultListCallback<ReactionExercise>() {
                            @Override
                            public void onListQueryResult(QueryTransaction transaction, @NonNull List<ReactionExercise> tResult) {
                                assertTrue(isExerciseEqual(exerciseToUpdate, tResult.get(0)));
                                countDownLatch.countDown();
                            }
                        }).execute();
            }
        }).build().execute();

        countDownLatch.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void testDeleteReactionExercise_ShouldBeDeleted() {
        REACTION_EXERCISE.insert();
        REACTION_EXERCISE.delete();

        List<ReactionExercise> exercises = SQLite.select()
                .from(ReactionExercise.class)
                .queryList();

        assertEquals(0, exercises.size());
    }

    @Test
    public void testDeleteReactionExerciseAsync_ShouldBeDeleted() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        REACTION_EXERCISE.insert();
        FlowManager.getDatabase(ExerciseDatabase.class).beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                SQLite.delete(ReactionExercise.class)
                        .where(ReactionExercise_Table.id.eq(REACTION_EXERCISE.id))
                        .execute();
            }
        }).success(new Transaction.Success() {
            @Override
            public void onSuccess(@NonNull Transaction transaction) {
                SQLite.select()
                        .from(ReactionExercise.class)
                        .async()
                        .queryListResultCallback(new QueryTransaction.QueryResultListCallback<ReactionExercise>() {
                            @Override
                            public void onListQueryResult(QueryTransaction transaction, @NonNull List<ReactionExercise> tResult) {
                                assertEquals(0, tResult.size());
                                countDownLatch.countDown();
                            }
                        }).execute();
            }
        }).build().execute();

        countDownLatch.await(10, TimeUnit.SECONDS);
    }

    private boolean isExerciseEqual(ReactionExercise r1, ReactionExercise r2) {
        return r1.reps == r2.reps
                && r1.sets == r2.sets
                && r1.cones == r2.cones
                && r1.repDuration == r2.repDuration
                && r1.restDuration == r2.restDuration;
    }

}
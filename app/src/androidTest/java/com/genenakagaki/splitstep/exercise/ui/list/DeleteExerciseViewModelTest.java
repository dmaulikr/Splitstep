package com.genenakagaki.splitstep.exercise.ui.list;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.genenakagaki.splitstep.exercise.data.ExerciseDatabase;
import com.genenakagaki.splitstep.exercise.data.entity.Exercise;
import com.genenakagaki.splitstep.exercise.data.entity.ExerciseType;
import com.genenakagaki.splitstep.exercise.data.entity.ReactionExercise;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by gene on 9/8/17.
 */

@RunWith(AndroidJUnit4.class)
public class DeleteExerciseViewModelTest {

    private Context mContext = InstrumentationRegistry.getTargetContext();

    @Before
    public void setUp() throws Exception {
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
    public void testDeleteExercise_WithRegularExercise_ShouldBeDeleted() {
        Exercise exercise = new Exercise(ExerciseType.REGULAR_VALUE, "Reps exercise");
        exercise.insert();

        DeleteExerciseViewModel viewModel = new DeleteExerciseViewModel(exercise.id, ExerciseType.REGULAR, "deleting");

        viewModel.deleteExerciseCompletable()
                .test()
                .awaitTerminalEvent();

        List<Exercise> exercises = SQLite.select()
                .from(Exercise.class)
                .queryList();

        assertEquals(0, exercises.size());
    }

    @Test
    public void testDeleteExercise_WithReactionExercise_ShouldBeDeleted() {
        Exercise exercise = new Exercise(ExerciseType.REACTION_VALUE, "Reaction exercise");
        exercise.insert();

        DeleteExerciseViewModel viewModel = new DeleteExerciseViewModel(exercise.id, ExerciseType.REACTION, "deleting");

        ReactionExercise reactionExercise = new ReactionExercise(exercise.id, 1, 1);
        reactionExercise.insert();

        viewModel.deleteExerciseCompletable()
                .test()
                .awaitTerminalEvent();

        List<Exercise> exercises = SQLite.select()
                .from(Exercise.class)
                .queryList();

        List<ReactionExercise> reactionExercises = SQLite.select()
                .from(ReactionExercise.class)
                .queryList();

        assertEquals(0, exercises.size());
        assertEquals(0, reactionExercises.size());
    }
}
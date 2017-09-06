package com.genenakagaki.splitstep.exercise.ui.add;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.genenakagaki.splitstep.R;
import com.genenakagaki.splitstep.exercise.data.ExerciseDatabase;
import com.genenakagaki.splitstep.exercise.data.entity.Exercise;
import com.genenakagaki.splitstep.exercise.data.entity.ExerciseType;
import com.genenakagaki.splitstep.exercise.data.entity.ReactionExercise;
import com.genenakagaki.splitstep.exercise.data.entity.RepsExercise;
import com.genenakagaki.splitstep.exercise.data.entity.TimedSetsExercise;
import com.genenakagaki.splitstep.exercise.ui.model.ValidationModel;
import com.genenakagaki.splitstep.exercise.utils.DatabaseUtils;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;

import static org.junit.Assert.assertTrue;

/**
 * Created by Gene on 9/6/2017.
 */

@RunWith(AndroidJUnit4.class)
public class AddExerciseViewModelTest {

    private static final Exercise EXERCISE = new Exercise(1, "test", false);

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
    public void testValidateExerciseName_WithEmptyString_ShouldBeInvalid() {
        AddExerciseViewModel viewModel = new AddExerciseViewModel(mContext, ExerciseType.REPS);

        viewModel.validateExerciseName("")
                .test()
                .assertValue(new Predicate<ValidationModel>() {
                    @Override
                    public boolean test(@NonNull ValidationModel validationModel) throws Exception {
                        String errorMessage = mContext.getString(R.string.error_exercise_name_duplicate);
                        return validationModel.isValid() == false;
                    }
                });
    }

    @Test
    public void testValidateExerciseName_WithExistingNameAndType_ShouldBeInvalid() {
        AddExerciseViewModel viewModel = new AddExerciseViewModel(mContext, ExerciseType.REPS);

        String[] exerciseNames = new String[] {
                "Exercise1",
                "Exercise2",
                "Exercise3"
        };

        DatabaseUtils.insertExercises(exerciseNames, ExerciseType.REPS_VALUE, false);

        viewModel.validateExerciseName("Exercise1")
                .test()
                .assertValue(new Predicate<ValidationModel>() {
                    @Override
                    public boolean test(@NonNull ValidationModel validationModel) throws Exception {
                        String errorMessage = mContext.getString(R.string.error_exercise_name_duplicate);
                        return validationModel.isValid() == false
                                && validationModel.getErrorMessage().equals(errorMessage);
                    }
                });
    }

    @Test
    public void testValidateExerciseName_WithUniqueName_ShouldBeValid() {
        AddExerciseViewModel viewModel = new AddExerciseViewModel(mContext, ExerciseType.REPS);

        viewModel.validateExerciseName("Exercise1")
                .test()
                .assertValue(new Predicate<ValidationModel>() {
                    @Override
                    public boolean test(@NonNull ValidationModel validationModel) throws Exception {
                        return validationModel.isValid() == true;
                    }
                });
    }

    @Test
    public void testValidExerciseName_WithExistingNameOfDifferentType_ShouldBeValid() {
        AddExerciseViewModel viewModel = new AddExerciseViewModel(mContext, ExerciseType.REPS);

        String[] exerciseNames = new String[] {
                "Exercise1",
                "Exercise2",
                "Exercise3"
        };

        DatabaseUtils.insertExercises(exerciseNames, ExerciseType.TIMED_SETS_VALUE, false);

        viewModel.validateExerciseName("Exercise1")
                .test()
                .assertValue(new Predicate<ValidationModel>() {
                    @Override
                    public boolean test(@NonNull ValidationModel validationModel) throws Exception {
                        String errorMessage = mContext.getString(R.string.error_exercise_name_duplicate);
                        return validationModel.isValid() == true;
                    }
                });
    }

    @Test
    public void testInsertExercise_WithRepsExercise_ShouldBeInserted() {
        AddExerciseViewModel viewModel = new AddExerciseViewModel(mContext, ExerciseType.REPS);

        String newName = "Exercise1";
        viewModel.insertExercise(newName)
                .test()
                .awaitTerminalEvent();

        Exercise exercise = SQLite.select()
                .from(Exercise.class)
                .queryList().get(0);

        RepsExercise repsExercise = SQLite.select()
                .from(RepsExercise.class)
                .queryList().get(0);

        assertTrue(exercise.name.equals(newName)
                && exercise.type == ExerciseType.REPS_VALUE
                && repsExercise.id == exercise.id);
    }

    @Test
    public void testInsertExercise_WithTimedSetsExercise_ShouldBeInserted() {
        AddExerciseViewModel viewModel = new AddExerciseViewModel(mContext, ExerciseType.TIMED_SETS);

        String newName = "Exercise1";
        viewModel.insertExercise(newName)
                .test()
                .awaitTerminalEvent();

        Exercise exercise = SQLite.select()
                .from(Exercise.class)
                .queryList().get(0);

        TimedSetsExercise repsExercise = SQLite.select()
                .from(TimedSetsExercise.class)
                .queryList().get(0);

        assertTrue(exercise.name.equals(newName)
                && exercise.type == ExerciseType.TIMED_SETS_VALUE
                && repsExercise.id == exercise.id);
    }

    @Test
    public void testInsertExercise_WithReactionExercise_ShouldBeInserted() {
        AddExerciseViewModel viewModel = new AddExerciseViewModel(mContext, ExerciseType.REACTION);

        String newName = "Exercise1";
        viewModel.insertExercise(newName)
                .test()
                .awaitTerminalEvent();

        Exercise exercise = SQLite.select()
                .from(Exercise.class)
                .queryList().get(0);

        ReactionExercise repsExercise = SQLite.select()
                .from(ReactionExercise.class)
                .queryList().get(0);

        assertTrue(exercise.name.equals(newName)
                && exercise.type == ExerciseType.REACTION_VALUE
                && repsExercise.id == exercise.id);
    }

}
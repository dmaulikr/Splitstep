package com.genenakagaki.splitstep.exercise.ui.coach;

import android.view.View;

import com.genenakagaki.splitstep.R;
import com.genenakagaki.splitstep.exercise.ui.model.DurationDisplayable;

import butterknife.OnClick;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by Gene on 9/13/2017.
 */

public class RegularCoachFragment extends CoachFragment {

    private TimerViewModel mTimedSetsTimerViewModel;

    @Override
    public void setupExerciseSet() {
        switch (getViewModel().getExerciseSubType()) {
            case REPS:
                int reps = getViewModel().getExercise().reps;
                mMainProgressTopText.setText(getString(R.string.reps_count, reps));
                break;
            case TIMED_SETS:
                int setDuration = getViewModel().getExercise().setDuration;

                mTimedSetsTimerViewModel = new TimerViewModel(
                        new DurationDisplayable(DurationDisplayable.TYPE_SET_DURATION, setDuration));
                mTimedSetProgressBar.setMax(mTimedSetsTimerViewModel.getMax());
                break;
        }
    }

    @Override
    public void startExerciseSet() {
        switch (getViewModel().getExerciseSubType()) {
            case REPS:
                mDoneButton.setVisibility(View.VISIBLE);
                break;
            case TIMED_SETS:
                mTimedSetProgressBar.setVisibility(View.VISIBLE);
                mTimedSetProgressBar.setProgress(mTimedSetsTimerViewModel.getMax());
                animateProgress(mTimedSetProgressBar, 0, mTimedSetsTimerViewModel.getAnimateDuration());
                mMainProgressText.setText(mTimedSetsTimerViewModel.getTimerDisplay());

                getDisposable().add(mTimedSetsTimerViewModel.startTimer().subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mMainProgressText.setText(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        mTimedSetProgressBar.setVisibility(View.INVISIBLE);
                        onFinishExerciseSet();
                    }
                }));
                break;
        }
    }

    @OnClick(R.id.done_button)
    public void onClickDone() {
        mDoneButton.setVisibility(View.INVISIBLE);
        onFinishExerciseSet();
    }
}

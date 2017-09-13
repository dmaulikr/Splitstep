package com.genenakagaki.splitstep.exercise.ui.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.genenakagaki.splitstep.R;
import com.genenakagaki.splitstep.exercise.data.ExerciseSharedPref;
import com.genenakagaki.splitstep.exercise.data.entity.Exercise;
import com.genenakagaki.splitstep.exercise.data.entity.ExerciseSubType;
import com.genenakagaki.splitstep.exercise.ui.model.DurationDisplayable;
import com.genenakagaki.splitstep.exercise.ui.view.NumberInput;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Gene on 9/8/2017.
 */

public class ExerciseDetailFragment extends Fragment
        implements NumberInput.OnInputChangedListener {

    @BindView(R.id.exercise_name_textview)
    TextView mExerciseNameTextView;
    @BindView(R.id.favorite_imageswitcher)
    ImageSwitcher mFavoriteImageSwitcher;
    @BindView(R.id.sets_numberinput)
    NumberInput mSetsNumberInput;
    @BindView(R.id.reps_numberinput)
    NumberInput mRepsNumberInput;
    @BindView(R.id.cones_numberinput)
    NumberInput mConesNumberInput;
    @BindView(R.id.set_duration_layout)
    LinearLayout mSetDurationLayout;
    @BindView(R.id.set_duration_textview)
    TextView mSetDurationTextView;
    @BindView(R.id.rest_duration_layout)
    LinearLayout mRestDurationLayout;
    @BindView(R.id.rest_duration_textview)
    TextView mRestDurationTextView;

    private Unbinder mUnbinder;
    private CompositeDisposable mDisposable;
    private ExerciseDetailViewModel mViewModel;

    public ExerciseDetailFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long exerciseId = ExerciseSharedPref.getExerciseId(getActivity());
        mViewModel = new ExerciseDetailViewModel(getActivity(), exerciseId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.d("onCreateView");
        View view = inflater.inflate(R.layout.fragment_exercise_detail, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mConesNumberInput.setVisibility(View.GONE);

        mFavoriteImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView switcherImageView = new ImageView(getActivity());
                switcherImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                switcherImageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                return switcherImageView;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDisposable = new CompositeDisposable();

        mDisposable.add(mViewModel.getExerciseSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Consumer<Exercise>() {
                    @Override
                    public void accept(Exercise exercise) throws Exception {
                        mExerciseNameTextView.setText(exercise.name);
                        if (exercise.favorite) {
                            mFavoriteImageSwitcher.setImageResource(R.drawable.ic_star);
                        } else {
                            mFavoriteImageSwitcher.setImageResource(R.drawable.ic_star_border);
                        }

                        mSetsNumberInput.setNumber(exercise.sets);

                        switch (ExerciseSubType.fromValue(exercise.subType)) {
                            case REPS:
                                mSetDurationLayout.setVisibility(View.GONE);
                                mRepsNumberInput.setNumber(exercise.reps);
                                break;
                            case TIMED_SETS:
                                mRepsNumberInput.setVisibility(View.GONE);
                                break;
                        }
                    }
                }));

        mDisposable.add(mViewModel.getSetDurationSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Consumer<DurationDisplayable>() {
                    @Override
                    public void accept(DurationDisplayable durationDisplayable) throws Exception {
                        mSetDurationTextView.setText(durationDisplayable.getDisplay());
                    }
                }));

        mDisposable.add(mViewModel.getRestDurationSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Consumer<DurationDisplayable>() {
                    @Override
                    public void accept(DurationDisplayable durationDisplayable) throws Exception {
                        mRestDurationTextView.setText(durationDisplayable.getDisplay());
                    }
                }));

        mDisposable.add(mViewModel.setExercise()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Override
    public void onDestroyView() {
        Timber.d("onDestroyView");
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onInputChanged(View view, int number) {
        Timber.d("onInputChanged " + number);
        if (view.getId() == mRepsNumberInput.getId()) {
            Timber.d("reps");
            mViewModel.setReps(number)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.computation())
                    .subscribe();
        } else if (view.getId() == mSetsNumberInput.getId()) {
            Timber.d("sets");
            mViewModel.setSets(number)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.computation())
                    .subscribe();
        }
    }

    @OnClick(R.id.rest_duration_layout)
    public void onClickRestDuration() {
        DurationPickerDialog fragment =
                DurationPickerDialog.newInstance(mViewModel.getRestDuration());
        fragment.show(getFragmentManager(), DurationPickerDialog.class.getSimpleName());
    }

    @OnClick(R.id.set_duration_layout)
    public void onClickSetDuration() {
        DurationPickerDialog fragment =
                DurationPickerDialog.newInstance(mViewModel.getRestDuration());
        fragment.show(getFragmentManager(), DurationPickerDialog.class.getSimpleName());
    }

    public CompositeDisposable getDisposable() {
        return mDisposable;
    }

    public void setDuration(DurationDisplayable durationDisplayable) {
        mDisposable.add(mViewModel.setRestDuration(durationDisplayable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe());
    }

    public void startCoachFragment() {
//        RegularExercise repsExercise = new RegularExercise(mViewModel.getExerciseId());
//        repsExercise.reps = mRepsNumberInput.getNumber();
//        repsExercise.sets = mSetsNumberInput.getNumber();
//        repsExercise.restDuration = mViewModel.getDuration(mRestDurationTextView.getText().toString());

//        mDisposable.add(getViewModel().updateRepsExercise(repsExercise)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.computation())
//                .subscribe(new Action() {
//                    @Override
//                    public void run() throws Exception {
//                        ((MainActivity)getActivity()).showFragment(
//                                new RepsCoachFragment(), RepsCoachFragment.class.getSimpleName(), true);
//                    }
//                }));
    }


}

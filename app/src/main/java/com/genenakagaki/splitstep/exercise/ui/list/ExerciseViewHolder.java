package com.genenakagaki.splitstep.exercise.ui.list;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.genenakagaki.splitstep.R;
import com.genenakagaki.splitstep.exercise.data.ExerciseSharedPref;
import com.genenakagaki.splitstep.exercise.data.entity.Exercise;
import com.genenakagaki.splitstep.exercise.ui.ExerciseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Gene on 9/8/2017.
 */

public class ExerciseViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.delete_button) ImageButton mDeleteButton;
    @BindView(R.id.favorite_imageswitcher) ImageSwitcher mFavoriteImageSwitcher;
    @BindView(R.id.exercise_textview) TextView mExerciseTextButton;

    private Context mContext;
    private ExerciseListItemViewModel mListItemViewModel;

    public ExerciseViewHolder(View view, Context context) {
        super(view);
        mContext = context;
        ButterKnife.bind(this, view);
        mFavoriteImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView switcherImageView = new ImageView(mContext);
                switcherImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                switcherImageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                return switcherImageView;
            }
        });
    }

    public void setExercise(Exercise exercise) {
        mListItemViewModel = new ExerciseListItemViewModel(mContext, exercise);

        ExerciseActivity activity = (ExerciseActivity) mContext;
        ExerciseListFragment fragment = (ExerciseListFragment) activity.getSupportFragmentManager()
                .findFragmentByTag(ExerciseListFragment.class.getSimpleName());

        fragment.getDisposable().add(mListItemViewModel.getExerciseSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Consumer<Exercise>() {
                    @Override
                    public void accept(Exercise exercise) throws Exception {
                        if (exercise.favorite) {
                            mFavoriteImageSwitcher.setImageResource(R.drawable.ic_star);
                        } else {
                            mFavoriteImageSwitcher.setImageResource(R.drawable.ic_star_border);
                        }
                    }
                }));
    }

    @OnClick(R.id.delete_button)
    public void onDeleteButtonClick() {
        DeleteExerciseDialog dialog = DeleteExerciseDialog.newInstance(
                mListItemViewModel.getExercise().id, mListItemViewModel.getDeleteMessage());
        AppCompatActivity activity = (AppCompatActivity) mContext;
        dialog.show(activity.getSupportFragmentManager(), DeleteExerciseDialog.class.getSimpleName());
    }

    @OnClick(R.id.favorite_imageswitcher)
    public void onFavoriteClick() {
        Timber.d("onFavoriteClick");
        mListItemViewModel.toggleExerciseFavorite()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe();
    }

    @OnClick(R.id.exercise_textview)
    public void onExerciseClick() {
        Timber.d("onExerciseClick");
        ExerciseActivity activity = (ExerciseActivity) mContext;
        ExerciseSharedPref.setExerciseId(mContext, mListItemViewModel.getExercise().id);

//        switch (ExerciseSharedPref.getExerciseType(mContext)) {
//            case REPS:
//                activity.showFragment(new RepsExerciseDetailFragment(),
//                        RepsExerciseDetailFragment.class.getSimpleName(),
//                        true);
//                break;
//            case TIMED_SETS:
//                activity.showFragment(new TimedSetsExerciseDetailFragment(),
//                        TimedSetsExerciseDetailFragment.class.getSimpleName(),
//                        true);
//                break;
//            default:// REACTION:
//                activity.showFragment(new ReactionExerciseDetailFragment(),
//                        ReactionExerciseDetailFragment.class.getSimpleName(),
//                        true);
//        }
    }
}

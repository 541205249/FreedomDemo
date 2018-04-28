package com.jiazy.freedomdemo.frameanimation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import com.jiazy.freedomdemo.R;

public class PropertyAnimationActivity extends Activity implements AvatarView.AvatarViewListener{

    private AvatarView mAvatarView;
    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_animation);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        PropertyAnimFragment fragment = new PropertyAnimFragment();
        fragmentTransaction.add(R.id.container, fragment);
        fragmentTransaction.commit();

        mView = findViewById(R.id.container);

        mAvatarView = findViewById(R.id.img);
        mAvatarView.setAvatarViewListener(this);


        findViewById(R.id.btn_result).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.setVisibility(View.GONE);
                mAvatarView.setPlaySpecificFrame(14);
                mAvatarView.switchState(AvatarView.STATE_RESULT);

            }
        });


    }

    private void startPlayPropertyAnim(){
        mView.setVisibility(View.VISIBLE);

        playFragmentAnimStart(mView);
    }

    private void playFragmentAnimStart(View view) {
        ObjectAnimator zoomXAnim = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
        zoomXAnim.setDuration(500);

        ObjectAnimator zoomYAnim = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f);
        zoomYAnim.setDuration(500);

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        alphaAnim.setDuration(500);

        ObjectAnimator translationXAnim = ObjectAnimator.ofFloat(view, "translationY", 265f, 100f);
        translationXAnim.setDuration(500);

        AnimatorSet animSet = new AnimatorSet();
        animSet.play(zoomXAnim).with(alphaAnim).with(translationXAnim).with(zoomYAnim);

        animSet.start();
    }

    @Override
    public void afterPlaySpecificFrame() {
        startPlayPropertyAnim();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

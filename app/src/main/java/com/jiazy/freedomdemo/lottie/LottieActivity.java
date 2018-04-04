package com.jiazy.freedomdemo.lottie;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.jiazy.freedomdemo.R;

public class LottieActivity extends Activity {
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lottie);

        lottieAnimationView = findViewById(R.id.lottie_animation_view);
        initLottieAnimationView(lottieAnimationView);

        findViewById(R.id.btn).setOnClickListener(v -> {
            LottieComposition.Factory.fromAssetFileName(getApplicationContext(), "mubu_close.json",
                    composition -> {
                        lottieAnimationView.setComposition(composition);
                        lottieAnimationView.playAnimation();
                    });
        });
    }

    private void initLottieAnimationView(final LottieAnimationView lottieAnimationView) {
        // 自定义速度与时长
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f)
                .setDuration(5000);
        animator.addUpdateListener(animation -> {
            lottieAnimationView.setProgress((float) animation.getAnimatedValue());
        });
        animator.start();

//        lottieAnimationView.cancelAnimation();

        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                Toast.makeText(getApplicationContext(),"!!!", Toast.LENGTH_SHORT).show();
//                lottieAnimationView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                lottieAnimationView.clearAnimation();
//                lottieAnimationView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
}

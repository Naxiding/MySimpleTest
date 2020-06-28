package com.jitian.mysimpletest.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.jitian.mysimpletest.R;

/**
 * @author YangDing
 * @date 2020/6/17
 */
public class XianKongActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_layout);
        ToggleButton toggleButton = findViewById(R.id.control_button);
        constraintLayout = findViewById(R.id.control_area);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dealControlAreaToggle(isChecked);
            }
        });

    }

    private void dealControlAreaToggle(boolean isChecked) {
        if (isChecked) {
            startShowAnimation();
        } else {
            startHideAnimation();
        }
    }

    private void startShowAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.control_in);
        if (constraintLayout != null) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    constraintLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            constraintLayout.startAnimation(animation);
        }
    }

    private void startHideAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.control_out);
        if (constraintLayout != null) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    constraintLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            constraintLayout.startAnimation(animation);
        }
    }

}

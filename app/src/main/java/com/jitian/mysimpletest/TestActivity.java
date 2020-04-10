package com.jitian.mysimpletest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.jitian.view.PinyinInputLinearLayout;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author YangDing
 * @date 2019/12/2
 */
public class TestActivity extends AppCompatActivity implements PinyinInputLinearLayout.OnItemClickCallback {

    private ViewFlipper viewFlipper;
    private static final String TAG = "LogUtil";
    private TextPaint mPaint;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        viewFlipper = findViewById(R.id.pinyin_flipper);
        viewFlipper.setAutoStart(false);
        mPaint = ((TextView) LayoutInflater.from(this).inflate(R.layout.result_textview, null)).getPaint();
        viewFlipper.post(new Runnable() {
            @Override
            public void run() {
                updateViewFlipper(null);
            }
        });
        findViewById(R.id.page_pre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrePage();
            }
        });
        findViewById(R.id.page_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextPage();
            }
        });
        final EditText editText = findViewById(R.id.edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && !"".equals(editable.toString().trim())) {
                    String text = editable.toString();
                    Log.d(TAG, "text:" + text);
                    updateViewFlipper(getSearchResults(text));
                }
            }
        });
    }

    private void showPrePage() {
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.top_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.bottom_out));
        viewFlipper.showPrevious();
    }

    private void updateViewFlipper(String[] searchResults) {
        viewFlipper.removeAllViews();
        updateView(measureAllResult(searchResults, viewFlipper));
    }

    private ArrayList<ArrayList<String>> measureAllResult(String[] searchResults, View parent) {
        if (searchResults == null) {
            searchResults = PinyinInputLinearLayout.testString(5);
        }
        int parentWidth = parent.getWidth();
        int totalMargin = getResources().getDimensionPixelOffset(R.dimen.result_text_margin_left)
                + getResources().getDimensionPixelOffset(R.dimen.result_text_margin_right);
        ArrayList<ArrayList<String>> finishedArrays = new ArrayList<>(5);
        ArrayList<String> lineResults = new ArrayList<>(PinyinInputLinearLayout.DEFAULT_MAX_TEXT_NUM);
        float lineWidth = 0;
        for (String result : searchResults) {
            lineWidth += (mPaint.measureText(result) + totalMargin);
            if (lineWidth <= parentWidth && lineResults.size() < PinyinInputLinearLayout.DEFAULT_MAX_TEXT_NUM) {
                lineResults.add(result);
                if (result.equals(searchResults[searchResults.length - 1])) {
                    finishedArrays.add(lineResults);
                }
            } else {
                finishedArrays.add(new ArrayList<>(lineResults));
                lineResults.clear();
                lineResults.add(result);
                lineWidth = mPaint.measureText(result) + totalMargin;
            }
        }
        return finishedArrays;
    }

    private void updateView(ArrayList<ArrayList<String>> finishedArrays) {
        viewFlipper.removeAllViews();
        for (int index = 0; index < finishedArrays.size(); index++) {
            PinyinInputLinearLayout pinyinInputLinearLayout = new PinyinInputLinearLayout(this);
            pinyinInputLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            pinyinInputLinearLayout.updateText(finishedArrays.get(index));
            pinyinInputLinearLayout.setClickCallback(this);
            viewFlipper.addView(pinyinInputLinearLayout);
        }
    }


    private void showNextPage() {
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.bottom_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.top_out));
        viewFlipper.showNext();
    }

    private static final int MAX_NUM = 50;
    private static final int MIN_NUM = 30;

    private String[] getSearchResults(String text) {
        int num = generateRandomByScope(MIN_NUM, MAX_NUM);
        String[] results = new String[num];
        for (int index = 0; index < num; index++) {
            results[index] = index + text;
        }
        return results;
    }

    @Override
    public void onClickText(String text) {
        Log.d(TAG, "text:" + text);
    }

    private static int generateRandomByScope(int small, int big) {
        int num = -1;
        Random random = new Random();
        num = random.nextInt(big - small) + small;
        return num;
    }
}

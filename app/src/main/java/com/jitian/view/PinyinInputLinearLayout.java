package com.jitian.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jitian.mysimpletest.R;

import java.util.ArrayList;

/**
 * @author YangDing
 * @date 2019/12/9
 */
public class PinyinInputLinearLayout extends LinearLayout {

    public static final int DEFAULT_MAX_TEXT_NUM = 10;
    private Context mContext;
    private LayoutParams mLayoutParams;

    public PinyinInputLinearLayout(Context context) {
        this(context, null);
    }

    public PinyinInputLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinyinInputLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.HORIZONTAL);
        mContext = context;
        mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = context.getResources().getDimensionPixelOffset(R.dimen.result_text_margin_left);
        mLayoutParams.rightMargin = context.getResources().getDimensionPixelOffset(R.dimen.result_text_margin_right);
    }

    public void updateText(ArrayList<String> results) {
        if (results == null) {
            return;
        }
        for (int index = 0; index < results.size(); index++) {
            @SuppressLint("InflateParams") final TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.result_textview, null);
            textView.setText(results.get(index));
            textView.setLayoutParams(mLayoutParams);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickCallback != null && textView.getText() != null) {
                        mClickCallback.onClickText(textView.getText().toString());
                    }
                }
            });
            addView(textView);
        }
    }

    public static String[] testString(int pageNum) {
        int sum = DEFAULT_MAX_TEXT_NUM * pageNum;
        String[] result = new String[sum];
        for (int index = 0; index < sum; index++) {
            result[index] = "Page" + (index / DEFAULT_MAX_TEXT_NUM) + " Index" + index % DEFAULT_MAX_TEXT_NUM;
        }
        return result;
    }

    private OnItemClickCallback mClickCallback;

    public void setClickCallback(OnItemClickCallback clickCallback) {
        mClickCallback = clickCallback;
    }

    public interface OnItemClickCallback {
        /**
         * 点击item 的回调
         *
         * @param text 返回text
         */
        void onClickText(String text);
    }

}

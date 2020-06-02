package com.jitian.mysimpletest.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jitian.mysimpletest.R;

import java.util.ArrayList;

/**
 * @author YangDing
 * @date 2020/5/7
 */
public class RecyclerTestActivity extends AppCompatActivity {

    private ArrayList<String> mData = new ArrayList<>();
    private Handler mHandler = new Handler();
    private TestAdapter mTestAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        mTestAdapter = new TestAdapter(mData);
        recyclerView.setAdapter(mTestAdapter);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> updateData = new ArrayList<>();
                updateData.add("1");
                updateData.add("2");
                updateData.add("3");
                updateData.add("4");
                mData = updateData;
                mTestAdapter.notifyDataSetChanged();
            }
        }, 5000);
    }

    class TestAdapter extends RecyclerView.Adapter<TestHolder> {

        private ArrayList<String> mData;

        TestAdapter(ArrayList<String> data) {
            mData = data;
        }

        @NonNull
        @Override
        public TestHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new TestHolder(LayoutInflater.from(RecyclerTestActivity.this).inflate(R.layout.test_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull TestHolder testHolder, int i) {
            testHolder.testView.setText("test:" + mData.get(i));
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

    }

    private static class TestHolder extends RecyclerView.ViewHolder {

        TextView testView;

        TestHolder(@NonNull View itemView) {
            super(itemView);
            testView = itemView.findViewById(R.id.item_text);
        }
    }

}

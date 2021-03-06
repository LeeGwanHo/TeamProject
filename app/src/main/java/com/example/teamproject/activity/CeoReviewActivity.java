package com.example.teamproject.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.teamproject.data.CeoReview;
import com.example.teamproject.adapter.CeoReviewAdapter;
import com.example.teamproject.R;

import java.util.ArrayList;

public class CeoReviewActivity extends AppCompatActivity {
    private RecyclerView recyclerCeoReview;
    private TextView tvCeoGrade, tvCeoReview;

    private CeoReviewAdapter adapter;

    private ArrayList<CeoReview> list = new ArrayList<>();

    private String storeName;
    private float grade;
    private int review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceo_review);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent intent = getIntent();

        list = (ArrayList<CeoReview>) intent.getSerializableExtra("list");
        grade = intent.getFloatExtra("grade", 0);
        review = intent.getIntExtra("review", 0);

        tvCeoGrade = findViewById(R.id.tvCeoGrade);
        tvCeoReview = findViewById(R.id.tvCeoReview);
        recyclerCeoReview = findViewById(R.id.recyclerCeoReview);

        tvCeoGrade.setText(String.valueOf(grade));
        tvCeoReview.setText("후기 "+review+"개");

        adapter = new CeoReviewAdapter(getApplicationContext(), list);
        recyclerCeoReview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerCeoReview.setAdapter(adapter);


    }

}
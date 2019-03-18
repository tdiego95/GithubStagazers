package com.diegoturchi.githubstargazers.githubstargazers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentSearch()).commit();
    }

    public void LookForStargazers(View view) {

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentStargazersList()).commit();
    }
}
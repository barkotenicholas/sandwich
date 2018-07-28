package com.udacity.sandwichclub.ui.sandwichlist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;

import com.udacity.sandwichclub.DetailActivity;
import com.udacity.sandwichclub.R;
import com.udacity.sandwichclub.model.Sandwich;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SandwichAdapter mSandwichAdapter;

    private SandwichListViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] sandwiches = getResources().getStringArray(R.array.sandwich_names);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, sandwiches);

        // Simplification: Using a ListView instead of a RecyclerView
//        ListView listView = findViewById(R.id.sandwiches_listview);
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                launchDetailActivity(position);
//            }
//        });

        setupToolbar();

        setupListAdapter();

        mViewModel = obtainViewModel(this);

        // subscribe to sandwich observable livedata
        mViewModel.getSandwichList().observe(this, new Observer<List<Sandwich>>() {
            @Override
            public void onChanged(@Nullable List<Sandwich> sandwiches) {
                if (sandwiches != null) {
                    mSandwichAdapter.replaceData(sandwiches);
                }
            }
        });
    }

    private SandwichListViewModel obtainViewModel(FragmentActivity activity) {
        return ViewModelProviders.of(activity).get(SandwichListViewModel.class);
    }

    private void setupListAdapter() {
        RecyclerView recyclerView = findViewById(R.id.recycler_sandwich_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        mSandwichAdapter = new SandwichAdapter(
                this,
                new ArrayList<Sandwich>(0)
        );
        recyclerView.setAdapter(mSandwichAdapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void launchDetailActivity(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_POSITION, position);
        startActivity(intent);
    }
}

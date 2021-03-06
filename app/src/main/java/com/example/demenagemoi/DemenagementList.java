package com.example.demenagemoi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.demenagemoi.helpers.Constants;
import com.example.demenagemoi.helpers.Utils;
import com.example.demenagemoi.model.Removal;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import okhttp3.Response;

public class DemenagementList extends AppCompatActivity implements RemovalRecyclerViewAdapter.ItemClickListener {

    RemovalRecyclerViewAdapter removalRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.isAuthenticated()) {
            if (!Utils.isTokenValid()) {
                Utils.refreshToken(DemenagementList.this);
            }
            setContentView(R.layout.activity_demenagement_list);
            HashMap<String, Object> params = new HashMap<>();
            params.put("route", Constants.Removal.GET_ALL);
            params.put("context", this);
            params.put("method", "GET");

            final AsyncTask<HashMap<String, Object>, Void, Response> requestManager = new RequestManager().execute(params);
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        int code = requestManager.get().code();

                        if (code != 200) {
                            Toast.makeText(DemenagementList.this, "Erreur lors de la récupération des déménagements", Toast.LENGTH_SHORT).show();
                        } else {
                            Response response = requestManager.get();
                            String jsonData = response.body().string();
                            JSONArray jsonArray = new JSONArray(jsonData);
                            ArrayList<Removal> removals = new ArrayList<>(Removal.fillList(jsonArray));

                            if (!removals.isEmpty()) {
                                RecyclerView recyclerView = findViewById(R.id.removalList);
                                recyclerView.setLayoutManager(new LinearLayoutManager(DemenagementList.this));
                                removalRecyclerViewAdapter = new RemovalRecyclerViewAdapter(DemenagementList.this, removals);
                                removalRecyclerViewAdapter.setClickListener(DemenagementList.this);
                                recyclerView.setAdapter(removalRecyclerViewAdapter);
                            } else {
                                Toast.makeText(DemenagementList.this, "Pas de déménagement trouvé", Toast.LENGTH_SHORT).show();

                            }

                        }
                    } catch (ExecutionException | InterruptedException | IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, 2500);

        }
    }

    @Override
    public void onItemClick(View view, int position) {

        Toast.makeText(this, "Vous participez à ce déménagement", Toast.LENGTH_SHORT).show();
    }

    public void BackHomeActivity(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}

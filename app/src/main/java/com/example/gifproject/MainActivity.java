package com.example.gifproject;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.gifproject.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements DataAdapter.OnItemClickListener{

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    ListView listView;
    ArrayAdapter<String> arrayAdapter;


    RecyclerView rView;
    ArrayList<DataModel> dataModelArrayList = new ArrayList<>();
    DataAdapter dataAdapter;


    public static final String API_KEY = "PHMfjVVZajwpABTbLu6GGAuywUJjup1r"; // API KEY TO GIPHY
    public static final String BASE_URL = "https://api.giphy.com/v1/gifs/trending?api_key="; // Base url for trending GIFS

    String url = BASE_URL + API_KEY;
    private Object spanCount;
    private Object recyclerViewWidth;
    private Object singleItemWidth;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rView = findViewById(R.id.recyclerView);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new GridLayoutManager(this, 2));
        rView.addItemDecoration(new SpaceItemDecoration(10));

        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listview);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(arrayAdapter);


        // JSON CODE FOR GETTING DATA FROM GIPHY
        // Link where this was got
        //https://api.giphy.com/v1/gifs/trending?api_key=PHMfjVVZajwpABTbLu6GGAuywUJjup1r


        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray dataArray = response.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject obj = dataArray.getJSONObject(i);

                        JSONObject obj1 = obj.getJSONObject("images");
                        JSONObject obj2 = obj1.getJSONObject("downsized_medium");

                        String sourceUrl = obj2.getString("url");

                        dataModelArrayList.add(new DataModel(sourceUrl));
                    }

                    dataAdapter = new DataAdapter(MainActivity.this, dataModelArrayList);
                    rView.setAdapter(dataAdapter);
                    dataAdapter.setOnItemClickListener(MainActivity.this::onItemClick);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // ADDING DATA FOR REQUEST
        MySingleton.getInstance(this).addToRequestQueue(objectRequest);

    }

    @Override
    public void onItemClick(int pos) {
        Intent fullView = new Intent(this, FullActivity.class);
        DataModel clickedItem = dataModelArrayList.get(pos);

        fullView.putExtra("imageUrl", clickedItem.getImageUrl());
        startActivity(fullView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main,menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Here is place to write what you want");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                arrayAdapter.getFilter().filter(newText);

                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }
}


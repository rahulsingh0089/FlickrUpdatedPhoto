package com.example.rahul.flickrphoto.activity;

import android.app.SearchManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rahul.flickrphoto.MyApplication;
import com.example.rahul.flickrphoto.R;
import com.example.rahul.flickrphoto.adapter.PhotoAdapter;
import com.example.rahul.flickrphoto.listener.OnLoadMoreListener;
import com.example.rahul.flickrphoto.model.PhotoDetail;
import com.example.rahul.flickrphoto.parse.JsonParse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PhotoAdapter.ContactsAdapterListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private PhotoAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<PhotoDetail> photoList;
    private static List<PhotoDetail>  dummyList;
    private SearchView searchView;
    Parcelable listState;
   // LinearLayoutManager mLayoutManager;
    public final static String LIST_STATE_KEY = "recycler_list_state";
    private static final String JSON_URL = "https://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key=9f89151d82e427401680cd48dd2d5cf5&per_page=30&page=1&format=json&nojsoncallback=1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
         mLayoutManager = new LinearLayoutManager(getApplicationContext());

        mLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        photoList=new ArrayList<>();
        dummyList=new ArrayList<>();
        mAdapter=new PhotoAdapter(this,photoList,this,mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
       // if(savedInstanceState==null)
       dummyList= sendRequest(JSON_URL);

  mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
    @Override
    public void onLoadMore() {
        Log.e("haint", "Load More");
        photoList.add(null);
        mAdapter.notifyItemInserted(photoList.size() - 1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("haint", "Load More 2");
                photoList.remove(photoList.size() - 1);
                mAdapter.notifyItemRemoved(photoList.size());

                int index=photoList.size();
                /*String url="https://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key=9f89151d82e427401680cd48dd2d5cf5&per_page=5&page=2&format=json&nojsoncallback=1";
                dummyList=sendRequest(url);
                photoList.addAll(dummyList);
                mAdapter.notifyDataSetChanged();*/

            }
        },5000);
    }
});
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null)
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        listState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, listState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (listState != null) {
            mLayoutManager.onRestoreInstanceState(listState);
        }
    }
   //check net is available or not
   public static boolean isNetworkAvaliable(Context ctx) {
       ConnectivityManager connectivityManager = (ConnectivityManager) ctx
               .getSystemService(Context.CONNECTIVITY_SERVICE);
       if ((connectivityManager
               .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
               .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
               || (connectivityManager
               .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
               .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
               .getState() == NetworkInfo.State.CONNECTED)) {
           return true;
       } else {
           return false;
       }
   }
    //Call API and parse the JSON
    private List<PhotoDetail> sendRequest(String url){
        String changeUrl=url;
        if(isNetworkAvaliable(MainActivity.this)) {
         StringRequest stringRequest = new StringRequest(changeUrl,
            new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    // Toast.makeText(MainActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                    Bundle b = new Bundle();
                    b.putString("Json", response);
                    JsonParse pj = new JsonParse(response);
                    pj.parseJSON();
                    dummyList = pj.getPhotos();
                    List<PhotoDetail> mData1Set = pj.getPhotos();

                    Log.d("mDataSet ", "size" + dummyList.size());
                    photoList.clear();
                    photoList.addAll(mData1Set);

                    // refreshing recycler view
                    mAdapter.notifyDataSetChanged();

                }
            },
            new Response.ErrorListener() {


                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error: " + error.getMessage());
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });


       MyApplication.getInstance().addToRequestQueue(stringRequest);


    }else{
    Toast.makeText(MainActivity.this, "Internet is not Connected, Please connect first", Toast.LENGTH_LONG).show();
         }
         return dummyList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // call on search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onContactSelected(PhotoDetail contact) {
        Toast.makeText(getApplicationContext(), "Selected: " + contact.getTite() + " Select ", Toast.LENGTH_LONG).show();
    }
}



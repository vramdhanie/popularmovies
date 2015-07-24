package com.vincentramdhanie.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * A placeholder fragment containing a simple view.
 * API Key: 8400cfd0680e4bc54cccbec4961f3dc1
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private final String MOVIE_LIST = "movies";
    ImageAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();
        //loadMovies();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putParcelableArrayList(MOVIE_LIST, adapter.getItemList());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public MainActivityFragment() {
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myBroadcastReceiver, new IntentFilter("com.vincentramdhanie.sortChanged"));
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            if(adapter == null) {
                adapter = new ImageAdapter(getActivity());
            }
            adapter.addAll(savedInstanceState.getParcelableArrayList(MOVIE_LIST));
        }else{
            loadMovies();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridview = (GridView) rootView.findViewById(R.id.posters);
        if(adapter == null) {
            adapter = new ImageAdapter(getActivity());
        }
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "" + ((Movie)parent.getAdapter().getItem(position)).title,
                        Toast.LENGTH_SHORT).show();
                Movie item = (Movie)parent.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("movie", item);
                startActivity(intent);
            }
        });


        //loadMovies();


        return rootView;
    }

    protected BroadcastReceiver myBroadcastReceiver =
            new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    loadMovies();
                }
            };


    private void loadMovies(){
        Log.d(LOG_TAG, "About to start loading movies");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = sharedPref.getString(SettingsActivity.KEY_PREF_SORT_BY, "popularity.desc");

        new FetchMovies().execute(sort_by);
    }

    private ArrayList<Parcelable> getMoviesDataFromJson(String movieJson)
            throws JSONException, ParseException {
        final String IMAGE_URL = "poster_path";
        final String IMAGE_ID = "id";
        final String IMAGE_TITLE = "original_title";
        final String IMAGE_LIST = "results";
        final String IMAGE_DESCRIPTION = "overview";
        final String IMAGE_RELEASE_DATE = "release_date";
        final String IMAGE_VOTE_AVERAGE = "vote_average";


        JSONObject mJson = new JSONObject(movieJson);
        JSONArray mArray = mJson.getJSONArray(IMAGE_LIST);
        ArrayList<Parcelable> results = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");


        for(int i = 0; i < mArray.length(); i++){
            JSONObject movie = mArray.getJSONObject(i);
            Calendar rDate = Calendar.getInstance();
            Log.d(LOG_TAG, movie.getString(IMAGE_RELEASE_DATE));
            if(!movie.getString(IMAGE_RELEASE_DATE).equals("null")) {
                rDate.setTime(format.parse(movie.getString(IMAGE_RELEASE_DATE)));
            }else{
                rDate = null;
            }
            results.add(new Movie(movie.getLong(IMAGE_ID), movie.getString(IMAGE_TITLE),
                            movie.getString(IMAGE_URL), movie.getString(IMAGE_DESCRIPTION),
                            movie.getDouble(IMAGE_VOTE_AVERAGE), rDate));
        }

        return results;
    }

    class FetchMovies extends AsyncTask<String, Void, ArrayList<Parcelable>> {
        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        @Override
        protected ArrayList<Parcelable> doInBackground(String... params) {
            if(params.length < 1){
                Log.e(LOG_TAG, "No parameters passed to FetchMovies");
                return null;
            }

            String sortOrder = params[0];
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
            final String SORT_PARAM= "sort_by";
            final String KEY_PARAM = "api_key";
            final String api_key = "???";



            Uri apiUrl = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, sortOrder)
                    .appendQueryParameter(KEY_PARAM, api_key)
                    .build();
            Log.d(LOG_TAG, apiUrl.toString());
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJson = null;
            ArrayList<Parcelable> imgs = null;
            try{
                URL url = new URL(apiUrl.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    Log.e(LOG_TAG, "Input Stream is null. No data received from server");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    Log.d(LOG_TAG, "No data returned");
                    return null;
                }
                movieJson = buffer.toString();
                imgs = getMoviesDataFromJson(movieJson);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } finally{

            }
            return imgs;
        }

        @Override
        protected void onPostExecute(ArrayList<Parcelable> uris) {
            super.onPostExecute(uris);
            if(uris != null) {
                adapter.clear();
                adapter.addAll(uris);
                adapter.notifyDataSetChanged();
            }
        }
    }
}

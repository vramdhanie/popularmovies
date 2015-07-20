package com.vincentramdhanie.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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
import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 * API Key: 8400cfd0680e4bc54cccbec4961f3dc1
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    ImageAdapter adapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridview = (GridView) rootView.findViewById(R.id.posters);
        adapter = new ImageAdapter(getActivity());
        List<String> imgs = new ArrayList<String>();
        gridview.setAdapter(adapter);
        new FetchMovies().execute("popularity.desc");
        return rootView;
    }

    private List<String> getMoviesDataFromJson(String movieJson)
            throws JSONException {
        final String IMAGE_URL = "poster_path";
        final String IMAGE_ID = "id";
        final String IMAGE_TITLE = "original_title";
        final String IMAGE_LIST = "results";

        JSONObject mJson = new JSONObject(movieJson);
        JSONArray mArray = mJson.getJSONArray(IMAGE_LIST);
        List<String> results = new ArrayList<String>();


        for(int i = 0; i < mArray.length(); i++){
            JSONObject movie = mArray.getJSONObject(i);
            results.add(movie.getString(IMAGE_URL));
        }

        return results;
    }

    class FetchMovies extends AsyncTask<String, Void, List<String>> {
        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        @Override
        protected List<String> doInBackground(String... params) {
            if(params.length < 1){
                Log.e(LOG_TAG, "No parameters passed to FetchMovies");
                return null;
            }

            String sortOrder = params[0];
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
            final String SORT_PARAM= "sort_by";
            final String KEY_PARAM = "api_key";
            final String api_key = "";



            Uri apiUrl = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, sortOrder)
                    .appendQueryParameter(KEY_PARAM, api_key)
                    .build();
            Log.d(LOG_TAG, apiUrl.toString());
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJson = null;
            List<String> imgs = null;
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
            } finally{

            }
            return imgs;
        }

        @Override
        protected void onPostExecute(List<String> uris) {
            super.onPostExecute(uris);
            if(uris != null) {
                adapter.clear();
                adapter.addAll(uris);
                adapter.notifyDataSetChanged();
            }
        }
    }
}

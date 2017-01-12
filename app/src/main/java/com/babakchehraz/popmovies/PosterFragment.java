package com.babakchehraz.popmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import java.net.URL;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * A placeholder fragment containing a simple view.
 */
public class PosterFragment extends Fragment {

    ImageAdapter postersAdapter;
    private final String LOG_TAG = PosterFragment.class.getSimpleName();
    private JSONArray movieJsonArray;

    public PosterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void updateMoviesList() {
        FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortingPref = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        movieTask.execute(sortingPref);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMoviesList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Filler images
        ArrayList<Integer> imageList = new ArrayList<>();
        for (int i = 0; i < 20; ++i)
            imageList.add(R.drawable.poster);
        //------------------------------------//

        postersAdapter = new ImageAdapter(getActivity(), R.layout.poster_list_item, R.id.posters_list_imageview, imageList);
        GridView gridView = (GridView) rootView.findViewById(R.id.posters_listview);
        gridView.setAdapter(postersAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String BASE_URL = "https://image.tmdb.org/t/p/w500/"; // base path for image location
                final String _TITLE = "title";
                final String _RATING = "vote_average";
                final String _OVERVIEW = "overview";
                final String _POSTER_PATH = "poster_path";
                final String _RELEASE_DATE = "release_date";

                String title;
                String rating;
                String overview;
                String posterPath;
                String releaseDate;
                try {
                    //Pass data into next activity
                    JSONObject movieObj = movieJsonArray.getJSONObject(position);
                    title = movieObj.getString(_TITLE);
                    rating = movieObj.getString(_RATING);
                    overview = movieObj.getString(_OVERVIEW);
                    posterPath = BASE_URL + movieObj.getString(_POSTER_PATH);
                    releaseDate = movieObj.getString(_RELEASE_DATE);
                    Intent i = new Intent(getActivity(), DetailActivity.class);
                    i.putExtra("movie_title",title);
                    i.putExtra("movie_rating",rating);
                    i.putExtra("movie_overview",overview);
                    i.putExtra("movie_poster_path",posterPath);
                    i.putExtra("movie_release_date",releaseDate);
                    startActivity(i);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error", e);
                }
            }
        });

        return rootView;
    }

    //get movie data . Posters at least
    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        private JSONArray movieData = null;
        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String jsonResponse = null;

            try {
                final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie/";
                final String API_KEY_PARAM = "api_key";
                final String API_KEY = getString(R.string.API_KEY);
                final String LANG_PARAM = "language";
                final String LANG = "en_US";
                final String PAGE_PARAM = "page";
                final String PAGE = "1";

                String SEARCH_TYPE;//popular|top_rated
                if (params[0].equals("popular")) {
                    SEARCH_TYPE = "popular";
                } else {
                    SEARCH_TYPE = "top_rated";
                }

                Uri uri = Uri.parse(MOVIES_BASE_URL + SEARCH_TYPE).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, API_KEY)
                        .appendQueryParameter(LANG_PARAM, LANG)
                        .appendQueryParameter(PAGE_PARAM, PAGE).build();

                URL url = new URL(uri.toString());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }

                if (stringBuffer.length() == 0) {
                    return null;
                }

                jsonResponse = stringBuffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getPosterPathsFromJSON(jsonResponse);
            }catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            return null;
        }

        protected void onPostExecute(String[] result) {
            if (result != null) {
                postersAdapter.clear();
                for (String posterPath : result) {
                    postersAdapter.add(posterPath);
                }
            }
        }

        protected void onProgressUpdate(Void... progress) {

        }

        //get movie data from json response in http url. Figure out how to get multiple pages too . . .?
        private String[] getPosterPathsFromJSON(String jsonResponse) throws JSONException {
            //json object names
            final String J_MOVIEINFO = "results";
            final String J_POSTER = "poster_path";
            final String BASE_PATH = "https://image.tmdb.org/t/p/w500/";

            JSONObject movieJson = new JSONObject(jsonResponse);
            movieJsonArray = movieJson.getJSONArray((J_MOVIEINFO));

            String[] posterPaths = new String[20];

            for (int i = 0; i < movieJsonArray.length(); ++i) {
                JSONObject currentMovie = movieJsonArray.getJSONObject(i);
                posterPaths[i] = BASE_PATH + currentMovie.getString(J_POSTER);
            }

            return posterPaths;
        }
    }

}

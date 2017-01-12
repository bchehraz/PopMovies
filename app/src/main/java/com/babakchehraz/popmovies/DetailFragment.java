package com.babakchehraz.popmovies;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private String posterPath;
    private String movieTitle;
    private String movieOverview;
    private String movieRating;
    private String releaseDate;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        //Get data from PosterFragment
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            if (intent.hasExtra("movie_poster_path")) {
                posterPath = intent.getStringExtra("movie_poster_path");
                ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_poster);
                Picasso.with(getActivity()).load(posterPath).into(imageView);
            }

            if (intent.hasExtra("movie_title")) {
                movieTitle = intent.getStringExtra("movie_title");
                ((TextView) rootView.findViewById(R.id.detail_title)).setText(movieTitle);
            }

            if (intent.hasExtra("movie_overview")) {
                movieOverview = intent.getStringExtra("movie_overview");
                ((TextView) rootView.findViewById(R.id.detail_overview)).setText(movieOverview);
            }

            if (intent.hasExtra("movie_rating")) {
                movieRating = intent.getStringExtra("movie_rating");
                ((TextView) rootView.findViewById(R.id.detail_rating)).setText(movieRating + " / 10");
            }

            if (intent.hasExtra("movie_release_date")) {
                releaseDate = intent.getStringExtra("movie_release_date");

                //parse into any date format
                // Code based on citizen conn's answer on StackOverflow
                // http://stackoverflow.com/questions/6510724/how-to-convert-java-string-to-date-object
                SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
                Date mReleaseDate;
                try {
                    mReleaseDate = d.parse(releaseDate);
                    SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy");
                    String date = formatter.format(mReleaseDate);
                    ((TextView) rootView.findViewById(R.id.detail_release_date)).setText(date);
                } catch (ParseException e) {
                    Log.e(LOG_TAG, "Error", e);
                }
            }
        }
        return rootView;
    }
}

package com.vincentramdhanie.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    Movie movie;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        movie = intent.getParcelableExtra("movie");

        TextView mTitle = (TextView)view.findViewById(R.id.movieTitle);
        mTitle.setText(movie.title);

        ImageView mPoster = (ImageView)view.findViewById(R.id.mposter);
        Picasso.with(getActivity()).load(String.format("%s/%s/%s",ImageAdapter.BASE_URL, ImageAdapter.IMAGE_SIZE, movie.poster)).into(mPoster);

        TextView mYear = (TextView)view.findViewById(R.id.myear);
        mYear.setText(movie.releaseYear != 0?String.valueOf(movie.releaseYear):"Not Specified");

        TextView mRating = (TextView)view.findViewById(R.id.mrating);
        mRating.setText(String.format("%.1f/10", movie.voteAverage));

        TextView mOverview = (TextView)view.findViewById(R.id.moverview);
        mOverview.setText(movie.description);


        return view;
    }
}

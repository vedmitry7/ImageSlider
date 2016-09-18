package com.example.bacillo.imageslider;

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class ImageFragment extends Fragment  {
    DisplayMetrics displayMetrics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public static ImageFragment newInstance(String path){
        ImageFragment imageFragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(MainActivity.PATH, path);
        imageFragment.setArguments(args);
        return imageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.image_fragment, null);
        ImageView imageView = (ImageView) v.findViewById(R.id.image_view);

        String path = getArguments().getString(MainActivity.PATH);

        displayMetrics = getResources().getDisplayMetrics();
        Picasso.with(getActivity().getApplicationContext())
                .load("file://" + path)
                .error(R.drawable.error)
                .resize(displayMetrics.widthPixels, displayMetrics.heightPixels)
                .centerInside()
                .into(imageView);
         return v;
    }
}

package com.example.bacillo.imageslider;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ErrorFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public static ErrorFragment newInstance(String path){
        ErrorFragment errorFragment = new ErrorFragment();
        Bundle args = new Bundle();
        args.putString(MainActivity.PATH, path);
        errorFragment.setArguments(args);
        return errorFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.error_fragment, null);
        TextView textView = (TextView) v.findViewById(R.id.error_text);
        String path = getArguments().getString(MainActivity.PATH);
        textView.setText(path);
        return v;
    }
}

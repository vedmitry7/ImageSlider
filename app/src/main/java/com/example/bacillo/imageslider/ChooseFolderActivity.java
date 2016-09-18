package com.example.bacillo.imageslider;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ChooseFolderActivity extends ListActivity implements View.OnClickListener {

    private TextView textFolder, textPathToFolder;
    private Button btnChoose, btnBack;
    private FileFilter fileFilter;
    private MyArrayAdapter adapter;

    private String currentPath = File.separator;
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_folder_layout);

        initView();
        refreshListView();

        fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory())
                    return true;
                if (isImage(pathname))
                    return true;
                else
                    return false;
            }
        };
    }

    private void initView(){

        textFolder = (TextView) findViewById(R.id.text_folder);
        textPathToFolder = (TextView) findViewById(R.id.text_path_to_folder);

        String currentFolder = currentPath.substring(currentPath.lastIndexOf(File.separator) + 1);
        textFolder.setText(currentFolder);
        String pathToCurrentFolder = currentPath.substring(0, currentPath.lastIndexOf(File.separator));
        textPathToFolder.setText(pathToCurrentFolder);

        btnChoose = (Button) findViewById(R.id.button_choose_folder);
        btnChoose.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.back_button);
        btnBack.setOnClickListener(this);

        adapter = new MyArrayAdapter(this, list);
        setListAdapter(adapter);
        adapter.setNotifyOnChange(true);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.button_choose_folder:
                Intent intent = new Intent();
                intent.putStringArrayListExtra(MainActivity.ARRAY, getFiles(currentPath));
                intent.putExtra(MainActivity.PATH, currentPath);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.back_button:
                if(currentPath.lastIndexOf(File.separator)>0) {
                    correctCurrentPath();
                    refreshListView();
                }
                else
                    Toast.makeText(this, R.string.nothing_to_show, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public class MyArrayAdapter extends ArrayAdapter<String>{
        private final Context context;
        private final ArrayList<String> list;

        public MyArrayAdapter(Context context, ArrayList<String> list) {
            super(context, R.layout.list_item_layout, R.id.label, list);
            this.context = context;
            this.list = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_layout, parent, false);

            TextView textView = (TextView) rowView.findViewById(R.id.label);
            textView.setText(list.get(position));

            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            if(isImage(new File(currentPath + File.separator + textView.getText().toString()))){
                imageView.setImageResource(R.drawable.ic_image);
            } else {
                imageView.setImageResource(R.drawable.ic_folder);
            }
            return rowView;
        }
    }

    private static boolean isImage(File f){
        String ext;
        String s = f.getName();
        int i = s.lastIndexOf(".");

        if(i > 0 && i < s.length()-1) {
            ext = s.substring(i + 1).toLowerCase();
            if(ext.equals("jpg")||ext.equals("jpeg")||ext.equals("gif")||ext.equals("png"))
                return true;
        }
        return false;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String item = (String) getListAdapter().getItem(position);
        currentPath = currentPath + File.separator + item;

        if(isImage(new File(currentPath))) {
          correctCurrentPath();
        }

        if(currentPath.equals("//sdcard")||currentPath.equals("//mnt/sdcard")){
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                refreshListView();
            }
            else {
                Toast.makeText(this, R.string.sd_not_available, Toast.LENGTH_SHORT).show();
                correctCurrentPath();
            }
        }
        else refreshListView();
    }

    private void correctCurrentPath(){
        currentPath = currentPath.substring(0, currentPath.lastIndexOf(File.separator));
    }

    private ArrayList<String> getFilesString(String directoryPath)  {
        File directory = new File(directoryPath);
        File[] list = directory.listFiles(fileFilter);
        ArrayList<String> fileList = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            fileList.add(list[i].getName());
        }

        Collections.sort(fileList, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        return fileList;
    }

    public static ArrayList<String> getFiles(String directoryPath) {
        File directory = new File(directoryPath);
        File[] list = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (isImage(pathname)) {
                    return true;
                }
                return false;
            }
        });

        ArrayList<String> fileList = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            fileList.add(list[i].getAbsolutePath());
        }
        Collections.sort(fileList, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        return fileList;
    }

    private void refreshListView(){
        try {
            list = getFilesString(currentPath);
        }
        catch (NullPointerException e){
            correctCurrentPath();
            Toast.makeText(this, R.string.access_closed, Toast.LENGTH_SHORT).show();
        }
            adapter.clear();
            for (String el:list
                    ) {
                adapter.add(el);
            }
            String currentFolder = currentPath.substring(currentPath.lastIndexOf(File.separator) + 1);
            textFolder.setText(currentFolder);
            String pathToCurrentFolder = currentPath.substring(0,currentPath.lastIndexOf(File.separator));
            textPathToFolder.setText(pathToCurrentFolder);
    }
}
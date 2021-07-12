package com.example.myfile;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<mFile> fileList;
    MyAdapterFile fileAdapter;
    ListView view_List_File;
    int idCnt = 0;
    private static final int STORE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Request permission
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, STORE);

        //Read file
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File f = new File(path);
        File[] files = f.listFiles();


        //Bind file
        fileList = new ArrayList<>();
        for(int i=0;i<files.length;i++){
            if(files[i].isDirectory())
                fileList.add(new mFile(idCnt++,files[i].getName(),Integer.toString(files[i].list().length),"folder",files[i].getAbsolutePath()));
            else fileList.add(new mFile(idCnt++,files[i].getName(),"6 kb","file",files[i].getAbsolutePath()));

        }

        fileAdapter = new MyAdapterFile(fileList);
        view_List_File = findViewById(R.id.list_file);
        view_List_File.setAdapter(fileAdapter);

        //Choose file
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (requestCode == STORE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission WRITE Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Permission WRITE Denied", Toast.LENGTH_SHORT).show();
            }
            if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission READ Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Permission READ Denied", Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    class mFile{
        int id;
        String name;
        String size;
        String type;
        String path;
        public mFile(int id, String name, String size, String type, String path){
            this.id = id;
            this.name = name;
            this.size = size;
            this.type = type;
            this.path = path;
        }
    }
    class MyAdapterFile extends BaseAdapter{
        final ArrayList<mFile> fileList;
        MyAdapterFile(ArrayList<mFile> fileList){
            this.fileList = fileList;
        }
        @Override
        public int getCount() {
            return fileList.size();
        }

        @Override
        public Object getItem(int i) {
            return fileList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return fileList.get(i).id;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            mFile f = (mFile) getItem(i);
            View myView;
            String pre = "";
            if(view==null){
                myView = View.inflate(viewGroup.getContext(),R.layout.file_view,null);
            }else myView = view;
            if(f.type.equals("folder")){
                pre = " Mục";
                ((ImageView) myView.findViewById(R.id.file_img)).setImageDrawable(getDrawable(R.drawable.fol));
            }else if(f.type.equals("file")) {
                ((ImageView) myView.findViewById(R.id.file_img)).setImageDrawable(getDrawable(R.drawable.file));
            }
            ((TextView) myView.findViewById(R.id.file_name)).setText(String.format("%s",f.name));
            ((TextView) myView.findViewById(R.id.file_size)).setText(String.format("%s"+pre,f.size));
            return myView;
        }
    }
}
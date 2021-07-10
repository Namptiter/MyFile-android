package com.example.myfile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<mFile> fileList;
    MyAdapterFile fileAdapter;
    ListView view_List_File;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileList = new ArrayList<>();
        fileList.add(new mFile(0,"Folder1","","folder"));
        fileList.add(new mFile(1,"Folder2","","folder"));
        fileList.add(new mFile(2,"Folder3","","folder"));
        fileList.add(new mFile(3,"file1.txt","12kb","file"));
        fileList.add(new mFile(4,"file2.txt","5kb","file"));
        fileList.add(new mFile(5,"file3.txt","6kb","file"));
        fileList.add(new mFile(6,"Folder3","","folder"));
        fileList.add(new mFile(7,"Folder4","","folder"));
        fileList.add(new mFile(8,"Folder5","","folder"));
        fileAdapter = new MyAdapterFile(fileList);
        view_List_File = findViewById(R.id.list_file);
        view_List_File.setAdapter(fileAdapter);
    }

    class mFile{
        int id;
        String name;
        String size;
        String type;
        public mFile(int id, String name, String size, String type){
            this.id = id;
            this.name = name;
            this.size = size;
            this.type = type;
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

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            mFile f = (mFile) getItem(i);
            View myView;
            if(f.type=="folder"){
                if(view==null){
                    myView = View.inflate(viewGroup.getContext(),R.layout.folder_view,null);
                }else myView = view;
                ((TextView) myView.findViewById(R.id.folder_name)).setText(String.format("%s",f.name));
                return myView;
            }else {
                if(view==null){
                    myView = View.inflate(viewGroup.getContext(),R.layout.file_view,null);
                }else myView = view;
                ((TextView) myView.findViewById(R.id.file_name)).setText(String.format("%s",f.name));
                ((TextView) myView.findViewById(R.id.file_size)).setText(String.format("%s",f.size));
                return myView;
            }
        }
    }
}
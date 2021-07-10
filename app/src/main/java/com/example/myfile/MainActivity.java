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

    ArrayList<mFolder> folderList;
    MyAdapterFolder folferAdapter;
    ListView view_List_Folder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        folderList.add(new mFolder(0,"file.txt","60Kb"));
        folderList.add(new mFolder(1,"file2.txt","100Mb"));

        folferAdapter = new MyAdapterFolder(folderList);
        view_List_Folder = findViewById(R.id.list_folder);
        view_List_Folder.setAdapter(folferAdapter);
    }

    class mFile{
        int id;
        String name;
        String size;
        public mFile(int id, String name, String size){
            this.id = id;
            this.name = name;
            this.size = size;
        }
    }
    class mFolder{
        int id;
        String name;
        String size;
        public mFolder(int id, String name, String size){
            this.id = id;
            this.name = name;
            this.size = size;
        }
    }

    class MyAdapterFolder extends BaseAdapter{
        final ArrayList<mFolder> folderList;
        MyAdapterFolder(ArrayList<mFolder> folderList) {
            this.folderList = folderList;
        }

        @Override
        public int getCount() {
            return folderList.size();
        }

        @Override
        public Object getItem(int i) {
            return folderList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return folderList.get(i).id;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView;
            if(view==null){
                myView = View.inflate(viewGroup.getContext(),R.layout.folder_view,null);
            }else myView = view;

            mFolder f = (mFolder) getItem(i);
            ((TextView) myView.findViewById(R.id.folder_name)).setText(String.format("%s"), TextView.BufferType.valueOf(f.name));
            ((TextView) myView.findViewById(R.id.folder_size)).setText(String.format("%s"), TextView.BufferType.valueOf(f.size));

            return myView;
        }
    }
}
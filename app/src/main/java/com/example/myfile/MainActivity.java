package com.example.myfile;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<mFile> fileList;
    ArrayList<String> pathFileCopy;
    MyAdapterFile fileAdapter;
    ListView view_List_File;
    int idCnt = 0;
    private static final int STORE = 101;
    ListView choose;
    String[] path;
    boolean cBox = false;
    boolean Past = false;
    boolean isDel = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Request permission
        int MyOsVer = Build.VERSION.SDK_INT;
        if(MyOsVer > Build.VERSION_CODES.LOLLIPOP){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, STORE);
            }
            if(MyOsVer == Build.VERSION_CODES.R){
                if (!Environment.isExternalStorageManager()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        }

        //Read file
        path = new String[]{Environment.getExternalStorageDirectory().getAbsolutePath()};
        File f = new File(path[0]);
        File[] files = f.listFiles();

        //Copy file init
        pathFileCopy = new ArrayList<>();

        //Bind file
        fileList = new ArrayList<>();
        for(int i=0;i<files.length;i++){
            if(files[i].isDirectory())
                fileList.add(new mFile(idCnt++,files[i].getName(),Integer.toString(files[i].list().length),"folder",files[i].getAbsolutePath()));
            else {
                fileList.add(new mFile(idCnt++,files[i].getName(),String.valueOf(files[i].length()/1024),"file",files[i].getAbsolutePath()));
            }
        }

        fileAdapter = new MyAdapterFile(fileList);
        view_List_File = findViewById(R.id.list_file);
        view_List_File.setAdapter(fileAdapter);

        //Go to Directory
        choose = findViewById(R.id.list_file);
        choose.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int ii, long l) {
                if(fileList.get(ii).type.equals("file")) {
                    File ff =new File(fileList.get(ii).path);
                    try {
                        openFile(ff);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                path[0] = fileList.get(ii).path;
                updateView();
            }
        });
    }
    //Show menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_menu, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if (Past) {
            menu.findItem(R.id.menu_past).setEnabled(true);
        }else{
            menu.findItem(R.id.menu_past).setEnabled(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //Back to parent Directory
            case android.R.id.home:
                if(path[0].equals(Environment.getExternalStorageDirectory().getAbsolutePath())){
                    cBox = false;
                    view_List_File.setAdapter(fileAdapter);
                    return true;
                }
                int i = path[0].length()-1;
                for(;path[0].charAt(i)!='/';i--){
                }
                path[0] = path[0].substring(0,i);
                updateView();
                return true;

                //Choose menu
            case R.id.menu_choose:
                cBox = !cBox;
                fileAdapter = new MyAdapterFile(fileList);
                view_List_File = findViewById(R.id.list_file);
                view_List_File.setAdapter(fileAdapter);
                return true;
            case R.id.menu_copy:
                copyFile();
                cBox = false;
                view_List_File.setAdapter(fileAdapter);
                return true;
            case R.id.menu_move:
                copyFile();
                cBox = false;
                isDel = true;
                view_List_File.setAdapter(fileAdapter);
                return true;
            case R.id.menu_past:
                Past = false;
                moveFile();
                cBox = false;
                if(isDel){
                    isDel = false;
                    for(String flc : pathFileCopy){
                        File g = new File(flc);
                        deleteFile(g);
                    }
                }
                updateView();
                return true;
            case R.id.menu_delete:
                for(int ii=0;ii<fileList.size();ii++){
                    try {
                        CheckBox c = (CheckBox) view_List_File.getChildAt(ii).findViewById(R.id.checkbox);
                        TextView t = view_List_File.getChildAt(ii).findViewById(R.id.file_name);
                        if(c.isChecked()){
                            for(mFile fi : fileList){
                                if(fi.name.equals(t.getText().toString())){
                                    File file_del = new File(fi.path);
                                    deleteFile(file_del);
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                updateView();
                return true;
            case R.id.menu_new:
                int con = 0;
                while(true){
                    File folder = new File(path[0] + "/Folder"+Integer.toString(con));
                    if(!folder.exists()){
                        folder.mkdir();
                        break;
                    }else con++;
                }
                updateView();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void deleteFile(File f){
        if(f.isDirectory()) {
            for(File child : f.listFiles()) deleteFile(child);
        }
        f.delete();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
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
        public int getViewTypeCount() {
            if(getCount()<1) return 1;
            return getCount();
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            mFile f = (mFile) getItem(i);
            View myView;
            String pre = "B";
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
            if(cBox){
                CheckBox checkBox = (CheckBox) myView.findViewById(R.id.checkbox);
                checkBox.setVisibility(View.VISIBLE);
            }else {
                CheckBox checkBox = (CheckBox) myView.findViewById(R.id.checkbox);
                checkBox.setVisibility(View.GONE);
            }
            return myView;
        }
    }
    public void openFile(File file) {
        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
        String mime = getContentResolver().getType(uri);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
    public static class CopyUtil {
        public static void copyFile(File src, File dst) {
            try {
                if (src.isDirectory()) {
                    if (!dst.exists()) {
                        dst.mkdirs();
                    }
                    String[] children = src.list();
                    for (int i = 0; i < children.length; i++) {
                        copyFile(new File(src, children[i]), new File(
                                dst, children[i]));
                    }
                } else {
                    copySingleFile(src, dst);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // copy a file
        private static void copySingleFile(File sourceFile, File destFile)
                throws IOException {
            if (!destFile.getParentFile().exists())
                destFile.getParentFile().mkdirs();

            if (!destFile.exists()) {
                destFile.createNewFile();
            }

            FileChannel source = null;
            FileChannel destination = null;

            try {
                source = new FileInputStream(sourceFile).getChannel();
                destination = new FileOutputStream(destFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            }
        }
    }
    public void updateView(){
        File f = new File(path[0]);
        File[] files = f.listFiles();
        fileList.clear();

        fileList = new ArrayList<>();
        for(int i=0;i<files.length;i++){
            if(files[i].isDirectory())
                fileList.add(new mFile(idCnt++,files[i].getName(),Integer.toString(files[i].list().length),"folder",files[i].getAbsolutePath()));
            else {
                fileList.add(new mFile(idCnt++,files[i].getName(),String.valueOf(files[i].length()/1024),"file",files[i].getAbsolutePath()));
            }
        }
        cBox = false;
        fileAdapter = new MyAdapterFile(fileList);
        view_List_File = findViewById(R.id.list_file);
        view_List_File.setAdapter(fileAdapter);
    }
    public void moveFile(){
        String dst = null;
        for(int ii=0;ii<fileList.size();ii++) {
            try {
                CheckBox c = (CheckBox) view_List_File.getChildAt(ii).findViewById(R.id.checkbox);
                TextView t = view_List_File.getChildAt(ii).findViewById(R.id.file_name);
                if (c.isChecked()) {
                    for (mFile fi : fileList) {
                        if (fi.name.equals(t.getText().toString())) {
                            dst = fi.path;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(dst!=null){
            File dF = new File(dst);
            for(String p : pathFileCopy){
                File sF = new File(p);
                if(sF.isDirectory()){
                    dF = new File(dst,sF.getName());
                    dF.mkdir();
                    CopyUtil.copyFile(sF,dF.getAbsoluteFile());
                }else CopyUtil.copyFile(sF,dF);
            }
        }
    }
    public void copyFile(){
        if (pathFileCopy!=null) pathFileCopy.clear();
        Past = true;
        for(int ii=0;ii<fileList.size();ii++){
            try {
                CheckBox c = (CheckBox) view_List_File.getChildAt(ii).findViewById(R.id.checkbox);
                TextView t = view_List_File.getChildAt(ii).findViewById(R.id.file_name);
                if(c.isChecked()){
                    for(mFile fi : fileList){
                        if(fi.name.equals(t.getText().toString())){
                            pathFileCopy.add(fi.path);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
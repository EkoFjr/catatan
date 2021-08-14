package com.example.catatan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class menu extends AppCompatActivity {
    ListView listview;
    public static final int REQUEST_CODE_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Catatan");
        setContentView(R.layout.activity_menu);

        listview = (ListView) findViewById(R.id.tampil);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(menu.this,filedata.class);
                Map<String,Object> data = (Map<String,Object>)parent.getAdapter().getItem(position);
                intent.putExtra("filname",data.get("name").toString());
                Toast.makeText(menu.this, "You clicked"+data.get("name"), Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,Object> data = (Map<String, Object>)parent.getAdapter().getItem(position);
                tampilkanDialogKonfirmasiHapusCatatan(data.get("name").toString());
                return true;
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (Build.VERSION.SDK_INT>=24){
            if (periksaIzinPenyimpanan()){
                megambilListFilePadaFolder();
            }
        }else {
            megambilListFilePadaFolder();
        }
    }
    private boolean periksaIzinPenyimpanan()
    {
        if(Build.VERSION.SDK_INT>=24)
        {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                return true;
            }else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE);
                return false;
            }
        }else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResult)
    {
        super.onRequestPermissionsResult(requestCode, permissions,grantResult);
        switch (requestCode)
        {
            case REQUEST_CODE_STORAGE:
                if (grantResult[0]==PackageManager.PERMISSION_GRANTED)
                {
                    megambilListFilePadaFolder();
                }
                break;
        }
    }

    void megambilListFilePadaFolder()
    {
        String path = getExternalFilesDir(null)+"/catatan";
        File directory = new File(path);

        if (directory.exists())
        {
            File[]files=directory.listFiles();
            String[] filenames = new String[files.length];
            String[] dateCreated = new String[files.length];
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM YYYY HH:mm:ss");
            ArrayList<Map<String,Object>> itemDataList = new ArrayList<Map<String, Object>>();

            for (int i=0;i <files.length; i++)
            {
                filenames[i] = files[i].getName();
                Date lasModDate = new Date(files[i].lastModified());
                dateCreated[i] = simpleDateFormat.format(lasModDate);

                Map<String,Object> listitemMap = new HashMap<>();
                listitemMap.put("name",filenames[i]);
                listitemMap.put("date", dateCreated[i]);
                itemDataList.add(listitemMap);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(this,itemDataList, android.R.layout.simple_list_item_2,
                    new String[]{"nama","date"},new int[]{android.R.id.text1,android.R.id.text2});
            listview.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch(item.getItemId()){
            case R.id.Tambah:
                Intent i = new Intent(this,filedata.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void tampilkanDialogKonfirmasiHapusCatatan(final String filename)
    {
        new AlertDialog.Builder(this).setTitle("Hapus Catatan ini ?")
                .setMessage("Apakah Anda yakin ingin menghapus Catatan"+ filename +"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {
                        hapusFile(filename);
                    }
                }).setNegativeButton(android.R.string.no,null).show();
    }

    void hapusFile(String filename){
        String path = getExternalFilesDir(null)+"/catatan";
        File file = new File(path,filename);
        if (file.exists()){
            file.delete();
        }
        megambilListFilePadaFolder();
    }
}
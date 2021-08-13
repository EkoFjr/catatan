package com.example.catatan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Map;

public class menu extends AppCompatActivity {
    ListView item;
    public static final int REQUEST_CODE_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Catatan");
        setContentView(R.layout.activity_menu);

        item = (ListView) findViewById(R.id.tampil);
        item.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(menu.this,filedata.class);
                Map<String,Object> data = (Map<String, Object>)parent.getAdapter().getItem(position);
                intent.putExtra("filename",data.get("name").toString());
                Toast.makeText(menu.this, "You clicked"+data.get("name"), Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        item.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) 
            {
                Map<String,Object> data =(Map<String,Object>)parent.getAdapter().getItem(position);
                tampilkanDialogKonfirmasiHapusCatatan(data.get("name"),toString());
                return true;
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (Build.VERSION.SDK_INT>=23){
            if (periksaIzinPenyimpanan()){
                megambilListFilePadaFolder();
            }
        }else {
            megambilListFilePadaFolder();
        }
    }

    private boolean periksaIzinPenyimpanan() {
        if(Build.VERSION.SDK_INT>=23){
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
    public void onRequestPermissionResult(int requestCode, @NonNull String[]permissions,@NonNull int[] grantResult){
        super.onRequestPermissionsResult(requestCode, permissions,grantResult);
        switch (requestCode){
            case REQUEST_CODE_STORAGE:
                if (grantResult[0]==PackageManager.PERMISSION_GRANTED){
                    megambilListFilePadaFolder();
                }
                break;
        }
    }

    private void megambilListFilePadaFolder() {
    }

    private void tampilkanDialogKonfirmasiHapusCatatan(Object name, String toString) {
    }
}
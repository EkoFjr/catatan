package com.example.catatan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class filedata extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_CODE_STORAGE = 100;
    int eventID = 0;
    EditText editFileName, editContent;
    Button btnSimpan;
    String fileName = " ", tempCatatan = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filedata);

        editFileName = findViewById(R.id.editFilename);
        editContent = findViewById(R.id.editContent);
        btnSimpan = findViewById(R.id.simpan);

        btnSimpan.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            getSupportActionBar().setTitle("Ubah Catatan");
            fileName = extras.getString("filename");
            editFileName.setText(fileName);
            editFileName.setEnabled(false);
        } else {
            getSupportActionBar().setTitle("Tambah Catatan");
        }

        eventID = 1;
        if (Build.VERSION.SDK_INT >= 24) {
            if (periksaIzinPenyimpanan()) {
                bacaFile();
            }
        } else {
            bacaFile();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.simpan:
                eventID = 2;
                if (!tempCatatan.equals(editContent.getText().toString())) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        if (periksaIzinPenyimpanan()) {
                            tampilkanDialogKonfirmasiPenyimpanan();
                        }
                    } else {
                        tampilkanDialogKonfirmasiPenyimpanan();
                    }
                } else {
                    Toast.makeText(filedata.this, "Tidak ada perubahan yang dilakukan",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void tampilkanDialogKonfirmasiPenyimpanan() {
    }

    private void bacaFile() {
        String path = getExternalFilesDir(null)+"/catatan";
        File file = new File(path,editFileName.getText().toString());
        if (file.exists()){
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                while (line != null) {

                    text.append(line);
                    line=br.readLine();
                }
                br.close();
            }catch (IOException e){
                System.out.println("Error"+ e.getMessage());
            }
            tempCatatan=text.toString();
            editContent.setText(text.toString());
        }
    }

    private boolean periksaIzinPenyimpanan() {
        if (Build.VERSION.SDK_INT >= 24) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                ;
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE);
                return false;
            }
        }else {
            return false;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResult){
        super.onRequestPermissionsResult(requestCode,permissions,grantResult);
        switch (requestCode){
            case REQUEST_CODE_STORAGE:
                if (grantResult[0]==PackageManager.PERMISSION_GRANTED){
                    bacaFile();
                }else {
                    tampilkanDialogKonfirmasiPenyimpanan();
                }
                break;
        }
    }

}
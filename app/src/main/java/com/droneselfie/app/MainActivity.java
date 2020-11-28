package com.droneselfie.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.droneselfie.network.DroneSelfieClient;
import com.droneselfie.observer.PathObs;
import io.reactivex.rxjava3.disposables.Disposable;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final String TAG = "DEBUG";
    public final String pathToWatch = Environment.getExternalStorageDirectory().getPath()+"/test/";
    private Path path1;
    private DroneSelfieClient droneSelfieClient;
    private String metadata;
    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private List<String> missingPermission = new ArrayList<>();
    private static final int REQUEST_PERMISSION_CODE = 12345;
    private Button buttonStart, buttonStop;
    private TextView textView;
    private EditText editText;
    private Disposable disposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();
        }
        initUI();
        File file = new File(pathToWatch);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PathObs pathObs = PathObs.getInstance(file);
                pathObs.observable.subscribe(path -> sendFile(path));
            }
        });
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PathObs pathObs = PathObs.getInstance(file);

            }
        });
    }

    private void initUI() {
        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);
        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
    }

    private void checkAndRequestPermissions() {
        // Check for permissions
        for (String eachPermission : REQUIRED_PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(this, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                missingPermission.add(eachPermission);
            }
        }
        // Request for missing permissions
        if (missingPermission.isEmpty()) {
            //
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(MainActivity.this, "Need to grant the permissions!", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    missingPermission.toArray(new String[missingPermission.size()]),
                    REQUEST_PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check for granted permission and remove from missing list
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    missingPermission.remove(permissions[i]);
                }
            }
        }
        // If there is enough permission, we will start the registration
        if (missingPermission.isEmpty()) {
        } else {
            Toast.makeText(MainActivity.this, "Need to grant the permissions!", Toast.LENGTH_SHORT).show();

        }
    }


    private void sendFile(String path){
        DroneSelfieClient droneSelfieClient = DroneSelfieClient.getInstance("http://192.168.196.130:3223/");
        File fileToSend = new File(pathToWatch+path);
//        String someValue = getSomeValue(fileToSend);
//        droneSelfieClient.getFileListFromServer();

        droneSelfieClient.sendFile(fileToSend);
//        path1 = fileToSend.getAbsoluteFile().toPath();
//
//        UserDefinedFileAttributeView fileAttributeView = Files.getFileAttributeView(path1, UserDefinedFileAttributeView.class);
//        List<String> allAttrs = null;
//        if (fileAttributeView != null){
//            allAttrs = fileAttributeView.list();
//            for (String att : allAttrs) {
//                System.out.println("att = " + att);
//            }
//        }
        //            Files.setAttribute(fileToSend.toPath(), "user: metadata", metadata);
//        UserDefinedFileAttributeView view =
//                Files.getFileAttributeView(fileToSend.getAbsoluteFile().toPath(), UserDefinedFileAttributeView.class);
//        try {
//            System.out.println("-----------------------"+"\n"+view.list());
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        }
    }

    private String getSomeValue(File file) {
        Date lastModDate = new Date(file.lastModified());
        return lastModDate.toString();
    }

    private void init(){
        DroneSelfieClient droneSelfieClient = DroneSelfieClient.getInstance("http://192.168.196.130:3223/");
        List<String> initFilesOnServer = droneSelfieClient.getFileListFromServer();
        if (!initFilesOnServer.isEmpty()) {
//        comparation of files
        }

    }
}
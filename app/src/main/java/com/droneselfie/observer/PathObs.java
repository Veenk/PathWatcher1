package com.droneselfie.observer;

import android.os.Build;
import android.os.FileObserver;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.droneselfie.network.DroneSelfieClient;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

import java.io.File;

import static android.content.ContentValues.TAG;


public class PathObs extends FileObserver {

    private  Subject<String> subject = PublishSubject.<String>create().toSerialized();
    private static PathObs instance;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static PathObs getInstance(File file) {
        if (instance == null){
            System.out.println("******************************");
            instance = new PathObs(file);
        }
        return instance;
    }

    public Observable<String> observable =
            subject.doOnSubscribe(__ -> startWatching()).
                    doOnDispose(() -> stopWatching()).
                    share();

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public PathObs(@NonNull File file) {
        super(file);
    }

    @Override
    public void onEvent(int event, @Nullable String path) {
        Log.d(TAG, "onEvent: PathObs " + event);
        if (event == FileObserver.CLOSE_WRITE) {
            subject.onNext(path);
        }
    }

}



package com.salab.project.kakikana;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Hold Executors allowing easily switching between threads
 */
public class ExecutorStore {

    private static ExecutorStore sInstance;
    private static final Object LOCK = new Object();
    private Executor diskIO;
    private Executor networkIO;
    private Executor mainThread;

    public ExecutorStore(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public static ExecutorStore getInstance(){
        // For Singleton instantiation
        if (sInstance == null){
            synchronized (LOCK){
                sInstance = new ExecutorStore(
                        Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor());
            }
        }
        return sInstance;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    public Executor getDiskIO() {
        return diskIO;
    }

    public Executor getNetworkIO() {
        return networkIO;
    }

    public Executor getMainThread() {
        return mainThread;
    }
}

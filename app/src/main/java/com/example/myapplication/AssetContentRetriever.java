/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.myapplication;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.WorkerThread;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Loads the assets asynchronously on background threads.
 */
public class AssetContentRetriever {

    private static final String TAG = "AssetContentRetriever";

    private WeakReference<Context> mContextRef;
    private static AssetContentRetriever instance;

    private AssetContentRetriever(Context context) {
        mContextRef = new WeakReference<>(context);
    }

    /**
     * static initializer for the AssetContentRetriever.
     * @param context
     * @return instance of {@link AssetContentRetriever}
     */
    public synchronized static AssetContentRetriever create(Context context) {
        if (instance == null)
            instance = new AssetContentRetriever(context);
        return instance;
    }

    /**
     * Fetches the data from the source provided. The data is loaded synchronously.
     * @param source the source asset, file or url to load.
     * @return the data
     */
    public String fetch(String source) {
        try {
            return loadAsset(source);
        } catch (IOException e) {
            return "{}";
        }
    }

    /**
     * loads the assets from the source file.
     * @param source
     * @return the content of the asset file.
     * @throws IOException
     */
    private String loadAsset(String source) throws IOException {

        BufferedReader reader = null;
        try {
            if (mContextRef.get() != null) {
                Context context = mContextRef.get();
                reader = new BufferedReader(
                        new InputStreamReader(
                                context.getAssets().open(source)));
                String line;
                StringBuilder result = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return "";
    }
}
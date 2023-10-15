/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.myapplication;

import android.app.Application;
import android.os.StrictMode;

import com.amazon.apl.android.APLController;
import com.amazon.apl.android.BuildConfig;
import com.amazon.apl.android.RuntimeConfig;
import com.amazon.apl.android.content.LruPackageCache;

/**
 * APL Sample Application.
 */
public class APLExampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        APLController.initializeAPL(this,
                RuntimeConfig.builder()
                        .packageCache(new LruPackageCache())
                        .build());
        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults();
        }
    }
}


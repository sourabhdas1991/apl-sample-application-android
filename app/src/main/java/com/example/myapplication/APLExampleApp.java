package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.amazon.apl.android.APLController;
import com.amazon.apl.android.APLJSONData;
import com.amazon.apl.android.APLLayout;
import com.amazon.apl.android.APLOptions;
import com.amazon.apl.android.Content;
import com.amazon.apl.android.IAPLController;
import com.amazon.apl.android.RootConfig;
import com.amazon.apl.android.content.CachingPackageLoader;
import com.amazon.apl.android.content.ContentRetriever;
import com.amazon.apl.android.content.HttpRequestHandler;
import com.amazon.apl.android.content.LocalAssetRequestHandler;
import com.amazon.apl.android.content.LocalContentRequestHandler;
import com.amazon.apl.android.dependencies.IContentRetriever;
import com.amazon.apl.android.dependencies.IPackageLoader;
import com.amazon.apl.android.dependencies.ISendEventCallbackV2;
import com.amazon.apl.android.dependencies.impl.OpenUrlCallback;
import com.amazon.apl.android.media.RuntimeMediaPlayerFactory;
import com.amazon.apl.android.providers.impl.LoggingTelemetryProvider;
import com.amazon.apl.android.providers.impl.MediaPlayerProvider;
import com.amazon.apl.android.scaling.Scaling;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (APLController.waitForInitializeAPLToComplete(null)) {
            Log.e(TAG, "APLController initialized");
            setContentView(R.layout.activity_main);
            APLLayout aplLayout = findViewById(R.id.apl);
            aplLayout.setHandleConfigurationChangeOnSizeChanged(true);
            Scaling scaling = new Scaling(10, new ArrayList<>(), new ArrayList<>());
            aplLayout.setScaling(scaling);
            APLOptions.Builder optionsBuilder = createAPLOptions();
            APLOptions options = optionsBuilder.build();
            RootConfig config = createRootConfig();
            String mainTemplate = loadDocument("testavg.json");
            IAPLController aplController = APLController.builder()
                    .aplOptions(options)
                    .rootConfig(config)
                    .aplDocument(mainTemplate)
                    .startTime(System.currentTimeMillis())
                    .aplLayout(aplLayout)
                    .disableAsyncInflate(true)
                    .render();
        }
    }

    private APLOptions.Builder createAPLOptions() {
        ISendEventCallbackV2 callback = (args, components, sources, flags) ->
                Log.i(TAG, "Got Send event. args: " + Arrays.toString(args)
                        + ", components: " + components + ", sources: " + sources);

        IContentRetriever<Uri, String> contentRetriever = createContentRetriever(this);

        return APLOptions.builder()
                .sendEventCallbackV2(callback)
                .openUrlCallback(new OpenUrlCallback(this))
                .visualContextListener(visualContext -> Log.v(TAG, "Visual context update: " + visualContext))
                .dataSourceFetchCallback((type, payload) -> {
                    Log.e("dataSourceFetchCallback", type);
                })
                .telemetryProvider(new LoggingTelemetryProvider())
                .packageLoader(createPackageLoader(contentRetriever))
                .contentDataRetriever((source, successCallback, failureCallback) -> {
                })
                .avgRetriever(contentRetriever)
                .extensionGrantRequestCallback((isGranted) -> true);
    }

    /**
     * @return the configuration for the sample application
     */
    public RootConfig createRootConfig() {
        final String agentName = "APLSampleApp configuration";
        final String agentVersion = "1.0";
        // if the docName contains 'token-based', then should register the 'dynamicTokenList' type:

        RootConfig rootConfig = RootConfig.create(agentName, agentVersion)
                .mediaPlayerFactory(new RuntimeMediaPlayerFactory(new MediaPlayerProvider()));
        return rootConfig;
    }

    /**
     * Creates a package loader that delegates requests to the content retriever by building URIs from
     * the ImportRequest.
     *
     * @param contentRetriever a content retriever
     * @return the PackageLoader
     */
    public static IPackageLoader createPackageLoader(IContentRetriever<Uri, String> contentRetriever) {
        return new CachingPackageLoader((@NonNull Content.ImportRequest request, @NonNull IContentRetriever.SuccessCallback<Content.ImportRequest, APLJSONData> successCallback, @NonNull IContentRetriever.FailureCallback<Content.ImportRequest> failureCallback) -> {
            Uri uri;
            if (!TextUtils.isEmpty(request.getSource())) {
                uri = Uri.parse(request.getSource());
            } else {
                uri = Uri.parse(getDefaultPackageUrl(request.getPackageName(), request.getVersion()));
            }
            contentRetriever.fetch(uri,
                    (source, result) -> successCallback.onSuccess(request, APLJSONData.create(result)),
                    (source, message) -> failureCallback.onFailure(request, message));
        }, APLController.getRuntimeConfig().getPackageCache());
    }

    public static IContentRetriever<Uri, String> createContentRetriever(Context context) {
        return new ContentRetriever<String>()
                .addRequestHandler(new LocalAssetRequestHandler(context))
                .addRequestHandler(new LocalContentRequestHandler(context.getContentResolver()))
                .addRequestHandler(new HttpRequestHandler());
    }

    private static final String CLOUDFRONT_LOCATION_PREFIX =
            "https://arl.assets.apl-alexa.com/packages/";
    private static final String CLOUDFRONT_LOCATION_SUFFIX = "/document.json";

    private static String getDefaultPackageUrl(final String packageName, final String version) {
        return CLOUDFRONT_LOCATION_PREFIX + packageName + "/" + version + CLOUDFRONT_LOCATION_SUFFIX;
    }

    /**
     * Loads sample APL documents from embedded assets.
     *
     * @param fileName located in assets/
     * @return contents of file
     */
    private String loadDocument(String fileName) {
        AssetContentRetriever assetContentRetriever = AssetContentRetriever.create(getApplicationContext());
        return assetContentRetriever.fetch(fileName);
    }
}

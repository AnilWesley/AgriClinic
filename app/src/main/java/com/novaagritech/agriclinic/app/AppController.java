package com.novaagritech.agriclinic.app;


import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.novaagritech.agriclinic.BuildConfig;
import com.novaagritech.agriclinic.firebase.ForceUpdateChecker;

import java.util.HashMap;
import java.util.Map;


public class AppController extends Application {

	public static final String TAG = AppController.class
			.getSimpleName();

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	private static AppController mInstance;

	public static final String ACQUIRED_USER = "acquired_users";
	public static final int DATE_DISPLAY_COUNT = 2;
	private static final String REFRESH_TOKEN = "refreshToken";
	public static final String REGISTERED_USER = "registered_users";
	public static final int STATUS_DISPLAY_COUNT = 4;
	private static final String USER_ID = "userId";
	private static AppController fr_applicationInstance = null;

	private static boolean isChatFragmentAdded = false;
	private static boolean isChatScreenActive = false;
	private static boolean shouldNotShowSkip = false;
	private int articlesScrollCount;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		initImageLoader(getApplicationContext());



		final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

		// set in-app defaults
		Map<String, Object> remoteConfigDefaults = new HashMap();
		remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
		remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, BuildConfig.VERSION_NAME );
		remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
				"https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);

		firebaseRemoteConfig.setDefaults(remoteConfigDefaults);
		firebaseRemoteConfig.fetch(60) // fetch every minutes
				.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task.isSuccessful()) {
							Log.d(TAG, "remote config is fetched.");
							firebaseRemoteConfig.activateFetched();
						}
					}
				});

	}





	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config.build());





	}
	public static synchronized AppController getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(this.mRequestQueue,
					new LruBitmapCache());
		}
		return this.mImageLoader;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
}

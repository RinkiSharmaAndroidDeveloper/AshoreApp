package com.trutek.looped;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.onesignal.OneSignal;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.StoringMechanism;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.utils.Constants;
import com.trutek.looped.data.modules.DaggerAppComponent;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.data.modules.AppComponent;
import com.trutek.looped.data.modules.AppModule;
import com.trutek.looped.data.modules.DaoModule;
import com.trutek.looped.data.modules.MapperModule;
import com.trutek.looped.data.modules.RepositoryModule;
import com.trutek.looped.data.modules.ServiceModule;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(
        mailTo = "looped@mindfulsas.com",
        mode= ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_report_message,
        resDialogOkToast = R.string.crash_report_ok_message
)

public class App extends MultiDexApplication {

    private static App instance;
    private AppComponent component;
    private Tracker mTracker;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupGraph();
        ACRA.init(this);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.None)
                .init();
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        initApplication();
        getDefaultTracker();
    }
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    private void initApplication() {
        instance = this;
        new PreferenceHelper(this);

        initDb();
        initQb();
        initImageLoader(this);
    }

    private void setupGraph() {
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .daoModule(new DaoModule())
                .mapperModule(new MapperModule())
                .repositoryModule(new RepositoryModule())
                .serviceModule(new ServiceModule())
                .build();
        component.inject(this);
    }

    public AppComponent component() {
        return component;
    }

    private void initDb() {
        DataManager.init(this);
    }

    private void initQb() {
        QBSettings.getInstance().setStoringMehanism(StoringMechanism.UNSECURED);
        QBChatService.setDebugEnabled(Constants.DEBUG);
        QBSettings.getInstance().init(this,Constants.APP_ID,
                Constants.AUTH_KEY,
                Constants.AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(Constants.ACCOUNT_KEY);
    }

    private void initImageLoader(Context context) {
        ImageLoader.getInstance().init(ImageLoaderUtils.getImageLoaderConfiguration(context));
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

}

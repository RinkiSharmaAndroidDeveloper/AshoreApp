package com.trutek.looped.data.modules;

import android.app.Application;
import android.content.Context;

import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.data.impl.services.CommunityService;
import com.trutek.looped.utils.NotificationData;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    public Application providesApplication() {
        return mApplication;
    }

    @Provides
    Context providesContext(Application application) {
        return application;
    }


    @Provides
    NotificationData provideNotificationData(Context context, ICommunityService iCommunityService){
        return new NotificationData(context,iCommunityService);

    }

}

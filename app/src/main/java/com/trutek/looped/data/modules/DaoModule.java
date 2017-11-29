package com.trutek.looped.data.modules;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.impl.entities.ActivityDao;
import com.trutek.looped.data.impl.entities.CategoryDao;
import com.trutek.looped.data.impl.entities.CommentDao;
import com.trutek.looped.data.impl.entities.CommunityDao;
import com.trutek.looped.data.impl.entities.ConnectionDao;
import com.trutek.looped.data.impl.entities.DaoMaster;
import com.trutek.looped.data.impl.entities.DaoSession;
import com.trutek.looped.data.impl.entities.DiseaseDao;
import com.trutek.looped.data.impl.entities.HealthChartDao;
import com.trutek.looped.data.impl.entities.HealthParamDao;
import com.trutek.looped.data.impl.entities.HealthParamLogDao;
import com.trutek.looped.data.impl.entities.InterestDao;
import com.trutek.looped.data.impl.entities.LocationDao;
import com.trutek.looped.data.impl.entities.LoopDao;
import com.trutek.looped.data.impl.entities.NotificationDao;
import com.trutek.looped.data.impl.entities.ProfileDao;
import com.trutek.looped.data.impl.entities.ProviderDao;
import com.trutek.looped.data.impl.entities.RecipientDao;
import com.trutek.looped.data.impl.entities.TagDao;
import com.trutek.looped.data.impl.entities.UserDao;

import dagger.Module;
import dagger.Provides;

@Module
public class DaoModule {

    static SQLiteDatabase _database;
    static DaoMaster _daoMaster;
    static DaoSession _daoSession;

    @Provides
    SQLiteDatabase providesSQLiteDatabase(Context context) {
        if (_database == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "Simplehrms-db", null);
            _database = helper.getWritableDatabase();
        }

        return _database;
    }

    @Provides
    DaoMaster providesDaoMaster(SQLiteDatabase database) {
        if (_daoMaster == null) {
            _daoMaster = new DaoMaster(database);
        }
        return _daoMaster;
    }

    @Provides
    DaoSession providesDaoSession(DaoMaster daoMaster) {
        if (_daoSession == null) {
            _daoSession = daoMaster.newSession();
        }
        return _daoSession;
    }

    @Provides
    UserDao providesContactDao(DaoSession daoSession){
        return daoSession.getUserDao();
    }

    @Provides
    ProfileDao providesProfileDao(DaoSession daoSession){
        return daoSession.getProfileDao();
    }

    @Provides
    CommunityDao providesCommunityDao(DaoSession daoSession){
        return daoSession.getCommunityDao();
    }

    @Provides
    InterestDao providesInterestDao(DaoSession daoSession){
        return daoSession.getInterestDao();
    }

    @Provides
    TagDao providesTagDao(DaoSession daoSession){
        return daoSession.getTagDao();
    }

    @Provides
    ConnectionDao providesConnectionDao(DaoSession daoSession){
        return daoSession.getConnectionDao();
    }

    @Provides
    ActivityDao providesActivityDao(DaoSession daoSession){
        return daoSession.getActivityDao();
    }

    @Provides
    NotificationDao providesNotificationDao(DaoSession daoSession){
        return daoSession.getNotificationDao();
    }

    @Provides
    RecipientDao providesRecipientDao(DaoSession daoSession){
        return daoSession.getRecipientDao();
    }

    @Provides
    DiseaseDao providesDiseaseDao(DaoSession daoSession){
        return daoSession.getDiseaseDao();
    }

    @Provides
    HealthChartDao providesHealthChartDao(DaoSession daoSession){
        return daoSession.getHealthChartDao();
    }

    @Provides
    HealthParamDao providesHealthParamDao(DaoSession daoSession){
        return daoSession.getHealthParamDao();
    }

    @Provides
    LoopDao providesLoopDao(DaoSession daoSession){
        return daoSession.getLoopDao();
    }

    @Provides
    HealthParamLogDao ProvidesHealthParamLogDao(DaoSession daoSession){
        return daoSession.getHealthParamLogDao();
    }


    @Provides
    ProviderDao providesProviderDao(DaoSession daoSession){
        return daoSession.getProviderDao();
    }

    @Provides
    LocationDao providesLocationDao(DaoSession daoSession){
        return daoSession.getLocationDao();
    }

    @Provides
    CommentDao providesCommentDao(DaoSession daoSession){
        return daoSession.getCommentDao();
    }

    @Provides
    CategoryDao providesCategoryDao(DaoSession daoSession){
        return  daoSession.getCategoryDao();
    }

}
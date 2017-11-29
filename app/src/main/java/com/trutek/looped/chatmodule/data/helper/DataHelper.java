package com.trutek.looped.chatmodule.data.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.impl.entities.ActivityDao;
import com.trutek.looped.data.impl.entities.AttachmentDao;
import com.trutek.looped.data.impl.entities.ChatUserDao;
import com.trutek.looped.data.impl.entities.CommunityDao;
import com.trutek.looped.data.impl.entities.DaoMaster;
import com.trutek.looped.data.impl.entities.DaoSession;
import com.trutek.looped.data.impl.entities.DialogDao;
import com.trutek.looped.data.impl.entities.DialogNotificationDao;
import com.trutek.looped.data.impl.entities.DialogUsersDao;
import com.trutek.looped.data.impl.entities.MessageDao;

public class DataHelper {

    private SQLiteDatabase _database;
    private DaoMaster _daoMaster;
    private DaoSession _daoSession;

    public DataHelper(Context context){
        providesSQLiteDatabase(context);
        providesDaoMaster(_database);
        providesDaoSession(_daoMaster);
    }

    private SQLiteDatabase providesSQLiteDatabase(Context context) {
        if (_database == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "Simplehrms-db", null);
            _database = helper.getWritableDatabase();
        }
        return _database;
    }

    private DaoMaster providesDaoMaster(SQLiteDatabase database) {
        if (_daoMaster == null) {
            _daoMaster = new DaoMaster(database);
        }
        return _daoMaster;
    }

    private DaoSession providesDaoSession(DaoMaster daoMaster) {
        if (_daoSession == null) {
            _daoSession = daoMaster.newSession();
        }
        return _daoSession;
    }

    public MessageDao getMessageDao(){
        return _daoSession.getMessageDao();
    }

    public DialogDao getDialogDao(){
        return _daoSession.getDialogDao();
    }

    public AttachmentDao getAttachmentDao(){
        return _daoSession.getAttachmentDao();
    }

    public ChatUserDao getChatUserDao(){
        return _daoSession.getChatUserDao();
    }

    public DialogUsersDao getDialogUserMapDao(){
        return _daoSession.getDialogUsersDao();
    }

    public DialogNotificationDao getDialogNotificationDao(){
        return _daoSession.getDialogNotificationDao();
    }

    public ActivityDao getActivityDao(){
        return _daoSession.getActivityDao();
    }

    public CommunityDao getCommunityDao(){
        return _daoSession.getCommunityDao();
    }
}

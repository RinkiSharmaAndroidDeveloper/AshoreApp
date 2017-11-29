package com.trutek.looped.msas.common.helpers;


import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {

    public static final String USER_ID = "user_id";
    public static final String USER_EMAIL = "email";
    public static final String USER_MOBILE = "mobile";
    public static final String SESSION_TOKEN = "session_token";
    public static final String USER_LOGIN_TYPE = "login_type";
    public static final String USER_PASSWORD = "password";
    public static final String USER_QB_ID = "QB_id";
    public static final String USER_QB_PASSWORD = "QB_password";
    public static final String USER_SERVER_ID = "serverId";
    public static final String USER_PROFILE_ID = "profile_id";
    public static final String USER_FACEBOOK_ID = "facebookId";
    public static final String USER_FACEBOOK_PIC_URL = "facebookPicUrl";
    public static final String USER_PIC_URL = "picUrl";
    public static final String USER_IS_PROFILE_COMPLETE = "isProfileComplete";
    public static final String PARENT_ACTIVITY = "parentActivity";


    /*General details*/
    public static final String FULL_NAME = "full_name";
    public static final String GENDER = "gender";
    public static final String B_DAY = "Birthday";
    public static final String ABOUT_US = "AboutUs";
    public static final String LOCATION_NAME = "LOC_Name";
    public static final String LOCATION_DESCRIPTION = "LOC_Desc";
    public static final String LOCATION_LAT = "LOC_Lat";
    public static final String LOCATION_LONG = "LOC_Long";

    /*setting*/
    public static final String SOUND = "setting";

    /*push notification*/
    public static final String PUSH_NEED_TO_OPEN_DIALOG = "push_need_to_open_dialog";
    public static final String PUSH_DIALOG_ID = "push_dialog_id";
    public static final String PUSH_USER_ID = "push_user_id";
    public static final String CURRENTLY_VISIBLE_DIALOG_ID = "currently_visible_dialog";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static PreferenceHelper instance;

    public static PreferenceHelper getPrefsHelper() {
        return instance;
    }

    public PreferenceHelper(Context context) {
        instance = this;
        String prefsFile = context.getPackageName();
        sharedPreferences = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void delete(String key) {
        if (sharedPreferences.contains(key)) {
            editor.remove(key).commit();
        }
    }

    public void savePreference(String key, Object value) {
        delete(key);

        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        } else if (value != null) {
            throw new RuntimeException("Attempting to save non-primitive preference");
        }

        editor.commit();
    }

    @SuppressWarnings("unchecked")
    public <T> T getPreference(String key) {
        return (T) sharedPreferences.getAll().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPreference(String key, T defValue) {
        T returnValue = (T) sharedPreferences.getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }

    public boolean isPreferenceExists(String key) {
        return sharedPreferences.contains(key);
    }
}

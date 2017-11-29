package com.trutek.looped.chatmodule.utils;

import android.util.Log;

import com.quickblox.core.exception.QBResponseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by msas on 9/19/2016.
 */
public class Utils {

    public static final String TOKEN_REQUIRED_ERROR = "Token is required";

    public static int[] toIntArray(List<Integer> integerList) {
        int[] intArray = new int[integerList.size()];
        int i = 0;
        for (Integer e : integerList) {
            intArray[i++] = e.intValue();
        }
        return intArray;
    }


    public static ArrayList<Integer> toArrayList(int[] itemArray) {
        ArrayList<Integer> integerList = new ArrayList<Integer>(itemArray.length);
        for (int item : itemArray) {
            integerList.add(item);
        }
        return integerList;
    }

    public static boolean validateNotNull(Object object) {
        return object != null;
    }

    public static boolean isExactError(QBResponseException e, String msgError) {
        Log.d(Utils.class.getSimpleName(), "");
        List<String> errors = e.getErrors();
        for (String error : errors) {
            Log.d(Utils.class.getSimpleName(), "error =" + error);
            if (error.contains(msgError)) {
                Log.d(Utils.class.getSimpleName(), error + " contains " + msgError);
                return true;
            }
        }
        return false;
    }


    public static boolean isTokenDestroyedError(QBResponseException e) {
        List<String> errors = e.getErrors();
        for (String error : errors) {
            if (TOKEN_REQUIRED_ERROR.equals(error)) {
                return true;
            }
        }
        return false;
    }
}

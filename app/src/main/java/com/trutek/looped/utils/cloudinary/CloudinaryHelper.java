package com.trutek.looped.utils.cloudinary;

import android.content.Context;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.trutek.looped.chatmodule.helpers.BaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class CloudinaryHelper extends BaseHelper{

    private static Cloudinary cloudinary;

    public CloudinaryHelper(Context context){
        super(context);

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "do88olltv");
        config.put("api_key", "671911767542257");
        config.put("api_secret", "TxO4rzOQMrl4Ibp1ueY-7pfTFuw");
        cloudinary = new Cloudinary(config);
    }

    public Map uploadImage(final File file){
        Map map = null;
        try {
            map = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}

package com.trutek.looped.msas.common.repositories;

import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.IModel;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.models.DataModel;
import com.trutek.looped.msas.common.models.ModelState;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.models.RemoteData;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Hashtable;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RemoteRepository<TModel extends IModel> {

    protected final Gson _gson;
    protected final Type _dataType;
    protected final Type _pageType;
    private final MediaType _mediaType = MediaType.parse("application/json; charset=utf-8");
    private final String _key;
    private final String _url;
    OkHttpClient _httpClient = new OkHttpClient();


    public RemoteRepository(String key, Type pageType, Type dataType) {
        _dataType = dataType;
        _gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Date.class, new DateHelper())
                .registerTypeHierarchyAdapter(Date.class, new DateHelper())
                .create();

        _key = key;
        _pageType = pageType;
        _url = Constants.URL+ "/" + _key;
    }

    public String rawPost(String data) {
        return rawPost(null, data);
    }

    public String rawPost(String action, String data) {
        String url = _url;
        if (action != null) {
            if(!action.equals(""))
                url = _url + "/" + action;
        }
        if(null != data) {
            Log.i("POST:START", url + "; Data: " + data.toString());
        }else{
            Log.i("POST:START", url);
        }

        try {
            RequestBody body = RequestBody.create(_mediaType, data == null ? "" : data);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("x-access-token", PreferenceHelper.getPrefsHelper().getPreference(PreferenceHelper.SESSION_TOKEN, ""))
                    .build();
            Response response = _httpClient.newCall(request).execute();
            String dataBody = response.body().string();
            Log.d("POST:DATA[" + url + "]", dataBody);
            return dataBody;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String rawPut(String action, String data) {
        String url = _url + "/" + action;

        if(null != data) {
            Log.i("PUT:START", url + "; Data: " + data);
        }else{
            Log.i("PUT:START", url);
        }

        try {
            RequestBody body = RequestBody.create(_mediaType, data);
            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .addHeader("x-access-token", PreferenceHelper.getPrefsHelper().getPreference(PreferenceHelper.SESSION_TOKEN, ""))
                    .build();
            Response response = _httpClient.newCall(request).execute();
            String dataBody = response.body().string();
            Log.d("PUT:DATA[" + url + "]", dataBody);
            return dataBody;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public String delete(String id) {
        return delete(id, null);
    }
    public String delete(String id,String action) {
        String url = _url;
        if (action != null) {
            if(!action.equals(""))
                url = _url + "/" + id+ "/" + action;
        }else{
            url = _url + "/" + id;
        }
        if(null != action) {
            Log.i("DELETE:START", url + "; Data: " + action.toString());
        }else{
            Log.i("DELETE:START", url);
        }
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("x-access-token", PreferenceHelper.getPrefsHelper().getPreference(PreferenceHelper.SESSION_TOKEN,""))
                    .delete()
                    .build();
            Response response = _httpClient.newCall(request).execute();

            String dataBody = response.body().string();
            Log.d("DELETE:DATA[" + url + "]", dataBody);
            return dataBody;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String remoteGet(Hashtable<String, String> params, String action) {
        return action == null ? remoteGet(_url, params) : remoteGet(_url + "/" + action, params);
    }

    private String remoteGet(String url, Hashtable<String, String> params) {

        String query = "";
        if (params != null) {
            Uri.Builder uriBuilder = new Uri.Builder();

            for (String key : params.keySet()) {
                String value = params.get(key);
                if (!value.equals(""))
                    uriBuilder.appendQueryParameter(key, value);
            }

            query = uriBuilder.build().getEncodedQuery();
        }

        String getUrl = url + (query == "" ? "" : "?" + query);
        Log.i("GET:START", getUrl);
        Log.d("TOKEN: ", PreferenceHelper.getPrefsHelper().getPreference(PreferenceHelper.SESSION_TOKEN, ""));

        try {
            Request request = new Request.Builder()
                    .url(getUrl)
                    .addHeader("x-access-token", PreferenceHelper.getPrefsHelper().getPreference(PreferenceHelper.SESSION_TOKEN, ""))
                    .build();

            Response response = _httpClient.newCall(request).execute();
            String body = response.body().string();
            Log.d("GET:DATA[" + getUrl + "]", body);

            return body;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public TModel get(String id) throws RemoteException {
        return get(id, null);
    }

    public TModel get(String id, String action) throws RemoteException {
        String response = getRaw(id, action);
        DataModel<TModel> responseData = _gson.fromJson(response, _dataType);

        if (responseData.isSuccess) {
            return responseData.data;
        }

        throw new RemoteException(responseData.getError());
    }

    public TModel get(PageInput input, String action) throws RemoteException {
        String response = getRaw(input, action);
        DataModel<TModel> responseData = _gson.fromJson(response, _dataType);

        if (responseData.isSuccess) {
            return responseData.data;
        }

        throw new RemoteException(responseData.getError());
    }

    public String getRaw(String id) {
        return getRaw(id, null);
    }

    public String getRaw(PageInput input, String action) {
        return action == null ?
                remoteGet(_url +  "/" + _key, input.getParams()) :
                remoteGet(_url + "/" + action , input.getParams());
    }

    public String getRaw(String id, String action) {
        return action == null ?
                remoteGet(_url + "/" + id, null) :
                remoteGet(_url + "/" + action + "/" + id, null);
    }

    public Page<TModel> page(PageInput input) throws RemoteException {
        return page(input, _key);
    }

    public Page<TModel> page(PageInput input, String action) throws RemoteException {

        String response;
        if (input == null){
            response = pageRaw(action);
        } else {
            response = pageRaw(input, action);
        }

        Page<TModel> responseData = _gson.fromJson(response, _pageType);

        responseData.Total = (long) responseData.items.size();

        if (responseData.isSuccess) {
            return responseData;
        }
        throw new RemoteException(responseData.getError());
    }

    public Page<TModel> page(String token, PageInput input, String action) throws RemoteException {

        String response = pageRaw(input, action);

        Page<TModel> responseData = _gson.fromJson(response, _pageType);

        responseData.Total = (long) responseData.items.size();

        if (responseData.isSuccess) {
            return responseData;
        }
        throw new RemoteException(responseData.getError());
    }

    public String pageRaw(PageInput input) {
        return pageRaw(input, null);
    }

    public String pageRaw(PageInput input, String action) {
        return action == null ? remoteGet(input.getParams(), null) : remoteGet(input.getParams(), action);
    }

    public String pageRaw(String action) {
        return remoteGet(null, action);
    }

    public TModel update(TModel model) throws RemoteException {
        return update(null, model);
    }

    public TModel update(String action) throws RemoteException {
        return update(action, null);
    }

    public TModel update(String action, TModel model) throws RemoteException {
        String actionValue;
        String trackingId = null;
        if(model != null) {
            trackingId = model.getServerId();
        }

        actionValue = action == null ? trackingId : action;

        if (actionValue == null) {
            return create(model);
        }

        DataModel<TModel> responseData;

        if(model == null){
            responseData = _gson.fromJson(rawPut(actionValue, ""), _dataType);
        } else {
            String data = _gson.toJson(model);
            responseData = _gson.fromJson(rawPut(actionValue, data), _dataType);
        }

        if (responseData.isSuccess) {
//            if(model.getStatus().equals(ModelState.deleted)){
//                model.setStatus(ModelState.deleted .getValue());
//            }else {
//                model.setStatus(ModelState.synced.getValue());
//            }
            return responseData.data;
        }
        throw new RemoteException(responseData.getError());
    }

    public TModel create(TModel model) throws RemoteException {
        return create(null, model);
    }

    public TModel create(String action, TModel model) throws RemoteException {
        String data = _gson.toJson(model);
        String responseJson;

        if(action == null){
            responseJson = rawPost("", data);
        } else {
            responseJson = rawPost(action, data);
        }

        DataModel<TModel> responseData = _gson.fromJson(responseJson, _dataType);

        if (responseData.isSuccess) {
//            model.setStatus(ModelState.synced.getValue());
            return responseData.data;
        }
        throw new RemoteException(responseData.getError());
    }

    public Page<TModel> createPage(TModel model) throws RemoteException {
        return createPage(null, model);
    }

    public Page<TModel> createPage(String action, TModel model) throws RemoteException {
        String data = _gson.toJson(model);
        String responseJson;

        if(action == null){
            responseJson = rawPost("", data);
        } else {
            responseJson = rawPost(action, data);
        }

        Page<TModel> responseData = _gson.fromJson(responseJson, _pageType);

        if (responseData.isSuccess) {
//            model.setStatus(ModelState.synced.getValue());
            return responseData;
        }
        throw new RemoteException(responseData.getError());
    }

    public TModel deleteData(TModel model) throws RemoteException {
        return deleteData(null, model);
    }

    public TModel deleteData(String action, TModel model) throws RemoteException {
        String data = _gson.toJson(model);
        String responseJson;

        if(action == null){
            responseJson = delete("", data);
        } else {
            responseJson = delete(action, data);
        }

        DataModel<TModel> responseData = _gson.fromJson(responseJson, _dataType);

        if (responseData.isSuccess) {
//            model.setStatus(ModelState.synced.getValue());
            return responseData.data;
        }
        throw new RemoteException(responseData.getError());
    }
}

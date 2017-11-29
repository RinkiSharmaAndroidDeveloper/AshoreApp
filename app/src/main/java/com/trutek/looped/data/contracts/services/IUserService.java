package com.trutek.looped.data.contracts.services;

import com.trutek.looped.data.contracts.models.DeviceModel;
import com.trutek.looped.data.contracts.models.FacebookModel;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;
import com.trutek.looped.data.contracts.models.LoginModel;
import com.trutek.looped.data.contracts.models.UserModel;

public interface IUserService extends ICRUDService<UserModel> {

    void signUp(LoginModel model, AsyncResult<UserModel> result);

    void register(LoginModel model, AsyncResult<UserModel> result);

    void validatePin(LoginModel model, AsyncResult<UserModel> result);

    void signUpWithFacebook(FacebookModel model, AsyncResult<UserModel> result);

    void clearDatabase();

}

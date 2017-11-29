package com.trutek.looped.data.contracts.apis;

import com.trutek.looped.data.contracts.models.DeviceModel;
import com.trutek.looped.data.contracts.models.FacebookModel;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.data.contracts.models.LoginModel;
import com.trutek.looped.data.contracts.models.UserModel;

public interface IUserApi<TModel> {

    void signUp(LoginModel loginModel, AsyncResult<UserModel> result);

    void register(LoginModel loginModel, AsyncResult<UserModel> result);

    void validatePin(LoginModel loginModel, AsyncResult<UserModel> result);

    void signUpWithFacebook(FacebookModel model, AsyncResult<UserModel> result);

}
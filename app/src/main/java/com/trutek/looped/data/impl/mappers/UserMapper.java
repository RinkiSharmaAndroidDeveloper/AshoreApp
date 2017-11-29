package com.trutek.looped.data.impl.mappers;

import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.data.contracts.models.UserModel;
import com.trutek.looped.data.impl.entities.User;

public class UserMapper implements IModelMapper<User, UserModel> {

    @Override
    public UserModel Map(User user) {
        UserModel userModel = new UserModel();
        userModel.setId(user.getId());
        return userModel;
    }
}

package com.trutek.looped.data.impl.mappers;

import com.trutek.looped.data.contracts.models.ChatModel;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.impl.entities.Connection;
import com.trutek.looped.msas.common.contracts.IModelMapper;

/**
 * Created by msas on 9/29/2016.
 */
public class ConnectionMapper implements IModelMapper<Connection, ConnectionModel> {

    @Override
    public ConnectionModel Map(Connection connection) {
        ConnectionModel connectionModel = new ConnectionModel();
        connectionModel.setId(connection.getId());
        connectionModel.setServerId(connection.getServerId());
        connectionModel.profile = new ProfileModel();
        connectionModel.profile.name = connection.getName();
        connectionModel.profile.chat = new ChatModel();
        connectionModel.profile.chat.id = connection.getJabberId();
        connectionModel.profile.picUrl = connection.getPicUrl();
        connectionModel.profile.id = connection.getServerId();
        return connectionModel;

    }
}

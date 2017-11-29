package com.trutek.looped.chatmodule.data.impl.repository;

import com.trutek.looped.chatmodule.data.contracts.models.DialogUserModel;
import com.trutek.looped.data.impl.entities.DialogUsers;
import com.trutek.looped.data.impl.entities.DialogUsersDao;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.repositories.base.BaseRepository;

import java.util.Locale;
import java.util.Observer;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

/**
 * Created by msas on 9/15/2016.
 */
public class DialogUsersRepository extends BaseRepository<DialogUsers, DialogUserModel> {

    private AbstractDao<DialogUsers, Long> dao;

    public DialogUsersRepository(IModelMapper<DialogUsers, DialogUserModel> mapper, AbstractDao<DialogUsers, Long> dao) {
        super(null,mapper, dao, DialogUsersRepository.class.getSimpleName());
        this.dao = dao;
    }

    @Override
    protected QueryBuilder<DialogUsers> query(Long id) {
        return dao.queryBuilder().where(DialogUsersDao.Properties.Id.eq(id));
    }

    @Override
    protected QueryBuilder<DialogUsers> query(PageQuery query) {
        QueryBuilder<DialogUsers> queryBuilder = dao.queryBuilder();
        if(query.contains("id")){
            queryBuilder.where(DialogUsersDao.Properties.Id.eq(query.getDate("id")));
        }
        if(query.contains("dialogId")){
            queryBuilder.where(DialogUsersDao.Properties.DialogId.eq(query.getString("dialogId")));
        }
        if(query.contains("userId")){
            queryBuilder.where(DialogUsersDao.Properties.UserId.eq(query.getInteger("userId")));
        }
        if(query.contains("userStatus")){
            queryBuilder.where(DialogUsersDao.Properties.DialogId.ge(query.getString("userStatus")));
        }
        if(query.contains("actualDialogOccupantsByIds")){
            queryBuilder.where(DialogUsersDao.Properties.DialogId.eq(query.getString("dialogId")));
            queryBuilder.where(DialogUsersDao.Properties.UserStatus.eq(query.getString("userStatus")));
        }

        if(query.contains(Constants.QUERY_KEY_OCCUPANT_1_ID) && query.contains(Constants.QUERY_KEY_OCCUPANT_2_ID)){

            queryBuilder.where(DialogUsersDao.Properties.UserId.eq(query.getInteger(Constants.QUERY_KEY_OCCUPANT_1_ID))).join(DialogUsersDao.Properties.DialogId,DialogUsers.class, DialogUsersDao.Properties.DialogId)
                    .where(DialogUsersDao.Properties.UserId.eq(query.getInteger(Constants.QUERY_KEY_OCCUPANT_2_ID)));

           /* queryBuilder.where(new WhereCondition.StringCondition(
                    String.format(Locale.getDefault(),"select dialog_id from dialog_users where user_id='%d' " +
                    "INTERSECT select dialog_id from dialog_users where user_id='%d'",query.getInteger(Constants.QUERY_KEY_OCCUPANT_1_ID)
                            ,query.getInteger(Constants.QUERY_KEY_OCCUPANT_2_ID))
            ));*/
        }

        return queryBuilder;
    }

    @Override
    protected void map(DialogUserModel model) {

    }

    @Override
    protected void map(DialogUsers dialogUsers, DialogUserModel model) {
        dialogUsers.setId(model.getId());
        dialogUsers.setUserStatus(model.getUserStatus().name());

        if(model.getDialog() != null){
            dialogUsers.setDialogId(model.getDialog().getDialogId());
        } else {
            dialogUsers.setDialogId(model.getDialogId());
        }

        if(model.getChatUser() != null){
            dialogUsers.setUserId(model.getChatUser().getUserId());
        } else {
            dialogUsers.setUserId(model.getUserId());
        }
    }

    @Override
    protected DialogUsers newEntity() {
        return new DialogUsers();
    }

    @Override
    public void addObservers(Observer observer) {
        this.addObserver(observer);
    }

    @Override
    public void deleteObservers(Observer observer) {
        this.deleteObserver(observer);
    }
}

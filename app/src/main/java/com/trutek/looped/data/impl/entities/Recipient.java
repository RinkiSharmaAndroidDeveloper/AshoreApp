package com.trutek.looped.data.impl.entities;

import java.util.List;
import com.trutek.looped.data.impl.entities.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "RECIPIENT".
 */
public class Recipient {

    private Long id;
    private String name;
    private Integer age;
    private String gender;
    private String picUrl;
    private Long jabberId;
    private String serverId;
    private java.util.Date timeStamp;
    private String syncStatus;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient RecipientDao myDao;

    private List<Provider> providerList;

    public Recipient() {
    }

    public Recipient(Long id) {
        this.id = id;
    }

    public Recipient(Long id, String name, Integer age, String gender, String picUrl, Long jabberId, String serverId, java.util.Date timeStamp, String syncStatus) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.picUrl = picUrl;
        this.jabberId = jabberId;
        this.serverId = serverId;
        this.timeStamp = timeStamp;
        this.syncStatus = syncStatus;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRecipientDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Long getJabberId() {
        return jabberId;
    }

    public void setJabberId(Long jabberId) {
        this.jabberId = jabberId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public java.util.Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(java.util.Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Provider> getProviderList() {
        if (providerList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProviderDao targetDao = daoSession.getProviderDao();
            List<Provider> providerListNew = targetDao._queryRecipient_ProviderList(id);
            synchronized (this) {
                if(providerList == null) {
                    providerList = providerListNew;
                }
            }
        }
        return providerList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetProviderList() {
        providerList = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}

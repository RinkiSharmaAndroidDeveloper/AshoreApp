package com.trutek.looped.data.impl.entities;

import java.util.List;
import com.trutek.looped.data.impl.entities.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "PROFILE".
 */
public class Profile {

    private Long id;
    private String name;
    private java.util.Date dateOfBirth;
    private String about;
    private String interests;
    private Long jabberId;
    private Boolean isMine;
    private String serverId;
    private java.util.Date timeStamp;
    private Integer syncStatus;
    private Integer age;
    private String picUrl;
    private String gender;
    private String location;
    private String locationlat;
    private String locationlng;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient ProfileDao myDao;

    private List<Interest> interestList;
    private List<Category> categoryList;
    private List<Tag> tagList;

    public Profile() {
    }

    public Profile(Long id) {
        this.id = id;
    }

    public Profile(Long id, String name, java.util.Date dateOfBirth, String about, String interests, Long jabberId, Boolean isMine, String serverId, java.util.Date timeStamp, Integer syncStatus, Integer age, String picUrl, String gender, String location, String locationlat, String locationlng) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.about = about;
        this.interests = interests;
        this.jabberId = jabberId;
        this.isMine = isMine;
        this.serverId = serverId;
        this.timeStamp = timeStamp;
        this.syncStatus = syncStatus;
        this.age = age;
        this.picUrl = picUrl;
        this.gender = gender;
        this.location = location;
        this.locationlat = locationlat;
        this.locationlng = locationlng;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getProfileDao() : null;
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

    public java.util.Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(java.util.Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public Long getJabberId() {
        return jabberId;
    }

    public void setJabberId(Long jabberId) {
        this.jabberId = jabberId;
    }

    public Boolean getIsMine() {
        return isMine;
    }

    public void setIsMine(Boolean isMine) {
        this.isMine = isMine;
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

    public Integer getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationlat() {
        return locationlat;
    }

    public void setLocationlat(String locationlat) {
        this.locationlat = locationlat;
    }

    public String getLocationlng() {
        return locationlng;
    }

    public void setLocationlng(String locationlng) {
        this.locationlng = locationlng;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Interest> getInterestList() {
        if (interestList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            InterestDao targetDao = daoSession.getInterestDao();
            List<Interest> interestListNew = targetDao._queryProfile_InterestList(id);
            synchronized (this) {
                if(interestList == null) {
                    interestList = interestListNew;
                }
            }
        }
        return interestList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetInterestList() {
        interestList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Category> getCategoryList() {
        if (categoryList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CategoryDao targetDao = daoSession.getCategoryDao();
            List<Category> categoryListNew = targetDao._queryProfile_CategoryList(id);
            synchronized (this) {
                if(categoryList == null) {
                    categoryList = categoryListNew;
                }
            }
        }
        return categoryList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetCategoryList() {
        categoryList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Tag> getTagList() {
        if (tagList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TagDao targetDao = daoSession.getTagDao();
            List<Tag> tagListNew = targetDao._queryProfile_TagList(id);
            synchronized (this) {
                if(tagList == null) {
                    tagList = tagListNew;
                }
            }
        }
        return tagList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetTagList() {
        tagList = null;
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

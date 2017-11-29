package com.trutek.looped.msas.common.contracts;

import com.trutek.looped.msas.common.models.ModelState;

import java.io.Serializable;
import java.util.Date;

public interface IModel extends Serializable {

    Long getId();

    void setId(Long id);

    Date getTimeStamp();

    void setTimeStamp(Date timeStamp);

    ModelState getStatus();

    void setStatus(Integer status);

    String getServerId();

    void setServerId(String id);

}

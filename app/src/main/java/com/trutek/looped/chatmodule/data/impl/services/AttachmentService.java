package com.trutek.looped.chatmodule.data.impl.services;

import com.trutek.looped.chatmodule.data.contracts.models.AttachmentModel;
import com.trutek.looped.chatmodule.data.contracts.services.IAttachmentService;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.services.base.BaseService;

/**
 * Created by msas on 9/15/2016.
 */
public class AttachmentService extends BaseService<AttachmentModel> implements IAttachmentService {

    public AttachmentService(IRepository<AttachmentModel> local) {
        super(local);
    }
}

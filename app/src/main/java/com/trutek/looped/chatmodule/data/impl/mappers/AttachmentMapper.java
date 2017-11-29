package com.trutek.looped.chatmodule.data.impl.mappers;

import com.trutek.looped.chatmodule.data.contracts.models.AttachmentModel;
import com.trutek.looped.data.impl.entities.Attachment;
import com.trutek.looped.msas.common.contracts.IModelMapper;

/**
 * Created by msas on 9/15/2016.
 */
public class AttachmentMapper implements IModelMapper<Attachment, AttachmentModel> {

    @Override
    public AttachmentModel Map(Attachment attachment) {
        AttachmentModel attachmentModel = new AttachmentModel();
        attachmentModel.setId(attachment.getId());
        attachmentModel.setAttachmentId(attachment.getAttachmentId());
        attachmentModel.setName(attachment.getName());
        attachmentModel.setType(AttachmentModel.Type.valueOf(attachment.getType()));
        attachmentModel.setSize(attachment.getSize());
        attachmentModel.setURL(attachment.getURL());
        return attachmentModel;
    }
}

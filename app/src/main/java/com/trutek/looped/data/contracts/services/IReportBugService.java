package com.trutek.looped.data.contracts.services;

import com.trutek.looped.data.contracts.models.ReportBugModel;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;

public interface IReportBugService extends ICRUDService<ReportBugModel> {

    void reportBug(ReportBugModel reportModel, AsyncResult<ReportBugModel> result);
}

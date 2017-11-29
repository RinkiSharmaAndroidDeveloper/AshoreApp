package com.trutek.looped.data.impl.services;

import com.trutek.looped.data.contracts.apis.IReportBugApi;
import com.trutek.looped.data.contracts.models.ReportBugModel;
import com.trutek.looped.data.contracts.services.IReportBugService;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.services.base.BaseService;

/**
 * Created by msas on 10/20/2016.
 */
public class ReportBugService extends BaseService<ReportBugModel> implements IReportBugService {

    private IReportBugApi<ReportBugModel> remote;

    public ReportBugService(IReportBugApi<ReportBugModel> remote) {
        super(null);
        this.remote = remote;
    }

    @Override
    public void reportBug(ReportBugModel reportModel, AsyncResult<ReportBugModel> result) {
        remote.create(reportModel, result);
    }

}

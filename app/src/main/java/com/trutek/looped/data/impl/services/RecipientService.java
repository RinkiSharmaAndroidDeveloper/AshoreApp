package com.trutek.looped.data.impl.services;

import android.util.Log;

import com.trutek.looped.data.contracts.models.DiseaseModel;
import com.trutek.looped.data.contracts.models.LoopModel;
import com.trutek.looped.data.contracts.models.ProviderModel;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.contracts.services.IRecipientService;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IAsyncRemoteApi;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.services.base.BaseService;

import java.util.List;

public class RecipientService extends BaseService<RecipientModel> implements IRecipientService {

    final static String TAG = RecipientService.class.getSimpleName();

    private IRepository<DiseaseModel> localDisease;
    private final IRepository<LoopModel> localLoop;
    private IRepository<ProviderModel> localProvider;
    private IAsyncRemoteApi<RecipientModel> remoteApi;

    public RecipientService(IRepository<RecipientModel> local,
                            IRepository<DiseaseModel> localDisease,
                            IRepository<LoopModel> localLoop,
                            IRepository<ProviderModel> localProvider,
                            IAsyncRemoteApi<RecipientModel> remoteApi) {
        super(local);
        this.localDisease = localDisease;
        this.localLoop = localLoop;
        this.localProvider = localProvider;
        this.remoteApi = remoteApi;
    }

    @Override
    public void createRecipient(RecipientModel recipientModel, final String intentAction, final AsyncResult<RecipientModel> result) {
        remoteApi.create(recipientModel, new AsyncResult<RecipientModel>() {
            @Override
            public void success(RecipientModel serverModel) {

                serverModel.setId(_local.create(serverModel, intentAction).getId());
                for (DiseaseModel disease : serverModel.diseases) {
                    disease.setRecipientId(serverModel.getServerId());
                    localDisease.create(disease);
                }

                if(serverModel.diseases != null){
                    for (DiseaseModel disease: serverModel.diseases) {
                        disease.setRecipientId(serverModel.getServerId());
                        localDisease.create(disease);
                    }
                }

                if(serverModel.loops != null){
                    for (LoopModel loop : serverModel.loops) {
                        loop.setRecipientId(serverModel.getServerId());
                        localLoop.create(loop);
                    }
                }

                if(serverModel.getProviders() != null) {
                    for (ProviderModel providerModel : serverModel.getProviders()) {
                        providerModel.setRecipientId(serverModel.getId());
                        localProvider.create(providerModel);
                    }
                }

                if (null == result) {
                    Log.d(TAG, "Recipient added successfully");
                } else {
                    result.success(serverModel);
                }
            }

            @Override
            public void error(String error) {
                if (null == result) {
                    if (null != error) {
                        Log.e(TAG, "adding Recipient failed. Reason: " + error);
                    }
                } else {
                    result.error(error);
                }

            }
        });
    }

    @Override
    public void createRecipient(RecipientModel recipientModel, String intentAction) {
        createRecipient(recipientModel, intentAction,null);
    }

    @Override
    public void createRecipient(final RecipientModel recipientModel, final AsyncResult<RecipientModel> result) {
        createRecipient(recipientModel, null, result);
    }

    @Override
    public void updateRecipient(final RecipientModel model, final AsyncResult<RecipientModel> result) {
        String id = model.getServerId();
        remoteApi.update(id, model, new AsyncResult<RecipientModel>() {
            @Override
            public void success(RecipientModel recipientModel) {

                recipientModel.setId(model.getId());
                _local.update(model.getId(), recipientModel);

                if(recipientModel.diseases != null){
                    PageInput input = new PageInput();
                    input.query.add("recipientId", recipientModel.getServerId());
                    List<DiseaseModel> localModels = localDisease.page(input).items;
                    for (DiseaseModel model : localModels) {
                        localDisease.remove(model.getId());
                    }

                    for (DiseaseModel disease: recipientModel.diseases) {
                        disease.setRecipientId(recipientModel.getServerId());
                        localDisease.create(disease);
                    }
                }

                if(recipientModel.loops != null){
                    for (LoopModel loop : recipientModel.loops) {
                        loop.setRecipientId(recipientModel.getServerId());
                        LoopModel loopModel = localLoop.get(new PageQuery().add("recipientId", recipientModel.getServerId()).add("serverId", loop.getServerId()));
                        if (loopModel == null){
                            localLoop.create(loop);
                        } else {
                            loop.setId(loopModel.getId());
                            localLoop.update(loopModel.getId(), loop);
                        }
                    }
                }

                if(null != recipientModel.getProviders()){
                    localProvider.removeAll();
                    for (ProviderModel providerModel : recipientModel.getProviders()) {
                        providerModel.setRecipientId(model.getId());
                        localProvider.create(providerModel);
                    }
                }
                result.success(recipientModel);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public void getRecipientAndSave(String id, final AsyncResult<RecipientModel> result) {
        remoteApi.get(id, new AsyncResult<RecipientModel>() {
            @Override
            public void success(RecipientModel serverModel) {

                RecipientModel model = _local.getByServerId(serverModel.getServerId());
                if(model == null){
                    _local.create(serverModel);
                } else {
                    serverModel.setId(model.getId());
                    _local.update(model.getId(), serverModel);
                }

                if(serverModel.diseases != null){
                    PageInput input = new PageInput();
                    input.query.add("recipientId", serverModel.getServerId());
                    List<DiseaseModel> localModels = localDisease.page(input).items;
                    for (DiseaseModel disease : localModels) {
                        localDisease.remove(disease.getId());
                    }

                    for (DiseaseModel disease: serverModel.diseases) {
                        disease.setRecipientId(serverModel.getServerId());
                        localDisease.create(disease);
                    }
                }

                if(serverModel.loops != null){
                    for (LoopModel loop : serverModel.loops) {
                        loop.setRecipientId(serverModel.getServerId());
                        LoopModel loopModel = localLoop.get(new PageQuery().add("recipientId", serverModel.getServerId()).add("serverId", loop.getServerId()));
                        if (loopModel == null){
                            localLoop.create(loop);
                        } else {
                            loop.setId(loopModel.getId());
                            localLoop.update(loopModel.getId(), loop);
                        }
                    }
                }

                if(null != serverModel.getProviders()){
                    localProvider.removeAll();
                    for (ProviderModel providerModel : serverModel.getProviders()) {
                        providerModel.setRecipientId(model.getId());
                        localProvider.create(providerModel);
                    }
                }

                result.success(serverModel);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public void getRecipient(String id, final AsyncResult<RecipientModel> result) {
        remoteApi.get(id, new AsyncResult<RecipientModel>() {
            @Override
            public void success(RecipientModel serverModel) {
                result.success(serverModel);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public void getRecipient(String id, final String intentAction) {
        remoteApi.get(id, new AsyncResult<RecipientModel>() {
            @Override
            public void success(RecipientModel serverModel) {

                RecipientModel model = _local.getByServerId(serverModel.getServerId());

                if(null == model){
                    _local.create(serverModel);
                }else {
                    serverModel.setId(model.getId());
                    _local.update(serverModel.getId(),serverModel);
                }

                if (serverModel.diseases != null) {
                    localDisease.removeAll();

                    for (DiseaseModel disease : serverModel.diseases) {
                        disease.setRecipientId(serverModel.getServerId());
                        localDisease.create(disease);
                    }
                }

                if(serverModel.loops != null){
                    for (LoopModel loop : serverModel.loops) {
                        loop.setRecipientId(serverModel.getServerId());
                        LoopModel loopModel = localLoop.get(new PageQuery().add("recipientId", serverModel.getServerId()).add("serverId", loop.getServerId()));
                        if (loopModel == null){
                            localLoop.create(loop);
                        } else {
                            loop.setId(loopModel.getId());
                            localLoop.update(loopModel.getId(), loop);
                        }
                    }
                }

                if(null !=serverModel.getProviders()) {
                    localProvider.removeAll();

                    for (ProviderModel providerModel : serverModel.getProviders()) {
                        providerModel.setRecipientId(serverModel.getId());
                        providerModel.setId(localProvider.create(providerModel, intentAction).getId());
                    }
                }

            }

            @Override
            public void error(String error) {
                if (null != error) {
                    Log.e(TAG, "Not able to get a recipient. Reason: " + error);
                }
            }
        });
    }

    @Override
    public void acceptRecipientInvitation(final RecipientModel recipientModel, final AsyncResult<RecipientModel> result) {
        String action = "acceptRecipientInvitation/" + recipientModel.getServerId();
        remoteApi.update(action, new AsyncResult<RecipientModel>() {
            @Override
            public void success(RecipientModel userProfile) {

                _local.create(recipientModel);

                if(recipientModel.diseases != null){
                    for (DiseaseModel disease: recipientModel.diseases) {
                        disease.setRecipientId(recipientModel.getServerId());
                        localDisease.create(disease);
                    }
                }

                if(recipientModel.loops != null){
                    for (LoopModel loop : recipientModel.loops) {
                        loop.setRecipientId(recipientModel.getServerId());
                        localLoop.create(loop);
                    }
                }

                if(recipientModel.getProviders() != null) {
                    for (ProviderModel providerModel : recipientModel.getProviders()) {
                        providerModel.setRecipientId(recipientModel.getId());
                        localProvider.create(providerModel);
                    }
                }

                result.success(recipientModel);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public void rejectRecipientInvitation(String id, AsyncResult<RecipientModel> result) {

    }

    @Override
    public RecipientModel getLastRecipientFromLocal() {
        PageInput input = new PageInput();
        input.query.add("lastRecipient", true);
        List<RecipientModel> recipients = _local.page(input).items;
        if(recipients.size() > 0)
            return _local.page(input).items.get(0);
        else
            return null;
    }
}

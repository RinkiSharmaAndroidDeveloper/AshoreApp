package com.trutek.looped.data.modules;

import android.database.sqlite.SQLiteDatabase;

import com.trutek.looped.data.contracts.apis.IActivityApi;
import com.trutek.looped.data.contracts.apis.ICategoryApi;
import com.trutek.looped.data.contracts.apis.ICommentApi;
import com.trutek.looped.data.contracts.apis.ICommunityApi;
import com.trutek.looped.data.contracts.apis.IConnectionApi;
import com.trutek.looped.data.contracts.apis.IDiseaseApi;
import com.trutek.looped.data.contracts.apis.IHealthChartApi;
import com.trutek.looped.data.contracts.apis.IHealthChartLogApi;
import com.trutek.looped.data.contracts.apis.IHealthParamApi;
import com.trutek.looped.data.contracts.apis.IInterestApi;
import com.trutek.looped.data.contracts.apis.IMedicineApi;
import com.trutek.looped.data.contracts.apis.INotificationApi;
import com.trutek.looped.data.contracts.apis.IRecipientApi;
import com.trutek.looped.data.contracts.apis.IReportBugApi;
import com.trutek.looped.data.contracts.apis.ITagApi;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CategoryModel;
import com.trutek.looped.data.contracts.models.CommentModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.DiseaseModel;
import com.trutek.looped.data.contracts.models.HealthChartLogsModel;
import com.trutek.looped.data.contracts.models.HealthChartModel;
import com.trutek.looped.data.contracts.models.HealthParameterModel;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.LocationModel;
import com.trutek.looped.data.contracts.models.LoopModel;
import com.trutek.looped.data.contracts.models.MedicineModel;
import com.trutek.looped.data.contracts.models.NotificationModel;
import com.trutek.looped.data.contracts.models.ProviderModel;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.contracts.models.ReportBugModel;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.data.contracts.services.IActivityService;
import com.trutek.looped.data.contracts.services.ICategoryService;
import com.trutek.looped.data.contracts.services.ICommentService;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.data.contracts.services.IDiseaseService;
import com.trutek.looped.data.contracts.services.IHealthChartLogService;
import com.trutek.looped.data.contracts.services.IHealthChartService;
import com.trutek.looped.data.contracts.services.IHealthParamService;
import com.trutek.looped.data.contracts.services.IInterestService;
import com.trutek.looped.data.contracts.services.ILocationService;
import com.trutek.looped.data.contracts.services.ILoopService;
import com.trutek.looped.data.contracts.services.IMedicineService;
import com.trutek.looped.data.contracts.services.INotificationService;
import com.trutek.looped.data.contracts.services.IProviderService;
import com.trutek.looped.data.contracts.services.IRecipientService;
import com.trutek.looped.data.contracts.services.IReportBugService;
import com.trutek.looped.data.contracts.services.ITagService;
import com.trutek.looped.data.impl.services.ActivityService;
import com.trutek.looped.data.impl.services.CategoryService;
import com.trutek.looped.data.impl.services.CommentService;
import com.trutek.looped.data.impl.services.CommunityService;
import com.trutek.looped.data.impl.services.ConnectionService;
import com.trutek.looped.data.impl.services.DiseaseService;
import com.trutek.looped.data.impl.services.HealthChartLogService;
import com.trutek.looped.data.impl.services.HealthChartService;
import com.trutek.looped.data.impl.services.HealthParamService;
import com.trutek.looped.data.impl.services.InterestService;
import com.trutek.looped.data.impl.services.LocationService;
import com.trutek.looped.data.impl.services.LoopService;
import com.trutek.looped.data.impl.services.MedicineService;
import com.trutek.looped.data.impl.services.NotificationService;
import com.trutek.looped.data.impl.services.ProviderService;
import com.trutek.looped.data.impl.services.RecipientService;
import com.trutek.looped.data.impl.services.ReportBugService;
import com.trutek.looped.data.impl.services.TagService;
import com.trutek.looped.msas.common.contracts.IAsyncRemoteApi;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.data.contracts.apis.IProfileApi;
import com.trutek.looped.data.contracts.apis.IUserApi;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.UserModel;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.data.contracts.services.IUserService;
import com.trutek.looped.data.impl.services.ProfileService;
import com.trutek.looped.data.impl.services.UserService;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    @Provides
    IUserService provideUserService(IRepository<UserModel> local, IUserApi<UserModel> remote, SQLiteDatabase database) {
        return new UserService(local, remote, database);
    }

    @Provides
    IProfileService provideProfileService(IRepository<ProfileModel> localProfile, IRepository<RecipientModel> localRecipient, IRepository<InterestModel> localIntererest, IRepository<TagModel> localTag,IProfileApi<ProfileModel> profileRemote
            ,IRepository<CategoryModel> localCategory) {
        return new ProfileService(localProfile, localRecipient, localIntererest,localTag,profileRemote,localCategory);
    }

    @Provides
    ICommunityService provideCommunityService(IRepository<CommunityModel> communityLocal, IRepository<InterestModel> interestLocal, IRepository<TagModel> tagLocal,
                                              IRepository<ActivityModel> activityLocal,
                                              ICommunityApi<CommunityModel> remote) {
        return new CommunityService(communityLocal, interestLocal, tagLocal,activityLocal, remote);
    }

    @Provides
    IInterestService provideInterestService(IRepository<InterestModel> local, IInterestApi<InterestModel> remote) {
        return new InterestService(local, remote);
    }

    @Provides
    ITagService provideTagService(IRepository<TagModel> local, ITagApi<TagModel> remote) {
        return new TagService(local, remote);
    }

    @Provides
    IConnectionService provideConnectionService(IRepository<ConnectionModel> local, IConnectionApi<ConnectionModel> remote) {
        return new ConnectionService(local, remote);
    }

    @Provides
    IActivityService provideActivityService(IRepository<ActivityModel> local, IActivityApi<ActivityModel> remote) {
        return new ActivityService(local, remote);
    }

    @Provides
    IReportBugService provideReportBugService(IReportBugApi<ReportBugModel> remote) {
        return new ReportBugService(remote);
    }

    @Provides
    ICommentService provideCommentService(IRepository<CommentModel> local,ICommentApi<CommentModel> remote) {
        return new CommentService(local,remote);
    }

    @Provides
    INotificationService provideNotificationService(IRepository<NotificationModel> local, INotificationApi<NotificationModel> remote) {
        return new NotificationService(local, remote);
    }

    @Provides
    IDiseaseService providesDiseaseService(IDiseaseApi<DiseaseModel> remote){
        return new DiseaseService(remote);
    }

    @Provides
    IRecipientService providesRecipientService(IRepository<RecipientModel> localRecipient,
                                               IRepository<DiseaseModel> localDisease,
                                               IRepository<LoopModel> localLoop,
                                               IRepository<ProviderModel> localProvider,
                                               IRecipientApi<RecipientModel> remoteApi){
        return new RecipientService(localRecipient, localDisease, localLoop, localProvider, remoteApi);
    }

    @Provides
    IHealthParamService providesHealthParamService(IRepository<HealthParameterModel> local,IHealthParamApi<HealthParameterModel> remote){
        return new HealthParamService(local,remote);
    }

    @Provides
    IHealthChartLogService providesHealthChartLogService(IRepository<HealthChartLogsModel> local,IHealthChartLogApi<HealthChartLogsModel> remoteApi){
        return  new HealthChartLogService(local,remoteApi);
    }

    @Provides
    IHealthChartService providesHealthChartService(IRepository<HealthChartModel> local,IRepository<HealthParameterModel> localHealthParam,
                                                   IRepository<HealthChartLogsModel> localHealthParamLog,IHealthChartApi<HealthChartModel> remote){
        return new HealthChartService(local, localHealthParam,localHealthParamLog,remote);
    }

    @Provides
    ILoopService providesLoopService(IRepository<LoopModel> local){
        return new LoopService(local);
    }

    @Provides
    IProviderService providesProviderService(IRepository<ProviderModel> localRecipient){
        return new ProviderService(localRecipient);
    }
    @Provides
    IMedicineService providesMedicineService(IMedicineApi<MedicineModel> remote){
        return new MedicineService(remote);
    }
    @Provides
    ICategoryService providesCategoryService(ICategoryApi<CategoryModel> remote){
        return new CategoryService(remote);
    }
    @Provides
    ILocationService providesLocationService(IAsyncRemoteApi<LocationModel> remoteApi){
        return new LocationService(null,remoteApi);
    }
}

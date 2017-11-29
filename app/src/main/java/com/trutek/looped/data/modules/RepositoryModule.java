package com.trutek.looped.data.modules;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.reflect.TypeToken;
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
import com.trutek.looped.data.impl.apis.ActivityApi;
import com.trutek.looped.data.impl.apis.CategoryApi;
import com.trutek.looped.data.impl.apis.CommentApi;
import com.trutek.looped.data.impl.apis.CommunityApi;
import com.trutek.looped.data.impl.apis.ConnectionApi;
import com.trutek.looped.data.impl.apis.DiseaseApi;
import com.trutek.looped.data.impl.apis.HealthChartApi;
import com.trutek.looped.data.impl.apis.HealthChartLogApi;
import com.trutek.looped.data.impl.apis.HealthParamApi;
import com.trutek.looped.data.impl.apis.InterestApi;
import com.trutek.looped.data.impl.apis.MedicineApi;
import com.trutek.looped.data.impl.apis.NotificationApi;
import com.trutek.looped.data.impl.apis.RecipientApi;
import com.trutek.looped.data.impl.apis.ReportBugApi;
import com.trutek.looped.data.impl.apis.TagApi;
import com.trutek.looped.data.impl.entities.Activity;
import com.trutek.looped.data.impl.entities.ActivityDao;
import com.trutek.looped.data.impl.entities.Category;
import com.trutek.looped.data.impl.entities.CategoryDao;
import com.trutek.looped.data.impl.entities.Comment;
import com.trutek.looped.data.impl.entities.CommentDao;
import com.trutek.looped.data.impl.entities.Community;
import com.trutek.looped.data.impl.entities.CommunityDao;
import com.trutek.looped.data.impl.entities.Connection;
import com.trutek.looped.data.impl.entities.ConnectionDao;
import com.trutek.looped.data.impl.entities.Disease;
import com.trutek.looped.data.impl.entities.DiseaseDao;
import com.trutek.looped.data.impl.entities.HealthChart;
import com.trutek.looped.data.impl.entities.HealthChartDao;
import com.trutek.looped.data.impl.entities.HealthParam;
import com.trutek.looped.data.impl.entities.HealthParamDao;
import com.trutek.looped.data.impl.entities.HealthParamLog;
import com.trutek.looped.data.impl.entities.HealthParamLogDao;
import com.trutek.looped.data.impl.entities.Interest;
import com.trutek.looped.data.impl.entities.InterestDao;
import com.trutek.looped.data.impl.entities.Location;
import com.trutek.looped.data.impl.entities.LocationDao;
import com.trutek.looped.data.impl.entities.Loop;
import com.trutek.looped.data.impl.entities.LoopDao;
import com.trutek.looped.data.impl.entities.Medicine;
import com.trutek.looped.data.impl.entities.Notification;
import com.trutek.looped.data.impl.entities.NotificationDao;
import com.trutek.looped.data.impl.entities.Provider;
import com.trutek.looped.data.impl.entities.ProviderDao;
import com.trutek.looped.data.impl.entities.Recipient;
import com.trutek.looped.data.impl.entities.RecipientDao;
import com.trutek.looped.data.impl.entities.Tag;
import com.trutek.looped.data.impl.entities.TagDao;
import com.trutek.looped.data.impl.repositories.ActivityRepository;
import com.trutek.looped.data.impl.repositories.CategoryRepository;
import com.trutek.looped.data.impl.repositories.CommentRepository;
import com.trutek.looped.data.impl.repositories.CommunityRepository;
import com.trutek.looped.data.impl.repositories.ConnectionRepository;
import com.trutek.looped.data.impl.repositories.DiseaseRepository;
import com.trutek.looped.data.impl.repositories.HealthChartRepository;
import com.trutek.looped.data.impl.repositories.HealthParamLogRepository;
import com.trutek.looped.data.impl.repositories.HealthParamRepository;
import com.trutek.looped.data.impl.repositories.InterestRepository;
import com.trutek.looped.data.impl.repositories.LocationRepository;
import com.trutek.looped.data.impl.repositories.LoopRepository;
import com.trutek.looped.data.impl.repositories.MedicineRepository;
import com.trutek.looped.data.impl.repositories.NotificationRepository;
import com.trutek.looped.data.impl.repositories.ProviderRepository;
import com.trutek.looped.data.impl.repositories.RecipientRepository;
import com.trutek.looped.data.impl.repositories.TagRepository;
import com.trutek.looped.msas.common.contracts.IAsyncRemoteApi;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.DataModel;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.data.contracts.apis.IProfileApi;
import com.trutek.looped.data.contracts.apis.IUserApi;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.UserModel;
import com.trutek.looped.data.impl.apis.ProfileApi;
import com.trutek.looped.data.impl.apis.UserApi;
import com.trutek.looped.data.impl.entities.Profile;
import com.trutek.looped.data.impl.entities.ProfileDao;
import com.trutek.looped.data.impl.entities.User;
import com.trutek.looped.data.impl.entities.UserDao;
import com.trutek.looped.data.impl.repositories.ProfileRepository;
import com.trutek.looped.data.impl.repositories.UserRepository;
import com.trutek.looped.msas.common.repositories.AsyncRemoteApi;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Provides
    IRepository<UserModel> providesUserRepository(UserDao notices, IModelMapper<User, UserModel> mapper) {
        return new UserRepository(mapper, notices);
    }

    @Provides
    IUserApi<UserModel> providesAsyncUserRepository(Context context, SQLiteDatabase database) {
        return new UserApi<>(context, "users", new TypeToken<UserModel>() {
        }.getType(), new TypeToken<Page<UserModel>>() {
        }.getType(), new TypeToken<DataModel<UserModel>>() {
        }.getType(), database);
    }

    @Provides
    IRepository<ProfileModel> providesProfileRepository(Context context,ProfileDao profileDao, IModelMapper<Profile, ProfileModel> mapper) {
        return new ProfileRepository(context,mapper, profileDao);
    }

    @Provides
    IProfileApi<ProfileModel> providesAsyncProfileRepository(Context context, SQLiteDatabase database) {
        return new ProfileApi<>(context, "profiles", new TypeToken<ProfileModel>() {
        }.getType(), new TypeToken<Page<ProfileModel>>() {
        }.getType(), new TypeToken<DataModel<ProfileModel>>() {
        }.getType(), database);
    }

    @Provides
    IRepository<CommunityModel> providesCommunityRepository(Context context,CommunityDao communityDao, IModelMapper<Community, CommunityModel> mapper) {
        return new CommunityRepository(context,mapper, communityDao);
    }

    @Provides
    ICommunityApi<CommunityModel> providesAsyncCommunityApi(Context context, SQLiteDatabase database) {
        return new CommunityApi<>(context, "communities", new TypeToken<CommunityModel>() {
        }.getType(), new TypeToken<Page<CommunityModel>>() {
        }.getType(), new TypeToken<DataModel<CommunityModel>>() {
        }.getType(), database);
    }

    @Provides
    IRepository<InterestModel> providesInterestRepository(InterestDao dao, IModelMapper<Interest, InterestModel> mapper) {
        return new InterestRepository(mapper, dao);
    }

    @Provides
    IInterestApi<InterestModel> providesAsyncInterestApi(Context context, SQLiteDatabase database) {
        return new InterestApi<>(context, "interests", new TypeToken<InterestModel>() {
        }.getType(), new TypeToken<Page<InterestModel>>() {
        }.getType(), new TypeToken<DataModel<InterestModel>>() {
        }.getType(), database);
    }

    @Provides
    IRepository<TagModel> providesTagRepository(TagDao dao, IModelMapper<Tag, TagModel> mapper) {
        return new TagRepository(mapper, dao);
    }

    @Provides
    ITagApi<TagModel> providesAsyncTagApi(Context context, SQLiteDatabase database) {
        return new TagApi<>(context, "tags", new TypeToken<TagModel>() {
        }.getType(), new TypeToken<Page<TagModel>>() {
        }.getType(), new TypeToken<DataModel<TagModel>>() {
        }.getType(), database);
    }

    @Provides
    IRepository<ConnectionModel> providesConnectionRepository(Context context,ConnectionDao dao, IModelMapper<Connection, ConnectionModel> mapper) {
        return new ConnectionRepository(context,mapper, dao);
    }

    @Provides
    IConnectionApi<ConnectionModel> providesAsyncConnectionApi(Context context, SQLiteDatabase database) {
        return new ConnectionApi<>(context, "connections", new TypeToken<ConnectionModel>() {
        }.getType(), new TypeToken<Page<ConnectionModel>>() {
        }.getType(), new TypeToken<DataModel<ConnectionModel>>() {
        }.getType(), database);
    }

    @Provides
    IRepository<ActivityModel> providesActivityRepository(Context context,ActivityDao dao, IModelMapper<Activity, ActivityModel> mapper) {
        return new ActivityRepository(context,mapper, dao);
    }

    @Provides
    IActivityApi<ActivityModel> providesAsyncActivityApi(Context context, SQLiteDatabase database) {
        return new ActivityApi<>(context, "activities", new TypeToken<ActivityModel>() {
        }.getType(), new TypeToken<Page<ActivityModel>>() {
        }.getType(), new TypeToken<DataModel<ActivityModel>>() {
        }.getType(), database);
    }

    @Provides
    IReportBugApi<ReportBugModel> providesAsyncReportBugApi(Context context, SQLiteDatabase database) {
        return new ReportBugApi<>(context, "messages", new TypeToken<ReportBugModel>() {
        }.getType(), new TypeToken<Page<ReportBugModel>>() {
        }.getType(), new TypeToken<DataModel<ReportBugModel>>() {
        }.getType(), database);
    }

    @Provides
    ICommentApi<CommentModel> providesAsyncCommentApi(Context context, SQLiteDatabase database) {
        return new CommentApi<>(context, "comments", new TypeToken<CommentModel>() {
        }.getType(), new TypeToken<Page<CommentModel>>() {
        }.getType(), new TypeToken<DataModel<CommentModel>>() {
        }.getType(), database);
    }
    @Provides
    IRepository<CommentModel> providesCommentRepository(Context context, CommentDao dao, IModelMapper<Comment, CommentModel> mapper) {
        return new CommentRepository(context,mapper, dao);
    }
    @Provides
    IRepository<NotificationModel> providesNotificationRepository(Context context,NotificationDao dao, IModelMapper<Notification, NotificationModel> mapper) {
        return new NotificationRepository(context,mapper, dao);
    }

    @Provides
    INotificationApi<NotificationModel> providesAsyncNotificationApi(Context context, SQLiteDatabase database) {
        return new NotificationApi<>(context, "notifications", new TypeToken<NotificationModel>() {
        }.getType(), new TypeToken<Page<NotificationModel>>() {
        }.getType(), new TypeToken<DataModel<NotificationModel>>() {
        }.getType(), database);
    }

    @Provides
    IRepository<DiseaseModel> providesDiseaseRepository(Context context,DiseaseDao dao, IModelMapper<Disease, DiseaseModel> mapper) {
        return new DiseaseRepository(context,mapper, dao);
    }

    @Provides
    IDiseaseApi<DiseaseModel> providesAsyncDiseaseApi(Context context, SQLiteDatabase database){
        return new DiseaseApi(context,"diseases", new TypeToken<DiseaseModel>(){}.getType(),
                new TypeToken<Page<DiseaseModel>>(){}.getType(),
                new TypeToken<DataModel<DiseaseModel>>(){}.getType(),
                database);
    }
    @Provides
    IMedicineApi<MedicineModel> providesAsyncMedicineApi(Context context, SQLiteDatabase database){
        return new MedicineApi(context,"medications", new TypeToken<MedicineModel>(){}.getType(),
                new TypeToken<Page<MedicineModel>>(){}.getType(),
                new TypeToken<DataModel<MedicineModel>>(){}.getType(),
                database);
    }
    @Provides
    ICategoryApi<CategoryModel> providesAsyncCategoryeApi(Context context, SQLiteDatabase database){
        return new CategoryApi(context,"categories", new TypeToken<CategoryModel>(){}.getType(),
                new TypeToken<Page<CategoryModel>>(){}.getType(),
                new TypeToken<DataModel<CategoryModel>>(){}.getType(),
                database);
    }
    /*@Provides
    IRepository<MedicineModel> providesMedicineRepository(MedicineDao dao, IModelMapper<Medicine, MedicineModel> mapper) {
        return new MedicineRepository(mapper, dao);
    }*/

    @Provides
    IRepository<RecipientModel> providesRecipientRepository(Context context,RecipientDao dao, IModelMapper<Recipient, RecipientModel> mapper) {
        return new RecipientRepository(context,mapper, dao);
    }

    @Provides
    IRepository<LocationModel> providesLocationRepository(LocationDao dao, IModelMapper<Location, LocationModel> mapper) {
        return new LocationRepository(mapper, dao);
    }

    @Provides
    IRecipientApi<RecipientModel> providesAsyncRecipientApi(Context context, SQLiteDatabase database){
        return new RecipientApi(context,"profiles/recipient", new TypeToken<RecipientModel>(){}.getType(),
                new TypeToken<Page<RecipientModel>>(){}.getType(),
                new TypeToken<DataModel<RecipientModel>>(){}.getType(),
                database);
    }

    @Provides
    IRepository<HealthParameterModel> providesHealthParamRepositiory(Context context,IModelMapper<HealthParam,HealthParameterModel> mapper, HealthParamDao dao){
        return new HealthParamRepository(context,mapper,dao);
    }

    @Provides
    IRepository<HealthChartModel> providesHealthChartRepositiory(Context context,IModelMapper<HealthChart,HealthChartModel> mapper, HealthChartDao dao){
        return new HealthChartRepository(context,mapper,dao);
    }

    @Provides
    IRepository<HealthChartLogsModel> providesHealthParamLogRepository(Context context, IModelMapper<HealthParamLog,HealthChartLogsModel> mapper, HealthParamLogDao dao){
        return new HealthParamLogRepository(context,mapper,dao);
    }

    @Provides
    IHealthParamApi<HealthParameterModel> providesAsyncHealthParamApi(Context context, SQLiteDatabase database){
        return  new HealthParamApi(context,"healthParams",new TypeToken<HealthParameterModel>(){}.getType(),
                new TypeToken<Page<HealthParameterModel>>(){}.getType(),
                new TypeToken<DataModel<HealthParameterModel>>(){}.getType(),
                database);
    }

    @Provides
    IHealthChartLogApi<HealthChartLogsModel> providesAsyncHealthChartLogApi(Context context, SQLiteDatabase database)
    {
        return new HealthChartLogApi(context,"healthChartLogs",new TypeToken<HealthChartLogsModel>(){

        }.getType()
                ,new TypeToken<Page<HealthChartLogsModel>>(){}.getType()
                ,new TypeToken<DataModel<HealthChartLogsModel>>(){}.getType()
                ,database);
    }
    @Provides
    IHealthChartApi<HealthChartModel> providesAsyncHealthChartApi(Context context, SQLiteDatabase database){
        return new HealthChartApi(context, "healthCharts", new TypeToken<HealthChartModel>(){}.getType()
                ,new TypeToken<Page<HealthChartModel>>(){}.getType(),new TypeToken<DataModel<HealthChartModel>>(){}.getType(),database);
    }

    @Provides
    IRepository<LoopModel> providesLoopRepository(Context context, LoopDao dao, IModelMapper<Loop, LoopModel> mapper) {
        return new LoopRepository(context, mapper, dao);
    }

    @Provides
    IRepository<ProviderModel> providesProviderRepository(Context context,ProviderDao dao, IModelMapper<Provider, ProviderModel> mapper) {
        return new ProviderRepository(context,mapper, dao);
    }

    @Provides
    IAsyncRemoteApi<LocationModel> providesLocationApi(Context context, SQLiteDatabase database){
        return new AsyncRemoteApi<>(context,"locations",new TypeToken<LocationModel>(){}.getType()
                ,new TypeToken<Page<LocationModel>>(){}.getType()
                ,new TypeToken<DataModel<LocationModel>>(){}.getType(),database);
    }
    @Provides
    IRepository<CategoryModel> providesCategoryRepository(Context context, CategoryDao dao, IModelMapper<Category,CategoryModel> mapper){
        return new CategoryRepository(context,mapper,dao);
    }


}

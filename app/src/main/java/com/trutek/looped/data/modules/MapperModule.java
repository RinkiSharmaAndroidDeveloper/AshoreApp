package com.trutek.looped.data.modules;

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
import com.trutek.looped.data.contracts.models.NotificationModel;
import com.trutek.looped.data.contracts.models.ProviderModel;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.data.impl.entities.Activity;
import com.trutek.looped.data.impl.entities.Category;
import com.trutek.looped.data.impl.entities.Comment;
import com.trutek.looped.data.impl.entities.Community;
import com.trutek.looped.data.impl.entities.Connection;
import com.trutek.looped.data.impl.entities.Disease;
import com.trutek.looped.data.impl.entities.HealthChart;
import com.trutek.looped.data.impl.entities.HealthParam;
import com.trutek.looped.data.impl.entities.HealthParamLog;
import com.trutek.looped.data.impl.entities.Interest;
import com.trutek.looped.data.impl.entities.Location;
import com.trutek.looped.data.impl.entities.Loop;
import com.trutek.looped.data.impl.entities.Notification;
import com.trutek.looped.data.impl.entities.Provider;
import com.trutek.looped.data.impl.entities.Recipient;
import com.trutek.looped.data.impl.entities.Tag;
import com.trutek.looped.data.impl.mappers.ActivityMapper;
import com.trutek.looped.data.impl.mappers.CategoryMapper;
import com.trutek.looped.data.impl.mappers.CommentMapper;
import com.trutek.looped.data.impl.mappers.CommunityMapper;
import com.trutek.looped.data.impl.mappers.ConnectionMapper;
import com.trutek.looped.data.impl.mappers.DiseaseMapper;
import com.trutek.looped.data.impl.mappers.HealthChartMapper;
import com.trutek.looped.data.impl.mappers.HealthParamLogMapper;
import com.trutek.looped.data.impl.mappers.HealthParamMapper;
import com.trutek.looped.data.impl.mappers.InterestMapper;
import com.trutek.looped.data.impl.mappers.LocationMapper;
import com.trutek.looped.data.impl.mappers.LoopMapper;
import com.trutek.looped.data.impl.mappers.NotificationMapper;
import com.trutek.looped.data.impl.mappers.ProviderMapper;
import com.trutek.looped.data.impl.mappers.RecipientMapper;
import com.trutek.looped.data.impl.mappers.TagMapper;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.UserModel;
import com.trutek.looped.data.impl.entities.Profile;
import com.trutek.looped.data.impl.entities.User;
import com.trutek.looped.data.impl.mappers.ProfileMapper;
import com.trutek.looped.data.impl.mappers.UserMapper;
import com.trutek.looped.msas.common.contracts.IRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class MapperModule {

    @Provides
    IModelMapper<User, UserModel> providesContactMapper(){
        return new UserMapper();
    }

    @Provides
    IModelMapper<Profile, ProfileModel> providesProfileMapper(IRepository<InterestModel> interestModelIRepository,
                                                              IRepository<LocationModel> locationRepo,
                                                              IRepository<TagModel> tagRepo,IRepository<CategoryModel> categoryRepo){
        return new ProfileMapper(interestModelIRepository,locationRepo,tagRepo,categoryRepo);
    }


    @Provides
    IModelMapper<Comment, CommentModel> providesCommentMapper( IRepository<ProfileModel> profileModelRepository, IRepository<CommunityModel> communityModelRepository, IRepository<ActivityModel> activityModelRepository){
        return new CommentMapper(profileModelRepository,communityModelRepository,activityModelRepository);
    }
    @Provides
    IModelMapper<Community, CommunityModel> providesCommunityMapper(IRepository<ActivityModel> activityLocal){
        return new CommunityMapper(activityLocal);
    }

    @Provides
    IModelMapper<Interest, InterestModel> providesInterestMapper(){
        return new InterestMapper();
    }

    @Provides
    IModelMapper<Location, LocationModel> providesLocationMapper(){
        return new LocationMapper();
    }

    @Provides
    IModelMapper<Tag, TagModel> providesTagMapper(){
        return new TagMapper();
    }

    @Provides
    IModelMapper<Connection, ConnectionModel> providesConnectionMapper(){
        return new ConnectionMapper();
    }

    @Provides
    IModelMapper<Activity, ActivityModel> providesActivityMapper(){
        return new ActivityMapper();
    }

    @Provides
    IModelMapper<Notification, NotificationModel> providesNotificationMapper(){
        return new NotificationMapper();
    }

    @Provides
    IModelMapper<HealthParam,HealthParameterModel> providesHealthParamMapper(){
        return new HealthParamMapper();
    }

    @Provides
    IModelMapper<HealthChart,HealthChartModel> providesHealthChartMapper(IRepository<HealthParameterModel> localHealthParameter,
                                                                         IRepository<HealthChartLogsModel> localHealthParamLogRepo){
        return new HealthChartMapper(localHealthParameter,localHealthParamLogRepo);
    }

    @Provides
    IModelMapper<HealthParamLog,HealthChartLogsModel> providesHealthParamLogMapper(){
        return new HealthParamLogMapper();
    }

    @Provides
    IModelMapper<Recipient, RecipientModel> providesRecipientMapper(IRepository<DiseaseModel> local){
        return new RecipientMapper(local);
    }

    @Provides
    IModelMapper<Disease, DiseaseModel> providesDiseaseMapper(){
        return new DiseaseMapper();
    }

    @Provides
    IModelMapper<Provider, ProviderModel> providesProviderMapper(){
        return new ProviderMapper();
    }

    @Provides
    IModelMapper<Loop, LoopModel> providesLoopMapper(){
        return new LoopMapper();
    }

    @Provides
    IModelMapper<Category,CategoryModel> providesCategoryMapper(){
        return new CategoryMapper();
    }
}

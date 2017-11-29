package com.trutek.looped.data.modules;

import com.trutek.looped.App;
import com.trutek.looped.androidservices.NotificationBackGroundService;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.data.impl.services.NotificationService;
import com.trutek.looped.ui.activity.create.CreateActivity;
import com.trutek.looped.ui.activity.create.CommunityListActivity;
import com.trutek.looped.ui.activity.display.discussion.DiscussionActivity;
import com.trutek.looped.ui.activity.display.DisplayActivity;
import com.trutek.looped.ui.activity.edit.EditActivity;
import com.trutek.looped.ui.authenticate.CategoryFragment;
import com.trutek.looped.ui.authenticate.InterestFragment;
import com.trutek.looped.ui.authenticate.SignupLocation;
import com.trutek.looped.ui.authenticate.SignupLocationCategoryActivity;
import com.trutek.looped.ui.authenticate.SignupLocationInterestActivity;
import com.trutek.looped.ui.chats.AddFriendsToGroupActivity;
import com.trutek.looped.ui.chats.CreateGroupDialogActivity;
import com.trutek.looped.ui.chats.DetailsDialogActivity;
import com.trutek.looped.ui.chats.DialogsFragment;

import com.trutek.looped.ui.chats.InviteToGroupActivity;
import com.trutek.looped.ui.chats.InviteToGroupFragment;
import com.trutek.looped.ui.communityDashboard.discoverCommunity.DiscoverCommunityActivity;
import com.trutek.looped.ui.communityDashboard.discoverPeople.DiscoverPeopleActivity;
import com.trutek.looped.ui.communityDashboard.myCommunities.display.AddMembersToCommunity;
import com.trutek.looped.ui.communityDashboard.myCommunities.display.DisplayMembersActivity;
import com.trutek.looped.ui.communityDashboard.myCommunities.display.PastEventsActivity;
import com.trutek.looped.ui.communityDashboard.myCommunities.edit.EditCommunity;
import com.trutek.looped.ui.communityDashboard.myCommunities.CreatedCommunityFragment;
import com.trutek.looped.ui.communityDashboard.myCommunities.JoinedCommunityFragment;
import com.trutek.looped.ui.communityDashboard.myCommunities.create.CommunityStep1Fragment;
import com.trutek.looped.ui.communityDashboard.myCommunities.create.CreateCommunityActivity;
import com.trutek.looped.ui.communityDashboard.myCommunities.create.InviteFromContactFragment;
import com.trutek.looped.ui.communityDashboard.myCommunities.create.InviteFromLoopFragment;
import com.trutek.looped.ui.communityDashboard.maindashboard.CommunityDashboardFragment;
import com.trutek.looped.ui.communityDashboard.myConnections.InviteConnectionFromContacts;
import com.trutek.looped.ui.communityDashboard.myConnections.MyConnectionActivity;
import com.trutek.looped.ui.communityDashboard.myConnections.SelectConnectionActivity;
import com.trutek.looped.ui.communityDashboard.myCommunities.display.DisplayCommunity;
import com.trutek.looped.ui.communityDashboard.publiccommunity.PublicCommunityActivity;
import com.trutek.looped.ui.home.HomeActivity;
import com.trutek.looped.ui.home.HomeFragment;
import com.trutek.looped.ui.home.NotificationActivity;
import com.trutek.looped.ui.location.SearchLocationActivity;
import com.trutek.looped.ui.medicine.create.AddMedicineActivity;
import com.trutek.looped.ui.medicine.create.CreateMedicineActivity;
import com.trutek.looped.ui.medicine.edit.DosagesScheduleActivity;
import com.trutek.looped.ui.medicine.edit.EditMedicineActivity;
import com.trutek.looped.ui.medicine.edit.UpdateFrequencyDuration;
import com.trutek.looped.ui.planner.PlannerFragment;
import com.trutek.looped.chatmodule.helpers.BaseChatHelper;
import com.trutek.looped.ui.chats.BaseDialogActivity;
import com.trutek.looped.ui.profile.InterestTagsActivity;
import com.trutek.looped.ui.profile.create.CreateProfileActivity;
import com.trutek.looped.ui.profile.create.CreateProfileStep1GeneralFragment;
import com.trutek.looped.ui.profile.create.CreateProfileStep1InterestFragment;
import com.trutek.looped.ui.profile.create.CreateProfileStep2Activity;
import com.trutek.looped.ui.authenticate.SignUpActivity;
import com.trutek.looped.ui.authenticate.SplashActivity;
import com.trutek.looped.ui.profile.create.CreateProfileStep3Activity;
import com.trutek.looped.ui.profile.display.DisplayProfile;
import com.trutek.looped.ui.profile.edit.EditProfileActivity;
import com.trutek.looped.ui.recipient.recipient.create.AddRecipientActivity;
import com.trutek.looped.ui.recipient.recipient.disease.DisplayDiseaseActivity;
import com.trutek.looped.ui.recipient.ContactProvider;
import com.trutek.looped.ui.recipient.recipient.display.DisplayRecipientActivity;
import com.trutek.looped.ui.recipient.RecipientProviderActivity;
import com.trutek.looped.ui.recipient.healthchart.DisplayHealthChartActivity;
import com.trutek.looped.ui.recipient.healthparameter.AddHealthParameterActivity;
import com.trutek.looped.ui.recipient.healthparameter.DisplayHealthParamLogActivity;
import com.trutek.looped.ui.recipient.recipient.loops.DisplayLoopsActivity;
import com.trutek.looped.ui.recipient.recipient.loops.InviteFromLoopActivity;
import com.trutek.looped.ui.settings.ContactUsActivity;
import com.trutek.looped.ui.settings.PrivacyPolicyActivity;
import com.trutek.looped.ui.settings.SettingsFragment;
import com.trutek.looped.ui.recipient.RecipientDashBoardFragment;
import com.trutek.looped.ui.settings.StarredMessagesActivity;
import com.trutek.looped.ui.settings.TermsActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                AppModule.class,
                DaoModule.class,
                ServiceModule.class,
                MapperModule.class,
                RepositoryModule.class

        }
)
public interface AppComponent {
    void inject(App app);

    void inject(CreateProfileStep1GeneralFragment generalFragment);

    void inject(CreateProfileStep1InterestFragment createProfileStep1InterestFragment);

    void inject(InviteToGroupFragment inviteToGroupFragment);

    void inject(SignUpActivity activity);

    void inject(SignupLocation signupLocation);

    void inject(SignupLocationCategoryActivity signupLocationCategoryActivity);

    void inject(StarredMessagesActivity starredMessagesActivity);

    void inject(SignupLocationInterestActivity signupLocationInterestActivity);

    void inject(AddMembersToCommunity addMembersToCommunity);

    void inject(ContactUsActivity contactUsActivity);

    void inject(CreateProfileStep2Activity createProfileStep2Activity);

    void inject(CreateProfileStep3Activity createProfileStep3Activity);

    void inject(CreateProfileActivity activity);

    void inject(CreatedCommunityFragment createdCommunityFragment);

    void inject(CategoryFragment categoryFragment);

    void inject(InterestFragment interestFragment);

    void inject(JoinedCommunityFragment joinedCommunityFragment);

    void inject(InviteFromLoopFragment inviteFromLoopFragment);

    void inject(InviteToGroupActivity inviteToGroupActivity);

    void inject(InviteFromContactFragment inviteFromContactFragment);

    void inject(CommunityDashboardFragment communityDashboardFragment);

    void inject(HomeFragment homeFragment);

    void inject(PlannerFragment plannerFragment);

    void inject(PastEventsActivity pastEventsActivity);

    void inject(PrivacyPolicyActivity privacyPolicyActivity);

    void inject(TermsActivity termsActivity);

    void inject(SplashActivity splash);

    void inject(BaseDialogActivity activity);

    void inject(CreateCommunityActivity activity);

    void inject(CommunityStep1Fragment fragment);

    void inject(DiscoverCommunityActivity activity);

    void inject(DiscoverPeopleActivity activity);

    void inject(MyConnectionActivity activity);

    void inject(DisplayProfile profile);

    void inject(InviteConnectionFromContacts inviteConnectionFromContacts);

    void inject(DisplayMembersActivity displayMembersActivity);

    void inject(DisplayCommunity community);

    void inject(CommunityListActivity activity);

    void inject(CreateActivity activity);

    void inject(DisplayActivity activity);

    void inject(EditCommunity activity);

    void inject(InterestTagsActivity activity);

    void inject(EditProfileActivity activity);

    void inject(EditActivity activity);

    void inject(SelectConnectionActivity activity);

    void inject(HomeActivity activity);

    void inject(DiscussionActivity activity);

    void inject(NotificationActivity activity);

    void inject(DetailsDialogActivity activity);

    void inject(AddFriendsToGroupActivity activity);

    void inject(SettingsFragment fragment);

    void inject(AddRecipientActivity addRecipientActivity);

    void inject(RecipientDashBoardFragment recipientDashBoardFragment);

    void inject(DisplayRecipientActivity displayRecipientActivity);

    void inject(DisplayDiseaseActivity diseaseActivity);

    void inject(DisplayHealthChartActivity displayHealthChartActivity);

    void inject(AddHealthParameterActivity addHealthParameterActivity);

    void inject(DisplayHealthParamLogActivity displayHealthParamLogActivity);

    void inject(DisplayLoopsActivity activity);

    void inject(InviteFromLoopActivity activity);

    void inject(PublicCommunityActivity publicCommunityActivity);

    void inject(SearchLocationActivity searchLocationActivity);

        /*chats*/
    void inject(NotificationBackGroundService notificationBackGroundService);
        void inject(BaseChatHelper baseHelper);
        void inject(QuickBloxService service);
        void inject(DialogsFragment fragment);
        void inject(CreateGroupDialogActivity activity);
        void inject(ContactProvider contactProvider);

        void inject(RecipientProviderActivity recipientProviderActivity);
        void inject(NotificationService notificationService);
        /*medicine*/
        void inject(CreateMedicineActivity createMedicineActivity);
        void inject(EditMedicineActivity editMedicineActivity);
        void inject(AddMedicineActivity addMedicineActivity);
        void inject(DosagesScheduleActivity dosagesScheduleActivity);
        void inject(UpdateFrequencyDuration updateFrequencyDuration);
}

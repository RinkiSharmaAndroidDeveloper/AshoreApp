package com.trutek.looped.ui.communityDashboard.myCommunities.create;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.trutek.looped.App;
import com.trutek.looped.R;

import com.trutek.looped.chatmodule.commands.cloudinary.CloudinaryImageUploadCommand;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.tasks.GetFilepathFromUriTask;
import com.trutek.looped.data.contracts.models.CloudinaryModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.net.NetworkDetector;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.communityDashboard.myCommunities.MyCommunitiesActivity;
import com.trutek.looped.ui.recipient.recipient.loops.InviteFromLoopActivity;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.image.ImageUtils;
import com.trutek.looped.utils.listeners.OnImagePickedListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class CreateCommunityActivity extends BaseAppCompatActivity implements CommunityStep2Fragment.OnFragmentInteractionListener, CommunityStep3Fragment.OnFragmentInteractionListener, OnImagePickedListener {

    // public final static String COMMUNITY_STEP_ONE = "communityStep1Fragment";
    public final static String COMMUNITY_STEP_TWO = "communityStep2Fragment";
    public final static String COMMUNITY_STEP_THREE = "communityStep3Fragment";
    public final static String COMMUNITY_MODEL = "communityModel";

    static final int REQUEST_INVITE_MEMBER = 1;
    static final int CROP_PIC = 2;

    @Inject
    ICommunityService communityService;
    @Inject
    IConnectionService connectionService;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private CommunityModel community;
    private CloudinaryUploadSuccessAction cloudinaryUploadSuccessAction;
    private MaskedImageView communityPic;
    Boolean isConnectionAvilable=true;
    Intent cropIntent;
    private Uri picUri;
    File myFile;

    @Override
    protected int getContentResId() {
        return R.layout.activity_create_community;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        initFields();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getDataFromServer();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void initFields() {
        community = new CommunityModel();
        cloudinaryUploadSuccessAction = new CloudinaryUploadSuccessAction();

        addFragmentWithoutStackEntry(R.id.create_community_frame, CommunityStep2Fragment.newInstance(community), COMMUNITY_STEP_TWO);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        addActions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeActions();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void setCommunityPicReference(MaskedImageView communityPic) {
        this.communityPic = communityPic;
    }

    @Override
    public void CreateCommunity() {
        createCommunity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void addActions() {
        addAction(QuickBloxServiceConsts.CLOUDINARY_LOAD_SUCCESS_ACTION, cloudinaryUploadSuccessAction);
        addAction(QuickBloxServiceConsts.CLOUDINARY_LOAD_FAIL_ACTION, failAction);

        updateBroadcastActionList();
    }

    protected void removeActions() {
        removeAction(QuickBloxServiceConsts.CLOUDINARY_LOAD_SUCCESS_ACTION);
        removeAction(QuickBloxServiceConsts.CLOUDINARY_LOAD_FAIL_ACTION);

        updateBroadcastActionList();
    }

    public void createCommunity() {
        if (isNetworkAvailable()) {
            showProgress();
            if (community.picUrl == null) {
                create();
            } else {
                startLoadAttachFile(new File(community.picUrl));
            }
        } else {
            ToastUtils.longToast(NetworkDetector.NETWORK_ERROR);
        }
    }

    protected void startLoadAttachFile(final File file) {
        CloudinaryImageUploadCommand.start(this, file);
    }
    public void getDataFromServer() {

        connectionService.myConnection(new PageInput(), new AsyncResult<List<ConnectionModel>>() {
            @Override
            public void success(final List<ConnectionModel> connectionModels) {
               if(connectionModels.size()>0){
                   isConnectionAvilable =true;
               }else{
                   isConnectionAvilable =false;
               }
            }
            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(error);
                        hideProgress();
                    }
                });
            }
        });

    }

    private void create() {
        showProgress();
        communityService.createCommunity(community, new AsyncResult<CommunityModel>() {
            @Override
            public void success(final CommunityModel communityModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        if (isConnectionAvilable) {
                            Intent intent = new Intent(CreateCommunityActivity.this, InviteFromLoopActivity.class);
                            CommunityModel model = new CommunityModel();
                            model.setServerId(communityModel.getServerId());
                            intent.putExtra(Constants.MODEL_COMMUNITY, model);
                            startActivityForResult(intent, REQUEST_INVITE_MEMBER);
                        }
                        else {
                            Intent intent =new Intent(CreateCommunityActivity.this, MyCommunitiesActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        ToastUtils.longToast(error);
                    }
                });
            }
        });
    }

    @Override
    public void onImagePicked(int requestCode, File file) {
        Uri contentUri = Uri.fromFile(file);
        performCrop(contentUri);

    }
    private void performCrop(Uri contentUri) {
        try {
            cropIntent= new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(contentUri, "image/*");

            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 2);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);

            setResult(RESULT_OK);

            startActivityForResult(cropIntent, CROP_PIC);

        }

        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    @Override
    public void onImagePickError(int requestCode, Exception e) {
        ErrorUtils.logError(e);
        ToastUtils.longToast(e.getMessage());
    }

    @Override
    public void onImagePickClosed(int requestCode) {

    }

    private void initMaskedView(Bitmap bitmap) {
        communityPic.setImageBitmap(bitmap);
    }

    public class CloudinaryUploadSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            CloudinaryModel model = (CloudinaryModel) bundle.getSerializable(QuickBloxServiceConsts.EXTRA_CLOUDINARY_MODEL);
            if (model != null) {
                community.picUrl = model.url;
            }
            create();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_INVITE_MEMBER) {
                finish();
            }
            if(requestCode == CROP_PIC)
            {
                Bitmap thePic;
                Bundle extras = data.getExtras();
                try {
                    thePic = extras.getParcelable("data");
                }catch (Exception ex){
                    String path=data.getDataString().replace("file://","");
                    thePic = BitmapFactory.decodeFile(path);
                }
                Uri uri= getImageUri(this,thePic);
                GetFilepathFromUriTask obj =new GetFilepathFromUriTask();
                String path_image= getRealPathFromURI(uri);
                myFile= obj.getFileFromBitmap(path_image);
                community.picUrl = myFile.getAbsolutePath();
                initMaskedView(thePic);
            }
        }
    }

    private Uri getImageUri(CreateCommunityActivity createCommunityActivity, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(createCommunityActivity.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}

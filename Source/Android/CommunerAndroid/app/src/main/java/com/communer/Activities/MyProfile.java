package com.communer.Activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.communer.Application.AppController;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.communer.Utils.PathUtil;
import com.communer.Utils.PhotoMultipartRequest;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static java.util.Locale.setDefault;

public class MyProfile extends AppCompatActivity {

    private EditText myProfileFirstName, myProfileLastName, myProfileBirthdayDay, myProfileBirthdayYear, myProfileUserEmail;
    private Spinner myProfileGenderSpinner, myProfileBirthdaySpinner, myProfileMaritalSpinner;

    private Toolbar toolbar;

    private ImageView myProfileProfilePic;
    private ImageButton editProfilePic;
    private ProgressView request_pb;

    private static final int YOUR_SELECT_PICTURE_REQUEST_CODE = 1;

    private Boolean imageTaken = false;

    private Uri outputFileUri;
    private static Uri tempUri;
    private String[] birthday;

    private RadioGroup radioGroup;
    private SharedPreferences prefs;
    private boolean languageChanged = false;
    private String languagePicked = "";

    DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.noprofilepic)
            .showImageOnFail(R.drawable.noprofilepic)
            .showImageForEmptyUri(R.drawable.noprofilepic)
            .resetViewBeforeLoading(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .cacheOnDisk(true)
            .cacheInMemory(false)
            .considerExifParams(true)
            .build();
    ImageLoader mLoader = ImageLoader.getInstance();

    private String mixPanelProjectToken = "537b3567fe348d6c5dfa0d6e2ae688b3";
    private MixpanelAPI mixpanel;

    private String updateLangUrl = "http://93.188.166.107/selfow/server/1696164739/ahSdQpBNXx22XSgTZuyEoI3t/com.solidPeak.smartcom.requests.application.setClientLanguage.htm?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);

        mixpanel = MixpanelAPI.getInstance(MyProfile.this, mixPanelProjectToken);
        mixpanel.track("Modules - My Profile Page");

        setViewsReference();

        prefs = getSharedPreferences("SmartCommunity", Activity.MODE_PRIVATE);

        initToolbar();

        setupSpinners();
        initRadioGroup();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_myprofile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_send:
                request_pb.setVisibility(View.VISIBLE);
                if (imageTaken) {
                    File imgFile = new File(PathUtil.getPath(MyProfile.this, outputFileUri));
                    setProfileDetailsWithImg(imgFile);
                }else {
                    String showMessage = resolveShowMessage();
                    if (!showMessage.equals("")) {
                        Toast.makeText(MyProfile.this, showMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        setProfileDetailsGetMethod();
                    }
                }
                break;

            case R.id.action_edit:
                openImageIntent();
                break;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(MyProfile.this, R.color.white));
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        request_pb = (ProgressView)toolbar.findViewById(R.id.toolbar_login_pb);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getResources().getString(R.string.my_profile_title));
    }

    private void setViewsReference(){
        AppUser appUserInstance = AppUser.getInstance();

        String firstName = appUserInstance.getUserFirstName();
        String lastName = appUserInstance.getUserLastName();
        birthday = appUserInstance.getDate(appUserInstance.getUserBday(),"dd/MM/yyyy").split("/");
        String email = appUserInstance.getUserEmail();
        String profilePic = appUserInstance.getUserImageUrl();

        myProfileFirstName = (EditText)findViewById(R.id.login_details_firstname);
        if (!firstName.equals(""))
            myProfileFirstName.setText(firstName);

        myProfileLastName = (EditText) findViewById(R.id.login_details_lastname);
        if (!lastName.equals("")) {
            myProfileLastName.setText(lastName);
        }
        myProfileBirthdayDay = (EditText) findViewById(R.id.login_details_day);
        if (birthday.length >= 1) {
            myProfileBirthdayDay.setText(birthday[0]);
        }

        myProfileBirthdayYear = (EditText) findViewById(R.id.login_details_year);
        if (birthday.length >= 3) {
            myProfileBirthdayYear.setText(birthday[2]);
        }

        myProfileUserEmail = (EditText) findViewById(R.id.login_details_email);
        if (!email.equals("")) {
            myProfileUserEmail.setText(email);
        }

        myProfileGenderSpinner = (Spinner) findViewById(R.id.login_details_gender_spinner);
        myProfileBirthdaySpinner = (Spinner) findViewById(R.id.login_details_month_spinner);

        myProfileMaritalSpinner = (Spinner) findViewById(R.id.login_details_marital_spinner);

        myProfileProfilePic = (ImageView) findViewById(R.id.login_details_profile_pic);
        if (!profilePic.equals("")) {
            if (profilePic.contains("facebook")){
                profilePic = profilePic.replace("%26","&");
            }
            mLoader.displayImage(profilePic, myProfileProfilePic, imageOptions);
        }
    }

    private void setupSpinners() {
        ////Gender Spinner
        ArrayList<String> genderList = new ArrayList<String>();
        genderList.add("Male");
        genderList.add("Female");

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, genderList);
        myProfileGenderSpinner.setAdapter(genderAdapter);

        AppUser appUserInstance = AppUser.getInstance();
        String gender = appUserInstance.getUserGender();

        if (gender.equalsIgnoreCase("female")) {
            myProfileGenderSpinner.setSelection(1);
        } else {
            myProfileGenderSpinner.setSelection(0);
        }

        ////Status Spinner
        ArrayList<String> maritalList = new ArrayList<String>();
        maritalList.add("Single");
        maritalList.add("Married");
        maritalList.add("It's complicated");
        ArrayAdapter<String> maritalAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, maritalList);
        myProfileMaritalSpinner.setAdapter(maritalAdapter);

        String marriageStatus = appUserInstance.getUserStatus();

        if (marriageStatus.equalsIgnoreCase("single"))
            myProfileMaritalSpinner.setSelection(0);
        else if(marriageStatus.equalsIgnoreCase("married"))
            myProfileMaritalSpinner.setSelection(1);
        else if(marriageStatus.equalsIgnoreCase("it's complicated"))
            myProfileMaritalSpinner.setSelection(2);

        ////Month Spinner
        ArrayList<String> birthdayList = new ArrayList<String>();
        birthdayList.add("January");
        birthdayList.add("February");
        birthdayList.add("March");
        birthdayList.add("April");
        birthdayList.add("May");
        birthdayList.add("June");
        birthdayList.add("July");
        birthdayList.add("August");
        birthdayList.add("September");
        birthdayList.add("October");
        birthdayList.add("November");
        birthdayList.add("December");

        ArrayAdapter<String> birthdayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, birthdayList);
        myProfileBirthdaySpinner.setAdapter(birthdayAdapter);
        if (birthday.length >= 2) {
            myProfileBirthdaySpinner.setSelection(Integer.valueOf(birthday[1]) - 1);
        }
    }

    private void setProfileDetailsGetMethod(){
        request_pb.setVisibility(View.VISIBLE);

        final AppUser appUserInstance = AppUser.getInstance();
        String userHash = appUserInstance.getUserHash();
        String url = appUserInstance.getBaseUrl() + userHash + "/com.solidPeak.smartcom.requests.application.SendUserUpdateData.htm?";

        JSONObject user = new JSONObject();

        String userID = "";
        final String first_name = myProfileFirstName.getText().toString();
        final String last_name = myProfileLastName.getText().toString();
        final String gender = myProfileGenderSpinner.getSelectedItem().toString();
        final String status = myProfileMaritalSpinner.getSelectedItem().toString();
//        final String bday = myProfileBirthdayDay.getText().toString() + "-" + String.valueOf(myProfileBirthdaySpinner.getSelectedItemPosition() + 1) + "-" + myProfileBirthdayYear.getText().toString();

        Calendar bdayTime = Calendar.getInstance();
        bdayTime.set(Calendar.YEAR, Integer.valueOf(myProfileBirthdayYear.getText().toString()));
        bdayTime.set(Calendar.MONTH, myProfileBirthdaySpinner.getSelectedItemPosition());
        bdayTime.set(Calendar.DAY_OF_MONTH, Integer.valueOf(myProfileBirthdayDay.getText().toString()));
        final Long millis = bdayTime.getTimeInMillis();

        final String email = myProfileUserEmail.getText().toString();

        try {
            user.put("userID", userID);
            user.put("name", first_name);
            user.put("lastName", last_name);
            user.put("gender", gender);
            user.put("mStatus", status);
            user.put("bDay", millis);
            user.put("email", email);
            user.put("imageURL", appUserInstance.getUserImageUrl());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String query = null;
        try{
            query = URLEncoder.encode(user.toString(), "utf-8");
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }

        String sendPostUrl = url + "user=" + query;
        String tag_json_obj = "update_profile_details_call";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, sendPostUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String s) {
                String newProfilePic = s;

                appUserInstance.setUserImageUrl(newProfilePic);
                appUserInstance.setUserFirstName(first_name);
                appUserInstance.setUserLastName(last_name);
                appUserInstance.setUserGender(gender);
                appUserInstance.setUserStatus(status);
                appUserInstance.setUserBday(millis);
                appUserInstance.setUserEmail(email);

                request_pb.setVisibility(View.GONE);
                //TODO remember to add the profile pic url when the server supports it
                Toast.makeText(MyProfile.this, "Details updated succesfully", Toast.LENGTH_SHORT).show();
                if (languageChanged){
                    Locale locale = null;
                    if (languagePicked.equals("heb")){
                        prefs.edit().putString("last_language_picked", "iw").commit();
                        locale = new Locale("iw");
                    }else if (languagePicked.equals("en")){
                        prefs.edit().putString("last_language_picked", "en").commit();
                        locale = new Locale("en");
                    }

                    setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

                    Intent mIntent = new Intent(MyProfile.this, TabsActivity.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivity(mIntent,
                                ActivityOptions.makeSceneTransitionAnimation(MyProfile.this).toBundle());
                    }else {
                        startActivity(mIntent);
                    }
                }else{
                    onBackPressed();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MyProfile.this, "Error updating details", Toast.LENGTH_SHORT).show();
                request_pb.setVisibility(View.GONE);
            }
        });

        stringRequest.setShouldCache(false);
        String update_lang_json_obj = "update_user_language";
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(25000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);

        if (languageChanged){
            String updateUserLanguageUrl = "";
            if (languagePicked.equals("heb")){
                updateUserLanguageUrl = updateLangUrl + "he";
            }else if (languagePicked.equals("en")){
                updateUserLanguageUrl = updateLangUrl + "en";
            }

            StringRequest languageStringRequest = new StringRequest(Request.Method.GET, updateUserLanguageUrl, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            languageStringRequest.setShouldCache(false);
            AppController.getInstance().addToRequestQueue(languageStringRequest, update_lang_json_obj);
        }
    }

    private void setProfileDetailsWithImg(File imageFile) {
        request_pb.setVisibility(View.VISIBLE);

        AppUser appUserInstance = AppUser.getInstance();
        String userHash = appUserInstance.getUserHash();
        String url = appUserInstance.getBaseUrl() + userHash + "/com.solidPeak.smartcom.requests.application.SendUserUpdateData.htm?";

        JSONObject user = new JSONObject();

        String userID = "";
        String first_name = myProfileFirstName.getText().toString();
        String last_name = myProfileLastName.getText().toString();
        String gender = myProfileGenderSpinner.getSelectedItem().toString();
        String status = myProfileMaritalSpinner.getSelectedItem().toString();
        String bday = myProfileBirthdayDay.getText().toString() + "-" + String.valueOf(myProfileBirthdaySpinner.getSelectedItemPosition() + 1) + "-" + myProfileBirthdayYear.getText().toString();
        String email = myProfileUserEmail.getText().toString();

        try {
            user.put("userID", userID);
            user.put("name", first_name);
            user.put("lastName", last_name);
            user.put("gender", gender);
            user.put("mStatus", status);
            user.put("bDay", bday);
            user.put("email", email);
            user.put("imageURL", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String sendPostUrl = url + "user=" + user.toString();
        sendPostUrl = sendPostUrl.replace(" ", "%20");
        String tag_json_obj = "update_profile_details_call";

        PhotoMultipartRequest stringRequest = new PhotoMultipartRequest(sendPostUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String s) {
                if (s != null){
                    if (s.equals("success")){
                        Toast.makeText(MyProfile.this, "Details updated succesfully", Toast.LENGTH_SHORT).show();
                        if (languageChanged){
                            Locale locale = null;
                            if (languagePicked.equals("heb")){
                                prefs.edit().putString("last_language_picked", "iw").commit();
                                locale = new Locale("iw");
                            }else if (languagePicked.equals("en")){
                                prefs.edit().putString("last_language_picked", "en").commit();
                                locale = new Locale("en");
                            }

                            setDefault(locale);
                            Configuration config = new Configuration();
                            config.locale = locale;
                            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

                            Intent mIntent = new Intent(MyProfile.this, TabsActivity.class);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                startActivity(mIntent,
                                        ActivityOptions.makeSceneTransitionAnimation(MyProfile.this).toBundle());
                            }else {
                                startActivity(mIntent);
                            }
                        }else{
                            onBackPressed();
                        }
                    }else{
                        Toast.makeText(MyProfile.this, "Error updating details", Toast.LENGTH_SHORT).show();
                    }
                    request_pb.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MyProfile.this, "Error updating details", Toast.LENGTH_SHORT).show();
                request_pb.setVisibility(View.GONE);
            }
        },imageFile);


        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(25000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private String resolveShowMessage() {
        String message = "";

        if (!myProfileBirthdayYear.getText().toString().equals("")) {
            if (Integer.valueOf(myProfileBirthdayYear.getText().toString()) < 1920 || Integer.valueOf(myProfileBirthdayYear.getText().toString()) > 2015) {
                message = "Please provide a valid date";
            }
        }

        if (!myProfileBirthdayDay.getText().toString().equals("")){
            if (Integer.valueOf(myProfileBirthdayDay.getText().toString()) < 1 || Integer.valueOf(myProfileBirthdayDay.getText().toString()) > 30){
                message = "Please provide a valid date";
            }
        }

        if (!myProfileUserEmail.getText().toString().equals("")){
            if (!AppController.isValidEmail(myProfileUserEmail.getText())){
                message = "Please provide a valid email";
            }
        }

        return message;
    }

    private void openImageIntent() {
        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Communer" + File.separator);
        if (!root.exists())
            root.mkdirs();

        final String fname = "Communer" + System.currentTimeMillis();
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getApplicationContext().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == YOUR_SELECT_PICTURE_REQUEST_CODE && resultCode == RESULT_OK) {

            final boolean isCamera;
            if (data == null) {
                isCamera = true;
            } else {
                final String action = data.getAction();
                if (action == null) {
                    isCamera = false;
                } else {
                    isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                }
            }
            if (isCamera) {
                try {
                    InputStream image_stream = getApplicationContext().getContentResolver().openInputStream(outputFileUri);
                    Bitmap thumb = BitmapFactory.decodeStream(image_stream);

                    image_stream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                outputFileUri = data == null ? null : data.getData();
            }
            beginCrop(outputFileUri);
        }

        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
            imageTaken = true;
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).withAspect(2, 1).start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Crop.getOutput(result));
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                ExifInterface exif = new ExifInterface(outputFileUri.getPath());
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = exifToDegrees(rotation);
                if (rotationInDegrees == 0)
                    bitmap = RotateBitmap(bitmap, 0);
                else if (rotationInDegrees == 90)
                    bitmap = RotateBitmap(bitmap, 90);
                else if (rotationInDegrees == 270)
                    bitmap = RotateBitmap(bitmap, 270);
                else
                    bitmap = RotateBitmap(bitmap, 270);
            } catch (IOException e) {
                e.printStackTrace();
            }
            myProfileProfilePic.setImageBitmap(bitmap);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        tempUri = outputFileUri;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        outputFileUri = tempUri;
    }

    private void initRadioGroup() {
        radioGroup = (RadioGroup) findViewById(R.id.language_radiogroup);

        String latestLanguage = prefs.getString("last_language_picked", "en");
        if (latestLanguage.equals("en")){
            radioGroup.check(R.id.eng_radio);
        }else{
            radioGroup.check(R.id.heb_radio);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                languageChanged = true;
                if(checkedId == R.id.heb_radio) {
                    languagePicked = "heb";
                }else if (checkedId == R.id.eng_radio) {
                    languagePicked = "en";
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

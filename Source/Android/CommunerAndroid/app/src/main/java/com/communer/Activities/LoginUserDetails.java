package com.communer.Activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LoginUserDetails extends AppCompatActivity {

    private Toolbar toolbar;

    private String user_id, name, first_name, last_name, email, birthday, gender, photoUrl;
    private boolean isFacebookLogin = false;
    private boolean isGoogleLogin = false;

    private EditText firstNameEdit, lastNameEdit, emailEdit, dayBirthEdit, yearBirthEdit;
    private Spinner genderSpin, birthdaySpin, maritalSpin;
    private ImageView profilePic;
    private ProgressView request_pb;

    boolean doubleBackToExitPressedOnce = false;
    String imageUrl = "";

    ////Taking Picture Vars
    private static final int YOUR_SELECT_PICTURE_REQUEST_CODE = 1111;
    private Uri outputFileUri;
    private static Uri tempUri;
    private Boolean cameraCapture = true;
    private Boolean imageTaken = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginuserdetails);

        new MaterialDialog.Builder(this)
                .content("Your number has successfully been identified. Please add your details and then you can begin connecting to your communities.")
                .contentColor(ContextCompat.getColor(LoginUserDetails.this,R.color.PrimaryText))
                .positiveColor(ContextCompat.getColor(LoginUserDetails.this, R.color.DividerColor))
                .positiveText("Continue")
                .dividerColor(ContextCompat.getColor(LoginUserDetails.this, R.color.DividerColor))
                .show();


        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(LoginUserDetails.this,R.color.white));
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getResources().getString(R.string.sign_up_title));

        ImageButton sendBtn = (ImageButton)toolbar.findViewById(R.id.toolbarSendBtn);
        ImageButton editPicBtn = (ImageButton)toolbar.findViewById(R.id.editProfileImage);
        request_pb = (ProgressView)toolbar.findViewById(R.id.toolbar_login_pb);

        firstNameEdit = (EditText)findViewById(R.id.login_details_firstname);
        lastNameEdit = (EditText)findViewById(R.id.login_details_lastname);
        emailEdit = (EditText)findViewById(R.id.login_details_email);
        dayBirthEdit = (EditText)findViewById(R.id.login_details_day);
        yearBirthEdit = (EditText)findViewById(R.id.login_details_year);
        profilePic = (ImageView)findViewById(R.id.login_details_profile_pic);

        genderSpin = (Spinner)findViewById(R.id.login_details_gender_spinner);
        maritalSpin = (Spinner)findViewById(R.id.login_details_marital_spinner);
        birthdaySpin = (Spinner) findViewById(R.id.login_details_month_spinner);

        ArrayList<String> genderList = new ArrayList<String>();
        genderList.add("Male");
        genderList.add("Female");
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,genderList);
        genderSpin.setAdapter(genderAdapter);

        ArrayList<String> maritalList = new ArrayList<String>();
        maritalList.add("Single");
        maritalList.add("Married");
        maritalList.add("It's complicated");
        ArrayAdapter<String> maritalAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,maritalList);
        maritalSpin.setAdapter(maritalAdapter);

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
        ArrayAdapter<String> birthdayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,birthdayList);
        birthdaySpin.setAdapter(birthdayAdapter);

        handleIntentExtras();
        setEntranceCount();

        getSupportActionBar().setDisplayShowTitleEnabled(true);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request_pb.setVisibility(View.VISIBLE);
                if (imageTaken){
                    File imgFile = new File(PathUtil.getPath(LoginUserDetails.this, outputFileUri));
                    setProfileDetailsWithImg(imgFile);
                }else{
                String showMessage = resolveShowMessage();
                    if (!showMessage.equals("")){
                        Toast.makeText(LoginUserDetails.this, showMessage, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        sendUserDetails();
                    }
                }
            }
        });

        editPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });
    }

    private String resolveShowMessage() {
        String message = "";

        if (!yearBirthEdit.getText().toString().equals("")) {
            if (Integer.valueOf(yearBirthEdit.getText().toString()) < 1920 || Integer.valueOf(yearBirthEdit.getText().toString()) > 2015) {
                message = "Please provide a valid date";
            }
        }

        if (!dayBirthEdit.getText().toString().equals("")){
            if (Integer.valueOf(dayBirthEdit.getText().toString()) < 1 || Integer.valueOf(dayBirthEdit.getText().toString()) > 30){
                message = "Please provide a valid date";
            }
        }

        if (!emailEdit.getText().toString().equals("")){
            if (!AppController.isValidEmail(emailEdit.getText())){
                message = "Please provide a valid email";
            }
        }

        return message;
    }

    private void sendUserDetails(){
        SharedPreferences sp = getSharedPreferences("SmartCommunity", Activity.MODE_PRIVATE);
        String userHash = sp.getString("userHash", "noData");

        AppUser appUserInstance = AppUser.getInstance();
        String url = appUserInstance.getBaseUrl() + userHash + "/com.solidPeak.smartcom.requests.application.SendUserUpdateData.htm?";

        JSONObject user = new JSONObject();

        String userID = "";
        final String first_name = firstNameEdit.getText().toString();
        final String last_name = lastNameEdit.getText().toString();
        final String gender = genderSpin.getSelectedItem().toString();
        final String status = maritalSpin.getSelectedItem().toString();
        final String bday = dayBirthEdit.getText().toString() + "-" + String.valueOf(birthdaySpin.getSelectedItemPosition() + 1) + "-" + yearBirthEdit.getText().toString();
        final String email = emailEdit.getText().toString();

        String imageURL = imageUrl;

        Long bdayMillis = 0L;
        String yearVal = yearBirthEdit.getText().toString();
        String dayVal = dayBirthEdit.getText().toString();

        if (yearVal.length() > 0 && dayVal.length() > 0){
            Calendar bdayTime = Calendar.getInstance();
            bdayTime.set(Calendar.YEAR,Integer.valueOf(yearVal));
            bdayTime.set(Calendar.MONTH, birthdaySpin.getSelectedItemPosition());
            bdayTime.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dayVal));
            bdayMillis = bdayTime.getTimeInMillis();
        }

//        String imageURL = "";

        try {
            user.put("userID",userID);
            user.put("name",first_name);
            user.put("lastName",last_name);
            user.put("gender",gender);
            user.put("mStatus",status);
            user.put("bDay",bdayMillis);
            user.put("email", email);

            if (!imageURL.equals(""))
                imageURL = imageURL.replace("&","%26");

            user.put("imageURL", imageURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String sendPostUrl = url + "user=" + user.toString();
        sendPostUrl = sendPostUrl.replace(" ","%20");
        String tag_json_obj = "update_profile_details_call";

        final String finalImageURL = imageURL;
        final Long finalBdayMillis = bdayMillis;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, sendPostUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String s) {
                if (s.equals("success")){
                    setAppUserDetails(first_name, last_name, gender, status, bday, email, finalImageURL, finalBdayMillis);
                    Intent mIntent = new Intent(LoginUserDetails.this, SearchCommunity.class);
                    startActivity(mIntent);
                }else{
                    request_pb.setVisibility(View.GONE);
                    Toast.makeText(LoginUserDetails.this, "Error updating details", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                request_pb.setVisibility(View.GONE);
                Toast.makeText(LoginUserDetails.this, "Error updating details", Toast.LENGTH_SHORT).show();
            }
        });

        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(25000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private void setProfileDetailsWithImg(File imageFile) {
        SharedPreferences sp = getSharedPreferences("SmartCommunity", Activity.MODE_PRIVATE);
        String userHash = sp.getString("userHash", "noData");

        AppUser appUserInstance = AppUser.getInstance();
        String url = appUserInstance.getBaseUrl() + userHash + "/com.solidPeak.smartcom.requests.application.SendUserUpdateData.htm?";

        JSONObject user = new JSONObject();

        String userID = "";
        final String first_name = firstNameEdit.getText().toString();
        final String last_name = lastNameEdit.getText().toString();
        final String gender = genderSpin.getSelectedItem().toString();
        final String status = maritalSpin.getSelectedItem().toString();
        final String bday = dayBirthEdit.getText().toString() + "-" + String.valueOf(birthdaySpin.getSelectedItemPosition() + 1) + "-" + yearBirthEdit.getText().toString();
        final String email = emailEdit.getText().toString();

        Calendar bdayTime = Calendar.getInstance();
        bdayTime.set(Calendar.YEAR,Integer.valueOf(yearBirthEdit.getText().toString()));
        bdayTime.set(Calendar.MONTH, birthdaySpin.getSelectedItemPosition());
        bdayTime.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dayBirthEdit.getText().toString()));
        final Long millis = bdayTime.getTimeInMillis();

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
                if (s.equals("success")){
                    setAppUserDetails(first_name,last_name,gender,status,bday,email, "", millis);
                    Intent mIntent = new Intent(LoginUserDetails.this, SearchCommunity.class);
                    startActivity(mIntent);
                }else{
                    request_pb.setVisibility(View.GONE);
                    Toast.makeText(LoginUserDetails.this, "Error updating details", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                request_pb.setVisibility(View.GONE);
                Toast.makeText(LoginUserDetails.this, "Error updating details", Toast.LENGTH_SHORT).show();
            }
        },imageFile);


        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(25000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private void handleIntentExtras() {
        Intent intent = getIntent();
        String login_method = intent.getStringExtra("login_method");
        if (login_method != null){
            switch (login_method){
                case "facebook":
                    isFacebookLogin = true;
                    email = intent.getStringExtra("email");
                    first_name = intent.getStringExtra("first_name");
                    last_name = intent.getStringExtra("last_name");
                    birthday = intent.getStringExtra("birthday");
                    gender = intent.getStringExtra("gender");
                    user_id = intent.getStringExtra("id");

                    editDetails();
                    break;

                case "google":
                    isGoogleLogin = true;
                    email = intent.getStringExtra("email");
                    first_name = intent.getStringExtra("first_name");
                    last_name = intent.getStringExtra("last_name");
                    photoUrl = intent.getStringExtra("photo");
                    imageUrl = photoUrl.replace("sz=50","sz=600");
                    if (intent.getIntExtra("gender",0)==0){
                        gender = "male";
                    }
                    else {
                        gender = "female";
                    }
                    editDetails();
                    break;

                default:
                    setDetailsFromAppUser();
                    break;
            }
        }
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

    private void editDetails() {
        if (isFacebookLogin){
            if (first_name != null)
                firstNameEdit.setText(first_name);

            if (last_name != null)
                lastNameEdit.setText(last_name);

            if (email != null)
                emailEdit.setText(email);

            if (gender != null){
                if (gender.equals("male")){
                    genderSpin.setSelection(0);
                }else{
                    genderSpin.setSelection(1);
                }
            }

            String month = null;
            String day = null;
            String year = null;

            if (birthday != null){
                String[] separatedBirth = birthday.split("/");
                month = separatedBirth[0];
                day = separatedBirth[1];
                year = separatedBirth[2];
            }


            if (month != null && day != null && year != null){
                birthdaySpin.setSelection(Integer.valueOf(month) - 1);
                dayBirthEdit.setText(day);
                yearBirthEdit.setText(year);
            }

            String firstSection = "https://graph.facebook.com/v2.4/";
            String secondSection = "/picture?width=600&height=600";
            String fbProfilePic = firstSection+ user_id + secondSection;
            imageUrl = fbProfilePic;
            Picasso.with(LoginUserDetails.this)
                    .load(fbProfilePic)
                    .error(R.drawable.noprofilepic)
                    .into(profilePic);

        }else if (isGoogleLogin){
            if (first_name != null)
                firstNameEdit.setText(first_name);

            if (last_name != null)
                lastNameEdit.setText(last_name);

            if (email != null)
                emailEdit.setText(email);

            if (!imageUrl.equals("")){
                Picasso.with(LoginUserDetails.this)
                        .load(imageUrl)
                        .error(R.drawable.noprofilepic)
                        .resize(dpToPx(300), dpToPx(150))
                        .centerInside()
                        .into(profilePic);
            }
            else{
                Picasso.with(LoginUserDetails.this)
                        .load(R.drawable.noprofilepic)
                        .into(profilePic);
            }

            if (gender.equals("male"))
                genderSpin.setSelection(0);
            else
                genderSpin.setSelection(1);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent setIntent = new Intent(Intent.ACTION_MAIN);
            setIntent.addCategory(Intent.CATEGORY_HOME);
            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(setIntent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void setEntranceCount(){
        SharedPreferences sp = getSharedPreferences("SmartCommunity", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        int entranceCount = sp.getInt("entrance_count", -1);
        if (entranceCount == -1){
            editor.putInt("entrance_count", 1);
        }else{
            editor.putInt("entrance_count", entranceCount + 1);
        }
        editor.commit();
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
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
//        Crop.of(source, destination).withAspect(2, 1).start(this);
        Crop.of(source, destination).withAspect(4, 3).start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Crop.getOutput(result));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (cameraCapture) {
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
            }
            profilePic.setImageBitmap(bitmap);
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

    private void setAppUserDetails(String first_name, String last_name, String gender, String status, String bday, String email, String imageURL, Long millis){
        AppUser appUserInstance = AppUser.getInstance();

        appUserInstance.setUserFirstName(first_name);
        appUserInstance.setUserLastName(last_name);
        appUserInstance.setUserGender(gender);
        appUserInstance.setUserStatus(status);
        appUserInstance.setUserEmail(email);
        appUserInstance.setUserImageUrl(imageURL);
        appUserInstance.setUserBday(millis);
    }

    private void setDetailsFromAppUser() {
        AppUser appUserInstance = AppUser.getInstance();

        String userFirstName = appUserInstance.getUserFirstName();
        String userLastName = appUserInstance.getUserLastName();
        String userGender = appUserInstance.getUserGender();
        String userStatus = appUserInstance.getUserStatus();
        String userImageUrl = appUserInstance.getUserImageUrl();
        String userEmail = appUserInstance.getUserEmail();
        Long userBday = appUserInstance.getUserBday();

        firstNameEdit.setText(userFirstName);
        lastNameEdit.setText(userLastName);
        emailEdit.setText(userEmail);

        if (userGender != null){
            if (userGender.equals("male")){
                genderSpin.setSelection(0);
            }else{
                genderSpin.setSelection(1);
            }
        }

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(userBday);

        birthdaySpin.setSelection(c.get(Calendar.MONTH) - 1);
        dayBirthEdit.setText(c.get(Calendar.DAY_OF_MONTH));
        yearBirthEdit.setText(c.get(Calendar.YEAR));

        if (!userImageUrl.equals("")){
            Picasso.with(LoginUserDetails.this)
                    .load(userImageUrl)
                    .error(R.drawable.noprofilepic)
                    .resize(dpToPx(300), dpToPx(150))
                    .centerInside()
                    .into(profilePic);
        }
        else{
            Picasso.with(LoginUserDetails.this)
                    .load(R.drawable.noprofilepic)
                    .into(profilePic);
        }

        if (userStatus.equals("Single"))
            maritalSpin.setSelection(0);
        else
            maritalSpin.setSelection(1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

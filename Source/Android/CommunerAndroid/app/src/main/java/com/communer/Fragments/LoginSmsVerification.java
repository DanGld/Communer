package com.communer.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.communer.Activities.LoginUserDetails;
import com.communer.Application.AppController;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.communer.Parsers.FirstDataParser;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.rey.material.widget.ProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by ���� on 28/07/2015.
 */
public class LoginSmsVerification extends Fragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private ImageButton signInBtn;
    private ProgressView smsPB;
    private EditText sms_edittext;

    private String userPhone = "";
    private String smsURL = "";

    private CallbackManager callbackManager;

    private static final int RC_SIGN_IN = 0;

    private GoogleApiClient mGoogleApiClient;
    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;

    private final static String TAG = "SmartCommunity_Tag";
    private boolean googleSignIn = false;
    private SharedPreferences sharedPreferences;

    private String mixPanelProjectToken = "537b3567fe348d6c5dfa0d6e2ae688b3";
    private MixpanelAPI mixpanel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loginpagesms, container, false);
        Bundle extras = getArguments();
        userPhone = extras.getString("user_phone");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mixpanel = MixpanelAPI.getInstance(getActivity(), mixPanelProjectToken);

        smsPB = (ProgressView)view.findViewById(R.id.sms_pb_progressview);
        signInBtn = (ImageButton)view.findViewById(R.id.signInSMSButton);
        sms_edittext = (EditText)view.findViewById(R.id.signInSMCode);

        AppUser appUserInstance = AppUser.getInstance();
        smsURL = appUserInstance.getBaseUrl() + "KLR9zwarL1iGs8EFyZLs4keh/com.solidPeak.smartcom.requests.application.getUserKeyCheck.htm?";

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifySMS(sms_edittext.getText().toString());
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        sharedPreferences = getActivity().getSharedPreferences("SmartCommunity", Activity.MODE_PRIVATE);
    }

    private void verifySMS(String smsCode){
        smsPB.setVisibility(View.VISIBLE);
        String url = smsURL + "phone=" + userPhone + "&key=" + smsCode;
        String tag_json_obj = "sms_verify_request";
        Log.d("Volley", url);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley", response.toString());
                        try {
                            String userHash = response.getString("hash");
                            if (!userHash.equals("keyCode error")){
                                SharedPreferences sp = getActivity().getSharedPreferences("SmartCommunity", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("userHash",userHash).commit();

                                AppUser appUserInstance = AppUser.getInstance();
                                appUserInstance.setUserHash(userHash);

                                FirstDataParser parser = new FirstDataParser(getActivity(), userHash,"login_sms");
                                parser.getFirstTimeData("24.323288", "26.135633");
                            }else{
                                smsPB.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "Wrong code, please check you messages", Toast.LENGTH_SHORT).show();
                                sms_edittext.setError("Wrong Code");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
                Toast.makeText(getActivity(), "Couldn't connect to server, try again", Toast.LENGTH_SHORT).show();
            }
        });

        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void showSocialLoginDialog(){
        smsPB.setVisibility(View.GONE);
        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.smartdialog, false)
                .title("Sign Up")
                .titleGravity(GravityEnum.CENTER)
                .positiveText("SKIP")
                .cancelable(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        dialog.dismiss();

                        mixpanel.track("Login - Skipped");
                        Intent mIntent = new Intent(getActivity(), LoginUserDetails.class);
                        startActivity(mIntent);
                    }
                })
                .show();

        View view = dialog.getCustomView();

        callbackManager = CallbackManager.Factory.create();
        LoginButton facebookBtn = (LoginButton)view.findViewById(R.id.smartDialogFacebook);
        facebookBtn.setFragment(this);
        facebookBtn.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
        facebookBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Facebook login success");
                try {
                    getUserFacebookDetails(loginResult);
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Facebook login cancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d(TAG, "Facebook login error");
                Toast.makeText(getActivity(), "Couldn't login with facebook, try again" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        SignInButton googleBtn = (SignInButton)view.findViewById(R.id.smartDialogGoogle);

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                } else {
                    mShouldResolve = true;
                    mGoogleApiClient.connect();
                }
            }
        });
    }

    private void getUserFacebookDetails(LoginResult loginResult) {
        final AccessToken token = loginResult.getAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        Log.v("LoginActivity", graphResponse.toString());
                        sharedPreferences.edit().putString("latestFbLogin", jsonObject.toString());
                        parseFBData(jsonObject, token);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,gender, birthday,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case RC_SIGN_IN:
                    handleActivityResult(resultCode);
                    break;

                default:
                    break;
            }
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            String personPhoto = currentPerson.getImage().getUrl();
            String firstName = currentPerson.getName().getGivenName();
            String lastName = currentPerson.getName().getFamilyName();
            String personGooglePlusProfile = currentPerson.getUrl();
            String date = currentPerson.getBirthday();
            if (date==null) {
                date = "";
            }
            int gender = currentPerson.getGender();
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

            Intent mIntent = new Intent(getActivity(), LoginUserDetails.class);
            mIntent.putExtra("login_method", "google");
            mIntent.putExtra("email", email);
            mIntent.putExtra("first_name", firstName);
            mIntent.putExtra("gender",gender);
            mIntent.putExtra("last_name",lastName);
            mIntent.putExtra("photo", personPhoto);

            mixpanel.track("Login - Google");
            startActivity(mIntent);
        }
        else{
            Toast.makeText(getContext(),"You do not have a google+ account",Toast.LENGTH_SHORT).show();
            showSocialLoginDialog();
        }

        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("error",i+"");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            }else{
                Log.d(TAG,"Error while sign-in");
            }
        } else {
            showSignedOutUI();
        }
    }

    private void showSignedOutUI() {
        Toast.makeText(getContext(),"You were logged out, please click again to continue",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    public void handleActivityResult(int resultCode){
        if (resultCode != Activity.RESULT_OK) {
            mShouldResolve = false;
        }

        mIsResolving = false;
        mGoogleApiClient.connect();
    }

    public boolean isFacebookLoggedIn() {
        if(AccessToken.getCurrentAccessToken()!=null)
            return true;
        else
            return false;
    }

    private void parseFBData(JSONObject jsonObject, AccessToken token){
        try {
            String id = jsonObject.getString("id");
            String email = jsonObject.getString("email");
//                            String name = jsonObject.getString("name");
            String gender = jsonObject.getString("gender");
            String first_name = jsonObject.getString("first_name");
            String last_name = jsonObject.getString("last_name");

            Intent mIntent = new Intent(getActivity(), LoginUserDetails.class);
            mIntent.putExtra("login_method", "facebook");
            mIntent.putExtra("email", email);
            mIntent.putExtra("first_name", first_name);
            mIntent.putExtra("last_name", last_name);
//            mIntent.putExtra("id", id);
            mIntent.putExtra("id", token.getUserId());
//                            mIntent.putExtra("name",name);
            if (jsonObject.has("birthday")) {
                String birthday = jsonObject.getString("birthday");
                mIntent.putExtra("birthday", birthday);
            }

            mixpanel.track("Login - Facebook");
            mIntent.putExtra("gender", gender);
            startActivity(mIntent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

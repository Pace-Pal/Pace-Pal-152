package com.group2.pacepal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener; //may need to update other files
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
/*

Purpose: This class handles login/register functionality. It currently utilizes Google, FaceBook, and email registration/login.
         The key to understanding the sign in flow is that Google and Facebook methods both converge into the FirebaseAuth function,
         whereas the email sign in/registration route does not.
TODO: allow a user to safely sign in with multiple methods using firebase auth tutorial
TODO: make usernames unique b/c they ain't yet [looked into the firebase rules but that may require a write before a check so that is a pain given the flow]
 */



public class MainActivity extends AppCompatActivity  implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private SignInButton signIn;
    private TextView Name, Email;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 111;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> data = new HashMap<>();

    CallbackManager mCallbackManager = CallbackManager.Factory.create();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
        //set the onclick listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.createAccount2).setOnClickListener(this);
        findViewById(R.id.create_account_with_email_btn).setOnClickListener(this);
        findViewById(R.id.Submit).setOnClickListener(this);

        //Set visibility for buttons and fields
        findViewById(R.id.fnameField).setVisibility(View.INVISIBLE);
        findViewById(R.id.lnameField).setVisibility(View.INVISIBLE);
        findViewById(R.id.unameField).setVisibility(View.INVISIBLE);
        findViewById(R.id.createAccount2).setVisibility(View.INVISIBLE);
        findViewById(R.id.create_account_with_email_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.emailField).setVisibility(View.VISIBLE);
        findViewById(R.id.email_sign_in).setVisibility(View.VISIBLE);
        findViewById(R.id.enter_more_info_text).setVisibility(View.INVISIBLE);
        findViewById(R.id.Submit).setVisibility(View.INVISIBLE);

        //create the edite text fields
        EditText email = findViewById(R.id.emailField);
        EditText password = findViewById(R.id.password_field);
        EditText lastname = findViewById(R.id.lnameField);
        EditText firstname = findViewById(R.id.fnameField);
        EditText username = findViewById(R.id.unameField);



        //facebook shite
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        LoginButton loginButton = findViewById(R.id.login_button);


        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }




        });






        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        printHashKey(this);



        String emailParam = email.getText().toString();
        String usernameParam = username.getText().toString();
        String passwordParam = password.getText().toString();
        String firstnameParam = firstname.getText().toString();
        String lastnameParam = lastname.getText().toString();


    }



    //todo: ensure fields for email and password are unique before getting here
    private void createAccount() {

        EditText passwordParam = findViewById(R.id.password_field);
        EditText emailParam = findViewById(R.id.emailField);
        EditText fName = (EditText)findViewById(R.id.fnameField);
        EditText lName = (EditText) findViewById(R.id.lnameField);
        EditText uName = (EditText) findViewById(R.id.unameField);

        String passwordParamString = passwordParam.getText().toString();
        String emailParamString = emailParam.getText().toString();

        mAuth.createUserWithEmailAndPassword(emailParamString, passwordParamString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("CUEAS", "createUserWithEmail:success");

                            Map<String, Object> data = new HashMap<>();

                            String userid = mAuth.getInstance().getCurrentUser().getUid();

                            data.put("first", fName.getText().toString());
                            data.put("last", lName.getText().toString());
                            data.put("username", uName.getText().toString());
                            data.put("email", emailParamString);
                            data.put("password", passwordParamString);
                            data.put("miles", 0);
                            data.put("friends",0);
                            data.put("challenges",0);
                            data.put("profilepic", "https://firebasestorage.googleapis.com/v0/b/pace-pal-ad8c4.appspot.com/o/defaultAVI.png?alt=media&token=6c9c47df-8151-4e5b-8843-3440e317346c");
                            db.collection("users").document(userid).set(data);

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("CUEAF", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }


                    }
                });

        //remember to make checks for fields before this. Also need to add username and the other fields into the database





        //toMenu();

    }

    private void displayEmailAcctCreationForm() {
        findViewById(R.id.email_sign_in).setVisibility(View.INVISIBLE);
        findViewById(R.id.or).setVisibility(View.INVISIBLE);
        findViewById(R.id.create_account_with_email_btn).setVisibility(View.INVISIBLE);
        findViewById(R.id.fnameField).setVisibility(View.VISIBLE);
        findViewById(R.id.lnameField).setVisibility(View.VISIBLE);
        findViewById(R.id.unameField).setVisibility(View.VISIBLE);
        findViewById(R.id.createAccount2).setVisibility(View.VISIBLE);
        findViewById(R.id.login_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.sign_in_options_text).setVisibility(View.INVISIBLE);
        findViewById(R.id.enter_more_info_text).setVisibility(View.VISIBLE);
    }


    //this function makes it so that a user does not have to sign in upon closing the application and re-opening it
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.v("User", "The user value is " + currentUser);
        updateUI(currentUser);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //This function will activate when the app opens. It checks if the user is already signed in, and takes them to the app's main menu if they are
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //The second step of signing in using the Google sign in button. After this the account object generated in the function is passed to the FirebeAuth
    //function where it is converted to a Firebase Auth token and signs the user in to the Firebase Auth database.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("GSF", "Google sign in failed", e);
                // ...
            }
        }

        //Facebook's sign in manager. If the above isn't true, then it wasn';t a google sign in so call this ( I think is how this works)
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }



    //third step of signing in via google
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("FAGA", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential) //TODO: Get rid of this when integrating multiple auth sign in I am trying to implement
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FAGAS", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d("FAGASU", "The user google found is" + user); 
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FAGAF", "signInWithCredential:failure", task.getException());

                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
    //third step of signing in via Facebook
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    //fourth step of signing in no matter the method
    //All of the UI is updated here accordingly
    private void updateUI(FirebaseUser account) {
        if (account != null) {
            //user has account
            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid(); //may need to switch back to acquiring a new instance altogether
            Log.d("retUid", "The found user in updateUI is: " + userid);
            //Attempt to grab the UID from the firestore database and then check if that retrieval was successful and respond accordingly
            DocumentReference docRef = db.collection("users").document(userid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) { //in this scenario a user has already signed in once before and has a username, first name, and last name in the system. Simply continue to main menu now
                            Log.d("User exists", "DocumentSnapshot data: " + document.getData());
                           // findViewById(R.id.continue_button).setVisibility(View.VISIBLE);
                            //findViewById(R.id.sign_out).setVisibility(View.VISIBLE);
                            toMenu();
                        } else { //in this scenario a user has signed in either with email, FB, or google, for the very first time. So we prompt the user to give fname, lname, and username to store in the database
                            Log.d(TAG, "No such document");
                            findViewById(R.id.fnameField).setVisibility(View.VISIBLE);
                            findViewById(R.id.lnameField).setVisibility(View.VISIBLE);
                            findViewById(R.id.unameField).setVisibility(View.VISIBLE);
                            findViewById(R.id.createAccount2).setVisibility(View.INVISIBLE);
                            findViewById(R.id.create_account_with_email_btn).setVisibility(View.INVISIBLE);
                            findViewById(R.id.enter_more_info_text).setVisibility(View.VISIBLE);
                            findViewById(R.id.email_sign_in).setVisibility(View.INVISIBLE);
                            findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
                            findViewById(R.id.login_button).setVisibility(View.VISIBLE);
                            findViewById(R.id.or).setVisibility(View.INVISIBLE);
                            findViewById(R.id.emailField).setVisibility(View.INVISIBLE);
                            findViewById(R.id.password_field).setVisibility(View.INVISIBLE);
                            findViewById(R.id.login_button).setVisibility(View.INVISIBLE);
                            findViewById(R.id.sign_in_options_text).setVisibility(View.INVISIBLE);
                            findViewById(R.id.Submit).setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        } else {
            //user does not have an account yet
            //display google sign in button to start the sign in flow that brings the user back to this function
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);


        }
    }

    //after hitting the continue button to sign in. This takes us to the main menu
    public void toMenu() {
        startActivity(new Intent(MainActivity.this, Main2Activity.class));
    }


    //This function covers a general case. If the user does not yet have an account created, it will scan the forms and make sure all fields are valid.
    //THis function does not cover the user email account creation case. In this case, the user wants to create an acct with email. There are different checks
    //and different actions required.
    //TODO: Make unqiue username checking work
    public void toMenuSubmit() {
        EditText fName = (EditText)findViewById(R.id.fnameField);
        EditText lName = (EditText) findViewById(R.id.lnameField);
        EditText uName = (EditText) findViewById(R.id.unameField);
         boolean[] usernameExists = new boolean[1];

        if((fName.getText().toString().matches("") == false) && (lName.getText().toString().matches("") == false) && (uName.getText().toString().matches("") == false))
        {
            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Toast.makeText(this,userid,Toast.LENGTH_SHORT).show();
            Map<String, Object> data = new HashMap<>();

            data.put("first", fName.getText().toString());
            data.put("last", lName.getText().toString());
            data.put("username", uName.getText().toString());
            data.put("miles", 0);
            data.put("friends",0);
            data.put("challenges",0);
            data.put("profilepic", "https://firebasestorage.googleapis.com/v0/b/pace-pal-ad8c4.appspot.com/o/defaultAVI.png?alt=media&token=6c9c47df-8151-4e5b-8843-3440e317346c");

            db.collection("users")
                    .whereEqualTo("username", uName.getText().toString())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.v("Queryidpull", document.getId() + " => " + document.getData());
                                    usernameExists[0] = true;

                                    Log.v("Usernames:", "Usernames are " + document.getData());
                                    Log.v("Bool array value", "Bool value is: " + usernameExists[0]);
                                }
                            } else {
                                Log.v("queryidError", "Error getting documents: ", task.getException());
                            }
                        }
                    });

            Log.v("After", "Bool value after is" + usernameExists[0]);

            if(usernameExists[0]){
                Toast.makeText(this,"Username taken!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                db.collection("users").document(userid).set(data);
                toMenu();
            }



        }
        else {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
        }



    }

    //TODO: Make pop up for when form is not valid
    public boolean validateEmailAccountCreationForm() {
        boolean formValid = false;

        EditText fName = (EditText)findViewById(R.id.fnameField);
        EditText lName = (EditText) findViewById(R.id.lnameField);
        EditText uName = (EditText) findViewById(R.id.unameField);
        EditText email = (EditText) findViewById(R.id.emailField);
        EditText pwWord = (EditText) findViewById(R.id.password_field);

        if((fName.getText().toString().matches("") == false)
                && (lName.getText().toString().matches("") == false)
                && (uName.getText().toString().matches("") == false)
                && (email.getText().toString().matches("") == false)
                && (pwWord.getText().toString().matches("") == false)) {

            formValid = true;

        }


        return formValid;
    }



    //easy way to generate a key necessary for FaceBook sign in integration on development machines
    public static void printHashKey(Context context) {
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("AppLog", "key:" + hashKey + "=");
            }
        } catch (Exception e) {
            Log.e("AppLog", "error:", e);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.createAccount2:
                if(validateEmailAccountCreationForm()) {
                    createAccount();
                }
                break;
            case R.id.create_account_with_email_btn:
                displayEmailAcctCreationForm();
                break;
            case R.id.email_sign_in:
                if(validateEmailSignInForm()) {
                    emailSignIn();
                }
                break;
            case R.id.Submit:
                toMenuSubmit();
                break;
        }
    }

   //TODO: Validate that the form with the database. Basically we need to make sure that the email and password are actually in the database before continuing
    private boolean validateEmailSignInForm() {
        boolean formValid = false;

        EditText email = (EditText) findViewById(R.id.emailField);
        EditText pwWord = (EditText) findViewById(R.id.password_field);

        if((email.getText().toString().matches("") == false)
                && (pwWord.getText().toString().matches("") == false)) {

            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            formValid = true;

        }


        return formValid;
    }

    void emailSignIn() {

        EditText email = (EditText) findViewById(R.id.emailField);
        EditText passWord = (EditText) findViewById(R.id.password_field);

        String emailStringParam = email.getText().toString();
        String passWordStringParam = passWord.getText().toString();


        mAuth.signInWithEmailAndPassword(emailStringParam, passWordStringParam)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
        return;
    }


}

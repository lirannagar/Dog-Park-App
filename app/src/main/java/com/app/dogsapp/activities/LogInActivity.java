package com.app.dogsapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dogsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*This activity is responsible  for the log in procedure
I used the same activity for Log in,register and forgot password as soon as they share the same view
I have defined a States variable that describes the current state of the activity


 */


public class LogInActivity extends AppCompatActivity {

    //Views
    private EditText username;
    private EditText password;
    private Button submit;
    private TextView forgotPass;
    private TextView register;
    private FirebaseAuth mAuth;
    private Context mContext;
    private ProgressBar loading;


    //Activity states
    enum States {LOGIN, REGISTER, FORGOT_PASSWORKD}


    private States state = States.LOGIN;


    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {


        savedInstanceState.putString("state", state.toString());
        savedInstanceState.putString("email", username.getText().toString());
        savedInstanceState.putString("password", password.getText().toString());


        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        mContext = getApplicationContext();

        //Initialize the views
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        submit = findViewById(R.id.submit);
        forgotPass = findViewById(R.id.forgot_pass_tv);
        register = findViewById(R.id.register_tv);
        loading = findViewById(R.id.loading);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmitClick();
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onForgotBtnClick();

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegisterBtnClick();

            }
        });


        //Save the current data
        if (savedInstanceState != null) {
            state = States.valueOf(savedInstanceState.getString("state"));
            changeViews();
            username.setText(savedInstanceState.getString("email", ""));
            password.setText(savedInstanceState.getString("password", ""));

        } else {
            state = States.LOGIN;
            changeViews();
        }


    }


    //When the forgot text is clicked
    public void onForgotBtnClick() {

        //Change the state based on the current state
        switch (state) {
            case LOGIN:
                state = States.FORGOT_PASSWORKD;
                break;
            case REGISTER:
                state = States.FORGOT_PASSWORKD;
                break;
            case FORGOT_PASSWORKD:
                state = States.LOGIN;
                break;
        }

        changeViews();
    }


    //When the register text is clicked
    public void onRegisterBtnClick() {
        //Change the state based on the current state
        switch (state) {
            case LOGIN:
                state = States.REGISTER;
                break;
            case REGISTER:
                state = States.LOGIN;
                break;
            case FORGOT_PASSWORKD:
                state = States.REGISTER;
                break;
        }
        changeViews();


    }

    //Change the views based on the current state
    public void changeViews() {

        username.setText("");
        password.setText("");
        password.setVisibility(View.VISIBLE);
        switch (state) {
            case LOGIN:
                forgotPass.setText(mContext.getString(R.string.forgot_pass));
                register.setText(mContext.getString(R.string.register_text));
                submit.setText(mContext.getString(R.string.login_btn));

                break;
            case REGISTER:
                forgotPass.setText(mContext.getString(R.string.forgot_pass));
                register.setText(mContext.getString(R.string.login_text));
                submit.setText(mContext.getString(R.string.register_btn));
                break;
            case FORGOT_PASSWORKD:
                forgotPass.setText(mContext.getString(R.string.login_text));
                register.setText(mContext.getString(R.string.register_text));
                submit.setText(mContext.getString(R.string.submit_btn));
                password.setVisibility(View.INVISIBLE);
                break;
        }

    }

    //When the button is pressed
    public void onSubmitClick() {
        //Get the mail and password from the edit text views
        String email = username.getText().toString();
        String pass = password.getText().toString();
        //Check if the mail has more than 0 characters
        if (email != null && email.length() > 0) {

            //Based on the current state do an action
            switch (state) {
                case LOGIN:
                    if (pass != null && pass.length() > 0) {
                        loading.setVisibility(View.VISIBLE);
                        //Sign in with email and password
                        mAuth.signInWithEmailAndPassword(email, pass)

                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        //If the task is successful start the main activity
                                        if (task.isSuccessful()) {
                                            loading.setVisibility(View.GONE);
                                            startActivity(new Intent(mContext, MainActivity.class));
                                            finish();


                                        } else {
                                            // If sign in fails, display a message to the user.
                                            loading.setVisibility(View.GONE);
                                            Toast.makeText(mContext, "Authentication failed." + task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();


                                        }


                                    }
                                });
                    } else
                        password.setError(mContext.getString(R.string.password_error));
                    break;
                case REGISTER:
                    if (pass != null && pass.length() > 0) {
                        loading.setVisibility(View.VISIBLE);
                        //Create a user with email and password
                        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    loading.setVisibility(View.GONE);
                                    Toast.makeText(mContext, "Registered successfully", Toast.LENGTH_LONG).show();
                                    state = States.LOGIN;
                                    changeViews();


                                } else {
                                    Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    loading.setVisibility(View.GONE);
                                }


                            }
                        });

                    } else
                        password.setError(mContext.getString(R.string.password_error));
                    break;
                case FORGOT_PASSWORKD:
                    loading.setVisibility(View.VISIBLE);
                    //Asks for password recovery
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loading.setVisibility(View.GONE);
                                Toast.makeText(mContext, "Check your email",
                                        Toast.LENGTH_SHORT).show();


                            } else {

                                loading.setVisibility(View.GONE);
                                Toast.makeText(mContext, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                    break;
            }
        } else
            username.setError(mContext.getString(R.string.email_error));

    }


}

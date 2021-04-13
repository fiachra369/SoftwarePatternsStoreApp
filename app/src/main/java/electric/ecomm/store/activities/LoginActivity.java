package electric.ecomm.store.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import electric.ecomm.store.GlideApp;
import electric.ecomm.store.R;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    ImageView loginImage;
    EditText loginEmailEditText;
    EditText loginPasswordEditText;
    Button loginBtn;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenWindow();
        setContentView(R.layout.activity_login);
        loginImage = findViewById(R.id.loginImage);
        loginEmailEditText = findViewById(R.id.loginEmailEditText);
        loginPasswordEditText = findViewById(R.id.loginPasswordEditText);
        loginBtn = findViewById(R.id.loginButton);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onLoginButtonClick();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        loadImage();
    }

    private void loadImage() {
        GlideApp.with(this)
                .load("https://goo.gl/kXrupd")
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.error_holder)
                .into(loginImage);
    }

    public void onLoginButtonClick() {
        if (!isEmail(loginEmailEditText)) {
            loginEmailEditText.setError(getString(R.string.register_email_error_msg));
        } else if (isTextEmpty(loginPasswordEditText)) {
            loginPasswordEditText.setError(getString(R.string.register_password_error_msg));
        } else {
            loginUser();
        }
    }

    private void loginUser() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        String email = loginEmailEditText.getText().toString().trim();
        String password = loginPasswordEditText.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            if (email.equals("admin@admin.com"))
                                launchAdminMainActivity();
                            else
                                launchMainActivity();
                        } else {
                            Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getMessage()+"", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchAdminMainActivity() {
        Intent intent = new Intent(this, MainAdminActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isTextEmpty(EditText editText) {
        return TextUtils.isEmpty(editText.getText().toString());
    }

    private boolean isEmail(EditText emailET) {
        String email = emailET.getText().toString().trim();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private void setFullScreenWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}

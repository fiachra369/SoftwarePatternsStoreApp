package electric.ecomm.store.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import electric.ecomm.store.R;
import electric.ecomm.store.GlideApp;


public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = electric.ecomm.store.activities.RegisterActivity.class.getSimpleName();

    TextView existedMembershipLabel;
    ConstraintLayout registerLayoutParent;
    ImageView registerImage;
    EditText registerEmailEditText;
    EditText registerPasswordEditText;
    Button registerBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenWindow();
        setContentView(R.layout.activity_register);
        existedMembershipLabel = findViewById(R.id.loginMemberLabelTextView);
        registerLayoutParent = findViewById(R.id.registerLayoutParent);
        registerImage = findViewById(R.id.registerImage);
        registerEmailEditText = findViewById(R.id.registerEmailEditText);
        registerPasswordEditText = findViewById(R.id.registerPasswordEditText);
        registerBtn = findViewById(R.id.signupButton);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignupButtonClick();
            }
        });
        loadRegisterImage();
        setupExistedMembershipLabel();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.getEmail().equals("admin@admin.com"))
                launchAdminMainActivity();
            else
            launchMainActivity();
        }
    }

    private void setFullScreenWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void loadRegisterImage() {
        GlideApp.with(this)
                .load(getString(R.string.register_activity_image))
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.error_holder)
                .into(registerImage);
    }

    private void setupExistedMembershipLabel() {
        SpannableString textLink = new SpannableString(getString(R.string.membership_register_label));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                launchLoginActivity();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        textLink.setSpan(clickableSpan, 17, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        existedMembershipLabel.setText(textLink);
        existedMembershipLabel.setMovementMethod(LinkMovementMethod.getInstance());
        existedMembershipLabel.setHighlightColor(Color.TRANSPARENT);
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private boolean isTextEmpty(EditText editText) {
        return TextUtils.isEmpty(editText.getText().toString());
    }

    private boolean isEmail(EditText emailET) {
        String email = emailET.getText().toString().trim();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public void onSignupButtonClick() {
        if (!isEmail(registerEmailEditText)) {
            registerEmailEditText.setError(getString(R.string.register_email_error_msg));
        } else if (isTextEmpty(registerPasswordEditText)) {
            registerPasswordEditText.setError(getString(R.string.register_password_error_msg));
        } else {
            registerNewUser();
        }
    }

    private void registerNewUser() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        String email = registerEmailEditText.getText().toString().trim();
        String password = registerPasswordEditText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            launchMainActivity();
                        } else {
                            Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage()+"", Toast.LENGTH_SHORT).show();
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

}

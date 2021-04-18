package electric.ecomm.store.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import electric.ecomm.store.GlideApp;
import electric.ecomm.store.R;
import electric.ecomm.store.model.Categories;
import electric.ecomm.store.utils.Constants;

import static electric.ecomm.store.utils.Constants.drawableToBitmap;

public class AddCategoryActivity extends AppCompatActivity {

    private static final int PICK_PHOTO_FOR_CATEGORY = 88;
    @BindView(R.id.iv_category_image)
    ImageView ivCategoryImage;
    @BindView(R.id.et_category_name)
    EditText etCategoryName;
    @BindView(R.id.btn_add_category)
    Button btnAddCategory;
    Bitmap image;
    String firebaseStorageLink = "gs://softwarepatternsca-92d65.appspot.com";
    private String mCategory;
    private String mCategoryImage;

    @OnClick(R.id.btn_add_category)
    void onAddCategory(){

        String name;
        name = etCategoryName.getText().toString();

        if (name.isEmpty()) {

            Toast.makeText(this, "Please add category name", Toast.LENGTH_SHORT).show();
        }
        else if (image == null) {

            Toast.makeText(this, "Please add category image", Toast.LENGTH_SHORT).show();
        }
        else {

            if (mCategoryImage==null || mCategoryImage.isEmpty())
                uploadFile(name, image);
            else editFile(name, image);
            MainAdminActivity.shouldReload = true;

        }
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_CATEGORY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_CATEGORY && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                Toast.makeText(this, "Error occurs while fetching image", Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    image = BitmapFactory.decodeStream(inputStream);
                    ivCategoryImage.setImageBitmap(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    private void uploadFile(String name, Bitmap bitmap) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Adding Category");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(firebaseStorageLink);
        StorageReference mountainImagesRef = storageRef.child("categories/"+ name + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                exception.printStackTrace();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        Uri uri = task.getResult();
                        Log.e("URI", String.valueOf(uri));
                        Categories category = new Categories();
                        category.setCategoryImage(String.valueOf(uri));
                        category.setCategoryName(name);

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference newRef = database.getReference().child(Constants.CATEGORY_CHILD).push();
                        newRef.setValue(category).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                Toast.makeText(AddCategoryActivity.this, "Category Added Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });

            }
        });

    }

    private void editFile(String name, Bitmap bitmap) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Editing Category");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(firebaseStorageLink);
        StorageReference mountainImagesRef = storageRef.child("categories/"+ name + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                exception.printStackTrace();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        Uri uri = task.getResult();
                        Log.e("URI", String.valueOf(uri));


                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference().child(Constants.CATEGORY_CHILD);
                        reference.orderByChild(Constants.CATEGORY_NAME_CHILD).equalTo(mCategory).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                Categories category = dataSnapshot.getValue(Categories.class);
                                category.setCategoryImage(String.valueOf(uri));
                                category.setCategoryName(name);
                                DatabaseReference newRef = database.getReference().child(Constants.CATEGORY_CHILD).child(dataSnapshot.getKey());
                                newRef.setValue(category).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        progressDialog.dismiss();
                                        Toast.makeText(AddCategoryActivity.this, "Category Edit Successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });

                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(R.string.add_category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mCategory = intent.getStringExtra(Intent.EXTRA_TEXT);
            mCategoryImage = intent.getStringExtra("Image");
            getSupportActionBar().setTitle(R.string.edit_category);
            btnAddCategory.setText(R.string.edit_category);
            etCategoryName.setText(mCategory);
            GlideApp.with(this)
                    .load(mCategoryImage)
                    .placeholder(R.drawable.place_holder)
                    .error(R.drawable.error_holder)
                    .apply(new RequestOptions().override(getResources().getInteger(R.integer.category_image_width),
                            getResources().getInteger(R.integer.category_image_height)))
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            image = drawableToBitmap(resource);
                            return false;
                        }
                    })
                    .into(ivCategoryImage);

        }

        ivCategoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickImage();
            }
        });
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    private void errorUponLaunch() {
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
        finish();
    }
}
package electric.ecomm.store.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import electric.ecomm.store.R;
import electric.ecomm.store.firebaseviewmodel.ProductsViewModel;
import electric.ecomm.store.firebaseviewmodel.ProductsViewModelFactory;
import electric.ecomm.store.model.Categories;
import electric.ecomm.store.model.Product;
import electric.ecomm.store.model.Review;
import electric.ecomm.store.utils.Constants;

public class ReviewActivity extends AppCompatActivity {

    private static final int PICK_PHOTO_FOR_CATEGORY = 88;
    @BindView(R.id.rb_review_rate)
    RatingBar ratingBar;
    @BindView(R.id.et_review)
    EditText etReview;
    String firebaseStorageLink = "gs://softwarepatternsca-92d65.appspot.com";
    private String mCategory;
    private String mProductCategory;
    private int mProductCode;
    private Product mProduct;

    @OnClick(R.id.btn_add_review)
    void onAddReview() {

        String review;
        review = etReview.getText().toString();

        if (review.isEmpty()) {

            Toast.makeText(this, "Please add review", Toast.LENGTH_SHORT).show();
        } else {

            ProductDetailsActivity.shouldReload = true;
            postReview(review, ratingBar.getRating());

        }
    }


    private void postReview(String comment, float stars) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Adding Review");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        Review reviewObj = new Review();
        reviewObj.setUserName(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        reviewObj.setComment(comment);
        reviewObj.setStars(stars);
        List<Review> reviewList = mProduct.getReviewList();
        if (reviewList == null) reviewList = new ArrayList<>();
        reviewList.add(reviewObj);
        mProduct.setReviewList(reviewList);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child(Constants.CATEGORY_CHILD);
        reference.orderByChild(Constants.CATEGORY_NAME_CHILD).equalTo(mProductCategory).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                boolean isCodeExist = false;
                Categories categories = dataSnapshot.getValue(Categories.class);
                List<Product> items = null;
                if (categories != null) {
                    items = categories.getCategoryProducts();
                }

                if (items != null) {
                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getCode() == mProductCode) {
                            dataSnapshot.getRef().child(Constants.CATEGORY_PRODUCTS_CHILD).child(String.valueOf(i)).setValue(mProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    progressDialog.dismiss();
                                    Toast.makeText(ReviewActivity.this, "Review Added Successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                            break;
                        }
                    }
                }



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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT) && intent.hasExtra(Intent.EXTRA_TITLE)) {
            mProductCategory = intent.getStringExtra(Intent.EXTRA_TITLE);
            mProductCode = intent.getIntExtra(Intent.EXTRA_TEXT, -1);
            setupViewModel();
        } else {
            errorUponLaunch();
        }

        getSupportActionBar().setTitle(R.string.review_product);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ratingBar.setNumStars(5);
        ratingBar.setMax(5);

    }

    private void setupViewModel() {
        ProductsViewModelFactory factory = new ProductsViewModelFactory(mProductCode, mProductCategory);
        ProductsViewModel model = ViewModelProviders.of(this, factory).get(ProductsViewModel.class);
        model.getSpecificProduct().observe(this, new Observer<Product>() {
            @Override
            public void onChanged(@Nullable Product product) {
                mProduct = product;
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
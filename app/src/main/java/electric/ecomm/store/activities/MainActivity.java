package electric.ecomm.store.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import electric.ecomm.store.GlideApp;
import electric.ecomm.store.R;
import electric.ecomm.store.adapters.CategoriesAdapter;
import electric.ecomm.store.firebaseviewmodel.CartViewModel;
import electric.ecomm.store.firebaseviewmodel.CategoriesViewModel;
import electric.ecomm.store.model.Categories;
import electric.ecomm.store.model.Product;
import electric.ecomm.store.model.User;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final String CATEGORIES_STATE_KEY = "electric.ecomm.store.activities";
    final List<Product> mFTProducts = new ArrayList<>();
    @BindView(R.id.categoriesRecyclerView)
    RecyclerView mCategoriesRecyclerView;
    @BindView(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigationView)
    NavigationView mNavigationView;
    @BindView(R.id.loadingIndicator)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.retryConnectionButton)
    Button mRetryButton;
    private ImageView mProfileImage;

    private TextView mUserNameTextView;
    private TextView mUserEmailTextView;
    private TextView mCartBadgeTextView;
    private TextView mWishlistProductsBadgeTextView;
    private CategoriesAdapter mCategoriesAdapter;
    private LinearLayoutManager mFTLayoutManager;
    private int mFTRecyclerViewPosition;
    private Parcelable mLayoutState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupHeader();
        if (savedInstanceState != null) {
            mLayoutState = savedInstanceState.getParcelable(CATEGORIES_STATE_KEY);
        }
        setupViews();
        setupNavigationDrawer();
        setupCartMenu();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mLayoutState = mCategoriesRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(CATEGORIES_STATE_KEY, mLayoutState);
    }


    private void loadCategories() {
        showLoadingIndicator();
        CategoriesViewModel categoriesViewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        categoriesViewModel.getCategories().observe(this, new Observer<List<Categories>>() {
            @Override
            public void onChanged(@Nullable List<Categories> categories) {
                mCategoriesAdapter = new CategoriesAdapter(MainActivity.this, categories);
                mCategoriesRecyclerView.setAdapter(mCategoriesAdapter);
                hideLoadingIndicator();
                mFTProducts.clear();
                if (categories != null) {
                    for (int i = 0; i < categories.size(); i++) {
                        List<Product> list = categories.get(i).getCategoryProducts();
                        if (list != null && !list.isEmpty())
                            mFTProducts.addAll(list);
                    }

                }
                if (mLayoutState != null) {
                    mCategoriesRecyclerView.getLayoutManager().onRestoreInstanceState(mLayoutState);
                }
            }
        });

    }

    private void setupViews() {
        if (isNetworkAvailable()) {
            hideRetryButton();
            setupRecyclerViews();
            loadCategories();
        } else {
            showRetryButton();
            showConnectionErrorMessage();
        }
    }

    @OnClick(R.id.retryConnectionButton)
    public void onRetryButtonClick() {
        setupViews();
    }

    private void hideRetryButton() {
        mRetryButton.setVisibility(View.GONE);
    }

    private void showRetryButton() {
        mRetryButton.setVisibility(View.VISIBLE);
    }

    private void showConnectionErrorMessage() {

        Toast.makeText(this, R.string.connection_alerter_text, Toast.LENGTH_SHORT).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showLoadingIndicator() {
        mCategoriesRecyclerView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoadingIndicator() {
        mLoadingIndicator.setVisibility(View.GONE);
        mCategoriesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        mToolbar.setNavigationIcon(R.drawable.ic_menu);
        mToolbar.setTitle(R.string.app_name);
    }

    private void setupHeader() {
        View navigationViewHeader = mNavigationView.getHeaderView(0);
        mProfileImage = navigationViewHeader.findViewById(R.id.profileImageView);
        mUserNameTextView = navigationViewHeader.findViewById(R.id.profileNameTextView);
        mUserEmailTextView = navigationViewHeader.findViewById(R.id.profileEmailTextView);
        setupHeaderViews();
    }

    private void setupHeaderViews() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        GlideApp.with(this).load(user.getPhotoUrl())
                .apply(new RequestOptions().override(400, 600))
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.error_holder)
                .apply(RequestOptions.circleCropTransform())
                .into(mProfileImage);
        mUserNameTextView.setText(String.valueOf(user.getDisplayName()));
        mUserEmailTextView.setText(String.valueOf(user.getEmail()));
    }

    private void setupCartMenu() {
        mCartBadgeTextView = mNavigationView.getMenu().findItem(R.id.cartMenuItemAction).getActionView().findViewById(R.id.menuItemCartBadgeNavDrawerTextView);
        CartViewModel model = ViewModelProviders.of(this).get(CartViewModel.class);
        model.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                mCartBadgeTextView.setText(String.valueOf(user.getProductsCount()));
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setupRecyclerViews() {
        mCategoriesRecyclerView.setNestedScrollingEnabled(false);
        mFTLayoutManager = new LinearLayoutManager(this);
        mFTLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profileMenuItemAction:
                launchUserOrdersActivity();
                break;
            case R.id.cartMenuItemAction:
                launchCartActivity();
                break;
            case R.id.signOutMenuItemAction:
                signOutUser();
                break;

        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void launchUserOrdersActivity() {
        Intent intent = new Intent(this, UserOrdersActivity.class);
        startActivity(intent);
    }

    private void signOutUser() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }

    private void launchCartActivity() {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

}

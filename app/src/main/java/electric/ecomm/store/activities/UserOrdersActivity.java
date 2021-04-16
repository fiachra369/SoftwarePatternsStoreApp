package electric.ecomm.store.activities;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import electric.ecomm.store.GlideApp;
import electric.ecomm.store.R;
import electric.ecomm.store.adapters.OrdersAdapter;
import electric.ecomm.store.firebaseviewmodel.UserOrdersViewModel;
import electric.ecomm.store.model.Order;

public class UserOrdersActivity extends AppCompatActivity {

    private static final String LAYOUT_KEY = "electric.ecomm.store.activities";
    @BindView(R.id.userOrdersRecyclerView)
    RecyclerView mOrdersRecyclerView;
    @BindView(R.id.userOrdersToolbar)
    Toolbar mToolbar;
    @BindView(R.id.userOrdersHeaderImage)
    ImageView mHeaderImage;
    @BindView(R.id.userOrdersCollapsingToolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.userOrdersEmptyState)
    View mEmptyState;
    private Parcelable mLayoutState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenWindow();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_user_orders);
        ButterKnife.bind(this);
        setupToolbar();
        loadOrders();

        if (savedInstanceState != null) {
            mLayoutState = savedInstanceState.getParcelable(LAYOUT_KEY);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mLayoutState = mOrdersRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(LAYOUT_KEY, mLayoutState);
    }

    private void setFullScreenWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mCollapsingToolbar.setTitle(getString(R.string.order_activity_label));
        mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        GlideApp.with(this)
                .load(getString(R.string.user_orders_header_image))
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.error_holder)
                .into(mHeaderImage);

    }

    private void loadOrders() {
        UserOrdersViewModel model = ViewModelProviders.of(this).get(UserOrdersViewModel.class);
        model.getUserOrders().observe(this, new Observer<List<Order>>() {
            @Override
            public void onChanged(@Nullable List<Order> orders) {
                if (orders.size() == 0) {
                    showEmptyState();
                } else {
                    hideEmptyState();
                    OrdersAdapter adapter = new OrdersAdapter(orders, UserOrdersActivity.this);
                    mOrdersRecyclerView.setAdapter(adapter);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLayoutState != null) {
            mOrdersRecyclerView.getLayoutManager().onRestoreInstanceState(mLayoutState);
        }
    }

    private void showEmptyState() {
        mEmptyState.setVisibility(View.VISIBLE);
    }

    private void hideEmptyState() {
        mEmptyState.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

        }
        return true;
    }

}

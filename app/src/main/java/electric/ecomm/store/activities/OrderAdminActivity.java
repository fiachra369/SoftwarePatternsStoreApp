package electric.ecomm.store.activities;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import electric.ecomm.store.GlideApp;
import electric.ecomm.store.R;
import electric.ecomm.store.adapters.AdminOrdersAdapter;
import electric.ecomm.store.model.Order;
import electric.ecomm.store.utils.Constants;

public class OrderAdminActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_order_admin);
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child(Constants.ORDER_PRODUCTS_CHILD);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final List<Order> ordersList = new ArrayList<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Result will be holded Here
                for (DataSnapshot dsp : snapshot.getChildren()) {
                    Order order = dsp.getValue(Order.class);
                    assert order != null;
                    order.setOrderKey(dsp.getKey());
                    ordersList.add(order); //add result into array list

                }
                if (ordersList.size() == 0) {
                    showEmptyState();
                } else {
                    hideEmptyState();
                    AdminOrdersAdapter adapter = new AdminOrdersAdapter(ordersList, OrderAdminActivity.this);
                    mOrdersRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*reference.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Order order = dataSnapshot.getValue(Order.class);
                assert order != null;
                order.setOrderKey(dataSnapshot.getKey());
                ordersList.add(order);
                if (ordersList.size() == 0) {
                    showEmptyState();
                } else {
                    hideEmptyState();
                    OrdersAdapter adapter = new OrdersAdapter(ordersList, OrderAdminActivity.this);
                    mOrdersRecyclerView.setAdapter(adapter);
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
        });*/

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

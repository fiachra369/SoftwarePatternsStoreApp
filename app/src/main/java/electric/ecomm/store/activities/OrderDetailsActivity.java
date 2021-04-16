package electric.ecomm.store.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import electric.ecomm.store.R;
import electric.ecomm.store.adapters.ProductImagesAdapter;
import electric.ecomm.store.firebaseviewmodel.OrderViewModel;
import electric.ecomm.store.firebaseviewmodel.OrderViewModelFactory;
import electric.ecomm.store.model.Order;
import electric.ecomm.store.model.Product;

public class OrderDetailsActivity extends AppCompatActivity {

    @BindView(R.id.orderProductsRecyclerView)
    RecyclerView mOrderProductsRecyclerView;
    @BindView(R.id.orderProductNameTextSwitcher)
    TextSwitcher mOrderProductName;
    @BindView(R.id.orderProductPriceTextSwitcher)
    TextSwitcher mOrderProductPrice;
    @BindView(R.id.userNameTextView)
    TextView mUserName;
    @BindView(R.id.userEnteredAddressTextView)
    TextView mUserAddress;
    @BindView(R.id.userPhoneNumberTextView)
    TextView mUserPhoneNumber;
    @BindView(R.id.userPaymentPriceTextView)
    TextView mOrderPayment;
    @BindView(R.id.userPaymentDiscountPriceTextView)
    TextView mOrderDiscount;
    @BindView(R.id.orderStatusTextView)
    TextView mOrderStatusTextView;
    private LinearLayoutManager mRecyclerViewLayoutManager;
    private int mOrderProductsPosition;
    private List<Product> mProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String key = intent.getStringExtra(Intent.EXTRA_TEXT);

            loadOrderDetails(key);
            setupTextSwitcher();
            setupRecyclerView();
        } else {
            errorUponLaunch();
        }


    }

    private void setupRecyclerView() {
        mRecyclerViewLayoutManager = new LinearLayoutManager(this);
        mOrderProductsRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
    }

    private void onActiveCardChange(int position) {
        mOrderProductName.setText(String.valueOf(mProducts.get(position).getName()));
        mOrderProductPrice.setText("$" + new DecimalFormat().format(mProducts.get(position).getPrice()));
        mOrderProductsPosition = position;
    }

    private void setupTextSwitcher() {
        mOrderProductName.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(getApplicationContext());
                textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
                textView.setTextColor(getResources().getColor(R.color.primaryTextColor));
                textView.setTextSize(getResources().getDimension(R.dimen.order_details_activity_default_text_size));
                Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.poppins);
                textView.setTypeface(typeface);
                return textView;
            }
        });
        mOrderProductPrice.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(getApplicationContext());
                textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
                textView.setTextColor(getResources().getColor(R.color.primaryTextColor));
                textView.setTextSize(getResources().getDimension(R.dimen.order_details_activity_default_text_size));
                Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.poppins);
                textView.setTypeface(typeface);
                return textView;
            }
        });
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        mOrderProductName.setInAnimation(in);
        mOrderProductName.setOutAnimation(out);
        mOrderProductPrice.setInAnimation(in);
        mOrderProductPrice.setOutAnimation(out);
    }

    private void loadOrderDetails(String key) {
        OrderViewModelFactory factory = new OrderViewModelFactory(key);
        OrderViewModel model = ViewModelProviders.of(this, factory).get(OrderViewModel.class);
        model.getOrder().observe(this, new Observer<Order>() {
            @Override
            public void onChanged(@Nullable Order order) {
                setupOrderDetails(order);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setupOrderDetails(Order order) {
        mProducts = order.getUserObject().getCart().getCartProducts();
        loadOrderProducts(mProducts);
        loadOrderStatus(order.getOrderStatus());
        mUserName.setText(order.getUserName());
        mUserPhoneNumber.setText(String.valueOf(order.getUserPhone()));
        mUserAddress.setText(order.getUserAddress());
        mOrderPayment.setText(getString(R.string.label_order_payment) + ": \n" + "€" + new DecimalFormat().format(order.getTotalPriceAfterDiscount()));
        mOrderDiscount.setText(getString(R.string.label_order_price_saved) + "€" + new DecimalFormat().format(order.getTotalDiscountPrice()));
    }

    private void loadOrderStatus(String orderStatus) {
        mOrderStatusTextView.setText(orderStatus);
    }

    private void loadOrderProducts(List<Product> mProducts) {
        ProductImagesAdapter adapter = new ProductImagesAdapter(this.mProducts, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mOrderProductsRecyclerView.setLayoutManager(linearLayoutManager);
        mOrderProductsRecyclerView.setAdapter(adapter);
        mOrderProductName.setText(String.valueOf(mProducts.get(0).getName()));
        mOrderProductPrice.setText("€" + new DecimalFormat().format(mProducts.get(0).getPrice()));
    }

    private void errorUponLaunch() {
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
        finish();
    }
}

package electric.ecomm.store.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.core.app.ShareCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import electric.ecomm.store.GlideApp;
import electric.ecomm.store.R;
import electric.ecomm.store.adapters.ReviewAdapter;
import electric.ecomm.store.firebaseviewmodel.CartViewModel;
import electric.ecomm.store.firebaseviewmodel.ProductsViewModel;
import electric.ecomm.store.firebaseviewmodel.ProductsViewModelFactory;
import electric.ecomm.store.model.Cart;
import electric.ecomm.store.model.Product;
import electric.ecomm.store.model.Review;
import electric.ecomm.store.model.User;
import electric.ecomm.store.utils.Constants;

public class ProductDetailsActivity extends AppCompatActivity {

    @BindView(R.id.detailProductImage)
    ImageView mProductImage;
    @BindView(R.id.detailProductDescriptionTextView)
    TextView mProductDescriptionTextView;
    @BindView(R.id.detailProductNameTextView)
    TextView mProductNameTextView;
    @BindView(R.id.detailProductTotalPrice)
    TextView mPriceTextView;
    @BindView(R.id.detailProductDiscountPrice)
    TextView mDiscountPriceTextView;
    @BindView(R.id.productCategoryHashtagTextView)
    TextView mCategoryHashTag;
    @BindView(R.id.productFABMenu)
    FloatingActionMenu mFABsMenu;
    @BindView(R.id.addToCartFAB)
    FloatingActionButton mAddToCartFab;
    @BindView(R.id.shareProductFAB)
    FloatingActionButton mShareFAB;
    @BindView(R.id.tv_rating)
    TextView tvRating;
    @BindView(R.id.rv_reviews)
    RecyclerView rvReviews;
    private TextView mCartBadgeTextView;
    private String mProductCategory;
    private int mProductCode;
    private Product mProduct;
    private ReviewAdapter mReviewAdapter;

    public static boolean shouldReload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT) && intent.hasExtra(Intent.EXTRA_TITLE)) {
            mProductCategory = intent.getStringExtra(Intent.EXTRA_TITLE);
            mProductCode = intent.getIntExtra(Intent.EXTRA_TEXT, -1);
            setupViewModel();
        } else {
            errorUponLaunch();
        }
    }

    @Override
    protected void onResume() {

        if (shouldReload) {
            setupViewModel();
            shouldReload = false;
        }
        super.onResume();
    }
    private void setupReviewsRecyclerView(List<Review> reviews) {
        mReviewAdapter = new ReviewAdapter(reviews, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvReviews.setLayoutManager(linearLayoutManager);
        rvReviews.setAdapter(mReviewAdapter);
    }
    @SuppressLint("RestrictedApi")
    private void setupFABs() {
        mAddToCartFab.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this, R.drawable.ic_shopping_cart));
        mShareFAB.setImageDrawable(AppCompatDrawableManager.get().getDrawable(this, R.drawable.ic_share));
    }

    @OnClick(R.id.shareProductFAB)
    public void onShareFABClick() {
        String text;
        if (mProduct.getSale() > 0) {
            text = shareTextWithSale();
        } else {
            text = shareTextWithoutSale();
        }
        shareProduct(text);
    }

    @OnClick(R.id.reviewProductFAB)
    public void onReviewFABClick() {
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, mProductCode);
        intent.putExtra(Intent.EXTRA_TITLE, mProductCategory);
        startActivity(intent);
    }

    @NonNull
    private String shareTextWithoutSale() {
        return mProduct.getName() + " " + getString(R.string.share_product_sale_for_part) + "€" + new DecimalFormat().format(mProduct.getPrice()) + "\n\n\n" +
                mProduct.getDescription();
    }

    @NonNull
    private String shareTextWithSale() {
        return getString(R.string.share_product_sale_first_part) + mProduct.getName() + getString(R.string.share_product_sale_for_part) +
                "€" + new DecimalFormat().format(mProduct.getPrice() - mProduct.getSale()) +
                getString(R.string.share_product_sale_second_part) + "€" + new DecimalFormat().format(mProduct.getPrice()) + getString(R.string.share_product_sale_on_part) + getString(R.string.app_name) +
                " " + mProduct.getName() + "\n\n\n" + mProduct.getDescription();
    }

    private void shareProduct(String text) {
        mFABsMenu.close(true);
        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle(R.string.share_product_chooser_title)
                .setText(text)
                .startChooser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_activity_menu, menu);
        final MenuItem cartMenuItem = menu.findItem(R.id.cartMenuItemAction);
        View actionView = cartMenuItem.getActionView();
        mCartBadgeTextView = actionView.findViewById(R.id.cartMenuItemBadge);

        setupCartBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(cartMenuItem);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void setupCartBadge() {
        CartViewModel model = ViewModelProviders.of(this).get(CartViewModel.class);
        model.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                mCartBadgeTextView.setText(String.valueOf(user.getProductsCount()));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cartMenuItemAction:
                launchCartActivity();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.addToCartFAB)
    public void addProductToUserCart() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference().child(Constants.USER_CHILD);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Product product = mProduct;
        final int productCode = product.getCode();

        reference.orderByChild(Constants.USER_ID_CHILD).equalTo(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    createNewCart(product, firebaseUser, reference);
                } else {
                    reference.orderByChild(Constants.USER_ID_CHILD).equalTo(firebaseUser.getUid()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            String key = dataSnapshot.getKey();
                            User user = dataSnapshot.getValue(User.class);
                            Cart cart = user.getCart();
                            List<Product> productList = cart.getCartProducts();
                            List<Integer> codes = new ArrayList<>();

                            for (int i = 0; i < productList.size(); i++) {
                                codes.add(productList.get(i).getCode());
                            }

                            for (int i = 0; i < productList.size(); i++) {
                                Product product1 = productList.get(i);
                                if (product1.getCode() == productCode) {
                                    product1.setShoppingCartQuantity(product1.getShoppingCartQuantity() + 1);
                                    productList.set(i, product1);
                                    cart.setCartProducts(productList);
                                    user.setCart(cart);
                                    user.setProductsCount(productList.size());
                                    reference.child(key).updateChildren(user.toMap());
                                    showProductCartQuantityAlerterMsg();
                                    break;
                                }

                            }

                            if (!codes.contains(productCode)) {
                                product.setShoppingCartQuantity(1);
                                productList.add(product);
                                cart.setCartProducts(productList);
                                user.setCart(cart);
                                user.setProductsCount(productList.size());
                                reference.child(key).setValue(user);
                                showCartAddedAlerterMsg();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createNewCart(Product product, FirebaseUser firebaseUser, DatabaseReference reference) {
        List<Product> products = new ArrayList<>();
        product.setShoppingCartQuantity(1);
        products.add(product);
        Cart cart = new Cart(products, firebaseUser.getUid());
        User user1 = new User(firebaseUser.getDisplayName(), firebaseUser.getUid(), cart, products.size());
        reference.push().setValue(user1);
        showCartAddedAlerterMsg();
    }

    private void showProductCartQuantityAlerterMsg() {
        mFABsMenu.close(true);
        Toast.makeText(this, R.string.cart_add_product_increase_quantity_alerter_text, Toast.LENGTH_SHORT).show();
    }

    private void showCartAddedAlerterMsg() {
        mFABsMenu.close(true);
        Toast.makeText(this, R.string.cart_add_product_alerter_msg, Toast.LENGTH_SHORT).show();
    }

    private void launchCartActivity() {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }


    private void setupViewModel() {
        ProductsViewModelFactory factory = new ProductsViewModelFactory(mProductCode, mProductCategory);
        ProductsViewModel model = ViewModelProviders.of(this, factory).get(ProductsViewModel.class);
        model.getSpecificProduct().observe(this, new Observer<Product>() {
            @Override
            public void onChanged(@Nullable Product product) {
                showProductDetails(product);
            }
        });
    }



    private void showProductDetails(Product product) {
        mProduct = product;
        setupFABs();

        float productSale = product.getSale();
        float productPrice = product.getPrice();
        if (productSale > 0) {
            setPriceWithSale(productSale, productPrice);
        } else {
            setPriceWithoutSale(productPrice);
        }
        setProductDetails(product);
        mCategoryHashTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setProductDetails(Product product) {
        GlideApp.with(this)
                .load(product.getImage())
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.error_holder)
                .into(mProductImage);
        mProductDescriptionTextView.setText(product.getDescription());
        mProductNameTextView.setText(product.getName());
        mCategoryHashTag.setText(String.valueOf("#" + product.getCategory()));
        List<Review> reviewList = product.getReviewList();
        if (reviewList != null) {

            tvRating.setText("("+reviewList.size()+")");
            setupReviewsRecyclerView(reviewList);
        }
    }

    private void setPriceWithoutSale(float productPrice) {
        mDiscountPriceTextView.setVisibility(View.GONE);
        mPriceTextView.setText(new DecimalFormat().format(productPrice));
    }

    @SuppressLint("SetTextI18n")
    private void setPriceWithSale(float productSale, float productPrice) {
        float saleInPercentage = (productSale / productPrice) * 100;
        String saleText = new DecimalFormat("#.#").format(saleInPercentage);
        mDiscountPriceTextView.setText("$" + new DecimalFormat().format(productPrice - productSale));
        mPriceTextView.setText("$" + new DecimalFormat().format(productPrice));
        mPriceTextView.setPaintFlags(mPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void errorUponLaunch() {
        Toast.makeText(this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
        finish();
    }
}

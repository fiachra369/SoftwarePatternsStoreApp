package electric.ecomm.store.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import electric.ecomm.store.R;
import electric.ecomm.store.adapters.ProductsAdapter;
import electric.ecomm.store.firebaseviewmodel.CartViewModel;
import electric.ecomm.store.firebaseviewmodel.ProductSearchViewModel;
import electric.ecomm.store.firebaseviewmodel.ProductSearchViewModelFactory;
import electric.ecomm.store.model.Product;
import electric.ecomm.store.model.User;

public class SearchableActivity extends AppCompatActivity {

    @BindView(R.id.productsSearchResultsRecyclerView)
    RecyclerView mProductsRecyclerView;
    private ProductsAdapter mProductsAdapter;
    private TextView mCartBadgeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String query = intent.getStringExtra(Intent.EXTRA_TEXT);
            String category = intent.getStringExtra(Intent.EXTRA_TITLE);
            setupRecyclerView();
            searchForProduct(query, category);
        } else {
            errorUponLaunch();
        }

        setupToolbar();

    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.searchable_activity_label);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupRecyclerView() {
        mProductsAdapter = new ProductsAdapter(this, new ArrayList<Product>());
        mProductsRecyclerView.setAdapter(mProductsAdapter);
    }

    private void errorUponLaunch() {
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void searchForProduct(String query, String category) {
        ProductSearchViewModelFactory factory = new ProductSearchViewModelFactory(query, category);
        ProductSearchViewModel model = ViewModelProviders.of(this, factory).get(ProductSearchViewModel.class);
        model.getSearchResult().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(@Nullable List<Product> products) {
                mProductsAdapter.addProducts(products);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_products_menu, menu);
        final MenuItem cartMenuItem = menu.findItem(R.id.cartMenuItemAction);
        MenuItem searchItem = menu.findItem(R.id.searchAction);
        searchItem.setVisible(false);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cartMenuItemAction:
                startCartActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startCartActivity() {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
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

}



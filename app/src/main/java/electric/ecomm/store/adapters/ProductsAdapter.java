package electric.ecomm.store.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import electric.ecomm.store.GlideApp;
import electric.ecomm.store.R;
import electric.ecomm.store.activities.AddProductActivity;
import electric.ecomm.store.activities.ProductDetailsActivity;
import electric.ecomm.store.model.Product;

public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FIRST_PRODUCT = 0;
    private final Context mContext;
    private List<Product> mProducts;

    public ProductsAdapter(Context mContext, List<Product> mProducts) {
        this.mContext = mContext;
        this.mProducts = mProducts;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case FIRST_PRODUCT: {
                View firstProductView = LayoutInflater.from(mContext).inflate(R.layout.list_item_first_product, parent, false);
                return new FirstProductViewHolder(firstProductView);
            }
            default: {
                View productsView = LayoutInflater.from(mContext).inflate(R.layout.list_item_products, parent, false);
                return new ProductsViewHolder(productsView);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case FIRST_PRODUCT:
                bindFirstView(holder, position);
                break;
            default:
                bindViews(holder, position);

        }

    }

    private void bindFirstView(RecyclerView.ViewHolder holder, int position) {
        FirstProductViewHolder firstViewHolder = (FirstProductViewHolder) holder;
        Product product = mProducts.get(position);


        float productPrice = product.getPrice();
        float productSale = product.getSale();
        if (productSale > 0) {
            setFirstProductPriceWithSale(firstViewHolder, productPrice, productSale);
        } else {
            setFirstProductPriceWithoutSale(firstViewHolder, productPrice);
        }

        showFirstProductDetails(firstViewHolder, product);
    }

    @SuppressLint("SetTextI18n")
    private void showFirstProductDetails(FirstProductViewHolder firstViewHolder, Product product) {
        GlideApp.with(mContext)
                .load(product.getImage())
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.error_holder)
                .into(firstViewHolder.mFirstProductImage);
        firstViewHolder.mFirstProductDescription.setText(product.getDescription());
        firstViewHolder.mFirstProductName.setText(product.getName());
        firstViewHolder.mfirstProductStockTextView.setText("Stock: "+product.getStock());
    }

    @SuppressLint("SetTextI18n")
    private void setFirstProductPriceWithoutSale(FirstProductViewHolder firstViewHolder, float productPrice) {
        firstViewHolder.mFirstProductPrice.setText("€" + new DecimalFormat().format(productPrice));
    }

    @SuppressLint("SetTextI18n")
    private void setFirstProductPriceWithSale(FirstProductViewHolder firstViewHolder, float productPrice, float productSale) {
        float saleInPercent = (productSale / productPrice) * 100;
        String saleInPercentText = new DecimalFormat("#.#").format(saleInPercent);
        firstViewHolder.mFirstProductPrice.setText("€" + new DecimalFormat().format(productPrice - productSale));
    }

    private void bindViews(RecyclerView.ViewHolder holder, int position) {
        ProductsViewHolder productsViewHolder = (ProductsViewHolder) holder;
        Product product = mProducts.get(position);
        showProductDetails(productsViewHolder, product);

        float productPrice = product.getPrice();
        float productSale = product.getSale();

        if (productSale > 0) {
            setProductPriceWithSale(productsViewHolder, productPrice, productSale);
        } else {
            setProductPriceWithoutSale(productsViewHolder, productPrice);
        }

    }

    @SuppressLint("SetTextI18n")
    private void setProductPriceWithoutSale(ProductsViewHolder productsViewHolder, float productPrice) {
        productsViewHolder.mProductPrice.setText("€" + new DecimalFormat().format(productPrice));
        productsViewHolder.mSaleInPercentageTextView.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void setProductPriceWithSale(ProductsViewHolder productsViewHolder, float productPrice, float productSale) {
        float saleInPercent = (productSale / productPrice) * 100;
        productsViewHolder.mSaleInPercentageTextView.setText(new DecimalFormat("#.#").format(saleInPercent) + mContext.getString(R.string.percent_sale_off_label));
        productsViewHolder.mProductPrice.setText("€" + new DecimalFormat().format(productPrice - productSale));
    }

    @SuppressLint("SetTextI18n")
    private void showProductDetails(ProductsViewHolder productsViewHolder, Product product) {
        productsViewHolder.mProductNameTextView.setText(product.getName());
        GlideApp.with(mContext)
                .load(product.getImage())
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.error_holder)
                .into(productsViewHolder.mProductImageView);
        productsViewHolder.mProductDescription.setText(product.getDescription());
        productsViewHolder.mProductStockTextView.setText("Stock: "+product.getStock());
    }


    public void addProducts(List<Product> products) {
        mProducts = products;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mProducts == null ? 0 : mProducts.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == FIRST_PRODUCT) {
            return FIRST_PRODUCT;
        } else {
            return position;
        }
    }


    public class FirstProductViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.firstProductLabelImageView)
        ImageView mFirstProductImage;
        @BindView(R.id.firstProductDescriptionTextView)
        TextView mFirstProductDescription;
        @BindView(R.id.firstProductNameTextView)
        TextView mFirstProductName;
        @BindView(R.id.firstProductPriceTextView)
        TextView mFirstProductPrice;
        @BindView(R.id.iv_edit)
        ImageView ivEdit;
        @BindView(R.id.firstProductStockTextView)
        TextView mfirstProductStockTextView;

        public FirstProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @OnClick(R.id.firstProductListParent)
        public void onParentClick() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                createSceneTransition();
            } else {
                LaunchProductDetailsActivity();
            }

        }

        @OnClick(R.id.iv_edit)
        public void onEditClick() {
            launchEditActivity();
        }

        private void launchEditActivity() {
            Intent intent = new Intent(mContext, AddProductActivity.class);
            intent.putExtra("product", (Serializable) mProducts.get(getAdapterPosition()));
            mContext.startActivity(intent);

        }

        private void LaunchProductDetailsActivity() {
            Intent intent = new Intent(mContext, ProductDetailsActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, mProducts.get(getAdapterPosition()).getCode());
            intent.putExtra(Intent.EXTRA_TITLE, mProducts.get(getAdapterPosition()).getCategory());
            mContext.startActivity(intent);
        }

        @SuppressLint("NewApi")
        private void createSceneTransition() {
            Bundle b = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext).toBundle();
            Intent intent = new Intent(mContext, ProductDetailsActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, mProducts.get(getAdapterPosition()).getCode());
            intent.putExtra(Intent.EXTRA_TITLE, mProducts.get(getAdapterPosition()).getCategory());
            mContext.startActivity(intent, b);
        }

    }


    public class ProductsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.productImage)
        ImageView mProductImageView;
        @BindView(R.id.productNameTextView)
        TextView mProductNameTextView;
        @BindView(R.id.productPriceTextView)
        TextView mProductPrice;
        @BindView(R.id.productDescriptionTextView)
        TextView mProductDescription;
        @BindView(R.id.productStockTextView)
        TextView mProductStockTextView;
        @BindView(R.id.addToWishlistImageButton)
        ImageButton mAddProductToWishlistImageButton;
        @BindView(R.id.saleInPercentageTextView)
        TextView mSaleInPercentageTextView;

        public ProductsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }


        @OnClick(R.id.productListParentCardView)
        public void onProductClick() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                createSceneTransition();
            } else {
                launchProductDetailsActivity();
            }
        }

        @OnClick(R.id.iv_edit)
        public void onEditClick() {
            launchEditActivity();
        }

        private void launchEditActivity() {
                Intent intent = new Intent(mContext, AddProductActivity.class);
                intent.putExtra("product", (Serializable) mProducts.get(getAdapterPosition()));
                mContext.startActivity(intent);

        }

        private void launchProductDetailsActivity() {
            Intent intent = new Intent(mContext, ProductDetailsActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, mProducts.get(getAdapterPosition()).getCode());
            intent.putExtra(Intent.EXTRA_TITLE, mProducts.get(getAdapterPosition()).getCategory());
            mContext.startActivity(intent);
        }

        @SuppressLint("NewApi")
        private void createSceneTransition() {
            Bundle b = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext).toBundle();
            Intent intent = new Intent(mContext, ProductDetailsActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, mProducts.get(getAdapterPosition()).getCode());
            intent.putExtra(Intent.EXTRA_TITLE, mProducts.get(getAdapterPosition()).getCategory());
            mContext.startActivity(intent, b);
        }

    }
}

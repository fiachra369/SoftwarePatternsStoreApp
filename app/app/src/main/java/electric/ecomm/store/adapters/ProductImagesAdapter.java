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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import electric.ecomm.store.GlideApp;
import electric.ecomm.store.R;
import electric.ecomm.store.activities.ProductDetailsActivity;
import electric.ecomm.store.model.Product;

public class ProductImagesAdapter extends RecyclerView.Adapter<ProductImagesAdapter.FeaturedProductsViewHolder> {

    private List<Product> mProducts;
    private Context mContext;

    public ProductImagesAdapter(List<Product> mProducts, Context mContext) {
        this.mProducts = mProducts;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public FeaturedProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View featuredProductsView = LayoutInflater.from(mContext).inflate(R.layout.list_item_product_images, parent, false);
        return new FeaturedProductsViewHolder(featuredProductsView);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedProductsViewHolder holder, int position) {
        Product product = mProducts.get(position);

        GlideApp.with(mContext)
                .load(product.getImage())
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.error_holder)
                .into(holder.mProductImage);

        holder.mProductTitle.setText(product.getName());
        holder.mProductPrice.setText("€" + product.getPrice());

    }

    @Override
    public int getItemCount() {
        return mProducts == null ? 0 : mProducts.size();
    }

    public void setProducts(List<Product> products) {
        mProducts = products;
        notifyDataSetChanged();
    }

    public class FeaturedProductsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.featuredProductImage)
        ImageView mProductImage;

        @BindView(R.id.featuredProductTitle)
        TextView mProductTitle;

        @BindView(R.id.featuredProductPrice)
        TextView mProductPrice;

        public FeaturedProductsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.featuredProductImage)
        public void onFeaturedProductClick() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                createSceneTransition();
            } else {
                viewProductDetails();
            }
        }

        private void viewProductDetails() {
            Intent intent = new Intent(mContext, ProductDetailsActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, mProducts.get(getAdapterPosition()).getCode());
            intent.putExtra(Intent.EXTRA_TITLE, mProducts.get(getAdapterPosition()).getCategory());
            mContext.startActivity(intent);
        }

        @SuppressLint("NewApi")
        private void createSceneTransition() {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext).toBundle();
            Intent intent = new Intent(mContext, ProductDetailsActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, mProducts.get(getAdapterPosition()).getCode());
            intent.putExtra(Intent.EXTRA_TITLE, mProducts.get(getAdapterPosition()).getCategory());
            mContext.startActivity(intent, bundle);
        }
    }
}

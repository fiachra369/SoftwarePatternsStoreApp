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

import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import electric.ecomm.store.GlideApp;
import electric.ecomm.store.R;
import electric.ecomm.store.activities.AddCategoryActivity;
import electric.ecomm.store.activities.AdminCategoryProductsActivity;
import electric.ecomm.store.activities.CategoryProductsActivity;
import electric.ecomm.store.activities.MainActivity;
import electric.ecomm.store.activities.MainAdminActivity;
import electric.ecomm.store.model.Categories;


public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewsHolder> {

    private Context mContext;
    private List<Categories> mCategoriesList;
    private RequestOptions mRequestOptions;

    public CategoriesAdapter(Context mContext, List<Categories> categories) {
        this.mContext = mContext;
        this.mCategoriesList = categories;
        mRequestOptions = new RequestOptions();
    }

    @NonNull
    @Override
    public CategoriesViewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoriesView = LayoutInflater.from(mContext).inflate(R.layout.list_item_categories, parent, false);
        return new CategoriesViewsHolder(categoriesView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewsHolder holder, int position) {
        String categoryImageUrl = mCategoriesList.get(position).getCategoryImage();
        String categoryName = mCategoriesList.get(position).getCategoryName();
        holder.mCategoryTextView.setText(categoryName);
        GlideApp.with(mContext)
                .load(categoryImageUrl)
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.error_holder)
                .apply(mRequestOptions.override(mContext.getResources().getInteger(R.integer.category_image_width),
                        mContext.getResources().getInteger(R.integer.category_image_height)))
                .into(holder.mCategoryImageView);
        if (mContext instanceof MainActivity) {
            holder.ivEdit.setVisibility(View.GONE);
        }
        else holder.ivEdit.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return mCategoriesList == null ? 0 : mCategoriesList.size();
    }


    public class CategoriesViewsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.categoryImageView)
        ImageView mCategoryImageView;
        @BindView(R.id.categoryNameTextView)
        TextView mCategoryTextView;
        @BindView(R.id.iv_edit)
        ImageView ivEdit;

        public CategoriesViewsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.categoriesCardView)
        public void onCategoryClick() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                launchSceneTransitions();
            } else {
                launchProductsActivity();
            }
        }

        @OnClick(R.id.iv_edit)
        public void onEditClick() {
            launchEditActivity();
        }

        private void launchProductsActivity() {
            if (mContext instanceof MainActivity) {

                Intent intent = new Intent(mContext, CategoryProductsActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, mCategoriesList.get(getAdapterPosition()).getCategoryName());
                mContext.startActivity(intent);
            }
            else if (mContext instanceof MainAdminActivity){
                Intent intent = new Intent(mContext, AdminCategoryProductsActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, mCategoriesList.get(getAdapterPosition()).getCategoryName());
                mContext.startActivity(intent);
            }

        }

        private void launchEditActivity() {
            if (mContext instanceof MainAdminActivity){
                Intent intent = new Intent(mContext, AddCategoryActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, mCategoriesList.get(getAdapterPosition()).getCategoryName());
                intent.putExtra("Image", mCategoriesList.get(getAdapterPosition()).getCategoryImage());
                mContext.startActivity(intent);
            }

        }

        @SuppressLint("NewApi")
        private void launchSceneTransitions() {

            if (mContext instanceof MainActivity) {

                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext).toBundle();
                Intent intent = new Intent(mContext, CategoryProductsActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, mCategoriesList.get(getAdapterPosition()).getCategoryName());
                mContext.startActivity(intent, bundle);
            }
            else if (mContext instanceof MainAdminActivity){
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext).toBundle();
                Intent intent = new Intent(mContext, AdminCategoryProductsActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, mCategoriesList.get(getAdapterPosition()).getCategoryName());
                mContext.startActivity(intent, bundle);
            }
        }
    }
}

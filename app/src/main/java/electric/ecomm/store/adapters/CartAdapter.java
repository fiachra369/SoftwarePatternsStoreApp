package electric.ecomm.store.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import electric.ecomm.store.GlideApp;
import electric.ecomm.store.R;
import electric.ecomm.store.model.Cart;
import electric.ecomm.store.model.Product;
import electric.ecomm.store.model.User;
import electric.ecomm.store.utils.Constants;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartProductsViewHolder> {

    private final Context mContext;
    private final CartPriceListener mCartPriceListener;
    private List<Product> mCartProducts;
    private String mNodeKey;

    public CartAdapter(List<Product> mCartProducts, Context mContext, CartPriceListener priceListener) {
        this.mCartProducts = mCartProducts;
        this.mContext = mContext;
        this.mCartPriceListener = priceListener;
    }

    public List<Product> getCartProducts() {
        return mCartProducts;
    }

    @NonNull
    @Override
    public CartProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cartProductView = LayoutInflater.from(mContext).inflate(R.layout.list_item_cart_products, parent, false);
        return new CartProductsViewHolder(cartProductView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartProductsViewHolder holder, int position) {
        Product product = mCartProducts.get(position);

        float productPrice = product.getPrice();
        float productSale = product.getSale();

        if (productSale > 0) {
            setProductPriceWithSale(holder, productPrice, productSale);
        } else {
            setProductPriceWithoutSale(holder, productPrice);
        }
        showProductDetails(holder, product);
    }


    private void showProductDetails(@NonNull CartProductsViewHolder holder, Product product) {
        GlideApp.with(mContext)
                .load(product.getImage())
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.error_holder)
                .into(holder.mCartProductImageView);
        holder.mCartProductQuantityCounter.setText(String.valueOf(mContext.getString(R.string.label_product_quantity)
                + product.getShoppingCartQuantity()));
        holder.mCartProductDescriptionTextView.setText(product.getDescription());
        holder.mCartProductNameTextView.setText(product.getName());
    }

    private void setProductPriceWithoutSale(@NonNull CartProductsViewHolder holder, float productPrice) {
        holder.mCartProductPriceTextView.setText(new DecimalFormat().format(productPrice));
        holder.mCartProductSaleTextView.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void setProductPriceWithSale(@NonNull CartProductsViewHolder holder, float productPrice, float productSale) {
        float saleInPercent = (productSale / productPrice) * 100;
        String saleInPercentText = new DecimalFormat("#.#").format(saleInPercent);
        holder.mCartProductSaleTextView.setText(String.valueOf(saleInPercentText + mContext.getString(R.string.percent_sale_off_label)));
        holder.mCartProductPriceTextView.setText("€" + new DecimalFormat().format(productPrice - productSale));
    }

    private String getTotalCartPrice() {
        if (mCartProducts.size() == 0) {
            return "";
        } else {
            float cartTotalPrice = 0;
            float cartQuantitySale = 0;
            float sale;

            for (int i = 0; i < mCartProducts.size(); i++) {
                int cartProductsQuantity = mCartProducts.get(i).getShoppingCartQuantity();
                cartTotalPrice = cartTotalPrice + (cartProductsQuantity * mCartProducts.get(i).getPrice());
                sale = mCartProducts.get(i).getSale();
                if (sale > 0) {
                    cartQuantitySale = cartQuantitySale + (cartProductsQuantity * sale);
                }
            }

            return new DecimalFormat().format(cartTotalPrice - cartQuantitySale);
        }
    }

    public void addCartProducts(List<Product> products, String key) {
        mCartProducts = products;
        notifyDataSetChanged();
        mCartPriceListener.onCartPriceChangeListener(getTotalCartPrice());
        mNodeKey = key;
    }

    @Override
    public int getItemCount() {
        return mCartProducts == null ? 0 : mCartProducts.size();
    }

    public interface CartPriceListener {
        void onCartPriceChangeListener(String price);
    }

    public class CartProductsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cartProductImageView)
        ImageView mCartProductImageView;
        @BindView(R.id.cartProductNameTextView)
        TextView mCartProductNameTextView;
        @BindView(R.id.cartProductDescriptionTextView)
        TextView mCartProductDescriptionTextView;
        @BindView(R.id.cartProductPriceTextView)
        TextView mCartProductPriceTextView;
        @BindView(R.id.cartProductQuantityTextView)
        TextView mCartProductQuantityCounter;
        @BindView(R.id.decreaseQuantityCartProductImageButton)
        ImageView mMinusQuantityButton;
        @BindView(R.id.increaseQuantityCartProductImageButton)
        ImageView mPlusQuantityButton;
        @BindView(R.id.cartProductSalePercentageTextView)
        TextView mCartProductSaleTextView;

        private FirebaseUser mFirebaseUser;
        private FirebaseDatabase mDatabase;
        private DatabaseReference mReference;

        public CartProductsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setupFirebaseDatabase();
        }

        private void setupFirebaseDatabase() {
            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance();
            mReference = mDatabase.getReference().child(Constants.USER_CHILD);
        }

        @SuppressLint("SetTextI18n")
        @OnClick(R.id.increaseQuantityCartProductImageButton)
        public void onIncreaseQuantityButtonClick() {
            Product product = mCartProducts.get(getAdapterPosition());
            product.setShoppingCartQuantity(product.getShoppingCartQuantity() + 1);
            mCartProducts.set(getAdapterPosition(), product);
            mCartProductQuantityCounter.setText(mContext.getString(R.string.label_product_quantity) + String.valueOf(product.getShoppingCartQuantity()));
            updateUserCart(mCartProducts);
        }

        private void updateUserCart(List<Product> cartProducts) {
            Cart cart = new Cart(cartProducts, mFirebaseUser.getUid());
            User user = new User(mFirebaseUser.getDisplayName(), mFirebaseUser.getUid(), cart, CartAdapter.this.mCartProducts.size());
            mReference.child(mNodeKey).setValue(user);
            mCartPriceListener.onCartPriceChangeListener(getTotalCartPrice());
            notifyUserIncreaseQuantity();
        }


        private void notifyUserIncreaseQuantity() {

            Toast.makeText(mContext, R.string.quantity_increase_msg, Toast.LENGTH_SHORT).show();
        }

        @SuppressLint("SetTextI18n")
        @OnClick(R.id.decreaseQuantityCartProductImageButton)
        public void onMinusQuantityButtonClick() {
            if (getAdapterPosition() != -1) {
                Product product = mCartProducts.get(getAdapterPosition());
                product.setShoppingCartQuantity(product.getShoppingCartQuantity() - 1);
                mCartProducts.set(getAdapterPosition(), product);

                if (product.getShoppingCartQuantity() <= 0) {
                    removeProductOfUserCart();
                } else {
                    mCartProductQuantityCounter.setText(mContext.getString(R.string.label_product_quantity) + String.valueOf(product.getShoppingCartQuantity()));
                    updateUserCart();
                }

                if (mCartProducts.isEmpty()) {
                    deleteUserNode();
                }

                mCartPriceListener.onCartPriceChangeListener(getTotalCartPrice());
            }
        }

        private void deleteUserNode() {
            mCartProducts.clear();
            mReference.child(mNodeKey).removeValue();
            notifyDataSetChanged();
        }

        private void updateUserCart() {
            Cart cart = new Cart(mCartProducts, mFirebaseUser.getUid());
            User user = new User(mFirebaseUser.getDisplayName(), mFirebaseUser.getUid(), cart, mCartProducts.size());
            mReference.child(mNodeKey).setValue(user);
            notifyUserQuantityMinusOne();
        }

        private void removeProductOfUserCart() {
            mCartProductQuantityCounter.setText(String.valueOf(0));
            mCartProducts.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
            Cart cart = new Cart(mCartProducts, mFirebaseUser.getUid());
            User user = new User(mFirebaseUser.getDisplayName(), mFirebaseUser.getUid(), cart, mCartProducts.size());
            mReference.child(mNodeKey).setValue(user);
            notifyUserProductRemoved();
        }

        private void notifyUserProductRemoved() {

            Toast.makeText(mContext, R.string.product_remove_cart_msg, Toast.LENGTH_SHORT).show();
        }

        private void notifyUserQuantityMinusOne() {

            Toast.makeText(mContext, R.string.quantity_decreased_msg, Toast.LENGTH_SHORT).show();
        }


        @OnClick(R.id.cartItemDeleteFab)
        public void onDeleteFabClick() {
            Product product = mCartProducts.get(getAdapterPosition());
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setIcon(R.drawable.ic_warning);
            builder.setTitle(R.string.delete_product_cart_dialog_title);
            builder.setMessage(mContext.getString(R.string.delete_product_cart_first_part_dialog_message)
                    + product.getName() + mContext.getString(R.string.delete_product_cart_second_part_dialog_message));
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    removeProductFromCart();
                }
            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();

        }

        private void removeProductFromCart() {
            mCartProducts.remove(getAdapterPosition());
            Cart cart = new Cart(mCartProducts, mFirebaseUser.getUid());
            User user = new User(mFirebaseUser.getDisplayName(), mFirebaseUser.getUid(), cart, mCartProducts.size());
            mReference.child(mNodeKey).setValue(user);
            notifyUserProductRemoved();
            notifyItemRemoved(getAdapterPosition());

            if (mCartProducts.isEmpty()) {
                deleteUserNode();
            }

            mCartPriceListener.onCartPriceChangeListener(getTotalCartPrice());
        }

    }
}


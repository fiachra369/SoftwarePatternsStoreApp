package electric.ecomm.store.firebaseviewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import electric.ecomm.store.model.Categories;
import electric.ecomm.store.model.Product;
import electric.ecomm.store.utils.Constants;


public class ProductSearchViewModel extends ViewModel {

    private String mSearchQuery;
    private String mCategory;
    private MutableLiveData<List<Product>> mProductsResult;

    public ProductSearchViewModel(String searchQuery, String category) {
        this.mSearchQuery = searchQuery;
        this.mCategory = category;
    }

    public LiveData<List<Product>> getSearchResult() {
        if (mProductsResult == null) {
            mProductsResult = new MutableLiveData<>();
            executeSearchQuery();
        }
        return mProductsResult;
    }

    private void executeSearchQuery() {
        final List<Product> products = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child(Constants.CATEGORY_CHILD);
        reference.orderByChild(Constants.CATEGORY_NAME_CHILD).equalTo(mCategory).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Categories categories = dataSnapshot.getValue(Categories.class);
                for (int i = 0; i < categories.getCategoryProducts().size(); i++) {
                    if (categories.getCategoryProducts().get(i).getName().toLowerCase().contains(mSearchQuery.toLowerCase())) {
                        products.add(categories.getCategoryProducts().get(i));
                    }
                }
                mProductsResult.postValue(products);
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


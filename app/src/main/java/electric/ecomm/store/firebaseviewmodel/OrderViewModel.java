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

import electric.ecomm.store.model.Order;
import electric.ecomm.store.utils.Constants;


public class OrderViewModel extends ViewModel {

    private String mOrderKey;
    private MutableLiveData<Order> mOrder;

    public OrderViewModel(String mOrderKey) {
        this.mOrderKey = mOrderKey;
    }

    public LiveData<Order> getOrder() {
        if (mOrder == null) {
            mOrder = new MutableLiveData<>();
            loadOrderDetails();
        }
        return mOrder;
    }

    private void loadOrderDetails() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child(Constants.ORDER_PRODUCTS_CHILD);
        reference.orderByKey().equalTo(mOrderKey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Order orders = dataSnapshot.getValue(Order.class);
                mOrder.postValue(orders);
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


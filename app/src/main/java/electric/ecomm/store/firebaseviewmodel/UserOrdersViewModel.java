package electric.ecomm.store.firebaseviewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import electric.ecomm.store.model.Order;
import electric.ecomm.store.utils.Constants;


public class UserOrdersViewModel extends ViewModel {
    private MutableLiveData<List<Order>> mUserOrders;

    public LiveData<List<Order>> getUserOrders() {
        if (mUserOrders == null) {
            mUserOrders = new MutableLiveData<>();
            loadUserOrders();
        }
        return mUserOrders;
    }

    private void loadUserOrders() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child(Constants.ORDER_PRODUCTS_CHILD);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final List<Order> ordersList = new ArrayList<>();
        reference.orderByChild(Constants.USER_ID_CHILD).equalTo(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Order order = dataSnapshot.getValue(Order.class);
                order.setOrderKey(dataSnapshot.getKey());
                ordersList.add(order);
                mUserOrders.postValue(ordersList);
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


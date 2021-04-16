package electric.ecomm.store.firebaseviewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class OrderViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private String mOrderKey;

    public OrderViewModelFactory(String mOrderKey) {
        this.mOrderKey = mOrderKey;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OrderViewModel.class)) {
            return (T) new OrderViewModel(mOrderKey);
        } else {
            throw new IllegalArgumentException("This is unknown ViewModel class :/ ");
        }
    }
}

package electric.ecomm.store.firebaseviewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ProductsViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private String categoryChosen;
    private int mProductCode;

    public ProductsViewModelFactory(String categoryChosen) {
        this.categoryChosen = categoryChosen;
    }

    public ProductsViewModelFactory(int code, String category) {
        this.mProductCode = code;
        this.categoryChosen = category;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProductsViewModel.class)) {
            if (mProductCode != 0) {
                return (T) new ProductsViewModel(mProductCode, categoryChosen);
            } else {
                return (T) new ProductsViewModel(categoryChosen);
            }
        } else {
            throw new IllegalArgumentException("This is unknown ViewModel class :/ ");
        }
    }
}

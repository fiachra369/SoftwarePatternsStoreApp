package electric.ecomm.store.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import electric.ecomm.store.R;
import electric.ecomm.store.activities.OrderDetailsActivity;
import electric.ecomm.store.model.Order;
import electric.ecomm.store.utils.Constants;


@SuppressWarnings("ALL")
public class AdminOrdersAdapter extends RecyclerView.Adapter<AdminOrdersAdapter.OrdersViewHolder> {

    private List<Order> mUserOrdersList;
    private Context mContext;

    public AdminOrdersAdapter(List<Order> mUserOrdersList, Context mContext) {
        this.mUserOrdersList = mUserOrdersList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ordersView = LayoutInflater.from(mContext).inflate(R.layout.list_item_user_orders_admin, parent, false);

        return new OrdersViewHolder(ordersView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        Order order = mUserOrdersList.get(position);

        holder.mOrderKeyTextView.setText(String.valueOf(mContext.getString(R.string.order_key_label) + order.getOrderKey()));

        holder.mOrderPaymentPriceTextView.setText(String.valueOf("€" + new DecimalFormat().format(order.getTotalPriceAfterDiscount())));
        if (order.getOrderStatus() != null) {
            String orderStatus = order.getOrderStatus();
            holder.mOrderStatusTextView.setText(orderStatus);
        } else {
            holder.mOrderStatusTextView.setText(R.string.order_status_process);
        }

        holder.btnChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
                CharSequence items[] = new CharSequence[] {mContext.getString(R.string.order_status_process),
                        mContext.getString(R.string.order_status_packed), mContext.getString(R.string.order_status_shipped),
                        mContext.getString(R.string.order_status_arrived), mContext.getString(R.string.order_status_delivered)};
                final int[] selected = {0};
                String status = holder.mOrderStatusTextView.getText().toString();
                for (int i = 0; i<items.length; i++) {
                    if (status.equals(items[i]))
                        selected[0] = i;
                }
                adb.setSingleChoiceItems(items, selected[0], new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface d, int n) {
                        // ...
                        selected[0] = n;
                    }

                });
                adb.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference().child(Constants.ORDER_PRODUCTS_CHILD);
                        order.setOrderStatus(String.valueOf(items[selected[0]]));
                        reference.child(order.getOrderKey()).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(mContext, "Status Changed Successfully", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                adb.setNegativeButton("Cancel", null);
                adb.setTitle("Choose Order Status");
                adb.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserOrdersList == null ? 0 : mUserOrdersList.size();
    }

    public class OrdersViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.userOrderKeyTextView)
        TextView mOrderKeyTextView;
        @BindView(R.id.userOrderPriceTextView)
        TextView mOrderPaymentPriceTextView;
        @BindView(R.id.userOrderStatusTextView)
        TextView mOrderStatusTextView;
        @BindView(R.id.btn_change_status)
        Button btnChangeStatus;

        public OrdersViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.userOrderCardView)
        public void onOrderClick() {
            Intent intent = new Intent(mContext, OrderDetailsActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, mUserOrdersList.get(getAdapterPosition()).getOrderKey());
            mContext.startActivity(intent);
        }
    }
}


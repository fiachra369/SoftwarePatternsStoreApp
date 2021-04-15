package electric.ecomm.store.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import electric.ecomm.store.R;
import electric.ecomm.store.model.Review;


@SuppressWarnings("ALL")
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.OrdersViewHolder> {

    private List<Review> reviewList;
    private Context mContext;

    public ReviewAdapter(List<Review> reviews, Context mContext) {
        this.reviewList = reviews;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ordersView = LayoutInflater.from(mContext).inflate(R.layout.list_item_reviews, parent, false);

        return new OrdersViewHolder(ordersView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.tvUsername.setText(review.getUserName());
        holder.tvComment.setText(review.getComment());
        holder.rbReviewRate.setRating(review.getStars());
        holder.rbReviewRate.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return reviewList == null ? 0 : reviewList.size();
    }

    public class OrdersViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_review_username)
        TextView tvUsername;
        @BindView(R.id.tv_review_comment)
        TextView tvComment;
        @BindView(R.id.rb_review_rate)
        RatingBar rbReviewRate;

        public OrdersViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}


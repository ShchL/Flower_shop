package it.mirea.kursovayaflowers.room;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import it.mirea.kursovayaflowers.R;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {
    private Context context;
    private List<CartItem> cartItemList;

    public CartItemAdapter(Context context) {
        this.context = context;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_busket, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItemList == null ? 0 : cartItemList.size();
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView, priceTextView;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.busket_image);
            nameTextView = itemView.findViewById(R.id.busket_name);
            priceTextView = itemView.findViewById(R.id.busket_price);
        }

        public void bind(CartItem cartItem) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(cartItem.image, 0, cartItem.image.length));
            nameTextView.setText(cartItem.name);
            priceTextView.setText(cartItem.price.concat(" руб."));
        }
    }
}
package com.greezxii.mobilecontroller.recycler;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.greezxii.mobilecontroller.R;

public class ItemViewHolder extends RecyclerView.ViewHolder{
    TextView item_address;
    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        item_address = itemView.findViewById(R.id.textView_item_address);
    }
}

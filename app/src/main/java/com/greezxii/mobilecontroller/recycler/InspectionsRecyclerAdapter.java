package com.greezxii.mobilecontroller.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.greezxii.mobilecontroller.R;
import com.greezxii.mobilecontroller.database.Inspection;

import java.util.List;

public class InspectionsRecyclerAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    Context context;
    List<Inspection> items;

    public InspectionsRecyclerAdapter(Context context, List<Inspection> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_view_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Inspection entity = items.get(position);
        holder.item_address.setText(entity.toString());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

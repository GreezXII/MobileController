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

    public InspectionsRecyclerAdapter(Context context, List<Inspection> items, OnInspectionClickListener onClickListener) {
        this.context = context;
        this.items = items;
        this.onInspectionClick = onClickListener;
    }

    public interface OnInspectionClickListener {
        void onInspectionClick(int position);
    }

    private final OnInspectionClickListener onInspectionClick;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_view_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Inspection inspection = items.get(position);
        String fullAddress = inspection.getAddress() + ", " + inspection.getApartment();
        holder.item_address.setText(fullAddress);
        holder.itemView.setOnClickListener(view -> onInspectionClick.onInspectionClick(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

package com.greezxii.mobilecontroller.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.greezxii.mobilecontroller.R;
import com.greezxii.mobilecontroller.database.Inspection;
import com.greezxii.mobilecontroller.databinding.RecyclerViewItemBinding;

import java.util.List;
import java.util.Locale;

public class InspectionsRecyclerAdapter extends RecyclerView.Adapter<InspectionsRecyclerAdapter.ViewHolder> {

    private List<Inspection> list;
    private final OnInspectionClickListener onInspectionClick;

    public InspectionsRecyclerAdapter(List<Inspection> list, OnInspectionClickListener onClickListener) {
        this.list = list;
        this.onInspectionClick = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerViewItemBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.recycler_view_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inspection item = list.get(position);
        String itemString = String.format(new Locale("ru"), "%s, %s",
                item.getAddress(), item.getApartment());
        holder.binding.textViewItem.setText(itemString);
        holder.binding.textViewItem.setOnClickListener(view -> onInspectionClick.onInspectionClick(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateInspections(List<Inspection> inspections) {
        this.list = inspections;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerViewItemBinding binding;

        public ViewHolder(@NonNull RecyclerViewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnInspectionClickListener {
        void onInspectionClick(int position);
    }
}

package com.greezxii.mobilecontroller.recycler;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.greezxii.mobilecontroller.R;
import com.greezxii.mobilecontroller.database.Inspection;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.flexibleadapter.utils.DrawableUtils;
import eu.davidea.viewholders.FlexibleViewHolder;

public class InspectionFlexibleItem extends AbstractFlexibleItem<InspectionFlexibleItem.InspectionViewHolder> {

    private Inspection inspection;

    public InspectionFlexibleItem(Inspection inspection) {
        this.inspection = inspection;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Inspection) {
            Inspection outInspection = (Inspection) o;
            return this.inspection.id == outInspection.id;
        }
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_view_item;
    }

    @Override
    public InspectionViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new InspectionViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, InspectionViewHolder holder, int position, List<Object> payloads) {
        String title = inspection.getAddress() + ", " + inspection.getApartment();
        holder.title.setText(title);
        holder.title.setEnabled(isEnabled());

        Context context = holder.itemView.getContext();
        Drawable drawable = DrawableUtils.getSelectableBackgroundCompat(
                Color.WHITE, Color.parseColor("#dddddd"), // Same color of divider
                DrawableUtils.getColorControlHighlight(context));
        DrawableUtils.setBackgroundCompat(holder.itemView, drawable);
    }

    public class InspectionViewHolder extends FlexibleViewHolder {

        public TextView title;

        public InspectionViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            title = (TextView) view.findViewById(R.id.title);
        }
    }
}

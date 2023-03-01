package com.greezxii.mobilecontroller.recycler;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.greezxii.mobilecontroller.R;
import com.greezxii.mobilecontroller.model.Card;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.flexibleadapter.utils.DrawableUtils;
import eu.davidea.viewholders.FlexibleViewHolder;

public class CardFlexibleItem extends AbstractFlexibleItem<CardFlexibleItem.CardViewHolder> {

    private final Card card;

    public CardFlexibleItem(Card card) {
        this.card = card;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Card) {
            Card outCard = (Card) o;
            return this.card.id == outCard.id;
        }
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_view_item;
    }

    @Override
    public CardViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new CardViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, CardViewHolder holder, int position, List<Object> payloads) {
        String title = card.address.getBuildingAddress() + ", "
                + card.address.getApartmentAddress();
        holder.title.setText(title);
        holder.title.setEnabled(isEnabled());

        Context context = holder.itemView.getContext();
        Drawable drawable = DrawableUtils.getSelectableBackgroundCompat(
                Color.WHITE, Color.parseColor("#dddddd"), // Same color of divider
                DrawableUtils.getColorControlHighlight(context));
        DrawableUtils.setBackgroundCompat(holder.itemView, drawable);
    }

    public static class CardViewHolder extends FlexibleViewHolder {

        public TextView title;

        public CardViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            title = (TextView) view.findViewById(R.id.title);
        }
    }
}

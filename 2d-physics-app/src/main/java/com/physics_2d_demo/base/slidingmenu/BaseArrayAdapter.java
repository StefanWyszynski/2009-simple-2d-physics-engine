package com.physics_2d_demo.base.slidingmenu;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public abstract class BaseArrayAdapter<T extends BaseArrayItem> extends ArrayAdapter<T> {
    protected Context context;

    public BaseArrayAdapter(Context context) {
        super(context, 0);
        this.context = context;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getItemID();
    }

    public void setItems(ArrayList<T> items) {
        clear();
        addAll(items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T item = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = item.getView(inflater, parent);
        }
        IBaseArrayItemHolder viewHolder = (IBaseArrayItemHolder) convertView.getTag();
        item.fillInViewHolder(viewHolder);
        return convertView;
    }
}

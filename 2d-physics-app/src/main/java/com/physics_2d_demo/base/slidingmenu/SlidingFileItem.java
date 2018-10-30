package com.physics_2d_demo.base.slidingmenu;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.physics_2d_demo.R;

public class SlidingFileItem extends BaseArrayItem {
    public String title;
    public int imgResID;

    public SlidingFileItem(String title, int imgResID) {
        super();
        this.title = title;
        this.imgResID = imgResID;
    }

    @Override
    public int getLayoutID() {
        return R.layout.sliding_menu_item;
    }

    @Override
    public IBaseArrayItemHolder createViewHolder(View view) {
        SlidingItemHolder slidingItemHolder = new SlidingItemHolder();
        slidingItemHolder.txtTitle = (TextView) view.findViewById(R.id.first_name_title);
        slidingItemHolder.imageItem = (ImageView) view.findViewById(R.id.avatar);
        return slidingItemHolder;
    }

    @Override
    public void fillInViewHolder(IBaseArrayItemHolder viewHolder) {
        SlidingItemHolder holder = (SlidingItemHolder) viewHolder;
        holder.txtTitle.setText(title);
        holder.imageItem.setImageResource(imgResID);
    }

    public class SlidingItemHolder implements IBaseArrayItemHolder {
        public TextView txtTitle;
        public ImageView imageItem;
    }
}

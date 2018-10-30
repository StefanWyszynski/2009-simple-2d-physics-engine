package com.physics_2d_demo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.physics_2d_demo.about.AboutFragment;
import com.physics_2d_demo.base.slidingmenu.BaseArrayItem;
import com.physics_2d_demo.base.slidingmenu.BaseSlidingMenuActivity;
import com.physics_2d_demo.base.slidingmenu.SlidingFileItem;
import com.physics_2d_demo.base.slidingmenu.SlidingFilesAdapter;
import com.physics_2d_demo.demo.Physics2DDemoFragment;

import java.util.ArrayList;

/*
 * @author Stefan Wyszyński 2009 przeniesiona do androida z J2ME
 */
public class DemoActivity extends BaseSlidingMenuActivity {
    public static final int SLIDING_ITEM_ABOUT = 1;
    public static final int SLIDING_ITEM_PHYSICS_DEMO = 2;

    @Override
    public int getActivityLayout() {
        return R.layout.activity_base_sliding_menu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_as_up);
        selectDrawerListItem(0);
    }

    @Override
    protected SlidingFilesAdapter getSlidingMenuAdapter() {
        SlidingFilesAdapter<BaseArrayItem> slidingFoldersAdapter = new SlidingFilesAdapter(this);
        ArrayList<BaseArrayItem> items = new ArrayList<>();

        addSlidingMenuItem(items, getString(R.string.fragment_main_title), R.drawable.ic_mvvm,
                SLIDING_ITEM_PHYSICS_DEMO);

        addSlidingMenuItem(items, getString(R.string.fragment_about_title), R.drawable.ic_about, SLIDING_ITEM_ABOUT);
        slidingFoldersAdapter.setItems(items);
        return slidingFoldersAdapter;
    }

    private SlidingFileItem addSlidingMenuItem(ArrayList<BaseArrayItem> items, String text, int imageResId,
                                               int itemID) {
        SlidingFileItem gridItem = new SlidingFileItem(text, imageResId);
        gridItem.setItemID(itemID);
        items.add(gridItem);
        return gridItem;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int itemId = (int) getSlidingMenuAdapter().getItemId(position);
        switch (itemId) {
            case SLIDING_ITEM_ABOUT:
                putFragment(new AboutFragment(), false, true, R.anim.fade_in, R.anim.fade_out);
                break;
            case SLIDING_ITEM_PHYSICS_DEMO:
                putFragment(new Physics2DDemoFragment(), false, true, R.anim.fade_in, R.anim.fade_out);
                break;

            default:
                break;
        }
        closeSlidingMenu();
    }
}

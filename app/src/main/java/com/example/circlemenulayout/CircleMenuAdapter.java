package com.example.circlemenulayout;

import java.util.List;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * author: lanweihua
 * created on: 2020/8/5 8:49 PM
 * description:
 */
public class CircleMenuAdapter extends BaseAdapter {

  private List<MenuItem> mMenuItems;
  private Context mContext;

  public CircleMenuAdapter(List<MenuItem> menuItems, Context context) {
    mMenuItems = menuItems;
    mContext = context;
  }

  @Override
  public int getCount() {
    return mMenuItems==null?0:mMenuItems.size();
  }

  @Override
  public MenuItem getItem(int i) {
    return mMenuItems==null?null:mMenuItems.get(i);
  }

  @Override
  public long getItemId(int i) {
    return 0;
  }

  @Override
  public View getView(int i, View convertview, ViewGroup parent) {
    LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
      View view = mInflater.inflate(R.layout.circle_menu_item, parent, false);
      initMenuItem(view,i);
      return view;
  }

  private void initMenuItem(View view,int postion){
    view.setVisibility(View.VISIBLE);
    ImageView iv = (ImageView) view
        .findViewById(R.id.id_circle_menu_item_image);
    iv.setVisibility(View.VISIBLE);
    TextView tv = (TextView) view
        .findViewById(R.id.id_circle_menu_item_text);
    tv.setVisibility(View.VISIBLE);
    final MenuItem menuItem = getItem(postion);
    iv.setImageResource(menuItem.mImageId);
    tv.setText(menuItem.title);
  }


}

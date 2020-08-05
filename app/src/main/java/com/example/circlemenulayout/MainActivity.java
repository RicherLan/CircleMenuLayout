package com.example.circlemenulayout;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  private CircleMenuLayout mCircleMenuLayout;

  private String[] mItemTexts = new String[]{"驻基 ", "金丹", "元婴",
      "凝体", "乘鼎", "劫变"};
  private int[] mItemImgs = new int[]{R.mipmap.ic_launcher,
      R.mipmap.ic_launcher, R.mipmap.ic_launcher,
      R.mipmap.ic_launcher, R.mipmap.ic_launcher,
      R.mipmap.ic_launcher};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mCircleMenuLayout = (CircleMenuLayout) findViewById(R.id.id_menulayout);

    List<MenuItem> menuItems = new ArrayList<>();
    for(int i=0;i<mItemImgs.length;++i){
      MenuItem menuItem = new MenuItem(mItemImgs[i],mItemTexts[i]);
      menuItems.add(menuItem);
    }

    CircleMenuAdapter adapter = new CircleMenuAdapter(menuItems,getApplicationContext());
    mCircleMenuLayout.setListAdapter(adapter);

    mCircleMenuLayout.setOnMenuItemClickListener(new CircleMenuLayout.OnMenuItemClickListener() {

      @Override
      public void itemClick(View view, int pos) {
        Toast.makeText(MainActivity.this, mItemTexts[pos],
            Toast.LENGTH_SHORT).show();
      }
    });

  }
}
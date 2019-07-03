package com.alekseyM73.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alekseyM73.R;
import com.alekseyM73.adapter.ViewPagerAdapter;
import com.alekseyM73.listeners.ViewPagerListener;
import com.alekseyM73.model.photo.Item;
import com.alekseyM73.model.user.UserResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity implements ViewPagerListener {

    public static final String ITEM = "com.alekseyM73.info.item";
    public static final String TYPE = "com.alekseyM73.info.type";
    public static final String TYPE_LIST = "com.alekseyM73.info.type_list";
    public static final String TYPE_ONE = "com.alekseyM73.info.type_one";
    public static final String CURRENT_ITEM = "com.alekseyM73.info.current_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ViewPager viewPager = findViewById(R.id.view_pager);
        Bundle arguments = getIntent().getExtras();
        List<Item> items = null;
        int currItem = 0;
        Gson gson = new Gson();
        if (arguments != null) {
            String type = arguments.getString(TYPE, "");
            if (type.equals(TYPE_ONE)){
            Item item = gson.fromJson(arguments.getString(ITEM), Item.class);
                items = new ArrayList<>();
                items.add(item);
            } else if (type.equals(TYPE_LIST)){
                Type listType = new TypeToken<List<Item>>() {
                }.getType();
                items = gson.fromJson(arguments.getString(ITEM), listType);
                System.out.println(items);
                currItem = arguments.getInt(CURRENT_ITEM);
            }
            viewPager.setAdapter(new ViewPagerAdapter(items, this, this));
            viewPager.setCurrentItem(currItem);
        }
    }

    @Override
    public void showPhoto(Item item) {
        Intent intent = new Intent(this, PageActivity.class);
        intent.putExtra(PageActivity.KEY_ID, item.getUser().getId());
        intent.putExtra(PageActivity.KEY_PHOTO_ID, item.getId());
        intent.putExtra(PageActivity.KEY_ACTION, PageActivity.ACTION_PHOTO);
        intent.putExtra(PageActivity.KEY_NAME, item.getUser().getFirstName() + " " + item.getUser().getLastName());
        Log.d("mylog", item.getUser().getId() + " ID");
        startActivity(intent);
    }

    @Override
    public void showPage(Item item) {
        Intent intent = new Intent(this, PageActivity.class);
        intent.putExtra(PageActivity.KEY_ID, item.getUser().getId());
        intent.putExtra(PageActivity.KEY_ACTION, PageActivity.ACTION_PAGE);
        intent.putExtra(PageActivity.KEY_NAME, item.getUser().getFirstName() + " " + item.getUser().getLastName());
        Log.d("mylog", item.getUser().getId() + " ID");
        startActivity(intent);
    }

}

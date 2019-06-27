package com.alekseyM73.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alekseyM73.R;
import com.alekseyM73.model.photo.Item;
import com.alekseyM73.model.photo.Photo;
import com.alekseyM73.model.user.UserResponse;
import com.squareup.picasso.Picasso;

public class InfoActivity extends AppCompatActivity {

    private UserResponse userResponse;
    private Photo photo;
    private Item item;
    private TextView firsec;
    private TextView bDate;
    private TextView sex;
    private Button butToLib;
    private Button butToPage;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Bundle arguments = getIntent().getExtras();


        getEssences(arguments);
        setInf();

        butToLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LibPhotoActivity.class);
                intent.putExtra("AlbubID", item.getAlbumId());
                startActivity(intent);
            }
        });

        butToPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PageActivity.class);
                intent.putExtra("AccID", item.getUserId());
                startActivity(intent);
            }
        });

    }

    private void getEssences(Bundle arguments){
       userResponse = (UserResponse)arguments
                .getSerializable(UserResponse.class.getSimpleName());
       photo = (Photo)arguments.getSerializable(Photo.class.getSimpleName());
       item = (Item)arguments.getSerializable(Item.class.getSimpleName());
    }

    //TODO: Разобраться с полом человека
    // и вставить в поле "sex".

    private void setInf(){
        firsec = findViewById(R.id.First_Second);
        bDate = findViewById(R.id.bday);
        sex = findViewById(R.id.sex);
        butToLib = findViewById(R.id.butToLib);
        butToPage = findViewById(R.id.butToPage);
        imageView = findViewById(R.id.imageView4);


        firsec.setText(userResponse.getFirstName() + " " + userResponse.getLastName());
        bDate.setText(userResponse.getBdate());
        Picasso.get().load(photo.getUrl()).into(imageView);
    }

}

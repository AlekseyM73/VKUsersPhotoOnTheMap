package com.alekseyM73.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alekseyM73.R;
import com.alekseyM73.model.user.UserResponse;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class InfoActivity extends AppCompatActivity {

    private UserResponse userResponse;
    private String photoUrl;
    private String albumId;

    private TextView tvFullName, bDate, tvCity;
    private Button actionToAlbum;
    private ImageView photo;
    private CircleImageView userPhoto;
    private Button actionToPage;

    public static final String USER = "com.alekseyM73.info.user";
    public static final String PHOTO_URL = "com.alekseyM73.info.photo";
    public static final String ALBUM_ID = "com.alekseyM73.info.album";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            userResponse = (UserResponse) arguments.getSerializable(USER);
            System.out.println("User = " + userResponse);
            photoUrl = arguments.getString(PHOTO_URL);
            albumId = arguments.getString(ALBUM_ID);
            Log.d("mylog", photoUrl + " " );

            setViews();
        }

        actionToPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PageActivity.class);
                intent.putExtra("ID", userResponse.getId());
                Log.d("mylog", userResponse.getId() + " ID");
                startActivity(intent);
            }
        });

        actionToAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PageActivity.class);
                intent.putExtra("IDalbum", albumId);
                Log.d("mylog", albumId + " IDalbum");
                startActivity(intent);
            }
        });
    }

    private void setViews(){
        tvFullName = findViewById(R.id.full_name);
        bDate = findViewById(R.id.bday);
        actionToAlbum = findViewById(R.id.butToAlbum);
        photo = findViewById(R.id.iv_photo);
        userPhoto = findViewById(R.id.user_photo);
        tvCity = findViewById(R.id.city);
        actionToPage = findViewById(R.id.butToPage);

        StringBuilder stringBuilder = new StringBuilder();
        if (userResponse.getFirstName() != null){
            stringBuilder.append(userResponse.getFirstName());
        }
        if (userResponse.getLastName() != null){
            stringBuilder.append(" ").append(userResponse.getLastName());
        }
        tvFullName.setText(stringBuilder.toString());

        if (userResponse.getCity() != null){
            tvCity.setText(userResponse.getCity().getTitle());
        }

//        if (userResponse.getBdate() != null) {
//            String[] bdate = userResponse.getBdate().split("\\.");
//            if (bdate.length == 3) {
//                int age = GregorianCalendar.getInstance().get(GregorianCalendar.YEAR) - Integer.valueOf(bdate[2]);
//                bDate.setText(String.valueOf(age));
//            }
//        }

        Picasso.get().load(userResponse.getPhoto()).into(userPhoto);
        Picasso.get().load(photoUrl).into(photo);

    }

}

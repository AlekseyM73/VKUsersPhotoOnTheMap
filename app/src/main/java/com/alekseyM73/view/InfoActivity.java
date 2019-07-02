package com.alekseyM73.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alekseyM73.R;
import com.alekseyM73.model.user.UserResponse;
import com.squareup.picasso.Picasso;

import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class InfoActivity extends AppCompatActivity {

    private UserResponse userResponse;
    private String photoUrl;
    private Long albumId;

    private TextView tvFullName, bDate, tvCity;
    private Button actionToAlbum;
    private ImageView photo;
    private CircleImageView userPhoto;

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
            albumId = arguments.getLong(ALBUM_ID);
            setViews();
        }
    }

    private void setViews(){
        tvFullName = findViewById(R.id.full_name);
        bDate = findViewById(R.id.bday);
        actionToAlbum = findViewById(R.id.butToLib);
        photo = findViewById(R.id.iv_photo);
        userPhoto = findViewById(R.id.user_photo);
        tvCity = findViewById(R.id.city);

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

        actionToAlbum.setOnClickListener(v -> {

        });
    }

}

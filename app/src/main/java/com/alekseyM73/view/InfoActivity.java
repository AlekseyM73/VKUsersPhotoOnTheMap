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
    private long photoId;

    private TextView tvFullName, bDate, tvCity;
    private Button actionToPhoto;
    private ImageView photo;
    private CircleImageView userPhoto;
    private View actionToPage;

    public static final String USER = "com.alekseyM73.info.user";
    public static final String PHOTO_URL = "com.alekseyM73.info.photo";
    public static final String PHOTO_ID = "com.alekseyM73.info.album";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            userResponse = (UserResponse) arguments.getSerializable(USER);
            photoUrl = arguments.getString(PHOTO_URL);
            photoId = arguments.getLong(PHOTO_ID);

            setViews();
        }

        actionToPage.setOnClickListener(v -> {
            Intent intent = new Intent(this, PageActivity.class);
            intent.putExtra(PageActivity.KEY_ID, userResponse.getId());
            intent.putExtra(PageActivity.KEY_ACTION, PageActivity.ACTION_PAGE);
            intent.putExtra(PageActivity.KEY_NAME, userResponse.getFirstName() + " " + userResponse.getLastName());
            Log.d("mylog", userResponse.getId() + " ID");
            startActivity(intent);
        });

        actionToPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(this, PageActivity.class);
            intent.putExtra(PageActivity.KEY_ID, userResponse.getId());
            intent.putExtra(PageActivity.KEY_PHOTO_ID, photoId);
            intent.putExtra(PageActivity.KEY_ACTION, PageActivity.ACTION_PHOTO);
            intent.putExtra(PageActivity.KEY_NAME, userResponse.getFirstName() + " " + userResponse.getLastName());
            Log.d("mylog", userResponse.getId() + " ID");
            startActivity(intent);
        });
    }

    private void setViews(){
        tvFullName = findViewById(R.id.full_name);
        bDate = findViewById(R.id.bday);
        actionToPhoto = findViewById(R.id.actionToPhoto);
        photo = findViewById(R.id.iv_photo);
        userPhoto = findViewById(R.id.user_photo);
        tvCity = findViewById(R.id.city);
        actionToPage = findViewById(R.id.actionToPage);

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

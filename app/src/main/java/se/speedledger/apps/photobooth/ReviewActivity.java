package se.speedledger.apps.photobooth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class ReviewActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getIntent().getExtras();
        setContentView(R.layout.activity_review);

        Button toStartButton = (Button) findViewById(R.id.button_go_to_start);

        toStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToStart();
            }
        });

        String firstPath = "";
        String secondPath = "";
        String thirdPath = "";
        String fourthPath = "";

        if(bundle != null) {
            firstPath = bundle.getString(Constants.FIRST);
            secondPath = bundle.getString(Constants.SECOND);
            thirdPath = bundle.getString(Constants.THIRD);
            fourthPath = bundle.getString(Constants.FOURTH);
        } else {
            Log.d(this.getLocalClassName(), "Bundle is null!");
        }

        File imgFile = new  File(firstPath);
        if(imgFile.exists()){
            ImageView firstImage = (ImageView) findViewById(R.id.image_first);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            firstImage.setImageBitmap(myBitmap);
        }

        File imgFile2 = new  File(secondPath);
        if(imgFile2.exists()){
            ImageView secondImage = (ImageView) findViewById(R.id.image_second);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile2.getAbsolutePath());
            secondImage.setImageBitmap(myBitmap);
        }

        File imgFile3 = new  File(firstPath);
        if(imgFile.exists()){
            ImageView thirdImage = (ImageView) findViewById(R.id.image_third);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile3.getAbsolutePath());
            thirdImage.setImageBitmap(myBitmap);
        }

        File imgFile4 = new  File(firstPath);
        if(imgFile.exists()){
            ImageView fourthImage = (ImageView) findViewById(R.id.image_fourth);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile4.getAbsolutePath());
            fourthImage.setImageBitmap(myBitmap);
        }

    }

    private void goToStart() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    }
}

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

import java.io.File;

public class ReviewActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle bundle = this.getIntent().getExtras();
        setContentView(R.layout.activity_review);

        Button toStartButton = (Button) findViewById(R.id.button_go_to_start);

        toStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToStart();
            }
        });

        Button printButton = (Button) findViewById(R.id.button_print);

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //printImages(bundle.getString(Constants.FIRST), bundle.getString(Constants.SECOND), bundle.getString(Constants.THIRD), bundle.getString(Constants.FOURTH));
                printImages(bundle.getString(Constants.FIRST));
                goToStart();
            }
        });

        String firstPath = "";
        String secondPath = "";
        String thirdPath = "";
        String fourthPath = "";

        if(bundle != null) {
            firstPath = bundle.getString(Constants.FIRST);
            //secondPath = bundle.getString(Constants.SECOND);
            //thirdPath = bundle.getString(Constants.THIRD);
            //fourthPath = bundle.getString(Constants.FOURTH);
        } else {
            Log.d(this.getLocalClassName(), "Bundle is null!");
        }

        File imgFile = new  File(firstPath);
        if(imgFile.exists()){
            ImageView firstImage = (ImageView) findViewById(R.id.image_first);
            Bitmap firstBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            firstImage.setImageBitmap(firstBitmap);
        }

        /*File imgFile2 = new  File(secondPath);
        if(imgFile2.exists()){
            ImageView secondImage = (ImageView) findViewById(R.id.image_second);
            Bitmap secondBitmap = BitmapFactory.decodeFile(imgFile2.getAbsolutePath());
            secondImage.setImageBitmap(secondBitmap);
        }

        File imgFile3 = new  File(thirdPath);
        if(imgFile.exists()){
            ImageView thirdImage = (ImageView) findViewById(R.id.image_third);
            Bitmap thirdBitmap = BitmapFactory.decodeFile(imgFile3.getAbsolutePath());
            thirdImage.setImageBitmap(thirdBitmap);
        }

        File imgFile4 = new  File(fourthPath);
        if(imgFile.exists()){
            ImageView fourthImage = (ImageView) findViewById(R.id.image_fourth);
            Bitmap fourthBitmap = BitmapFactory.decodeFile(imgFile4.getAbsolutePath());
            fourthImage.setImageBitmap(fourthBitmap);
        }*/
    }

    private void printImages(String... imagePaths) {
        new SendPhotosToLittlePrinterTask().execute(imagePaths);
    }

    private void goToStart() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    }
}

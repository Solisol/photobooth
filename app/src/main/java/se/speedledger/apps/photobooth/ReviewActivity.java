package se.speedledger.apps.photobooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

        Button printButton = (Button) findViewById(R.id.button_print);

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printPhotos();
            }
        });

        String paths = "";
        if(bundle != null) {
            Log.d(this.getLocalClassName(), "Bundle has stuffs!!!");
            paths = bundle.getString(Constants.FIRST) + " \n";
            paths = paths + bundle.getString(Constants.SECOND) + " \n";
            paths = paths + bundle.getString(Constants.THIRD) + " \n";
            paths = paths + bundle.getString(Constants.FOURTH) + " \n";
        } else {
            Log.d(this.getLocalClassName(), "Bundle is null!");
        }
    }

    private void printPhotos(String... paths) {
        PrintPhotosTask printPhotosTask = new PrintPhotosTask();
    }

    private void goToStart() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    }


}

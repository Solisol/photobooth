package se.speedledger.apps.photobooth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.*;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.widget.FrameLayout.*;

public class PhotoActivity extends Activity {
    private final String TAG = "PhotoActivity";
    Preview preview;
    Camera camera;
    Activity activity;
    Context context;
    ImageView countDownImage;
    Intent intent;
    private int pictureRound;
    private int saveRound;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        activity = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_photo);

        pictureRound = 1;
        saveRound = 0;

        intent = new Intent(this, ReviewActivity.class);

        countDownImage = (ImageView) findViewById(R.id.count_down);

        preview = new Preview(this, (SurfaceView) findViewById(R.id.surfaceView));
        preview.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        ((RelativeLayout) findViewById(R.id.layout)).addView(preview);
        preview.setKeepScreenOn(true);

        preview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.d(TAG, "Starting serie of pictures");
                takePicture();
            }
        });

        Toast.makeText(context, getString(R.string.take_photo_help), Toast.LENGTH_LONG).show();
    }

    private void countDown() {
        countDownImage.setImageResource(R.drawable.three_white);
        countDownImage.setVisibility(VISIBLE);
        final ArrayList<Integer> drawables = new ArrayList<Integer>();
        drawables.add(0, R.drawable.three_white);
        drawables.add(1, R.drawable.two_white);
        drawables.add(2, R.drawable.one_white);
        new CountDownTimer(4000, 1000) {

            int tick = 0;

            public void onTick(long millisUntilFinished) {
                if (tick < 3) {
                    countDownImage.setImageResource(drawables.get(tick));
                    tick++;
                }
            }

            public void onFinish() {
                countDownImage.setVisibility(INVISIBLE);
                Log.d(TAG, "Take picture");
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        }.start();
    }

    private void takePicture() {
        Log.d(TAG, "Round " + pictureRound);
        countDown();
    }

    private void goToReview() {
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        Log.d(this.getLocalClassName(), "Number of cameras: " + numCams);
        if (numCams > 0) {
            try {
                releaseCameraAndPreview();
                camera = Camera.open(1);
                camera.startPreview();
                preview.setCamera(camera);
            } catch (RuntimeException ex) {
                Log.e(this.getLocalClassName(), "Could not open camera");
                Toast.makeText(context, getString(R.string.camera_not_found), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void releaseCameraAndPreview() {
        preview.setCamera(null);
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }
        super.onPause();
    }

    private void resetCam() {
        camera.startPreview();
        preview.setCamera(camera);
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }

    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
        }
    };

    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };

    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            new SaveImageTask().execute(data);
            resetCam();
            Log.d(TAG, "onPictureTaken - jpeg");
            if (pictureRound < 1) {
                pictureRound++;
                takePicture();
            }
        }
    };

    private class SaveImageTask extends AsyncTask<byte[], Void, Boolean> {

        @Override
        protected Boolean doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                //"/Android/data/com.google.android.apps.docs/files" +
                File dir = new File(sdCard.getAbsolutePath() + "/photobooth");
                dir.mkdirs();

                File outFile = createImageFile(dir);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);

                outStream.flush();
                outStream.close();

                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

                refreshGallery(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                saveRound++;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (saveRound == 1) {
                Log.d(TAG, "Go to review");
                goToReview();
            }
        }

        private File createImageFile(File storageDir) throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Add file path to bundle
            String key = "";
            if (pictureRound == 1) {
                Log.d(TAG, "FIRST");
                intent.putExtra(Constants.FIRST, image.getAbsolutePath());
            } else if (pictureRound == 2) {
                Log.d(TAG, "SECOND");
                intent.putExtra(Constants.SECOND, image.getAbsolutePath());
            } else if (pictureRound == 3) {
                Log.d(TAG, "THIRD");
                intent.putExtra(Constants.THIRD, image.getAbsolutePath());
            } else {
                Log.d(TAG, "FOURTH");
                intent.putExtra(Constants.FOURTH, image.getAbsolutePath());
            }
            return image;
        }

    }
}

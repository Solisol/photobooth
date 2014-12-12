package se.speedledger.apps.photobooth;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * Only used to test static image
 */
public class PrintTask extends AsyncTask<String, Integer, Boolean> {

    private Context context;

    public PrintTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        sendFile2();
        return true;
    }

    private void sendFile2() {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.raw.skepp);

        // rotate
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedBitMap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

        // resize
        int maxWidth = 380;
        int rotatedWidth = rotatedBitMap.getWidth();
        double ratio = (double)maxWidth / (double)rotatedWidth;
        int newWidth = (int) (rotatedBitMap.getWidth() * ratio);
        int newHeight = (int) (rotatedBitMap.getHeight() * ratio);

        Bitmap smallBitmap = Bitmap.createScaledBitmap(rotatedBitMap, newWidth, newHeight, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        smallBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.NO_WRAP);
        sendImage(encodedImage);
    }

    private void sendImage(String image) {
        String imageTag = "<img class=dither src=\"data:image/jpg;base64," +
                image +
                "\" alt=\"Red dot\" />";

        String html = "<html><head><meta charset=\"utf-8\"></head><body><h1>An image!</h1>" +
                imageTag +
                "</body></html>";

        String url = "http://remote.bergcloud.com/playground/direct_print/WXNPLLKDLWHP";
        HttpPost post = new HttpPost(url);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("html", html));
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpClient client = new DefaultHttpClient();
        HttpResponse response = null;
        try {
            response = client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            InputStream inputStream = null;
            String s = null;
            try {
                inputStream = entity.getContent();
                s = inputStreamToString(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(s);
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String inputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }
}

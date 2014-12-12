package se.speedledger.apps.photobooth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SendPhotosToLittlePrinterTask extends AsyncTask<String, Integer, String> {

    private static final String TAG = SendPhotosToLittlePrinterTask.class.getName();
    private static final String URL_TO_LITTLE_PRINTER = "http://remote.bergcloud.com/playground/direct_print/WXNPLLKDLWHP";

    @Override
    protected String doInBackground(String... filePaths) {
        String result = "";
        for (String path : filePaths) {
            result = result + sendFile2(path) + " ";
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        StartActivity.makeToast(result);
    }

    private String sendFile2(String filePath) {
        File imgFile = new  File(filePath);
        Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        Bitmap scaledBitmap = rotateAndScaleImage(bm);

        String encodedImage = compressBitmapToBase64(scaledBitmap);

        String html = generateHtml(encodedImage);

        String response = sendToLittlePrinter(html);

        Log.d(TAG, "Respons from Little printer: " + response);
        return response;
    }

    private String sendToLittlePrinter(String html) {
        HttpPost post = new HttpPost(URL_TO_LITTLE_PRINTER);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("html", html));
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "Exception when setting post entity", e);
        }

        HttpClient client = new DefaultHttpClient();
        HttpResponse response = null;
        try {
            response = client.execute(post);
        } catch (IOException e) {
            Log.d(TAG, "Exception when sending", e);
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

            try {
                inputStream.close();
            } catch (IOException e) {
                Log.d(TAG, "Exception when closing input stream", e);
            }
            return s;
        }
        return "No response";
    }

    private String generateHtml(String encodedImage) {
        String imageTag = "<img class=dither src=\"data:image/jpg;base64," +
                encodedImage +
                "\" alt=\"Red dot\" />";

        return "<html><head><meta charset=\"utf-8\"></head><body><h1>An image!</h1>" +
                imageTag +
                "</body></html>";
    }

    private String compressBitmapToBase64(Bitmap scaledBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    private Bitmap rotateAndScaleImage(Bitmap bm) {
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

        return Bitmap.createScaledBitmap(rotatedBitMap, newWidth, newHeight, false);
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

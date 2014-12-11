package se.speedledger.apps.photobooth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PrintPhotosTask2 extends AsyncTask<String, Integer, Boolean> {

    private static final String TAG = PrintPhotosTask2.class.getName();

    private Context context;

    public PrintPhotosTask2(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... filePaths) {
        for (String path : filePaths) {
            sendFile2(path);
        }
        return true;
    }

    private void sendFile2(String filePath) {
        //InputStream is = getResources().openRawResource(R.raw.test);

        //Bitmap bm = BitmapFactory.decodeFile("/assets/8078313_orig.jpg");

        /*
        Bitmap bm = BitmapFactory.decodeStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        */
        File imgFile = new  File(filePath);
        Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        int width = (int) (bm.getWidth() * 0.10);
        int height = (int) (bm.getHeight() * 0.10);
        Bitmap smallBitmap = Bitmap.createScaledBitmap(bm, width, height, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        smallBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.NO_WRAP);
        /*
        String image = null;
        try {
            image = Base64.encodeToString(inputStreamToBytes(context.openFileInput(filePaths[0])), Base64.NO_WRAP);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        String imageTag = "<img class=dither src=\"data:image/jpg;base64," +
                encodedImage +
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

    private byte[] inputStreamToBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

}

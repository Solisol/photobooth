package se.speedledger.apps.photobooth;

import android.content.res.AssetManager;
import android.graphics.Path;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SendFileTask extends AsyncTask<String, Integer, Boolean> {


    @Override
    protected Boolean doInBackground(String... params) {



        return true;
    }

    private void sendFile() {
        /*
        HttpClient client = new DefaultHttpClient();

        String url = "http://remote.bergcloud.com/playground/direct_print/WXNPLLKDLWHP";
        AssetManager am = context.getAssets();

        InputStream is = am.open("test.txt");

        //Path path = Paths.get("C:\\photobooth\\8078313_orig.jpg");
        //byte[] data = MediaStore.Files.readAllBytes(path);
        //String base64String = Base64.encodeBase64String(data);

        String imageTag = "<img class=dither src=\"data:image/jpg;base64," +
                base64String +
                "\" alt=\"Red dot\" />";

        String html = "<html><head><meta charset=\"utf-8\"></head><body><h1>An image!</h1>" +
                imageTag +
                "</body></html>";

        HttpPost post = new HttpPost(url);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("html", html));
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpResponse response = null;
        try {
            response = client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            InputStream inputStream = null;
            try {
                inputStream = entity.getContent();

                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer, "UTF-8");
                String theString = writer.toString();
                System.out.println(theString);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */
    }

}

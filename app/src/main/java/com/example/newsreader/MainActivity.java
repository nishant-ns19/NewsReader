package com.example.newsreader;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
ArrayList<String> title=new ArrayList<>();
ArrayList<String> links=new ArrayList<>();
ArrayAdapter<String> ad;
ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv=findViewById(R.id.lv);

        DownloadTask task=new DownloadTask();
        String s= null;
        try {
            s = task.execute("https://newsapi.org/v2/top-headlines?country=in&apiKey=ccd406a76cb747b7a22cf003a2d34345").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("data",s);
        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(s);
            String articles=jsonObject.getString("articles");
            JSONArray array=new JSONArray(articles);
            Log.i("articles",articles);
            for(int i=0;i<array.length();i++)
            {
                JSONObject jsonObject1=array.getJSONObject(i);
                String desc=jsonObject1.getString("title");
                title.add(desc);
                links.add(jsonObject1.getString("url"));

            }
            Log.i("titles",title.toString());
            Log.i("urls",links.toString());
          // Toast.makeText(this, Integer.toString(title.size()), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ad=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,title);
        lv.setAdapter(ad);
        final Intent intent=new Intent(getApplicationContext(),web.class);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent.putExtra("url",links.get(position));
                startActivity(intent);
            }
        });


    }
public class DownloadTask extends AsyncTask<String,Void,String>
{
    @Override
    protected String doInBackground(String... urls) {

        try {
           URL url=new URL(urls[0]);
           HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();
            InputStream in=urlConnection.getInputStream();
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
            StringBuilder str=new StringBuilder();
            String line=null;
            while((line=reader.readLine())!=null)
            {
                str.append(line);
            }
            String result=str.toString();
            urlConnection.disconnect();
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
}

package com.codme.gldsmapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "UsingThingspeakAPI";
    private static final String THINGSPEAK_CHANNEL_ID = "577841";
    private static final String THINGSPEAK_API_KEY = "6BDV50VGDQ2AECCQ6BD"; //"THINGSPEAK READ API KEY"; //GARBAGE KEY
    private static final String THINGSPEAK_API_KEY_STRING = "api_key"; //"THINGSPEAK READ API KEY";
    /* Be sure to use the correct fields for your own app*/
    private static final String THINGSPEAK_FIELD1 = "field1";
    private static final String THINGSPEAK_FIELD3 = "field3";
    private static final String THINGSPEAK_UPDATE_URL = "https://api.thingspeak.com/update?";
    private static final String THINGSPEAK_CHANNEL_URL = "https://api.thingspeak.com/channels/";
    private static final String THINGSPEAK_FEEDS = "/feeds/last?";
    TextView t1,t2,t3;
    Switch Fan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t1=(TextView)findViewById(R.id.textView2);
        t2=(TextView)findViewById(R.id.textView3);
        t3=(TextView)findViewById(R.id.textView5);
        Fan=(Switch)findViewById(R.id.switch1);
        Fan.setTextColor(Color.GREEN);
        t1.setText("Present value : ");
        t2.setText("");
        Fan.setChecked(false);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_SMS))
            {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_SMS},1);
            }
            else
            {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_SMS},1);
            }
        }


      //  while(true)
       // {
            try {
                t2.setText("");
                new FetchThingspeakTask().execute();
            }
            catch(Exception e){
                Log.e("ERROR", e.getMessage(), e);
            }
       // }

    }
    class FetchThingspeakTask extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {
            t2.setText("Fetching Data from Server.Please Wait...");
        }
        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL(THINGSPEAK_CHANNEL_URL + THINGSPEAK_CHANNEL_ID +
                        THINGSPEAK_FEEDS + THINGSPEAK_API_KEY_STRING + "=" +
                        THINGSPEAK_API_KEY + "");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }
        protected void onPostExecute(String response) {
            String number="7036299791";
            String sms="alert gas leakage in your house";
            if(response == null) {
                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject channel = (JSONObject) new JSONTokener(response).nextValue();
                double v1 = channel.getDouble(THINGSPEAK_FIELD1);
                int motor=channel.getInt(THINGSPEAK_FIELD3);
                if(v1==0)
                    t2.setText("system is offline");
                else
                    t2.setText(""+v1);
                if(motor==0)
                {
                    Fan.setChecked(false);
                }
                else
                {
                    Fan.setChecked(true);
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(number, null, sms, null, null);
                        Toast.makeText(MainActivity.this, "Alert message sent to "+number, Toast.LENGTH_SHORT).show();
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(MainActivity.this,"sms sending failed!!",Toast.LENGTH_SHORT).show();
                    }
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1: {
                if(grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, "No Permission granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}

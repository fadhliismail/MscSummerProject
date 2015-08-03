package com.fadhliismail.physicalweb;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ShakeTurnOn extends Activity implements SensorEventListener{

    private static final float SHAKE_THRESHOLD = 1.1f;
    private static final int SHAKE_WAIT_TIME_MS = 250;

    private TextView mTextTitle;
    private TextView mTextValues, mTextView;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private long mShakeTime = 0;
    private Boolean lampState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_turn_on);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        getRequest();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {


                mTextValues = (TextView) findViewById(R.id.textValues);
                mTextView = (TextView) findViewById(R.id.textLamp);
                mTextTitle = (TextView) findViewById(R.id.textTitle);
            }
        });
    }

    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent event) {
        // If sensor is unreliable, then just return
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
        {
            return;
        }

        mTextValues.setText(
                "x = " + Float.toString(event.values[0]) + "\n" +
                        "y = " + Float.toString(event.values[1]) + "\n" +
                        "z = " + Float.toString(event.values[2]) + "\n"
        );


        detectShake(event);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // References:
    //  - http://jasonmcreynolds.com/?p=388
    //  - http://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125
    private void detectShake(SensorEvent event) {



        long now = System.currentTimeMillis();

        if((now - mShakeTime) > SHAKE_WAIT_TIME_MS) {
            mShakeTime = now;

            float gX = event.values[0] / SensorManager.GRAVITY_EARTH;
            float gY = event.values[1] / SensorManager.GRAVITY_EARTH;
            float gZ = event.values[2] / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement
            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            // Change background color if gForce exceeds threshold;
            // otherwise, reset the color

            if(gForce > SHAKE_THRESHOLD) {

                getRequest();

                getLampState();
                //Log.v("lampState2", toString(lampState));

                if (lampState) {
                    RequestQueue mRequestQueue;

                    // Instantiate the cache
                    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

                    // Set up the network to use HttpURLConnection as the HTTP client.
                    Network network = new BasicNetwork(new HurlStack());

                    // Instantiate the RequestQueue with the cache and network.
                    mRequestQueue = new RequestQueue(cache, network);

                    // Start the queue
                    mRequestQueue.start();
                    String url = "http://192.168.1.3/api/newdeveloper/lights/1/state";

                    final JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put("on", false);
                    } catch (JSONException e) {
                        // handle exception
                    }


                    JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                            new Response.Listener<JSONObject>()
                            {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // response
                                    Log.d("Response", response.toString());
                                    //txtDisplay.setText("Response : " + response.toString());
                                }
                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Log.d("Error.Response", error.toString());
                                    //txtDisplay.setText("Didnt work!");
                                }
                            }
                    ) {

                        @Override
                        public Map<String, String> getHeaders()
                        {
                            Map<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json");
                            headers.put("Accept", "application/json");
                            return headers;
                        }

                        @Override
                        public byte[] getBody() {

                            try {
                                Log.i("json", jsonObject.toString());
                                return jsonObject.toString().getBytes("UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    };

                    mRequestQueue.add(putRequest);


                } else {
                    RequestQueue mRequestQueue;

                    // Instantiate the cache
                    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

                    // Set up the network to use HttpURLConnection as the HTTP client.
                    Network network = new BasicNetwork(new HurlStack());

                    // Instantiate the RequestQueue with the cache and network.
                    mRequestQueue = new RequestQueue(cache, network);

                    // Start the queue
                    mRequestQueue.start();
                    String url = "http://192.168.1.3/api/newdeveloper/lights/1/state";

                    final JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put("on", true);
                    } catch (JSONException e) {
                        // handle exception
                    }


                    JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                            new Response.Listener<JSONObject>()
                            {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // response
                                    Log.d("Response", response.toString());
                                    //txtDisplay.setText("Response : " + response.toString());
                                }
                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Log.d("Error.Response", error.toString());
                                    //txtDisplay.setText("Didnt work!");
                                }
                            }
                    ) {

                        @Override
                        public Map<String, String> getHeaders()
                        {
                            Map<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json");
                            headers.put("Accept", "application/json");
                            return headers;
                        }

                        @Override
                        public byte[] getBody() {

                            try {
                                Log.i("json", jsonObject.toString());
                                return jsonObject.toString().getBytes("UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    };

                    mRequestQueue.add(putRequest);
                }
                //mView.setBackgroundColor(Color.rgb(0, 100, 0));

            }
            else {
                //mView.setBackgroundColor(Color.BLACK);
            }
        }
    }

    private void getRequest() {
        // get

        RequestQueue mRequestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        // Start the queue
        mRequestQueue.start();
        String url = "http://192.168.1.3/api/newdeveloper/lights/1";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //txtDisplay.setText("Response : " + response.toString());
                //Log.d(TAG, response.toString());

                String jsonResponse;

                try {
                    JSONObject state = response.getJSONObject("state");
                    lampState = state.getBoolean("on");

                    jsonResponse = "";
                    jsonResponse += "on: " + lampState + "\n\n";

                    //mTextView.setText(new String(lampState));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("Didnt work!");
            }
        });

        mRequestQueue.add(jsObjRequest);
    }

    private Boolean getLampState(){
        //Log.v("lampState", lampState);
        return lampState;
    }

}

package com.fadhliismail.physicalweb;

import android.app.Activity;
import android.content.Context;
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

public class TiltControlBrightnessColor extends Activity implements SensorEventListener {

    private static final float SHAKE_THRESHOLD = 1.1f;
    private static final int SHAKE_WAIT_TIME_MS = 250;
    //private static final float ROTATION_THRESHOLD = 2.0f;
    //private static final int ROTATION_WAIT_TIME_MS = 100;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TextView mTextValues, mTextView;
    private long mShakeTime = 0;
    //private long mRotationTime = 0;
    private Integer lampState;
    private int bright;
    private int finalBright;
    private JsonObjectRequest putRequest;


    StringBuilder builder = new StringBuilder();

    float [] g = new float[3];
    String [] direction = {"NONE","NONE"};
    int SENSOR_DELAY = 100000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rect_activity_tilt_control_brightness_color);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SENSOR_DELAY);
        //getRequest();

        mTextValues = (TextView) findViewById(R.id.text_values);
        mTextView = (TextView) findViewById(R.id.text_view);
//        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
//        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
//            @Override
//            public void onLayoutInflated(WatchViewStub stub) {
//
//
//
//
//
//
//            }
//        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
        {
            return;
        }

        g = event.values.clone();
        float norm_g = (float) Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]);

        //normalize accelerometer vector
        g[0] = g[0] / norm_g;
        g[1] = g[1] / norm_g;
        g[2] = g[2] / norm_g;

        int inclination1 = (int) Math.round(Math.toDegrees(Math.acos(g[1])));
        int inclination2 = (int) Math.round(Math.toDegrees(Math.acos(g[0])));


        //getting current lampBrightness

        if(inclination1 > 130){
            direction[0] = "Depan";



            //brightness
            RequestQueue mRequestQueue;

            // Instantiate the cache
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());



            for (bright = 0 ; bright <= 255 ; bright++ ){
                finalBright = bright;
            }

            // Instantiate the RequestQueue with the cache and network.
            mRequestQueue = new RequestQueue(cache, network);

            // Start the queue
            mRequestQueue.start();


            String url = "http://192.168.1.3/api/newdeveloper/lights/1/state";

            // put request
            final JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("bri", finalBright);
            } catch (JSONException e) {
                // handle exception
            }


            putRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            Log.d("Response", response.toString());
                            //txtDisplay.setText("Response : " + response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.toString());
                            //txtDisplay.setText("Didnt work!");
                        }
                    }
            ) {

                @Override
                public Map<String, String> getHeaders() {
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
        else if (inclination1 < 40) {
            direction[0] = "Belakang";



            //brightness
            RequestQueue mRequestQueue;

            // Instantiate the cache
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());



            for (bright = 255 ; bright >= 0 ; bright--){
                finalBright = bright;

            }

            // Instantiate the RequestQueue with the cache and network.
            mRequestQueue = new RequestQueue(cache, network);

            // Start the queue
            mRequestQueue.start();

            //finalBright = bright;
            String url = "http://192.168.1.3/api/newdeveloper/lights/1/state";

            // put request
            final JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("bri", finalBright);
            } catch (JSONException e) {
                // handle exception
            }


            putRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            Log.d("Response", response.toString());
                            //txtDisplay.setText("Response : " + response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.toString());
                            //txtDisplay.setText("Didnt work!");
                        }
                    }
            ) {

                @Override
                public Map<String, String> getHeaders() {
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
        else {
            direction[0] = "Stabil";
        }

        if(inclination2 > 130) {
            direction[1] = "Kanan";
        }

        else if(inclination2 < 40) {
            direction[1] = "Kiri";
        }

        else{
            direction[1] = "Stabil";
        }

        builder.setLength(0);
        builder.append(inclination1);
        builder.append(direction[0]);
        builder.append("\n");
        builder.append(inclination2);
        builder.append(direction[1]);

        mTextValues.setText(builder.toString());

//        mTextValues.setText(
//                "x = " + Float.toString(event.values[0]) + "\n" +
//                        "y = " + Float.toString(event.values[1]) + "\n" +
//                        "z = " + Float.toString(event.values[2]) + "\n"
//        );

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

     /*private void detectShake(SensorEvent event) {

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
            if (gForce > SHAKE_THRESHOLD) {

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
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // response
                                    Log.d("Response", response.toString());
                                    //txtDisplay.setText("Response : " + response.toString());
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Log.d("Error.Response", error.toString());
                                    //txtDisplay.setText("Didnt work!");
                                }
                            }
                    ) {

                        @Override
                        public Map<String, String> getHeaders() {
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
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // response
                                    Log.d("Response", response.toString());
                                    //txtDisplay.setText("Response : " + response.toString());
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Log.d("Error.Response", error.toString());
                                    //txtDisplay.setText("Didnt work!");
                                }
                            }
                    ) {

                        @Override
                        public Map<String, String> getHeaders() {
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

            } else {
                //mView.setBackgroundColor(Color.BLACK);
            }
        }
    }
*/

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
                    lampState = state.getInt("brightness");

                    jsonResponse = "";
                    jsonResponse += "brightness: " + lampState + "\n\n";

                    //mTextView.setText(new String(lampState));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("Didnt work!");
            }
        });

        mRequestQueue.add(jsObjRequest);
    }

    private Integer getLampState(){
        //Log.v("lampState", lampState);
        return lampState;
    }

}

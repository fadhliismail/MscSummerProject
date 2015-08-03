package com.fadhliismail.physicalweb;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private TextView mTextView;

    private String urlFinal;
    private Button goToShakeOnOff, goToShakeBrightColor, buttonOn, buttonOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);

                getUrlFinal();

                buttonOff = (Button) findViewById(R.id.buttonOff);

                goToShakeOnOff = (Button) findViewById(R.id.goToShakeOnOff);

                goToShakeOnOff.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, ShakeTurnOn.class);
                        startActivity(intent);
                    }

                });

                goToShakeBrightColor = (Button) findViewById(R.id.goToShakeBrightColor);

                goToShakeBrightColor.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, TiltControlBrightnessColor.class);
                        startActivity(intent);
                    }

                });




                buttonOff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        RequestQueue mRequestQueue;

                        // Instantiate the cache
                        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

                        // Set up the network to use HttpURLConnection as the HTTP client.
                        Network network = new BasicNetwork(new HurlStack());

                        // Instantiate the RequestQueue with the cache and network.
                        mRequestQueue = new RequestQueue(cache, network);

                        // Start the queue
                        mRequestQueue.start();


                        //RequestQueue queue = Volley.newRequestQueue(this);
                        String url = urlFinal;

                        // put request
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
                        //queue.add(putRequest);

                        //getRequest();
                    }
                });

                buttonOn = (Button) findViewById(R.id.buttonOn);

                buttonOn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //sendTrueRequest();


                        RequestQueue mRequestQueue;

                        // Instantiate the cache
                        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

                        // Set up the network to use HttpURLConnection as the HTTP client.
                        Network network = new BasicNetwork(new HurlStack());

                        // Instantiate the RequestQueue with the cache and network.
                        mRequestQueue = new RequestQueue(cache, network);

                        // Start the queue
                        mRequestQueue.start();

                        String url = urlFinal;

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

                        //getRequest();
                    }
                });

                // seekbar brightness

                SeekBar seekBar;
                final TextView textView;

                seekBar = (SeekBar) findViewById(R.id.seekBar1);
                textView = (TextView) findViewById(R.id.textView1);


                textView.setText("Covered: " + seekBar.getProgress() + "/" + seekBar.getMax());

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                        progress = progresValue;
                        //Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
                        textView.setText("Covered: " + progress + "/" + seekBar.getMax());

                        RequestQueue mRequestQueue;

                        // Instantiate the cache
                        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

                        // Set up the network to use HttpURLConnection as the HTTP client.
                        Network network = new BasicNetwork(new HurlStack());

                        // Instantiate the RequestQueue with the cache and network.
                        mRequestQueue = new RequestQueue(cache, network);

                        // Start the queue
                        mRequestQueue.start();
                        String url = urlFinal;
                        Log.i("url", url);

                        // put request
                        final JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("bri", progress);
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

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //textView.setText("Covered: " + progress + "/" + seekBar.getMax());
                        //Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
                    }
                });

                // seekbar saturation(color)
                SeekBar seekBar1;
                final TextView textView1;

                seekBar1 = (SeekBar) findViewById(R.id.seekBar2);
                textView1 = (TextView) findViewById(R.id.textView2);


                textView1.setText("Covered: " + seekBar1.getProgress() + "/" + seekBar1.getMax());

                seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar1, int progresValue, boolean fromUser) {
                        progress = progresValue;
                        //Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
                        textView1.setText("Covered: " + progress + "/" + seekBar1.getMax());

                        RequestQueue mRequestQueue;

                        // Instantiate the cache
                        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

                        // Set up the network to use HttpURLConnection as the HTTP client.
                        Network network = new BasicNetwork(new HurlStack());

                        // Instantiate the RequestQueue with the cache and network.
                        mRequestQueue = new RequestQueue(cache, network);

                        // Start the queue
                        mRequestQueue.start();
                        String url = urlFinal;

                        // put request
                        final JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("hue", progress);
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

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //textView.setText("Covered: " + progress + "/" + seekBar.getMax());
                        //Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            urlFinal = intent.getStringExtra("message");
            Log.v("myTag", "Main activity received message: " + urlFinal);
            // Display message in UI

            Map<String, String> map = new HashMap<>();
            map.put("http://192.168.1.3/api/newdeveloper/lights/1/state", "Hue Lamp 1");
            map.put("http://192.168.1.3/api/newdeveloper/lights/3/state", "Hue Lamp 3");;

            String urlTag = map.get(urlFinal);

            mTextView.setText(urlTag);

        }
    }

    public String getUrlFinal() {
        return urlFinal;
    }

}
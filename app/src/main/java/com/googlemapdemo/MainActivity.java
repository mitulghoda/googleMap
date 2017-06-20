package com.googlemapdemo;

import android.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.googlemapdemo.Utils.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String LOG_TAG = "Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyDlOJO2WpgIZRGSKTU_U221cwjj2_-nSwE";
    TextView current,multiple;
    AutoCompleteTextView autoCompView;
    Double latitude = 0.0, longitude = 0.0,latitude2 = 0.0, longitude2 = 0.0;
    private int CAM_PERMISSION_CODE = 24;
    private GoogleMap map;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543,
            -73.998585);
    private static final LatLng TIMES_SQUARE = new LatLng(40.7577, -73.9857);
    private static final LatLng BROOKLYN_BRIDGE = new LatLng(40.7057, -73.9964);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idMapping();
        if(iscameraallowed()){
            getLatAndLong();

        }
        else {
            requestCamera();
        }
//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                // checking for type intent filter
//                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
//                    // gcm successfully registered
//                    // now subscribe to `global` topic to receive app wide notifications
//                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
//
//                    displayFirebaseRegId();
//
//                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
//
//
//                    String message = intent.getStringExtra("message");
//
//
//
//
//                }
//            }
//        };

        setonClick();




    }



    private void idMapping() {
        current=(TextView)findViewById(R.id.current);
        multiple=(TextView)findViewById(R.id.multiple);

        autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(MainActivity.this, R.layout.list_item));
        autoCompView.setOnItemClickListener(this);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();


    }
    private void getLatAndLong() {


        LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return;
        }


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        Location myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (myLocation == null) {
            showSettingsAlert("NETWORK");
            return;


        } else {


            map.setMyLocationEnabled(true);
            map.getUiSettings().setRotateGesturesEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.getUiSettings().setIndoorLevelPickerEnabled(true);
            MapsInitializer.initialize(MainActivity.this);
            longitude = myLocation.getLongitude();
            latitude = myLocation.getLatitude();
            Log.e("latitude",""+latitude);
            Log.e("longitude",""+longitude);
            final LatLng LOCATION = new LatLng(latitude, longitude);
            Marker location = map.addMarker(new MarkerOptions()
                    .position(LOCATION)
                    .title("Dis")

            );

            final CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(LOCATION)      // Sets the center of the map to Mountain View
                    .zoom(5)
                    .build();


            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LOCATION, 12);
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            getCity();




        }











    }

    private void getCity() {



        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        //List<Address> addresses =geocoder.getFromLocation(latitude, longitude, 1);

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getSubLocality();
            String cityName = addresses.get(0).getLocality();
            String stateName = addresses.get(0).getAdminArea();
//            txt_paddress.setText(address);
//            txt_city.setText(cityName);
//            txt_state.setText(stateName);
            Log.e("address",""+address);
            Log.e("cityName",""+cityName);
            Log.e("stateName",""+stateName);
            autoCompView.setText(""+address+","+cityName+","+stateName);


        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage("GPS is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MainActivity.this.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void setonClick()
    {

        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iscameraallowed()){
                    getLatAndLong();
                }
                else {
                    requestCamera();
                }

            }
        });

        multiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                map
//                        .addPolyline((new PolylineOptions())
//                                .add(TIMES_SQUARE, BROOKLYN_BRIDGE, LOWER_MANHATTAN,
//                                        TIMES_SQUARE).width(5).color(Color.RED)
//                                .geodesic(true));
//                // move camera to zoom on map
//                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LOWER_MANHATTAN,
//                        5));
                map.clear();


                longitude = 23.0134;
                latitude = 72.5624;
                Log.e("latitude",""+latitude);
                Log.e("longitude",""+longitude);
                final LatLng LOCATION = new LatLng(latitude, longitude);
                Marker location = map.addMarker(new MarkerOptions()
                        .position(LOCATION)
                         .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                        .title("Dis")

                );


                final CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(LOCATION)      // Sets the center of the map to Mountain View
                        .zoom(5)
                        .build();


                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LOCATION, 12);
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));





                longitude2 = 23.0120;
                latitude2 = 72.5108;
                Log.e("latitude",""+latitude2);
                Log.e("longitude",""+longitude2);
                final LatLng LOCATION2 = new LatLng(latitude2, longitude2);
                Marker location2 = map.addMarker(new MarkerOptions()
                        .position(LOCATION2)
                        .title("2")

                );





            }
        });
    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        if(autoCompView.getText().toString().length()!=0){
            Geocoder coder = new Geocoder(MainActivity.this);
            List<Address> addresses;
            try {
                addresses = coder.getFromLocationName(autoCompView.getText().toString(), 5);
                if (addresses == null) {
                }
                Address location = addresses.get(0);
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                Log.i("Lat",""+lat);
                Log.i("Lng",""+lng);
                LatLng latLng = new LatLng(lat,lng);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
              sb.append("&types=address");
           // sb.append("&types=(cities)");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            Log.e("url",""+url.toString());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannots", e);
        }

        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return (String) resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    private boolean iscameraallowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }




    private void requestCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},CAM_PERMISSION_CODE);
    }
    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == CAM_PERMISSION_CODE  ){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast

                getLatAndLong();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(MainActivity.this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }

}

package iak4.com.restmap;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.sql.Connection;

public class Add extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener{

    Marker marker = null;
    EditText edt;
    GoogleMap map;
    ProgressBar pb;
    HttpURLConnection connection;
    LatLng lokasi;
    String judul;

    PushData pushData;
    void reload(){
        pushData = new PushData();
        pb.setVisibility(View.VISIBLE);
        pushData.execute("http://192.168.1.1/restmap/add.php");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        edt = (EditText) findViewById(R.id.editText);
        pb = (ProgressBar) findViewById(R.id.pb_add);
        Button btn = (Button) findViewById(R.id.btn_add);

        SupportMapFragment mp = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapX);
        mp.getMapAsync(this);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                judul = edt.getText().toString();
                reload();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tambah Lokasi");


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng koordinat = new LatLng(-7.7887848, 110.4083342);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(koordinat, 15));
        map = googleMap;
        map.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(marker != null){
            marker.remove();
        }
        Log.d("ini", "onMapClick: "+latLng.latitude+" "+latLng.longitude);
        lokasi = latLng;
        marker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(edt.getText().toString()));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class PushData extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String charset = "UTF-8";
                String data = "judul=" + URLEncoder.encode(judul, charset);
                data += "&lat=" + URLEncoder.encode(String.valueOf(lokasi.latitude), charset);
                data += "&lng=" + URLEncoder.encode(String.valueOf(lokasi.longitude), charset);

                connection.setFixedLengthStreamingMode(data.getBytes().length);
                PrintWriter printWriter= new PrintWriter(connection.getOutputStream());
                printWriter.print(data);
                printWriter.close();
                connection.connect();

                int status = connection.getResponseCode();
                if(status == 200){
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    sb.append(br.readLine()).append("\n");
                    br.close();

                    JSONObject callback = new JSONObject(sb.toString());
                    if (callback.getInt("status") == 1) {
                        return true;
                    }else{
                        return false;
                    }
                }

            }catch (UnknownHostException abc){
                if(connection != null){
                    connection.disconnect();
                    return false;
                }
            }catch (Exception abc){
                if(connection != null){
                    connection.disconnect();
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            pb.setVisibility(View.GONE);
            if(aBoolean){
                Toast.makeText(Add.this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Add.this, MainActivity.class));
            }else{
                Toast.makeText(Add.this, "Tidak berhasil menambahkan data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

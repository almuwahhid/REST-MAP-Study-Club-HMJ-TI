package iak4.com.restmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Detail extends AppCompatActivity implements OnMapReadyCallback {
    String title;
    Double lat, lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent data = getIntent();
        lat = data.getDoubleExtra("lat", 0);
        lng = data.getDoubleExtra("lng", 0);
        title = data.getStringExtra("title");

        SupportMapFragment mp = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapF);
        mp.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Map");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng koordinat = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions()
                .position(koordinat)
                .title(title));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(koordinat, 15));
    }
}

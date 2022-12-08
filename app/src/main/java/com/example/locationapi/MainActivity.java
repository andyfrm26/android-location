package com.example.locationapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private TextView tvNegara, tvProvinsi, tvKota, tvAlamat;
    private Button btnGetLocation;
    private LinearLayout llLocation;
    private final static int LOC_REQUEST_CODE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        tvNegara = findViewById(R.id.tv_negara);
        tvProvinsi = findViewById(R.id.tv_provinsi);
        tvKota = findViewById(R.id.tv_kota);
        tvAlamat = findViewById(R.id.tv_alamat);
        btnGetLocation = findViewById(R.id.btn_get_location);
        llLocation = findViewById(R.id.ll_location);

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
    }

    private void getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> addressList;
                                try {
                                    addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    setLocation(addressList);
                                    llLocation.setVisibility(View.VISIBLE);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        } else {
            String[] requests = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(MainActivity.this, requests, LOC_REQUEST_CODE);
        }
    }

    private void setLocation(List<Address> addressList) {
        tvNegara.setText("Negara: " + addressList.get(0).getCountryName());
        tvProvinsi.setText("Provinsi: " + addressList.get(0).getAdminArea());
        tvKota.setText("Kota: " + addressList.get(0).getSubAdminArea());
        tvAlamat.setText("Alamat: " + addressList.get(0).getAddressLine(0));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==LOC_REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission Error!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
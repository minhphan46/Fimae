package com.example.fimae.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fimae.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

public class MapPicker extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MapView mapView;
    private GoogleMap googleMap;
    Marker selectedMarker;
    FusedLocationProviderClient fusedLocationClient;
    OnMapReadyCallback callback;

    private boolean isPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);
        ProgressBar progressBar = findViewById(R.id.pick_progress_bar);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Button btnCurrentLocation = findViewById(R.id.btn_current_location);
        Button btnSelectLocation = findViewById(R.id.btn_selected_location);
        btnCurrentLocation.setOnClickListener(v -> {
            if (googleMap != null) {
                checkLocationPermission();
            }
            if (isPermissionGranted()) {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Location location = task.getResult();
                            if (location != null) {
                                LatLng selectedLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("selectedLatLng", selectedLatLng);
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Không thể xác định vị trí hiện tại.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(ProgressBar.VISIBLE);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Không thể xác định vị trí hiện tại.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(ProgressBar.VISIBLE);
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Vui lòng cấp quyền truy cập vị trí.", Toast.LENGTH_SHORT).show();
            }
        });

        btnSelectLocation.setOnClickListener(v -> {
            if (googleMap != null) {
                if (selectedMarker != null) {
                    LatLng selectedLatLng = selectedMarker.getPosition();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selectedLatLng", selectedLatLng);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Vui lòng chọn vị trí trên bản đồ.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        callback = gmap -> {
            googleMap = gmap;
            checkLocationPermission();
            // Enable other map settings and functionalities as needed
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull @NotNull LatLng latLng) {
                    if (selectedMarker != null) {
                        selectedMarker.remove();
                    }
                    btnSelectLocation.setVisibility(Button.VISIBLE);
                    selectedMarker = googleMap.addMarker(new MarkerOptions().position(latLng));
                }
            });
        };
        mapView.getMapAsync(callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            enableMyLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                // Handle the case where location permission is denied
                Toast.makeText(getApplicationContext(), "Vui lòng cấp quyền truy cập vị trí.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
    }
}

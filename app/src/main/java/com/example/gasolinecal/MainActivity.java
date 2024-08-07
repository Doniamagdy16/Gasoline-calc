package com.example.gasolinecal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mumayank.com.airlocationlibrary.AirLocation;

public class MainActivity extends AppCompatActivity implements TextWatcher, AirLocation.Callback {
    EditText PlaceText;
    TextView resultText,added_places;
    Button addButton,calcButton;
    ImageView location_icon;
    AirLocation airLocation;
    Location location1;
    ArrayList<String>places=new ArrayList<>();
    ArrayList<String>visited=new ArrayList<>();
    ArrayList<Double>final_distances=new ArrayList<>();
    ArrayList<Station>distances=new ArrayList<>();
    String place="";
    boolean loc=false;
    String addressLine="";
    boolean calc=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PlaceText=findViewById(R.id.PlaceText);
        addButton=findViewById(R.id.addButton);
        calcButton=findViewById(R.id.calcButton);
        location_icon=findViewById(R.id.locatio_icon);
        resultText=findViewById(R.id.resultText);
        added_places=findViewById(R.id.addes_places);
        PlaceText.addTextChangedListener(this);
        places.clear();
        distances.clear();
        visited.clear();
        loc=true;
        airLocation=new AirLocation(this,this,true,0,"");
        airLocation.start();
    }
    public void add(View view) {
        place=PlaceText.getText().toString();
        if (!place.equals("")) {
            places.add(place);
            added_places.append(place+"\n");
            PlaceText.setText("");
        }
    }
    public void calc(View view) {
        resultText.setText("");
        visited.clear();
        added_places.setText("");
        calc=true;
        airLocation=new AirLocation(this,this,true,0,"");
        airLocation.start();

         }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.length()!=0){
            addButton.setEnabled(true);
            resultText.setText("");
        }
        else if(s.length()==0) addButton.setEnabled(false);
        if (places.size() > 1) {
            calcButton.setEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        airLocation.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onFailure(@NonNull AirLocation.LocationFailedEnum locationFailedEnum) {
        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(@NonNull ArrayList<Location> arrayList) {
        Geocoder geocoder = new Geocoder(this);
        if (loc == true && !arrayList.isEmpty() && arrayList != null) {
            double latitude = arrayList.get(0).getLatitude();
            double longitude = arrayList.get(0).getLongitude();
            try {
                List<Address> fromLocation = geocoder.getFromLocation(latitude, longitude, 1);
                 addressLine = fromLocation.get(0).getSubAdminArea();
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + latitude+","+longitude));
//                startActivity(intent);
                visited.add(addressLine);
                PlaceText.setText(addressLine);
                loc = false;
                location_icon.setEnabled(false);
            //    location_icon.setEnabled(false);
            } catch (IOException e) {
                Toast.makeText(this, "connection error", Toast.LENGTH_SHORT).show();
            }
        }
        //}
        else place = places.get(0);

        if (calc == true) {
            final_distances.clear();
            for (int i = 0; i < places.size(); i++) {
                try {
                    List<Address> address1 = geocoder.getFromLocationName(place, 1);
                    if (!address1.isEmpty() && address1 != null) {
                        double latitude1 = address1.get(0).getLatitude();
                        double longitude1 = address1.get(0).getLongitude();
                        location1 = new Location("");
                        location1.setLatitude(latitude1);
                        location1.setLongitude(longitude1);
                    } else
                        Toast.makeText(this, "cannot find this place please make sure from spilling", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(this, "internet disconnected", Toast.LENGTH_SHORT).show();
                }
                for (int j = 0; j < places.size(); j++) {
                    if (!visited.contains(places.get(j))) {
                        try {
                            List<Address> address2 = geocoder.getFromLocationName(places.get(j), 1);
                            if (!address2.isEmpty() && address2 != null) {
                                double latitude2 = address2.get(0).getLatitude();
                                double longitude2 = address2.get(0).getLongitude();
                                Location location2 = new Location("");
                                location2.setLatitude(latitude2);
                                location2.setLongitude(longitude2);
                                double distance = location1.distanceTo(location2);
//                          Haversine algo
//                                final int R = 6371;
//                                double latDistance = Math.toRadians(latitude2 - location1.getLatitude());
//                                double lonDistance = Math.toRadians(longitude2 - location1.getLongitude());
//                                double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
//                                        Math.cos(Math.toRadians(location1.getLatitude())) * Math.cos(Math.toRadians(latitude2)) *
//                                                Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
//                                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//                                double distance = R * c;
                                // resultText.append("dddd "+distance)
                                Station station = new Station();
                                station.setName(places.get(j));
                                station.setDistance((distance)/1000);
                                distances.add(station);
                                //distences.add((double) location1.distanceTo(location2));
                            } else
                                Toast.makeText(this, "cannot find this place please make sure from spilling", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Toast.makeText(this, "internet disconnected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if (!distances.isEmpty()) {
                    Optional<Station> min = distances.stream().min(Comparator.comparingDouble(value -> value.getDistance()));
                    final_distances.add((min.get().getDistance()));
                    distances.clear();
                    place = min.get().getName();
                    visited.add(place);
                    loc = true;
                    location_icon.setEnabled(true);
                }
            }
            if(visited.size()>1) {
                resultText.append("the routes with min distances are " + visited.toString() + "\n");
                Optional<Double> distance = final_distances.stream().reduce((aDouble, aDouble2) -> aDouble + aDouble2);
                distance.ifPresent(aDouble -> {
                    int d = (((int) Math.round(aDouble)) * 2) + 6;
                    resultText.append("distance is almost " + d + " km" + "\n");
                    d = d / 2;
                    int coast = d * 8;
                    resultText.append("cost is almost " + coast + " pounds" + " for gasoline");
                });
            }
            else Toast.makeText(this, "the places should be greater than one place", Toast.LENGTH_SHORT).show();
            places.clear();
            calcButton.setEnabled(false);
        }
        calc=false;
    }
    public void loc_icon(View view) {
        loc=true;
        airLocation=new AirLocation(this,this,true,0,"");
        airLocation.start();
    }
}
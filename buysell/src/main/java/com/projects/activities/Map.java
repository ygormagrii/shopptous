package com.projects.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.facebook.Session;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.projects.buysell.R;

public class Map extends Activity implements OnMapReadyCallback {

    private String lat,lon;
    private float lat_ofc, lon_ofc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        Bundle bundle = getIntent().getExtras();
        lat = bundle.getString("lat");
        lon = bundle.getString("lon");

        lat_ofc = Float.parseFloat(lat);
        lon_ofc = Float.parseFloat(lon);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapview);
        mapFragment.getMapAsync(Map.this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng sydney = new LatLng(lat_ofc, lon_ofc);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.meuPerfil:
                UserAccessSession userAccess = UserAccessSession.getInstance(Map.this);
                UserSession userSession = userAccess.getUserSession();
                Bundle b = new Bundle();
                int user_id = userSession.getUser_id();
                b.putInt("user_id", user_id);
                Intent telaSecundaria = new Intent(getApplicationContext(), ProfileActivity.class);
                telaSecundaria.putExtras(b);
                startActivity(telaSecundaria);
                finish();

                break;
            case R.id.voltar:
                Intent intent2 = new Intent(getApplicationContext(), PedidosPre.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.sairApp:
                UserAccessSession accessSession = UserAccessSession.getInstance(this);
                if (accessSession != null)
                    accessSession.clearUserSession();

                Session session = Session.getActiveSession();
                if (session != null) { // not logged in
                    session.closeAndClearTokenInformation();
                }

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_form_pedido, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        // if nav drawer is opened, the action items
        return super.onPrepareOptionsMenu(menu);
    }
}
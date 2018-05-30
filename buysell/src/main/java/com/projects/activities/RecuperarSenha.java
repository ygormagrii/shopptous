package com.projects.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.projects.buysell.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Ygor on 23/02/2016.
 */
public class RecuperarSenha extends Activity {
    private EditText edtEmail;
    String email;
    private ProgressDialog pDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.recuperar_senha);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        this.getActionBar().setTitle("Shopptous");

        Button btnRec = (Button) findViewById(R.id.btnSenha);
        edtEmail = (EditText) findViewById(R.id.txtEmailrec);

        btnRec.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){

                email = edtEmail.getText().toString();

                new recuperaSenha().execute();
            }
        });
    }

    class recuperaSenha extends android.os.AsyncTask<String, Integer, String> {
        private StringBuilder sb;
        private ProgressDialog pr;
        private HttpResponse req;
        private InputStream is;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RecuperarSenha.this);
            pDialog.setMessage("Reenviando sua senha ...");
            pDialog.setIndeterminate(false); pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            UserAccessSession userAccess = UserAccessSession.getInstance(RecuperarSenha.this);
            UserSession userSession = userAccess.getUserSession();
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("email", email));

            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://shopptous.com/recuperar_senha.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                InputStreamReader ireader = new InputStreamReader(is);
                BufferedReader bf = new BufferedReader(ireader);
                sb = new StringBuilder();
                String line = null;
                while ((line = bf.readLine()) != null) {
                    sb.append(line);
                }
                Log.e("pass 1", "connection success ");
            }
            catch(Exception e)
            {
                Log.e("Fail 1", e.toString());
                Toast.makeText(getApplicationContext(), "Invalid IP Address",
                        Toast.LENGTH_LONG).show();
            }
            return email;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Sua senha foi enviada para seu e-mail."+email, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(RecuperarSenha.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        // if nav drawer is opened, hide the action items
        return super.onPrepareOptionsMenu(menu);
    }


}

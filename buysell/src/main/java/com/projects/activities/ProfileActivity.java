package com.projects.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.libraries.asynctask.MGAsyncTask;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.projects.buysell.MainActivity;
import com.projects.buysell.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ProfileActivity extends Activity {

	UserSession user;
	String fullName = null;
	String password = null;
	String email = null;
	String username = null;
    private ProgressDialog pDialog;
    private EditText txtFullName,txtUsername,txtEmail;
    String user_id;
    InputStream is=null;
    String result=null;
    String line=null;
    ArrayList<String> fullName_i = new ArrayList<String>();
    ArrayList<String> email_i = new ArrayList<String>();
    ArrayList<String> username_i = new ArrayList<String>();

    MGAsyncTask task;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		setContentView(R.layout.fragment_profile);

		this.getActionBar().setTitle("Shopptous");

        txtFullName=(EditText) findViewById(R.id.txtFullName);
        txtUsername=(EditText) findViewById(R.id.txtUsername);
        txtEmail=(EditText) findViewById(R.id.txtEmail);

        new select().execute();

        Button btnRegister = (Button) this.findViewById(R.id.btnRegister);

        // Apresenta dados do usuário
        /*
        txtFullName.setText("Ygor Magri", TextView.BufferType.EDITABLE);
        txtEmail.setText("ygormagrii@gmail.com", TextView.BufferType.EDITABLE);
        txtUsername.setText("ygormagrii", TextView.BufferType.EDITABLE);
        txtPassword.setText("magrii123", TextView.BufferType.EDITABLE);
        */

        /*
		String fullName = user.getFull_name();
		if(fullName == null) {
			fullName = String.format("%s-%s",
					MGUtilities.getStringFromResource(this, R.string.user),
					user.getUser_id());
		}

		else if(fullName != null && fullName.contains("null")) {
			fullName = String.format("%s-%s",
					MGUtilities.getStringFromResource(this, R.string.user),
					user.getUser_id());
		}

		txtFullName.setText( fullName );
		txtPassword.setText( "" );
		if( (user.getFacebook_id() != null && user.getFacebook_id().length() > 0) ||
				(user.getTwitter_id() != null && user.getTwitter_id().length() > 0) ) {
			txtPassword.setVisibility(View.INVISIBLE);
		} */

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtEmail.getText().toString().length() > 0 && txtUsername.getText().toString().length() != 0 && txtFullName.getText().toString().length() != 0) {
                    username = txtUsername.getText().toString();
                    email = txtEmail.getText().toString();
                    fullName = txtFullName.getText().toString();

                    new loadData().execute();
                    Toast.makeText(ProfileActivity.this, "Informações atualizadas com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    if (txtEmail.getText().length() == 0) {//como o tamanho é zero é nulla aresposta
                        txtEmail.setError("Campo vazio");
                    }

                    if (txtFullName.getText().length() == 0) {//como o tamanho é zero é nulla aresposta
                        txtFullName.setError("Campo vazio");
                    }

                    if (txtUsername.getText().length() == 0) {//como o tamanho é zero é nulla aresposta
                        txtUsername.setError("Campo vazio");
                    }
                }
            }
        });
    }

    public class select extends AsyncTask<String, Integer, String> {

        private StringBuilder sb;
        private ProgressDialog pr;
        private HttpResponse req;
        private InputStream is;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Captando ...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            UserAccessSession userAccess = UserAccessSession.getInstance(ProfileActivity.this);
            UserSession userSession = userAccess.getUserSession();

            Intent intent = getIntent();
            int user_id = intent.getIntExtra("user_id", 1);
            String user_str = String.valueOf(user_id);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("user_id", user_str));

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://shopptous.comusers_show.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                Log.e("pass 1", "connection success ");
            } catch (Exception e) {
                Log.e("Fail 1", e.toString());
                Toast.makeText(getApplicationContext(), "Invalid IP Address",
                        Toast.LENGTH_LONG).show();
            }

            try {
                BufferedReader reader = new BufferedReader
                        (new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();
                Log.e("pass 2", "connection success ");
            } catch (Exception e) {
                Log.e("Fail 2", e.toString());
            }

            try {

                JSONObject json_data = new JSONObject(result);
                JSONArray arr =  json_data.getJSONArray("message");
                for (int i=0; i < arr.length(); i++) {
                    JSONObject  json_dat = arr.getJSONObject(i);

                    fullName_i.add((json_dat.getString("full_name")));
                    username_i.add((json_dat.getString("username")));
                    email_i.add((json_dat.getString("email")));
                }


            } catch (Exception e) {
                Log.e("Fail 3", e.toString());
            }
            return user_str;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

                txtFullName.setText(fullName_i.get(0), TextView.BufferType.EDITABLE);
                txtEmail.setText(email_i.get(0), TextView.BufferType.EDITABLE);
                txtUsername.setText(username_i.get(0), TextView.BufferType.EDITABLE);

            }

        }

    class loadData extends android.os.AsyncTask<String, Integer, String> {
        private StringBuilder sb;
        private ProgressDialog pr;
        private HttpResponse req;
        private InputStream is;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProfileActivity.this);
            pDialog.setMessage("Enviando atualizações ...");
            pDialog.setIndeterminate(false); pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            UserAccessSession userAccess = UserAccessSession.getInstance(ProfileActivity.this);
            UserSession userSession = userAccess.getUserSession();

            Intent intent = getIntent();
            int user_id = intent.getIntExtra("user_id", 1);
            String user_str = String.valueOf(user_id);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();


            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("full_name", fullName));
            nameValuePairs.add(new BasicNameValuePair("user_id", user_str));

            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://shopptous.com/user_update.php");
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

            return user_str;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Dados atualizados com sucesso.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.voltar:
                Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
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
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        // if nav drawer is opened, the action items
        return super.onPrepareOptionsMenu(menu);
    }

}

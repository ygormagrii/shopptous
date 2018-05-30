package com.projects.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.db.DbHelper;
import com.db.Queries;
import com.facebook.Session;
import com.libraries.cache.util.AsyncTask;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.projects.buysell.MainActivity;
import com.projects.buysell.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ygor on 17/02/2016.
 */
public class FormPedidosEnd extends Activity {

    private Spinner spn1;
    private Spinner spn2;
    private List<String> categorias = new ArrayList<String>();
    private String nome;
    private String nomeUnidade;
    private EditText edtDescricao;
    private EditText edtQtd;
    private static SQLiteDatabase db;
    private static DbHelper dbHelper;
    private List<String> edtUnidade = new ArrayList<String>();
    private static Queries q;
    public Queries getQueries() {
        return q;
    }
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    //to determine JSON signal insert success/fail
    private int success;
    // url to insert new idiom (change accordingly)
    private static String url_insert_new = "http://shopptous.com/pedidos_insert.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.form_pedidostwo);

        this.getActionBar().setTitle("Shopptous");

        dbHelper = new DbHelper(this);
        q = new Queries(db, dbHelper);
        final Queries q = getQueries();

        //Identifica o Spinner no layout
        ArrayList<String> categories = q.getCategoryNames();
        String allCategories = "Todas categorias";
        categories.add(0, allCategories);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                FormPedidosEnd.this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spn1 = (Spinner) findViewById(R.id.spinnerCategories);
        spn1.setAdapter(dataAdapter);

        //Método do Spinner para capturar o item selecionado
        spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                //pega nome pela posição
                nome = parent.getItemAtPosition(posicao).toString();
                //imprime um Toast na tela com o nome que foi selecionado
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Adicionando Nomes no ArrayList
        edtUnidade.add("Unidade - un");
        edtUnidade.add("Caixa - cx");

        //Identifica o Spinner no layout
        spn2 = (Spinner) findViewById(R.id.Unidade);
        //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, edtUnidade);
        ArrayAdapter<String> spinnerArrayAdapter2 = arrayAdapter2;
        spinnerArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spn2.setAdapter(spinnerArrayAdapter2);

        //Método do Spinner para capturar o item selecionado
        spn2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                //pega nome pela posição
                nomeUnidade = parent.getItemAtPosition(posicao).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btnEnd = (Button) findViewById(R.id.button1);
        Button btnBack = (Button) findViewById(R.id.buttonCancelar);
        edtDescricao= (EditText)findViewById(R.id.descricao);
        edtQtd = (EditText)findViewById(R.id.quantidade);


        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtQtd.getText().toString().length()>0 && edtQtd.getText().toString().length() != 0) {

                    //new InsertNewPedidos().execute();
                    if (success==1){
                        Toast.makeText(getApplicationContext(), "New idiom saved...", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "New idiom FAILED to saved...", Toast.LENGTH_LONG).show();
                    }

                    Bundle b = new Bundle();
                    b.putString("descricao", edtDescricao.getText().toString());
                    b.putString("unidade", nomeUnidade);
                    b.putString("categoria", nome);
                    Integer quantidade = Integer.parseInt(edtQtd.getText().toString());
                    b.putInt("quantidade", quantidade);

                    //Cria uma intent para abrir a outra Activity
                    Intent telaSecundaria = new Intent(getApplicationContext(), FinalizarPedido.class);
                    //adiciona os dados na instância criada

                    telaSecundaria.putExtras(b);
                    startActivity(telaSecundaria);
                    finish();
                }else{
                    if(edtDescricao.getText().length() == 0){//como o tamanho é zero é nulla aresposta
                        edtDescricao.setError("Campo vazio");
                    }

                    if(edtQtd.getText().length() == 0){//como o tamanho é zero é nulla aresposta
                        edtQtd.setError("Campo vazio");
                    }
                }

            }
        });

        class InsertNewPedidos extends AsyncTask<String, String, String> {
            //capture values from EditText
            String entry = edtDescricao.getText().toString();
            String meaning = edtQtd.getText().toString();

            /** * Before starting background thread Show Progress Dialog * */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(FormPedidosEnd.this);
                pDialog.setMessage("Saving the new IDIOM ("+entry+")...");
                pDialog.setIndeterminate(false); pDialog.setCancelable(true);
                pDialog.show();
            }
            /** * Inserting the new idiom * */
            protected String doInBackground(String... args) {

                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("entry", entry));
                params.add(new BasicNameValuePair("meaning", meaning));

                // getting JSON Object
                // Note that create product url accepts GET method
                JSONObject json = jsonParser.makeHttpRequest(url_insert_new,
                        "GET", params);

                // check log cat from response
                Log.d("Insert Novo Pedido", json.toString());

                // check for success tag
                try {
                    success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // successfully save new idiom
                    } else {
                        // failed to add new idiom

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;

            }
            /** * After completing background task Dismiss the progress dialog * **/
            protected void onPostExecute(String file_url) {
                // dismiss the dialog once done
                pDialog.dismiss();
            }
        }


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormPedidosEnd.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.meuPerfil:
                UserAccessSession userAccess = UserAccessSession.getInstance(FormPedidosEnd.this);
                UserSession userSession = userAccess.getUserSession();
                Bundle b = new Bundle();
                int user_id = userSession.getUser_id();
                b.putInt("user_id", user_id);
                Intent telaSecundaria = new Intent(getApplicationContext(), ProfileActivity.class);
                telaSecundaria.putExtras(b);
                startActivity(telaSecundaria);
                finish();
            case R.id.sairApp:
                UserAccessSession accessSession = UserAccessSession.getInstance(this);
                if(accessSession != null)
                    accessSession.clearUserSession();

                Session session = Session.getActiveSession();
                if (session != null) { // not logged in
                    session.closeAndClearTokenInformation();
                }
                finish();
            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_form_pedido, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        // if nav drawer is opened, hide the action items
        return super.onPrepareOptionsMenu(menu);
    }


}
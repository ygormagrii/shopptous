package com.projects.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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
import java.util.List;

public class InsertNewPedidos extends Activity {

    String qtd;
    String descricao;
    InputStream is=null;
    String result=null;
    String line=null;
    int code;
    private Spinner spn1;
    private Spinner spn2;
    private List<String> categorias = new ArrayList<String>();
    private String nome,usr_id;
    private String nomeUnidade;
    private int user_id;
    private static SQLiteDatabase db;
    private static DbHelper dbHelper;
    private List<String> edtUnidade = new ArrayList<String>();
    private static Queries q;
    public Queries getQueries() {
        return q;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.form_pedidostwo);
        dbHelper = new DbHelper(this);
        q = new Queries(db, dbHelper);
        final Queries q = getQueries();

        this.getActionBar().setTitle("Shopptous");
        Bundle bundle = getIntent().getExtras();
        nome = bundle.getString("nome");

        UserAccessSession userAccess = UserAccessSession.getInstance(InsertNewPedidos.this);
        UserSession userSession = userAccess.getUserSession();
        user_id = userSession.getUser_id();

        Toast.makeText(InsertNewPedidos.this, "Categoria selecionada: "+nome, Toast.LENGTH_SHORT).show();

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


        final EditText e_qtd=(EditText) findViewById(R.id.quantidade);
        final EditText e_descricao=(EditText) findViewById(R.id.descricao);
        Button btnBack = (Button) findViewById(R.id.buttonCancelar);
        Button insert=(Button) findViewById(R.id.button1);


        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(e_qtd.getText().toString().length()>0 && e_descricao.getText().toString().length() != 0) {

                    // TODO Auto-generated method stub
                    qtd = e_qtd.getText().toString();
                    descricao = e_descricao.getText().toString();
                    new loadData().execute();

                }else{
                    if(e_descricao.getText().length() == 0){//como o tamanho é zero é nulla aresposta
                        e_descricao.setError("Campo vazio");
                    }

                    if(e_qtd.getText().length() == 0){//como o tamanho é zero é nulla aresposta
                        e_qtd.setError("Campo vazio");
                    }
                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
}


    class loadData extends AsyncTask<String, Integer, String> {
        private StringBuilder sb;
        private ProgressDialog pr;
        private HttpResponse req;
        private InputStream is;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Salvando ...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            UserAccessSession userAccess = UserAccessSession.getInstance(InsertNewPedidos.this);
            UserSession userSession = userAccess.getUserSession();
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();


            nameValuePairs.add(new BasicNameValuePair("nome", nome));
            nameValuePairs.add(new BasicNameValuePair("descricao", descricao));
            nameValuePairs.add(new BasicNameValuePair("qtd",qtd));
            nameValuePairs.add(new BasicNameValuePair("nomeUnidade", nomeUnidade));
            nameValuePairs.add(new BasicNameValuePair("user_id", String.valueOf(userSession.getUser_id())));

            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://shopptous.com/pedidos_insert.php");
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
            return qtd;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "Pedido adicionado com sucesso", Toast.LENGTH_LONG).show();
            Bundle b = new Bundle();
            b.putString("nome", nome);
            Intent intent = new Intent(InsertNewPedidos.this, FinalizarPedido.class);
            intent.putExtras(b);
            startActivity(intent);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.meuPerfil:
                UserAccessSession userAccess = UserAccessSession.getInstance(InsertNewPedidos.this);
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
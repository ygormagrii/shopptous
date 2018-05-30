package com.projects.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Ygor on 23/02/2016.
 */
public class FinalizarPedido extends Activity {

    private TextView tvDescricao;
    private TextView tvQuantidade;
    private String Descricao;
    private String Unidade;
    private String Categoria;
    private String Qtd;
    private AlertDialog alerta;
    public static String id;
    private ProgressDialog pDialog;
    String user_id;
    InputStream is=null;
    String result=null;
    String line=null;
    ArrayList<String> pedidos_id = new ArrayList<String>();
    ArrayList<String> pedidos_categoria = new ArrayList<String>();
    ArrayList<String> pedidos_descricao = new ArrayList<String>();
    ArrayList<String> pedidos_qtd = new ArrayList<String>();
    ArrayList<String> pedidos_unidade = new ArrayList<String>();
    private String nome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        setContentView(R.layout.finaliza_pedido);
        this.getActionBar().setTitle("Shopptous");

        Button novoPedido = (Button) findViewById(R.id.novo_pedido);
        Button btnEnd = (Button) findViewById(R.id.buttonFim);

        Bundle bundle = getIntent().getExtras();
        nome = bundle.getString("nome");

        new select().execute();

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                builder.setTitle("Finalizando Pedido");//define o titulo
                //define um botão como positivo
                builder.setPositiveButton("Enviar Pedido", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(FinalizarPedido.this, Cep.class);
                        startActivity(intent);
                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton("Salvar Pedido", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(FinalizarPedido.this, "Pedido salvo com sucesso!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(FinalizarPedido.this, PedidosPre.class);
                        startActivity(intent);
                    }
                });
                alerta = builder.create();//cria o AlertDialog
                alerta.show();//Exibe
            }
        });


        novoPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("nome", nome);
                Intent intent = new Intent(FinalizarPedido.this, InsertNewPedidos.class);
                intent.putExtras(b);
                startActivity(intent);
                finish();
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

            UserAccessSession userAccess = UserAccessSession.getInstance(FinalizarPedido.this);
            UserSession userSession = userAccess.getUserSession();
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("user_id", String.valueOf(userSession.getUser_id())));
            nameValuePairs.add(new BasicNameValuePair("nome", nome));

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://shopptous.com/pedidos_show.php");
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

                    pedidos_id.add((json_dat.getString("pedidos_id")));
                    pedidos_categoria.add((json_dat.getString("pedidos_categoria")));
                    pedidos_descricao.add((json_dat.getString("pedidos_descricao")));
                    pedidos_qtd.add((json_dat.getString("pedidos_qtd")));
                    pedidos_unidade.add((json_dat.getString("pedidos_unidade")));

                }


            } catch (Exception e) {
                Log.e("Fail 3", e.toString());
            }
            return user_id;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            for (int i=0; i < pedidos_id.size(); i++) {

                final ListView listaPedido = (ListView) findViewById(R.id.lista);

                TextView pedidoId = (TextView) findViewById(R.id.textView7);

                if(i == 0){
                    pedidoId.setText("Resumo do Pedido");
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 1){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 2){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 3){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 4){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 5){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 6){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 7){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 8){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 9){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 10){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 11){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 12){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 13){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 14){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 15){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 16){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 17){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 18){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 19){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 20){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 21){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 22){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 23){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 24){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 25){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 26){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 27){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 28){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 29){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28),pedidos_descricao.get(29)+"  "+pedidos_qtd.get(29)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 30){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28),pedidos_descricao.get(29)+"  "+pedidos_qtd.get(29),pedidos_descricao.get(30)+"  "+pedidos_qtd.get(30)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 31){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28),pedidos_descricao.get(29)+"  "+pedidos_qtd.get(29),pedidos_descricao.get(30)+"  "+pedidos_qtd.get(30),pedidos_descricao.get(31)+"  "+pedidos_qtd.get(31)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 32){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28),pedidos_descricao.get(29)+"  "+pedidos_qtd.get(29),pedidos_descricao.get(30)+"  "+pedidos_qtd.get(30),pedidos_descricao.get(31)+"  "+pedidos_qtd.get(31),pedidos_descricao.get(32)+"  "+pedidos_qtd.get(32)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 33){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28),pedidos_descricao.get(29)+"  "+pedidos_qtd.get(29),pedidos_descricao.get(30)+"  "+pedidos_qtd.get(30),pedidos_descricao.get(31)+"  "+pedidos_qtd.get(31),pedidos_descricao.get(32)+"  "+pedidos_qtd.get(32),pedidos_descricao.get(33)+"  "+pedidos_qtd.get(33)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 34){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28),pedidos_descricao.get(29)+"  "+pedidos_qtd.get(29),pedidos_descricao.get(30)+"  "+pedidos_qtd.get(30),pedidos_descricao.get(31)+"  "+pedidos_qtd.get(31),pedidos_descricao.get(32)+"  "+pedidos_qtd.get(32),pedidos_descricao.get(33)+"  "+pedidos_qtd.get(33),pedidos_descricao.get(34)+"  "+pedidos_qtd.get(34)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 35){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28),pedidos_descricao.get(29)+"  "+pedidos_qtd.get(29),pedidos_descricao.get(30)+"  "+pedidos_qtd.get(30),pedidos_descricao.get(31)+"  "+pedidos_qtd.get(31),pedidos_descricao.get(32)+"  "+pedidos_qtd.get(32),pedidos_descricao.get(33)+"  "+pedidos_qtd.get(33),pedidos_descricao.get(34)+"  "+pedidos_qtd.get(34),pedidos_descricao.get(35)+"  "+pedidos_qtd.get(35)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 36){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28),pedidos_descricao.get(29)+"  "+pedidos_qtd.get(29),pedidos_descricao.get(30)+"  "+pedidos_qtd.get(30),pedidos_descricao.get(31)+"  "+pedidos_qtd.get(31),pedidos_descricao.get(32)+"  "+pedidos_qtd.get(32),pedidos_descricao.get(33)+"  "+pedidos_qtd.get(33),pedidos_descricao.get(34)+"  "+pedidos_qtd.get(34),pedidos_descricao.get(35)+"  "+pedidos_qtd.get(35),pedidos_descricao.get(36)+"  "+pedidos_qtd.get(36)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 37){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28),pedidos_descricao.get(29)+"  "+pedidos_qtd.get(29),pedidos_descricao.get(30)+"  "+pedidos_qtd.get(30),pedidos_descricao.get(31)+"  "+pedidos_qtd.get(31),pedidos_descricao.get(32)+"  "+pedidos_qtd.get(32),pedidos_descricao.get(33)+"  "+pedidos_qtd.get(33),pedidos_descricao.get(34)+"  "+pedidos_qtd.get(34),pedidos_descricao.get(35)+"  "+pedidos_qtd.get(35),pedidos_descricao.get(36)+"  "+pedidos_qtd.get(36),pedidos_descricao.get(37)+"  "+pedidos_qtd.get(37)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 38){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28),pedidos_descricao.get(29)+"  "+pedidos_qtd.get(29),pedidos_descricao.get(30)+"  "+pedidos_qtd.get(30),pedidos_descricao.get(31)+"  "+pedidos_qtd.get(31),pedidos_descricao.get(32)+"  "+pedidos_qtd.get(32),pedidos_descricao.get(33)+"  "+pedidos_qtd.get(33),pedidos_descricao.get(34)+"  "+pedidos_qtd.get(34),pedidos_descricao.get(35)+"  "+pedidos_qtd.get(35),pedidos_descricao.get(36)+"  "+pedidos_qtd.get(36),pedidos_descricao.get(37)+"  "+pedidos_qtd.get(37),pedidos_descricao.get(38)+"  "+pedidos_qtd.get(38)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 39){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28),pedidos_descricao.get(29)+"  "+pedidos_qtd.get(29),pedidos_descricao.get(30)+"  "+pedidos_qtd.get(30),pedidos_descricao.get(31)+"  "+pedidos_qtd.get(31),pedidos_descricao.get(32)+"  "+pedidos_qtd.get(32),pedidos_descricao.get(33)+"  "+pedidos_qtd.get(33),pedidos_descricao.get(34)+"  "+pedidos_qtd.get(34),pedidos_descricao.get(35)+"  "+pedidos_qtd.get(35),pedidos_descricao.get(36)+"  "+pedidos_qtd.get(36),pedidos_descricao.get(37)+"  "+pedidos_qtd.get(37),pedidos_descricao.get(38)+"  "+pedidos_qtd.get(38),pedidos_descricao.get(39)+"  "+pedidos_qtd.get(39)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 40){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28),pedidos_descricao.get(29)+"  "+pedidos_qtd.get(29),pedidos_descricao.get(30)+"  "+pedidos_qtd.get(30),pedidos_descricao.get(31)+"  "+pedidos_qtd.get(31),pedidos_descricao.get(32)+"  "+pedidos_qtd.get(32),pedidos_descricao.get(33)+"  "+pedidos_qtd.get(33),pedidos_descricao.get(34)+"  "+pedidos_qtd.get(34),pedidos_descricao.get(35)+"  "+pedidos_qtd.get(35),pedidos_descricao.get(36)+"  "+pedidos_qtd.get(36),pedidos_descricao.get(37)+"  "+pedidos_qtd.get(37),pedidos_descricao.get(38)+"  "+pedidos_qtd.get(38),pedidos_descricao.get(39)+"  "+pedidos_qtd.get(39),pedidos_descricao.get(40)+"  "+pedidos_qtd.get(40)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 41){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28),pedidos_descricao.get(29)+"  "+pedidos_qtd.get(29),pedidos_descricao.get(30)+"  "+pedidos_qtd.get(30),pedidos_descricao.get(31)+"  "+pedidos_qtd.get(31),pedidos_descricao.get(32)+"  "+pedidos_qtd.get(32),pedidos_descricao.get(33)+"  "+pedidos_qtd.get(33),pedidos_descricao.get(34)+"  "+pedidos_qtd.get(34),pedidos_descricao.get(35)+"  "+pedidos_qtd.get(35),pedidos_descricao.get(36)+"  "+pedidos_qtd.get(36),pedidos_descricao.get(37)+"  "+pedidos_qtd.get(37),pedidos_descricao.get(38)+"  "+pedidos_qtd.get(38),pedidos_descricao.get(39)+"  "+pedidos_qtd.get(39),pedidos_descricao.get(40)+"  "+pedidos_qtd.get(40),pedidos_descricao.get(41)+"  "+pedidos_qtd.get(41)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 42){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28),pedidos_descricao.get(29)+"  "+pedidos_qtd.get(29),pedidos_descricao.get(30)+"  "+pedidos_qtd.get(30),pedidos_descricao.get(31)+"  "+pedidos_qtd.get(31),pedidos_descricao.get(32)+"  "+pedidos_qtd.get(32),pedidos_descricao.get(33)+"  "+pedidos_qtd.get(33),pedidos_descricao.get(34)+"  "+pedidos_qtd.get(34),pedidos_descricao.get(35)+"  "+pedidos_qtd.get(35),pedidos_descricao.get(36)+"  "+pedidos_qtd.get(36),pedidos_descricao.get(37)+"  "+pedidos_qtd.get(37),pedidos_descricao.get(38)+"  "+pedidos_qtd.get(38),pedidos_descricao.get(39)+"  "+pedidos_qtd.get(39),pedidos_descricao.get(40)+"  "+pedidos_qtd.get(40),pedidos_descricao.get(41)+"  "+pedidos_qtd.get(41),pedidos_descricao.get(42)+"  "+pedidos_qtd.get(42)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 43){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28),pedidos_descricao.get(29)+"  "+pedidos_qtd.get(29),pedidos_descricao.get(30)+"  "+pedidos_qtd.get(30),pedidos_descricao.get(31)+"  "+pedidos_qtd.get(31),pedidos_descricao.get(32)+"  "+pedidos_qtd.get(32),pedidos_descricao.get(33)+"  "+pedidos_qtd.get(33),pedidos_descricao.get(34)+"  "+pedidos_qtd.get(34),pedidos_descricao.get(35)+"  "+pedidos_qtd.get(35),pedidos_descricao.get(36)+"  "+pedidos_qtd.get(36),pedidos_descricao.get(37)+"  "+pedidos_qtd.get(37),pedidos_descricao.get(38)+"  "+pedidos_qtd.get(38),pedidos_descricao.get(39)+"  "+pedidos_qtd.get(39),pedidos_descricao.get(40)+"  "+pedidos_qtd.get(40),pedidos_descricao.get(41)+"  "+pedidos_qtd.get(41),pedidos_descricao.get(42)+"  "+pedidos_qtd.get(42),pedidos_descricao.get(43)+"  "+pedidos_qtd.get(43)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 44){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28),pedidos_descricao.get(29)+"  "+pedidos_qtd.get(29),pedidos_descricao.get(30)+"  "+pedidos_qtd.get(30),pedidos_descricao.get(31)+"  "+pedidos_qtd.get(31),pedidos_descricao.get(32)+"  "+pedidos_qtd.get(32),pedidos_descricao.get(33)+"  "+pedidos_qtd.get(33),pedidos_descricao.get(34)+"  "+pedidos_qtd.get(34),pedidos_descricao.get(35)+"  "+pedidos_qtd.get(35),pedidos_descricao.get(36)+"  "+pedidos_qtd.get(36),pedidos_descricao.get(37)+"  "+pedidos_qtd.get(37),pedidos_descricao.get(38)+"  "+pedidos_qtd.get(38),pedidos_descricao.get(39)+"  "+pedidos_qtd.get(39),pedidos_descricao.get(40)+"  "+pedidos_qtd.get(40),pedidos_descricao.get(41)+"  "+pedidos_qtd.get(41),pedidos_descricao.get(42)+"  "+pedidos_qtd.get(42),pedidos_descricao.get(43)+"  "+pedidos_qtd.get(43),pedidos_descricao.get(44)+"  "+pedidos_qtd.get(44)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }else if(i == 45){
                    String[] lista = {pedidos_descricao.get(0)+"  "+pedidos_qtd.get(0),pedidos_descricao.get(1)+"  "+pedidos_qtd.get(1),pedidos_descricao.get(2)+"  "+pedidos_qtd.get(2),pedidos_descricao.get(3)+"  "+pedidos_qtd.get(3),pedidos_descricao.get(4)+"  "+pedidos_qtd.get(4),pedidos_descricao.get(5)+"  "+pedidos_qtd.get(5),pedidos_descricao.get(6)+"  "+pedidos_qtd.get(6),pedidos_descricao.get(7)+"  "+pedidos_qtd.get(7),pedidos_descricao.get(8)+"  "+pedidos_qtd.get(8),pedidos_descricao.get(9)+"  "+pedidos_qtd.get(9),pedidos_descricao.get(10)+"  "+pedidos_qtd.get(10),pedidos_descricao.get(11)+"  "+pedidos_qtd.get(11),pedidos_descricao.get(12)+"  "+pedidos_qtd.get(12),pedidos_descricao.get(13)+"  "+pedidos_qtd.get(13),pedidos_descricao.get(14)+"  "+pedidos_qtd.get(14),pedidos_descricao.get(15)+"  "+pedidos_qtd.get(15),pedidos_descricao.get(16)+"  "+pedidos_qtd.get(16),pedidos_descricao.get(17)+"  "+pedidos_qtd.get(17),pedidos_descricao.get(18)+"  "+pedidos_qtd.get(18),pedidos_descricao.get(19)+"  "+pedidos_qtd.get(19),pedidos_descricao.get(20)+"  "+pedidos_qtd.get(20),pedidos_descricao.get(21)+"  "+pedidos_qtd.get(21),pedidos_descricao.get(22)+"  "+pedidos_qtd.get(22),pedidos_descricao.get(23)+"  "+pedidos_qtd.get(23),pedidos_descricao.get(24)+"  "+pedidos_qtd.get(24),pedidos_descricao.get(25)+"  "+pedidos_qtd.get(25),pedidos_descricao.get(26)+"  "+pedidos_qtd.get(26),pedidos_descricao.get(27)+"  "+pedidos_qtd.get(27),pedidos_descricao.get(28)+"  "+pedidos_qtd.get(28),pedidos_descricao.get(29)+"  "+pedidos_qtd.get(29),pedidos_descricao.get(30)+"  "+pedidos_qtd.get(30),pedidos_descricao.get(31)+"  "+pedidos_qtd.get(31),pedidos_descricao.get(32)+"  "+pedidos_qtd.get(32),pedidos_descricao.get(33)+"  "+pedidos_qtd.get(33),pedidos_descricao.get(34)+"  "+pedidos_qtd.get(34),pedidos_descricao.get(35)+"  "+pedidos_qtd.get(35),pedidos_descricao.get(36)+"  "+pedidos_qtd.get(36),pedidos_descricao.get(37)+"  "+pedidos_qtd.get(37),pedidos_descricao.get(38)+"  "+pedidos_qtd.get(38),pedidos_descricao.get(39)+"  "+pedidos_qtd.get(39),pedidos_descricao.get(40)+"  "+pedidos_qtd.get(40),pedidos_descricao.get(41)+"  "+pedidos_qtd.get(41),pedidos_descricao.get(42)+"  "+pedidos_qtd.get(42),pedidos_descricao.get(43)+"  "+pedidos_qtd.get(43),pedidos_descricao.get(44)+"  "+pedidos_qtd.get(44),pedidos_descricao.get(45)+"  "+pedidos_qtd.get(45)};
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(FinalizarPedido.this, android.R.layout.simple_list_item_1, lista);
                    listaPedido.setAdapter(adapter1);
                }


            listaPedido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> lista, View item, int posicao, long id) {
                        listaPedido.getItemAtPosition(posicao);
                        if (posicao == 0) {
                            int var_posicao = 1;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(0) + "\nDescrição: " + pedidos_descricao.get(0) + "\nQtd: " + pedidos_qtd.get(0) + "\nUnidade: " + pedidos_unidade.get(0));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(0).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 1) {
                            int var_posicao = 2;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(1) + "\nDescrição: " + pedidos_descricao.get(1) + "\nQtd: " + pedidos_qtd.get(1) + "\nUnidade: " + pedidos_unidade.get(1));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(1).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 2) {
                            int var_posicao = 3;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(2) + "\nDescrição: " + pedidos_descricao.get(2) + "\nQtd: " + pedidos_qtd.get(2) + "\nUnidade: " + pedidos_unidade.get(2));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(2).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 3) {
                            int var_posicao = 4;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(3) + "\nDescrição: " + pedidos_descricao.get(3) + "\nQtd: " + pedidos_qtd.get(3) + "\nUnidade: " + pedidos_unidade.get(3));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(3).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 4) {
                            int var_posicao = 5;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(4) + "\nDescrição: " + pedidos_descricao.get(4) + "\nQtd: " + pedidos_qtd.get(4) + "\nUnidade: " + pedidos_unidade.get(4));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(4).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 5) {
                            int var_posicao = 6;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(5) + "\nDescrição: " + pedidos_descricao.get(5) + "\nQtd: " + pedidos_qtd.get(5) + "\nUnidade: " + pedidos_unidade.get(5));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(5).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 6) {
                            int var_posicao = 7;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(6) + "\nDescrição: " + pedidos_descricao.get(6) + "\nQtd: " + pedidos_qtd.get(6) + "\nUnidade: " + pedidos_unidade.get(6));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(6).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        }  else if (posicao == 7) {
                            int var_posicao = 8;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(7) + "\nDescrição: " + pedidos_descricao.get(7) + "\nQtd: " + pedidos_qtd.get(7) + "\nUnidade: " + pedidos_unidade.get(7));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(7).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 8) {
                            int var_posicao = 9;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(8) + "\nDescrição: " + pedidos_descricao.get(8) + "\nQtd: " + pedidos_qtd.get(8) + "\nUnidade: " + pedidos_unidade.get(8));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(8).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 9) {
                            int var_posicao = 10;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(9) + "\nDescrição: " + pedidos_descricao.get(9) + "\nQtd: " + pedidos_qtd.get(9) + "\nUnidade: " + pedidos_unidade.get(9));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(9).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 10) {
                            int var_posicao = 11;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(10) + "\nDescrição: " + pedidos_descricao.get(10) + "\nQtd: " + pedidos_qtd.get(10) + "\nUnidade: " + pedidos_unidade.get(10));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(10).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 11) {
                            int var_posicao = 12;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(11) + "\nDescrição: " + pedidos_descricao.get(11) + "\nQtd: " + pedidos_qtd.get(11) + "\nUnidade: " + pedidos_unidade.get(11));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(11).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 12) {
                            int var_posicao = 13;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(12) + "\nDescrição: " + pedidos_descricao.get(12) + "\nQtd: " + pedidos_qtd.get(12) + "\nUnidade: " + pedidos_unidade.get(12));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(12).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 13) {
                            int var_posicao = 14;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(13) + "\nDescrição: " + pedidos_descricao.get(13) + "\nQtd: " + pedidos_qtd.get(13) + "\nUnidade: " + pedidos_unidade.get(13));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(13).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        }  else if (posicao == 14) {
                            int var_posicao = 15;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(14) + "\nDescrição: " + pedidos_descricao.get(14) + "\nQtd: " + pedidos_qtd.get(14) + "\nUnidade: " + pedidos_unidade.get(14));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(14).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        }  else if (posicao == 15) {
                            int var_posicao = 16;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(15) + "\nDescrição: " + pedidos_descricao.get(15) + "\nQtd: " + pedidos_qtd.get(15) + "\nUnidade: " + pedidos_unidade.get(15));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(15).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        }  else if (posicao == 16) {
                            int var_posicao = 17;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(16) + "\nDescrição: " + pedidos_descricao.get(16) + "\nQtd: " + pedidos_qtd.get(16) + "\nUnidade: " + pedidos_unidade.get(16));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(16).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        }  else if (posicao == 17) {
                            int var_posicao = 18;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(17) + "\nDescrição: " + pedidos_descricao.get(17) + "\nQtd: " + pedidos_qtd.get(17) + "\nUnidade: " + pedidos_unidade.get(17));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(17).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        }  else if (posicao == 18) {
                            int var_posicao = 19;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(18) + "\nDescrição: " + pedidos_descricao.get(18) + "\nQtd: " + pedidos_qtd.get(18) + "\nUnidade: " + pedidos_unidade.get(18));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(18).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        }  else if (posicao == 19) {
                            int var_posicao = 20;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(19) + "\nDescrição: " + pedidos_descricao.get(19) + "\nQtd: " + pedidos_qtd.get(19) + "\nUnidade: " + pedidos_unidade.get(19));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(19).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        }  else if (posicao == 20) {
                            int var_posicao = 21;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(20) + "\nDescrição: " + pedidos_descricao.get(20) + "\nQtd: " + pedidos_qtd.get(20) + "\nUnidade: " + pedidos_unidade.get(20));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(20).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 21) {
                            int var_posicao = 22;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(21) + "\nDescrição: " + pedidos_descricao.get(21) + "\nQtd: " + pedidos_qtd.get(21) + "\nUnidade: " + pedidos_unidade.get(21));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(21).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 22) {
                            int var_posicao = 23;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(22) + "\nDescrição: " + pedidos_descricao.get(22) + "\nQtd: " + pedidos_qtd.get(22) + "\nUnidade: " + pedidos_unidade.get(22));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(22).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 23) {
                            int var_posicao = 24;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(23) + "\nDescrição: " + pedidos_descricao.get(23) + "\nQtd: " + pedidos_qtd.get(23) + "\nUnidade: " + pedidos_unidade.get(23));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(23).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 24) {
                            int var_posicao = 25;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(24) + "\nDescrição: " + pedidos_descricao.get(24) + "\nQtd: " + pedidos_qtd.get(24) + "\nUnidade: " + pedidos_unidade.get(24));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(24).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 25) {
                            int var_posicao = 26;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(25) + "\nDescrição: " + pedidos_descricao.get(25) + "\nQtd: " + pedidos_qtd.get(25) + "\nUnidade: " + pedidos_unidade.get(25));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(25).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 26) {
                            int var_posicao = 27;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(26) + "\nDescrição: " + pedidos_descricao.get(26) + "\nQtd: " + pedidos_qtd.get(26) + "\nUnidade: " + pedidos_unidade.get(26));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(26).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 27) {
                            int var_posicao = 28;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(27) + "\nDescrição: " + pedidos_descricao.get(27) + "\nQtd: " + pedidos_qtd.get(27) + "\nUnidade: " + pedidos_unidade.get(27));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(27).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 28) {
                            int var_posicao = 29;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(28) + "\nDescrição: " + pedidos_descricao.get(28) + "\nQtd: " + pedidos_qtd.get(28) + "\nUnidade: " + pedidos_unidade.get(28));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(28).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 29) {
                            int var_posicao = 30;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(29) + "\nDescrição: " + pedidos_descricao.get(29) + "\nQtd: " + pedidos_qtd.get(29) + "\nUnidade: " + pedidos_unidade.get(29));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(29).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 30) {
                            int var_posicao = 31;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(30) + "\nDescrição: " + pedidos_descricao.get(30) + "\nQtd: " + pedidos_qtd.get(30) + "\nUnidade: " + pedidos_unidade.get(30));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(30).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 31) {
                            int var_posicao = 32;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(31) + "\nDescrição: " + pedidos_descricao.get(31) + "\nQtd: " + pedidos_qtd.get(31) + "\nUnidade: " + pedidos_unidade.get(31));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(31).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 32) {
                            int var_posicao = 33;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(32) + "\nDescrição: " + pedidos_descricao.get(32) + "\nQtd: " + pedidos_qtd.get(32) + "\nUnidade: " + pedidos_unidade.get(32));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(32).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 33) {
                            int var_posicao = 34;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(33) + "\nDescrição: " + pedidos_descricao.get(33) + "\nQtd: " + pedidos_qtd.get(33) + "\nUnidade: " + pedidos_unidade.get(33));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(33).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 34) {
                            int var_posicao = 35;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(34) + "\nDescrição: " + pedidos_descricao.get(34) + "\nQtd: " + pedidos_qtd.get(34) + "\nUnidade: " + pedidos_unidade.get(34));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(34).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 35) {
                            int var_posicao = 36;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(35) + "\nDescrição: " + pedidos_descricao.get(35) + "\nQtd: " + pedidos_qtd.get(35) + "\nUnidade: " + pedidos_unidade.get(35));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(35).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 36) {
                            int var_posicao = 37;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(36) + "\nDescrição: " + pedidos_descricao.get(36) + "\nQtd: " + pedidos_qtd.get(36) + "\nUnidade: " + pedidos_unidade.get(36));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(36).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 37) {
                            int var_posicao = 38;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(37) + "\nDescrição: " + pedidos_descricao.get(37) + "\nQtd: " + pedidos_qtd.get(37) + "\nUnidade: " + pedidos_unidade.get(37));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(37).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 38) {
                            int var_posicao = 39;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(38) + "\nDescrição: " + pedidos_descricao.get(38) + "\nQtd: " + pedidos_qtd.get(38) + "\nUnidade: " + pedidos_unidade.get(38));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(38).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 39) {
                            int var_posicao = 40;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(39) + "\nDescrição: " + pedidos_descricao.get(39) + "\nQtd: " + pedidos_qtd.get(39) + "\nUnidade: " + pedidos_unidade.get(39));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(39).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 40) {
                            int var_posicao = 41;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(40) + "\nDescrição: " + pedidos_descricao.get(40) + "\nQtd: " + pedidos_qtd.get(40) + "\nUnidade: " + pedidos_unidade.get(40));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(40).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 41) {
                            int var_posicao = 42;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(41) + "\nDescrição: " + pedidos_descricao.get(41) + "\nQtd: " + pedidos_qtd.get(41) + "\nUnidade: " + pedidos_unidade.get(41));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(41).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 42) {
                            int var_posicao = 43;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(42) + "\nDescrição: " + pedidos_descricao.get(42) + "\nQtd: " + pedidos_qtd.get(42) + "\nUnidade: " + pedidos_unidade.get(42));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(42).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 43) {
                            int var_posicao = 44;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(43) + "\nDescrição: " + pedidos_descricao.get(43) + "\nQtd: " + pedidos_qtd.get(43) + "\nUnidade: " + pedidos_unidade.get(43));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(43).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 44) {
                            int var_posicao = 45;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(44) + "\nDescrição: " + pedidos_descricao.get(44) + "\nQtd: " + pedidos_qtd.get(44) + "\nUnidade: " + pedidos_unidade.get(44));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(44).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 45) {
                            int var_posicao = 46;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(45) + "\nDescrição: " + pedidos_descricao.get(44) + "\nQtd: " + pedidos_qtd.get(45) + "\nUnidade: " + pedidos_unidade.get(45));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(45).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else if (posicao == 46) {
                            int var_posicao = 47;
                            AlertDialog.Builder builder = new AlertDialog.Builder(FinalizarPedido.this);//Cria o gerador do AlertDialog
                            builder.setTitle("Item: " + var_posicao);
                            builder.setMessage("Categoria: " + pedidos_categoria.get(46) + "\nDescrição: " + pedidos_descricao.get(46) + "\nQtd: " + pedidos_qtd.get(46) + "\nUnidade: " + pedidos_unidade.get(46));
                            builder.setNegativeButton("Fechar", null);
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FinalizarPedido.this, DeletaItem.class);
                                    Bundle params = new Bundle();
                                    String id = pedidos_id.get(46).toString();
                                    params.putString("id", id);
                                    params.putString("nome", nome);
                                    intent.putExtras(params);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        }

                    }
                });
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.meuPerfil:
                UserAccessSession userAccess = UserAccessSession.getInstance(FinalizarPedido.this);
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

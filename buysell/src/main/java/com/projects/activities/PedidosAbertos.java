package com.projects.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
 * Created by Ygor on 14/04/2016.
 */
public class PedidosAbertos extends Activity {
    private TextView tvDescricao;
    private TextView tvQuantidade;
    private String Descricao;
    private String Unidade;
    private String Categoria;
    private String Qtd;
    private AlertDialog alerta;
    String user_id;
    InputStream is=null;
    String result=null;
    String line=null;
    ArrayList<String> pedidos_id = new ArrayList<String>();
    ArrayList<String> pedidos_categoria = new ArrayList<String>();
    ArrayList<String> endereco = new ArrayList<String>();
    ArrayList<String> cidade = new ArrayList<String>();
    ArrayList<String> estado = new ArrayList<String>();
    ArrayList<String> data = new ArrayList<String>();
    ArrayList<String> troco = new ArrayList<String>();
    ArrayList<String> lat = new ArrayList<String>();
    ArrayList<String> lon = new ArrayList<String>();
    ArrayList<String> pedido_prod = new ArrayList<String>();
    ArrayList<String> ativo = new ArrayList<String>();
    ArrayList<String> hora = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedidos_abertos);

        new select().execute();

    }

    class select extends AsyncTask<String, Integer, String> {

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

            UserAccessSession userAccess = UserAccessSession.getInstance(PedidosAbertos.this);
            UserSession userSession = userAccess.getUserSession();
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            Intent intent = getIntent();
            int user_id = intent.getIntExtra("user_id", 1);
            String user_str = String.valueOf(user_id);

            nameValuePairs.add(new BasicNameValuePair("user_id", user_str));

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://shopptous.com/pedidos_abertos.php");
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
                    endereco.add((json_dat.getString("endereco")));
                    cidade.add((json_dat.getString("cidade")));
                    estado.add((json_dat.getString("estado")));
                    data.add((json_dat.getString("data")));
                    troco.add((json_dat.getString("troco")));
                    lat.add((json_dat.getString("lat")));
                    lon.add((json_dat.getString("lon")));
                    pedido_prod.add((json_dat.getString("pedido_prod")));
                    ativo.add((json_dat.getString("ativo")));
                    hora.add((json_dat.getString("hora")));

                }


            } catch (Exception e) {
                Log.e("Fail 3", e.toString());
            }
            return user_str;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(pedido_prod.size() != 0) {
                for (int i = 0; i < pedido_prod.size(); i++) {

                    final ListView listaPedido = (ListView) findViewById(R.id.lista);

                    final ListView listaExclui = (ListView) findViewById(R.id.lista_exclui);
                    final ListView listaProposta = (ListView) findViewById(R.id.lista_proposta);

                    TextView pedidoId = (TextView) findViewById(R.id.textView7);

                    if (i == 0) {
                        String[] lista = {" "+pedido_prod.get(0) + "  | Status: " + ativo.get(0)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                        String[] lista_exclui = {"╳"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_exclui);
                        listaExclui.setAdapter(adapter2);
                        String[] lista_proposta = {"Propostas"};
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_proposta);
                        listaProposta.setAdapter(adapter3);
                    } else if (i == 1) {
                        String[] lista = {" "+pedido_prod.get(0) + "  | Status: " + ativo.get(0), " "+pedido_prod.get(1) + "  | Status: " + ativo.get(1)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                        String[] lista_exclui = {"╳","╳"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_exclui);
                        listaExclui.setAdapter(adapter2);
                        String[] lista_proposta = {"Propostas","Propostas"};
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_proposta);
                        listaProposta.setAdapter(adapter3);
                    } else if (i == 2) {
                        String[] lista = {" "+pedido_prod.get(0) + "  | Status: " + ativo.get(0), " "+pedido_prod.get(1) + "  | Status: " + ativo.get(1), " "+pedido_prod.get(2) + "  | Status: " + ativo.get(2)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                        String[] lista_exclui = {"╳","╳","╳"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_exclui);
                        listaExclui.setAdapter(adapter2);
                        String[] lista_proposta = {"Propostas","Propostas","Propostas"};
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_proposta);
                        listaProposta.setAdapter(adapter3);
                    } else if (i == 3) {
                        String[] lista = {" "+pedido_prod.get(0) + "  | Status: " + ativo.get(0), " "+pedido_prod.get(1) + "  | Status: " + ativo.get(1), " "+pedido_prod.get(2) + "  | Status: " + ativo.get(2), " "+pedido_prod.get(3) + "  | Status: " + ativo.get(3)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                        String[] lista_exclui = {"╳","╳","╳","╳"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_exclui);
                        listaExclui.setAdapter(adapter2);
                        String[] lista_proposta = {"Propostas","Propostas","Propostas","Propostas"};
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_proposta);
                        listaProposta.setAdapter(adapter3);
                    } else if (i == 4) {
                        String[] lista = {" "+pedido_prod.get(0) + "  | Status: " + ativo.get(0), " "+pedido_prod.get(1) + "  | Status: " + ativo.get(1), " "+pedido_prod.get(2) + "  | Status: " + ativo.get(2), " "+pedido_prod.get(3) + "  | Status: " + ativo.get(3), " "+pedido_prod.get(4) + "  | Status: " + ativo.get(4)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                        String[] lista_exclui = {"╳","╳","╳","╳","╳"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_exclui);
                        listaExclui.setAdapter(adapter2);
                        String[] lista_proposta = {"Propostas","Propostas","Propostas","Propostas","Propostas"};
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_proposta);
                        listaProposta.setAdapter(adapter3);
                    } else if (i == 5) {
                        String[] lista = {" "+pedido_prod.get(0) + "  | Status: " + ativo.get(0), " "+pedido_prod.get(1) + "  | Status: " + ativo.get(1), " "+pedido_prod.get(2) + "  | Status: " + ativo.get(2), " "+pedido_prod.get(3) + "  | Status: " + ativo.get(3), " "+pedido_prod.get(4) + "  | Status: " + ativo.get(4), " "+pedido_prod.get(5) + "  | Status: " + ativo.get(5)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                        String[] lista_exclui = {"╳","╳","╳","╳","╳","╳"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_exclui);
                        listaExclui.setAdapter(adapter2);
                        String[] lista_proposta = {"Propostas","Propostas","Propostas","Propostas","Propostas","Propostas"};
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_proposta);
                        listaProposta.setAdapter(adapter3);
                    } else if (i == 6) {
                        String[] lista = {" "+pedido_prod.get(0) + "  | Status: " + ativo.get(0), " "+pedido_prod.get(1) + "  | Status: " + ativo.get(1), " "+pedido_prod.get(2) + "  | Status: " + ativo.get(2), " "+pedido_prod.get(3) + "  | Status: " + ativo.get(3), " "+pedido_prod.get(4) + "  | Status: " + ativo.get(4), " "+pedido_prod.get(5) + "  | Status: " + ativo.get(5), " "+pedido_prod.get(6) + "  | Status: " + ativo.get(6)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                        String[] lista_exclui = {"╳","╳","╳","╳","╳","╳","╳"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_exclui);
                        listaExclui.setAdapter(adapter2);
                        String[] lista_proposta = {"Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas"};
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_proposta);
                        listaProposta.setAdapter(adapter3);
                    } else if (i == 7) {
                        String[] lista = {" "+pedido_prod.get(0) + "  | Status: " + ativo.get(0), " "+pedido_prod.get(1) + "  | Status: " + ativo.get(1), " "+pedido_prod.get(2) + "  | Status: " + ativo.get(2), " "+pedido_prod.get(3) + "  | Status: " + ativo.get(3), " "+pedido_prod.get(4) + "  | Status: " + ativo.get(4), " "+pedido_prod.get(5) + "  | Status: " + ativo.get(5), " "+pedido_prod.get(6) + "  | Status: " + ativo.get(6), " "+pedido_prod.get(7) + "  | Status: " + ativo.get(7)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                        String[] lista_exclui = {"╳","╳","╳","╳","╳","╳","╳","╳"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_exclui);
                        listaExclui.setAdapter(adapter2);
                        String[] lista_proposta = {"Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas"};
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_proposta);
                        listaProposta.setAdapter(adapter3);
                    } else if (i == 8) {
                        String[] lista = {" "+pedido_prod.get(0) + "  | Status: " + ativo.get(0), " "+pedido_prod.get(1) + "  | Status: " + ativo.get(1), " "+pedido_prod.get(2) + "  | Status: " + ativo.get(2), " "+pedido_prod.get(3) + "  | Status: " + ativo.get(3), " "+pedido_prod.get(4) + "  | Status: " + ativo.get(4), " "+pedido_prod.get(5) + "  | Status: " + ativo.get(5), " "+pedido_prod.get(6) + "  | Status: " + ativo.get(6), " "+pedido_prod.get(7) + "  | Status: " + ativo.get(7), " "+pedido_prod.get(8) + "  | Status: " + ativo.get(8)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                        String[] lista_exclui = {"╳","╳","╳","╳","╳","╳","╳","╳","╳"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_exclui);
                        listaExclui.setAdapter(adapter2);
                        String[] lista_proposta = {"Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas"};
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_proposta);
                        listaProposta.setAdapter(adapter3);
                    } else if (i == 9) {
                        String[] lista = {" "+pedido_prod.get(0) + "  | Status: " + ativo.get(0), " "+pedido_prod.get(1) + "  | Status: " + ativo.get(1), " "+pedido_prod.get(2) + "  | Status: " + ativo.get(2), " "+pedido_prod.get(3) + "  | Status: " + ativo.get(3), " "+pedido_prod.get(4) + "  | Status: " + ativo.get(4), " "+pedido_prod.get(5) + "  | Status: " + ativo.get(5), " "+pedido_prod.get(6) + "  | Status: " + ativo.get(6), " "+pedido_prod.get(7) + "  | Status: " + ativo.get(7), " "+pedido_prod.get(8) + "  | Status: " + ativo.get(8), " "+pedido_prod.get(9) + "  | Status: " + ativo.get(9)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                        String[] lista_exclui = {"╳","╳","╳","╳","╳","╳","╳","╳","╳","╳"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_exclui);
                        listaExclui.setAdapter(adapter2);
                        String[] lista_proposta = {"Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas"};
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_proposta);
                        listaProposta.setAdapter(adapter3);
                    } else if (i == 10) {
                        String[] lista = {" "+pedido_prod.get(0) + "  | Status: " + ativo.get(0), " "+pedido_prod.get(1) + "  | Status: " + ativo.get(1), " "+pedido_prod.get(2) + "  | Status: " + ativo.get(2), " "+pedido_prod.get(3) + "  | Status: " + ativo.get(3), " "+pedido_prod.get(4) + "  | Status: " + ativo.get(4), " "+pedido_prod.get(5) + "  | Status: " + ativo.get(5), " "+pedido_prod.get(6) + "  | Status: " + ativo.get(6), " "+pedido_prod.get(7) + "  | Status: " + ativo.get(7), " "+pedido_prod.get(8) + "  | Status: " + ativo.get(8), " "+pedido_prod.get(9) + "  | Status: " + ativo.get(9), " "+pedido_prod.get(10) + "  | Status: " + ativo.get(10)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                        String[] lista_exclui = {"╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_exclui);
                        listaExclui.setAdapter(adapter2);
                        String[] lista_proposta = {"Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas"};
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_proposta);
                        listaProposta.setAdapter(adapter3);
                    } else if (i == 11) {
                        String[] lista = {" "+pedido_prod.get(0) + "  | Status: " + ativo.get(0), " "+pedido_prod.get(1) + "  | Status: " + ativo.get(1), " "+pedido_prod.get(2) + "  | Status: " + ativo.get(2), " "+pedido_prod.get(3) + "  | Status: " + ativo.get(3), " "+pedido_prod.get(4) + "  | Status: " + ativo.get(4), " "+pedido_prod.get(5) + "  | Status: " + ativo.get(5), " "+pedido_prod.get(6) + "  | Status: " + ativo.get(6), " "+pedido_prod.get(7) + "  | Status: " + ativo.get(7), " "+pedido_prod.get(8) + "  | Status: " + ativo.get(8), " "+pedido_prod.get(9) + "  | Status: " + ativo.get(9), " "+pedido_prod.get(10) + "  | Status: " + ativo.get(10), " "+pedido_prod.get(11) + "  | Status: " + ativo.get(11)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                        String[] lista_exclui = {"╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_exclui);
                        listaExclui.setAdapter(adapter2);
                        String[] lista_proposta = {"Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas"};
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_proposta);
                        listaProposta.setAdapter(adapter3);
                    } else if (i == 12) {
                        String[] lista = {" "+pedido_prod.get(0) + "  | Status: " + ativo.get(0), " "+pedido_prod.get(1) + "  | Status: " + ativo.get(1), " "+pedido_prod.get(2) + "  | Status: " + ativo.get(2), " "+pedido_prod.get(3) + "  | Status: " + ativo.get(3), " "+pedido_prod.get(4) + "  | Status: " + ativo.get(4), " "+pedido_prod.get(5) + "  | Status: " + ativo.get(5), " "+pedido_prod.get(6) + "  | Status: " + ativo.get(6), " "+pedido_prod.get(7) + "  | Status: " + ativo.get(7), " "+pedido_prod.get(8) + "  | Status: " + ativo.get(8), " "+pedido_prod.get(9) + "  | Status: " + ativo.get(9), " "+pedido_prod.get(10) + "  | Status: " + ativo.get(10), " "+pedido_prod.get(11) + "  | Status: " + ativo.get(11), " "+pedido_prod.get(12) + "  | Status: " + ativo.get(12)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                        String[] lista_exclui = {"╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_exclui);
                        listaExclui.setAdapter(adapter2);
                        String[] lista_proposta = {"Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas"};
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_proposta);
                        listaProposta.setAdapter(adapter3);
                    } else if (i == 13) {
                        String[] lista = {" "+pedido_prod.get(0) + "  | Status: " + ativo.get(0), " "+pedido_prod.get(1) + "  | Status: " + ativo.get(1), " "+pedido_prod.get(2) + "  | Status: " + ativo.get(2), " "+pedido_prod.get(3) + "  | Status: " + ativo.get(3), " "+pedido_prod.get(4) + "  | Status: " + ativo.get(4), " "+pedido_prod.get(5) + "  | Status: " + ativo.get(5), " "+pedido_prod.get(6) + "  | Status: " + ativo.get(6), " "+pedido_prod.get(7) + "  | Status: " + ativo.get(7), " "+pedido_prod.get(8) + "  | Status: " + ativo.get(8), " "+pedido_prod.get(9) + "  | Status: " + ativo.get(9), " "+pedido_prod.get(10) + "  | Status: " + ativo.get(10), " "+pedido_prod.get(11) + "  | Status: " + ativo.get(11), " "+pedido_prod.get(12) + "  | Status: " + ativo.get(12), " "+pedido_prod.get(13) + "  | Status: " + ativo.get(13)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                        String[] lista_exclui = {"╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_exclui);
                        listaExclui.setAdapter(adapter2);
                        String[] lista_proposta = {"Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas"};
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_proposta);
                        listaProposta.setAdapter(adapter3);
                    } else if (i == 14) {
                        String[] lista = {" "+pedido_prod.get(0) + "  | Status: " + ativo.get(0), " "+pedido_prod.get(1) + "  | Status: " + ativo.get(1), " "+pedido_prod.get(2) + "  | Status: " + ativo.get(2), " "+pedido_prod.get(3) + "  | Status: " + ativo.get(3), " "+pedido_prod.get(4) + "  | Status: " + ativo.get(4), " "+pedido_prod.get(5) + "  | Status: " + ativo.get(5), " "+pedido_prod.get(6) + "  | Status: " + ativo.get(6), " "+pedido_prod.get(7) + "  | Status: " + ativo.get(7), " "+pedido_prod.get(8) + "  | Status: " + ativo.get(8), " "+pedido_prod.get(9) + "  | Status: " + ativo.get(9), " "+pedido_prod.get(10) + "  | Status: " + ativo.get(10), " "+pedido_prod.get(11) + "  | Status: " + ativo.get(11), " "+pedido_prod.get(12) + "  | Status: " + ativo.get(12), " "+pedido_prod.get(13) + "  | Status: " + ativo.get(13), " "+pedido_prod.get(14) + "  | Status: " + ativo.get(14)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                        String[] lista_exclui = {"╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_exclui);
                        listaExclui.setAdapter(adapter2);
                        String[] lista_proposta = {"Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas"};
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_proposta);
                        listaProposta.setAdapter(adapter3);
                    } else if (i == 15) {
                        String[] lista = {" "+pedido_prod.get(0) + "  | Status: " + ativo.get(0), " "+pedido_prod.get(1) + "  | Status: " + ativo.get(1), " "+pedido_prod.get(2) + "  | Status: " + ativo.get(2), " "+pedido_prod.get(3) + "  | Status: " + ativo.get(3), " "+pedido_prod.get(4) + "  | Status: " + ativo.get(4), " "+pedido_prod.get(5) + "  | Status: " + ativo.get(5), " "+pedido_prod.get(6) + "  | Status: " + ativo.get(6), " "+pedido_prod.get(7) + "  | Status: " + ativo.get(7), " "+pedido_prod.get(8) + "  | Status: " + ativo.get(8), " "+pedido_prod.get(9) + "  | Status: " + ativo.get(9), " "+pedido_prod.get(10) + "  | Status: " + ativo.get(10), " "+pedido_prod.get(11) + "  | Status: " + ativo.get(11), " "+pedido_prod.get(12) + "  | Status: " + ativo.get(12), " "+pedido_prod.get(13) + "  | Status: " + ativo.get(13), " "+pedido_prod.get(14) + "  | Status: " + ativo.get(14), " "+pedido_prod.get(15) + "  | Status: " + ativo.get(15)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                        String[] lista_exclui = {"╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_exclui);
                        listaExclui.setAdapter(adapter2);
                        String[] lista_proposta = {"Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas"};
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_proposta);
                        listaProposta.setAdapter(adapter3);
                    } else if (i == 16) {
                        String[] lista = {" "+pedido_prod.get(0) + "  | Status: " + ativo.get(0), " "+pedido_prod.get(1) + "  | Status: " + ativo.get(1), " "+pedido_prod.get(2) + "  | Status: " + ativo.get(2), " "+pedido_prod.get(3) + "  | Status: " + ativo.get(3), " "+pedido_prod.get(4) + "  | Status: " + ativo.get(4), " "+pedido_prod.get(5) + "  | Status: " + ativo.get(5), " "+pedido_prod.get(6) + "  | Status: " + ativo.get(6), " "+pedido_prod.get(7) + "  | Status: " + ativo.get(7), " "+pedido_prod.get(8) + "  | Status: " + ativo.get(8), " "+pedido_prod.get(9) + "  | Status: " + ativo.get(9), " "+pedido_prod.get(10) + "  | Status: " + ativo.get(10), " "+pedido_prod.get(11) + "  | Status: " + ativo.get(11), " "+pedido_prod.get(12) + "  | Status: " + ativo.get(12), " "+pedido_prod.get(13) + "  | Status: " + ativo.get(13), " "+pedido_prod.get(14) + "  | Status: " + ativo.get(14), " "+pedido_prod.get(15) + "  | Status: " + ativo.get(15), " "+pedido_prod.get(16) + "  | Status: " + ativo.get(16)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                        String[] lista_exclui = {"╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳","╳"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_exclui);
                        listaExclui.setAdapter(adapter2);
                        String[] lista_proposta = {"Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas","Propostas"};
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(PedidosAbertos.this, android.R.layout.simple_list_item_1, lista_proposta);
                        listaProposta.setAdapter(adapter3);
                    }


                    listaProposta.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> lista, View item, int posicao, long id) {
                            listaProposta.getItemAtPosition(posicao);

                            if (posicao == 0) {
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(0).toString();
                                params.putString("user_id", id_ped);
                                Intent intent2 = new Intent(PedidosAbertos.this, Propostas.class);
                                intent2.putExtras(params);
                                startActivity(intent2);
                                finish();
                            }else if(posicao == 1){
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(1).toString();
                                params.putString("user_id", id_ped);
                                Intent intent2 = new Intent(PedidosAbertos.this, Propostas.class);
                                intent2.putExtras(params);
                                startActivity(intent2);
                                finish();
                            }else if(posicao == 2){
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(2).toString();
                                params.putString("user_id", id_ped);
                                Intent intent2 = new Intent(PedidosAbertos.this, Propostas.class);
                                intent2.putExtras(params);
                                startActivity(intent2);
                                finish();
                            }else if(posicao == 3){
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(3).toString();
                                params.putString("user_id", id_ped);
                                Intent intent2 = new Intent(PedidosAbertos.this, Propostas.class);
                                intent2.putExtras(params);
                                startActivity(intent2);
                                finish();
                            }else if(posicao == 4){
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(4).toString();
                                params.putString("user_id", id_ped);
                                Intent intent2 = new Intent(PedidosAbertos.this, Propostas.class);
                                intent2.putExtras(params);
                                startActivity(intent2);
                                finish();
                            }else if(posicao == 5){
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(5).toString();
                                params.putString("user_id", id_ped);
                                Intent intent2 = new Intent(PedidosAbertos.this, Propostas.class);
                                intent2.putExtras(params);
                                startActivity(intent2);
                                finish();
                            }else if(posicao == 6){
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(6).toString();
                                params.putString("user_id", id_ped);
                                Intent intent2 = new Intent(PedidosAbertos.this, Propostas.class);
                                intent2.putExtras(params);
                                startActivity(intent2);
                                finish();
                            }else if(posicao == 7){
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(7).toString();
                                params.putString("user_id", id_ped);
                                Intent intent2 = new Intent(PedidosAbertos.this, Propostas.class);
                                intent2.putExtras(params);
                                startActivity(intent2);
                                finish();
                            }else if(posicao == 8){
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(8).toString();
                                params.putString("user_id", id_ped);
                                Intent intent2 = new Intent(PedidosAbertos.this, Propostas.class);
                                intent2.putExtras(params);
                                startActivity(intent2);
                                finish();
                            }else if(posicao == 9){
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(9).toString();
                                params.putString("user_id", id_ped);
                                Intent intent2 = new Intent(PedidosAbertos.this, Propostas.class);
                                intent2.putExtras(params);
                                startActivity(intent2);
                                finish();
                            }else if(posicao == 10){
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(10).toString();
                                params.putString("user_id", id_ped);
                                Intent intent2 = new Intent(PedidosAbertos.this, Propostas.class);
                                intent2.putExtras(params);
                                startActivity(intent2);
                                finish();
                            }else if(posicao == 11){
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(11).toString();
                                params.putString("user_id", id_ped);
                                Intent intent2 = new Intent(PedidosAbertos.this, Propostas.class);
                                intent2.putExtras(params);
                                startActivity(intent2);
                                finish();
                            }else if(posicao == 12){
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(12).toString();
                                params.putString("user_id", id_ped);
                                Intent intent2 = new Intent(PedidosAbertos.this, Propostas.class);
                                intent2.putExtras(params);
                                startActivity(intent2);
                                finish();
                            }else if(posicao == 13){
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(13).toString();
                                params.putString("user_id", id_ped);
                                Intent intent2 = new Intent(PedidosAbertos.this, Propostas.class);
                                intent2.putExtras(params);
                                startActivity(intent2);
                                finish();
                            }else if(posicao == 14){
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(14).toString();
                                params.putString("user_id", id_ped);
                                Intent intent2 = new Intent(PedidosAbertos.this, Propostas.class);
                                intent2.putExtras(params);
                                startActivity(intent2);
                                finish();
                            }else if(posicao == 15){
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(15).toString();
                                params.putString("user_id", id_ped);
                                Intent intent2 = new Intent(PedidosAbertos.this, Propostas.class);
                                intent2.putExtras(params);
                                startActivity(intent2);
                                finish();
                            }else if(posicao == 16){
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(1).toString();
                                params.putString("user_id", id_ped);
                                Intent intent2 = new Intent(PedidosAbertos.this, Propostas.class);
                                intent2.putExtras(params);
                                startActivity(intent2);
                                finish();
                            }
                        }
                    });

                    listaExclui.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> lista, View item, int posicao, long id) {
                            listaExclui.getItemAtPosition(posicao);

                            if (posicao == 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(0));
                                builder.setNegativeButton("Confirmar exclusão", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosAbertos.this, DeletaItemAberto.class);
                                        Bundle params = new Bundle();
                                        String id_ped = pedido_prod.get(0).toString();
                                        params.putString("id_ped", id_ped);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            }else if(posicao == 1){
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(1));
                                builder.setNegativeButton("Confirmar exclusão", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosAbertos.this, DeletaItemAberto.class);
                                        Bundle params = new Bundle();
                                        String id_ped = pedido_prod.get(1).toString();
                                        params.putString("id_ped", id_ped);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            }else if(posicao == 2){
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(2));
                                builder.setNegativeButton("Confirmar exclusão", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosAbertos.this, DeletaItemAberto.class);
                                        Bundle params = new Bundle();
                                        String id_ped = pedido_prod.get(2).toString();
                                        params.putString("id_ped", id_ped);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            }else if(posicao == 3){
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                Intent intent = new Intent(PedidosAbertos.this, DeletaItemAberto.class);
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(3).toString();
                                params.putString("id_ped", id_ped);
                                intent.putExtras(params);
                                startActivity(intent);
                                finish();
                            }else if(posicao == 4){
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                Intent intent = new Intent(PedidosAbertos.this, DeletaItemAberto.class);
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(4).toString();
                                params.putString("id_ped", id_ped);
                                intent.putExtras(params);
                                startActivity(intent);
                                finish();
                            }else if(posicao == 5){
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                Intent intent = new Intent(PedidosAbertos.this, DeletaItemAberto.class);
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(5).toString();
                                params.putString("id_ped", id_ped);
                                intent.putExtras(params);
                                startActivity(intent);
                                finish();
                            }else if(posicao == 6){
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                Intent intent = new Intent(PedidosAbertos.this, DeletaItemAberto.class);
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(6).toString();
                                params.putString("id_ped", id_ped);
                                intent.putExtras(params);
                                startActivity(intent);
                                finish();
                            }else if(posicao == 7){
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                Intent intent = new Intent(PedidosAbertos.this, DeletaItemAberto.class);
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(7).toString();
                                params.putString("id_ped", id_ped);
                                intent.putExtras(params);
                                startActivity(intent);
                                finish();
                            }else if(posicao == 8){
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                Intent intent = new Intent(PedidosAbertos.this, DeletaItemAberto.class);
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(8).toString();
                                params.putString("id_ped", id_ped);
                                intent.putExtras(params);
                                startActivity(intent);
                                finish();
                            }else if(posicao == 9){
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                Intent intent = new Intent(PedidosAbertos.this, DeletaItemAberto.class);
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(9).toString();
                                params.putString("id_ped", id_ped);
                                intent.putExtras(params);
                                startActivity(intent);
                                finish();
                            }else if(posicao == 10){
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                Intent intent = new Intent(PedidosAbertos.this, DeletaItemAberto.class);
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(10).toString();
                                params.putString("id_ped", id_ped);
                                intent.putExtras(params);
                                startActivity(intent);
                                finish();
                            }else if(posicao == 11){
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                Intent intent = new Intent(PedidosAbertos.this, DeletaItemAberto.class);
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(11).toString();
                                params.putString("id_ped", id_ped);
                                intent.putExtras(params);
                                startActivity(intent);
                                finish();
                            }else if(posicao == 12){
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                Intent intent = new Intent(PedidosAbertos.this, DeletaItemAberto.class);
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(12).toString();
                                params.putString("id_ped", id_ped);
                                intent.putExtras(params);
                                startActivity(intent);
                                finish();
                            }else if(posicao == 13){
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                Intent intent = new Intent(PedidosAbertos.this, DeletaItemAberto.class);
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(13).toString();
                                params.putString("id_ped", id_ped);
                                intent.putExtras(params);
                                startActivity(intent);
                                finish();
                            }else if(posicao == 14){
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                Intent intent = new Intent(PedidosAbertos.this, DeletaItemAberto.class);
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(14).toString();
                                params.putString("id_ped", id_ped);
                                intent.putExtras(params);
                                startActivity(intent);
                                finish();
                            }else if(posicao == 15){
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                Intent intent = new Intent(PedidosAbertos.this, DeletaItemAberto.class);
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(15).toString();
                                params.putString("id_ped", id_ped);
                                intent.putExtras(params);
                                startActivity(intent);
                                finish();
                            }else if(posicao == 16){
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                Intent intent = new Intent(PedidosAbertos.this, DeletaItemAberto.class);
                                Bundle params = new Bundle();
                                String id_ped = pedido_prod.get(16).toString();
                                params.putString("id_ped", id_ped);
                                intent.putExtras(params);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                    listaPedido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> lista, View item, int posicao, long id) {
                            listaPedido.getItemAtPosition(posicao);

                            if (posicao == 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(0));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(0) + "\nEnd: " + endereco.get(0) + "\nCidade: " + cidade.get(0) + "\nEstado: " + estado.get(0) + "\nData: " + data.get(0) + "\nHorário: " + hora.get(0) + "\nTroco: " + troco.get(0));
                                builder.setNegativeButton("Itens", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String id_prod = pedido_prod.get(0).toString();
                                        b.putString("id_prod", id_prod);
                                        Intent intent = new Intent(PedidosAbertos.this, ListaAbertos.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(1));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(1) + "\nEnd: " + endereco.get(1) + "\nCidade: " + cidade.get(1) + "\nEstado: " + estado.get(1) + "\nData: " + data.get(1) + "\nHorário: " + hora.get(1) + "\nTroco: " + troco.get(1));
                                builder.setNegativeButton("Itens", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String id_prod = pedido_prod.get(1).toString();
                                        b.putString("id_prod", id_prod);
                                        Intent intent = new Intent(PedidosAbertos.this, ListaAbertos.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 2) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(2));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(2) + "\nEnd: " + endereco.get(2) + "\nCidade: " + cidade.get(2) + "\nEstado: " + estado.get(2) + "\nData: " + data.get(2) + "\nHorário: " + hora.get(2) + "\nTroco: " + troco.get(2));
                                builder.setNegativeButton("Itens", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String id_prod = pedido_prod.get(2).toString();
                                        b.putString("id_prod", id_prod);
                                        Intent intent = new Intent(PedidosAbertos.this, ListaAbertos.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 3) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(3));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(3) + "\nEnd: " + endereco.get(3) + "\nCidade: " + cidade.get(3) + "\nEstado: " + estado.get(3) + "\nData: " + data.get(3) + "\nHorário: " + hora.get(3) + "\nTroco: " + troco.get(3));
                                builder.setNegativeButton("Itens", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String id_prod = pedido_prod.get(3).toString();
                                        b.putString("id_prod", id_prod);
                                        Intent intent = new Intent(PedidosAbertos.this, ListaAbertos.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 4) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(4));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(4) + "\nEnd: " + endereco.get(4) + "\nCidade: " + cidade.get(4) + "\nEstado: " + estado.get(4) + "\nData: " + data.get(4) + "\nHorário: " + hora.get(4) + "\nTroco: " + troco.get(4));
                                builder.setNegativeButton("Itens", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String id_prod = pedido_prod.get(4).toString();
                                        b.putString("id_prod", id_prod);
                                        Intent intent = new Intent(PedidosAbertos.this, ListaAbertos.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 5) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(5));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(5) + "\nEnd: " + endereco.get(5) + "\nCidade: " + cidade.get(5) + "\nEstado: " + estado.get(5) + "\nData: " + data.get(5) + "\nHorário: " + hora.get(5) + "\nTroco: " + troco.get(5));
                                builder.setNegativeButton("Itens", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String id_prod = pedido_prod.get(5).toString();
                                        b.putString("id_prod", id_prod);
                                        Intent intent = new Intent(PedidosAbertos.this, ListaAbertos.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 6) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(6));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(6) + "\nEnd: " + endereco.get(6) + "\nCidade: " + cidade.get(6) + "\nEstado: " + estado.get(6) + "\nData: " + data.get(6) + "\nHorário: " + hora.get(6) + "\nTroco: " + troco.get(6));
                                builder.setNegativeButton("Itens", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String id_prod = pedido_prod.get(6).toString();
                                        b.putString("id_prod", id_prod);
                                        Intent intent = new Intent(PedidosAbertos.this, ListaAbertos.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 7) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(7));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(7) + "\nEnd: " + endereco.get(7) + "\nCidade: " + cidade.get(7) + "\nEstado: " + estado.get(7) + "\nData: " + data.get(7) + "\nHorário: " + hora.get(7) + "\nTroco: " + troco.get(7));
                                builder.setNegativeButton("Itens", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String id_prod = pedido_prod.get(7).toString();
                                        b.putString("id_prod", id_prod);
                                        Intent intent = new Intent(PedidosAbertos.this, ListaAbertos.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 8) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(8));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(8) + "\nEnd: " + endereco.get(8) + "\nCidade: " + cidade.get(8) + "\nEstado: " + estado.get(8) + "\nData: " + data.get(8) + "\nHorário: " + hora.get(8) + "\nTroco: " + troco.get(8));
                                builder.setNegativeButton("Itens", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String id_prod = pedido_prod.get(8).toString();
                                        b.putString("id_prod", id_prod);
                                        Intent intent = new Intent(PedidosAbertos.this, ListaAbertos.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 9) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(9));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(9) + "\nEnd: " + endereco.get(9) + "\nCidade: " + cidade.get(9) + "\nEstado: " + estado.get(9) + "\nData: " + data.get(9) + "\nHorário: " + hora.get(9) + "\nTroco: " + troco.get(9));
                                builder.setNegativeButton("Itens", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String id_prod = pedido_prod.get(9).toString();
                                        b.putString("id_prod", id_prod);
                                        Intent intent = new Intent(PedidosAbertos.this, ListaAbertos.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 10) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(10));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(10) + "\nEnd: " + endereco.get(10) + "\nCidade: " + cidade.get(10) + "\nEstado: " + estado.get(10) + "\nData: " + data.get(10) + "\nHorário: " + hora.get(10) + "\nTroco: " + troco.get(10));
                                builder.setNegativeButton("Itens", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String id_prod = pedido_prod.get(10).toString();
                                        b.putString("id_prod", id_prod);
                                        Intent intent = new Intent(PedidosAbertos.this, ListaAbertos.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 11) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(11));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(11) + "\nEnd: " + endereco.get(11) + "\nCidade: " + cidade.get(11) + "\nEstado: " + estado.get(11) + "\nData: " + data.get(11) + "\nHorário: " + hora.get(11) + "\nTroco: " + troco.get(11));
                                builder.setNegativeButton("Itens", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String id_prod = pedido_prod.get(11).toString();
                                        b.putString("id_prod", id_prod);
                                        Intent intent = new Intent(PedidosAbertos.this, ListaAbertos.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 12) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(12));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(12) + "\nEnd: " + endereco.get(12) + "\nCidade: " + cidade.get(12) + "\nEstado: " + estado.get(12) + "\nData: " + data.get(12) + "\nHorário: " + hora.get(12) + "\nTroco: " + troco.get(12));
                                builder.setNegativeButton("Itens", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String id_prod = pedido_prod.get(12).toString();
                                        b.putString("id_prod", id_prod);
                                        Intent intent = new Intent(PedidosAbertos.this, ListaAbertos.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 13) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(13));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(13) + "\nEnd: " + endereco.get(13) + "\nCidade: " + cidade.get(13) + "\nEstado: " + estado.get(13) + "\nData: " + data.get(13) + "\nHorário: " + hora.get(13) + "\nTroco: " + troco.get(13));
                                builder.setNegativeButton("Itens", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String id_prod = pedido_prod.get(13).toString();
                                        b.putString("id_prod", id_prod);
                                        Intent intent = new Intent(PedidosAbertos.this, ListaAbertos.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 14) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(14));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(14) + "\nEnd: " + endereco.get(14) + "\nCidade: " + cidade.get(14) + "\nEstado: " + estado.get(14) + "\nData: " + data.get(14) + "\nHorário: " + hora.get(14) + "\nTroco: " + troco.get(14));
                                builder.setNegativeButton("Itens", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String id_prod = pedido_prod.get(14).toString();
                                        b.putString("id_prod", id_prod);
                                        Intent intent = new Intent(PedidosAbertos.this, ListaAbertos.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 15) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedido_prod.get(15));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(15) + "\nEnd: " + endereco.get(15) + "\nCidade: " + cidade.get(15) + "\nEstado: " + estado.get(15) + "\nData: " + data.get(15) + "\nHorário: " + hora.get(15) + "\nTroco: " + troco.get(15));
                                builder.setNegativeButton("Itens", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String id_prod = pedido_prod.get(15).toString();
                                        b.putString("id_prod", id_prod);
                                        Intent intent = new Intent(PedidosAbertos.this, ListaAbertos.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                            } else if (posicao == 16) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosAbertos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(16));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(16) + "\nEnd: " + endereco.get(16) + "\nCidade: " + cidade.get(16) + "\nEstado: " + estado.get(16) + "\nData: " + data.get(16) + "\nHorário: " + hora.get(16) + "\nTroco: " + troco.get(16));
                                builder.setNegativeButton("Itens", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String id_prod = pedido_prod.get(16).toString();
                                        b.putString("id_prod", id_prod);
                                        Intent intent = new Intent(PedidosAbertos.this, ListaAbertos.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            }
                        }
                    });

                   /*
                   AQUI BOTÃO QUE VAI CHAMAR O MAPA
                   listaPedido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> lista, View item, int posicao, long id) {
                            Intent intent = new Intent(PedidosSalvos.this, DetailActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });*/

                }
            }else{
                Toast.makeText(PedidosAbertos.this, "Nenhum pedido aberto!", Toast.LENGTH_SHORT).show();
            }
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.meuPerfil:
                UserAccessSession userAccess = UserAccessSession.getInstance(PedidosAbertos.this);
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
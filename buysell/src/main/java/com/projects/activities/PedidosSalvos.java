package com.projects.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Ygor on 14/04/2016.
 */
public class PedidosSalvos extends Activity {
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
    ArrayList<String> pedidos_descricao = new ArrayList<String>();
    ArrayList<String> pedidos_qtd = new ArrayList<String>();
    ArrayList<String> pedidos_unidade = new ArrayList<String>();
    ArrayList<String> pedido_prod = new ArrayList<String>();
    ArrayList<String> ativo = new ArrayList<String>();
    private static SQLiteDatabase db;
    private static DbHelper dbHelper;
    private static Queries q;
    public Queries getQueries() {
        return q;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedidos_salvos);

        dbHelper = new DbHelper(this);
        q = new Queries(db, dbHelper);
        final Queries q = getQueries();

        this.getActionBar().setTitle("Shopptous");
        Button novoPedido = (Button) findViewById(R.id.novo_pedido);

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

            UserAccessSession userAccess = UserAccessSession.getInstance(PedidosSalvos.this);
            UserSession userSession = userAccess.getUserSession();
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            Intent intent = getIntent();
            int user_id = intent.getIntExtra("user_id", 1);
            String user_str = String.valueOf(user_id);

            nameValuePairs.add(new BasicNameValuePair("user_id", user_str));

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://shopptous.com/pedidos_salvos.php");
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
                    pedido_prod.add((json_dat.getString("pedido_prod")));
                    ativo.add((json_dat.getString("ativo")));

                }


            } catch (Exception e) {
                Log.e("Fail 3", e.toString());
            }
            return user_str;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(pedidos_id.size() != 0) {
                for (int i = 0; i < pedidos_id.size(); i++) {

                    final ListView listaPedido = (ListView) findViewById(R.id.lista);

                    TextView pedidoId = (TextView) findViewById(R.id.textView7);

                    if (i == 0) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: Aguardando envio"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosSalvos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 1) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(1) + "  | Status: Aguardando envio"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosSalvos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 2) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(1) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(2) + "  | Status: Aguardando envio"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosSalvos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 3) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(1) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(2) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(3) + "  | Status: Aguardando envio"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosSalvos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 4) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(1) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(2) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(3) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(4) + "  | Status: Aguardando envio"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosSalvos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 5) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(1) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(2) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(3) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(4) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(5) + "  | Status: Aguardando envio"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosSalvos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 6) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(1) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(2) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(3) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(4) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(5) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(6) + "  | Status: Aguardando envio"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosSalvos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 7) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(1) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(2) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(3) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(4) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(5) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(6) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(7) + "  | Status: Aguardando envio"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosSalvos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 8) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(1) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(2) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(3) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(4) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(5) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(6) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(7) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(8) + "  | Status: Aguardando envio"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosSalvos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 9) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(1) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(2) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(3) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(4) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(5) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(6) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(7) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(8) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(9) + "  | Status: Aguardando envio"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosSalvos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 10) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(1) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(2) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(3) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(4) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(5) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(6) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(7) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(8) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(9) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(10) + "  | Status: Aguardando envio"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosSalvos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 11) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(1) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(2) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(3) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(4) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(5) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(6) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(7) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(8) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(9) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(10) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(11) + "  | Status: Aguardando envio"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosSalvos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 12) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(1) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(2) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(3) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(4) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(5) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(6) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(7) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(8) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(9) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(10) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(11) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(12) + "  | Status: Aguardando envio"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosSalvos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 13) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(1) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(2) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(3) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(4) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(5) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(6) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(7) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(8) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(9) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(10) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(11) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(12) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(13) + "  | Status: Aguardando envio"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosSalvos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 14) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(1) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(2) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(3) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(4) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(5) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(6) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(7) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(8) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(9) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(10) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(11) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(12) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(13) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(14) + "  | Status: Aguardando envio"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosSalvos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 15) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(1) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(2) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(3) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(4) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(5) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(6) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(7) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(8) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(9) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(10) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(11) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(12) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(13) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(14) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(15) + "  | Status: Aguardando envio"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosSalvos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 16) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(1) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(2) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(3) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(4) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(5) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(6) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(7) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(8) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(9) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(10) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(11) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(12) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(13) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(14) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(15) + "  | Status: Aguardando envio", "#ID: "+pedidos_id.get(16) + "  | Status: Aguardando envio"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PedidosSalvos.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    }

                    listaPedido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> lista, View item, int posicao, long id) {
                            listaPedido.getItemAtPosition(posicao);

                            if (posicao == 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosSalvos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(0));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(0));
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Editar / Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Bundle b = new Bundle();
                                        String nome = pedidos_categoria.get(0).toString();
                                        b.putString("nome", nome);
                                        Intent intent = new Intent(PedidosSalvos.this, FinalizarPedido.class);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosSalvos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(1));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(1));
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Editar / Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosSalvos.this, FinalizarPedido.class);
                                        Bundle params = new Bundle();
                                        String nome = pedidos_categoria.get(1).toString();
                                        params.putString("nome", nome);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 2) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosSalvos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(2));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(2));
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Editar / Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosSalvos.this, FinalizarPedido.class);
                                        Bundle params = new Bundle();
                                        String nome = pedidos_categoria.get(2).toString();
                                        params.putString("nome", nome);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 3) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosSalvos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(3));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(3));
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Editar / Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosSalvos.this, FinalizarPedido.class);
                                        Bundle params = new Bundle();
                                        String nome = pedidos_categoria.get(3).toString();
                                        params.putString("nome", nome);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 4) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosSalvos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(4));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(4));
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Editar / Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosSalvos.this, FinalizarPedido.class);
                                        Bundle params = new Bundle();
                                        String nome = pedidos_categoria.get(4).toString();
                                        params.putString("nome", nome);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 5) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosSalvos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(5));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(5));
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Editar / Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosSalvos.this, FinalizarPedido.class);
                                        Bundle params = new Bundle();
                                        String nome = pedidos_categoria.get(5).toString();
                                        params.putString("nome", nome);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 6) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosSalvos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(6));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(6));
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Editar / Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosSalvos.this, FinalizarPedido.class);
                                        Bundle params = new Bundle();
                                        String nome = pedidos_categoria.get(6).toString();
                                        params.putString("nome", nome);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 7) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosSalvos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(7));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(7));
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Editar / Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosSalvos.this, FinalizarPedido.class);
                                        Bundle params = new Bundle();
                                        String nome = pedidos_categoria.get(7).toString();
                                        params.putString("nome", nome);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 8) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosSalvos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(8));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(8));
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Editar / Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosSalvos.this, FinalizarPedido.class);
                                        Bundle params = new Bundle();
                                        String nome = pedidos_categoria.get(8).toString();
                                        params.putString("nome", nome);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 9) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosSalvos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(9));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(9));
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Editar / Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosSalvos.this, FinalizarPedido.class);
                                        Bundle params = new Bundle();
                                        String nome = pedidos_categoria.get(9).toString();
                                        params.putString("nome", nome);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 10) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosSalvos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(10));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(10));
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Editar / Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosSalvos.this, FinalizarPedido.class);
                                        Bundle params = new Bundle();
                                        String nome = pedidos_categoria.get(10).toString();
                                        params.putString("nome", nome);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 11) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosSalvos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(11));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(11));
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Editar / Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosSalvos.this, FinalizarPedido.class);
                                        Bundle params = new Bundle();
                                        String nome = pedidos_categoria.get(11).toString();
                                        params.putString("nome", nome);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 12) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosSalvos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(12));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(12));
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Editar / Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosSalvos.this, FinalizarPedido.class);
                                        Bundle params = new Bundle();
                                        String nome = pedidos_categoria.get(12).toString();
                                        params.putString("nome", nome);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 13) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosSalvos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(13));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(13));
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Editar / Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosSalvos.this, FinalizarPedido.class);
                                        Bundle params = new Bundle();
                                        String nome = pedidos_categoria.get(13).toString();
                                        params.putString("nome", nome);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 14) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosSalvos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(14));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(14));
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Editar / Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosSalvos.this, FinalizarPedido.class);
                                        Bundle params = new Bundle();
                                        String nome = pedidos_categoria.get(14).toString();
                                        params.putString("nome", nome);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 15) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosSalvos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(15));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(15));
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Editar / Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosSalvos.this, FinalizarPedido.class);
                                        Bundle params = new Bundle();
                                        String nome = pedidos_categoria.get(15).toString();
                                        params.putString("nome", nome);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if (posicao == 16) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PedidosSalvos.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(16));
                                builder.setMessage("Categoria: " + pedidos_categoria.get(16));
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Editar / Enviar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PedidosSalvos.this, FinalizarPedido.class);
                                        Bundle params = new Bundle();
                                        String nome = pedidos_categoria.get(16).toString();
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
                Toast.makeText(PedidosSalvos.this, "Nenhum pedido salvo!", Toast.LENGTH_SHORT).show();
            }
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.meuPerfil:
                UserAccessSession userAccess = UserAccessSession.getInstance(PedidosSalvos.this);
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
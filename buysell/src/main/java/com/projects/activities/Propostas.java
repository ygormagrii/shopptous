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
public class Propostas extends Activity {
    private TextView tvDescricao;
    private TextView tvQuantidade;
    private String Descricao;
    private String Unidade;
    private String Categoria;
    private String status;
    private String Qtd;
    private AlertDialog alerta;
    String user_id;
    InputStream is=null;
    String result=null;
    String line=null;
    ArrayList<String> pedidos_id = new ArrayList<String>();
    ArrayList<String> pedidos_ofcid = new ArrayList<String>();
    ArrayList<String> pedidos_categoria = new ArrayList<String>();
    ArrayList<String> pedidos_descricao = new ArrayList<String>();
    ArrayList<String> pedidos_data = new ArrayList<String>();
    ArrayList<String> pedidos_valor = new ArrayList<String>();
    ArrayList<String> pedidos_status = new ArrayList<String>();
    ArrayList<String> pedidos_loc = new ArrayList<String>();
    ArrayList<String> pedidos_lat = new ArrayList<String>();
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
        setContentView(R.layout.propostas);

        dbHelper = new DbHelper(this);
        q = new Queries(db, dbHelper);
        final Queries q = getQueries();

        this.getActionBar().setTitle("Shopptous");

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

            UserAccessSession userAccess = UserAccessSession.getInstance(Propostas.this);
            UserSession userSession = userAccess.getUserSession();
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            Intent intent = getIntent();
            String user_str = intent.getStringExtra("user_id");
            nameValuePairs.add(new BasicNameValuePair("user_id", user_str));

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://shopptous.com/propostas_recebidas.php");
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

                    pedidos_id.add((json_dat.getString("store_id")));
                    pedidos_ofcid.add((json_dat.getString("id")));
                    pedidos_data.add((json_dat.getString("data")));
                    pedidos_valor.add((json_dat.getString("valor")));
                    pedidos_status.add((json_dat.getString("status")));
                    pedidos_loc.add((json_dat.getString("loc")));
                    pedidos_lat.add((json_dat.getString("lat")));
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
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: "+pedidos_status.get(0)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Propostas.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 1) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: "+pedidos_status.get(0), "#ID: "+pedidos_id.get(1) + "  | Status: "+pedidos_status.get(1)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Propostas.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 2) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: "+pedidos_status.get(0), "#ID: "+pedidos_id.get(1) + "  | Status: "+pedidos_status.get(1), "#ID: "+pedidos_id.get(2) + "  | Status: "+pedidos_status.get(2)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Propostas.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 3) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: "+pedidos_status.get(0), "#ID: "+pedidos_id.get(1) + "  | Status: "+pedidos_status.get(1), "#ID: "+pedidos_id.get(2) + "  | Status: "+pedidos_status.get(2), "#ID: "+pedidos_id.get(3) + "  | Status: "+pedidos_status.get(3)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Propostas.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 4) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: "+pedidos_status.get(0), "#ID: "+pedidos_id.get(1) + "  | Status: "+pedidos_status.get(1), "#ID: "+pedidos_id.get(2) + "  | Status: "+pedidos_status.get(2), "#ID: "+pedidos_id.get(3) + "  | Status: "+pedidos_status.get(3), "#ID: "+pedidos_id.get(4) + "  | Status: "+pedidos_status.get(4)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Propostas.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 5) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: "+pedidos_status.get(0), "#ID: "+pedidos_id.get(1) + "  | Status: "+pedidos_status.get(1), "#ID: "+pedidos_id.get(2) + "  | Status: "+pedidos_status.get(2), "#ID: "+pedidos_id.get(3) + "  | Status: "+pedidos_status.get(3), "#ID: "+pedidos_id.get(4) + "  | Status: "+pedidos_status.get(4), "#ID: "+pedidos_id.get(5) + "  | Status: "+pedidos_status.get(5)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Propostas.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 6) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: "+pedidos_status.get(0), "#ID: "+pedidos_id.get(1) + "  | Status: "+pedidos_status.get(1), "#ID: "+pedidos_id.get(2) + "  | Status: "+pedidos_status.get(2), "#ID: "+pedidos_id.get(3) + "  | Status: "+pedidos_status.get(3), "#ID: "+pedidos_id.get(4) + "  | Status: "+pedidos_status.get(4), "#ID: "+pedidos_id.get(5) + "  | Status: "+pedidos_status.get(5), "#ID: "+pedidos_id.get(6) + "  | Status: "+pedidos_status.get(6)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Propostas.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 7) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: "+pedidos_status.get(0), "#ID: "+pedidos_id.get(1) + "  | Status: "+pedidos_status.get(1), "#ID: "+pedidos_id.get(2) + "  | Status: "+pedidos_status.get(2), "#ID: "+pedidos_id.get(3) + "  | Status: "+pedidos_status.get(3), "#ID: "+pedidos_id.get(4) + "  | Status: "+pedidos_status.get(4), "#ID: "+pedidos_id.get(5) + "  | Status: "+pedidos_status.get(5), "#ID: "+pedidos_id.get(6) + "  | Status: "+pedidos_status.get(6), "#ID: "+pedidos_id.get(7) + "  | Status: "+pedidos_status.get(7)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Propostas.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 8) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: "+pedidos_status.get(0), "#ID: "+pedidos_id.get(1) + "  | Status: "+pedidos_status.get(1), "#ID: "+pedidos_id.get(2) + "  | Status: "+pedidos_status.get(2), "#ID: "+pedidos_id.get(3) + "  | Status: "+pedidos_status.get(3), "#ID: "+pedidos_id.get(4) + "  | Status: "+pedidos_status.get(4), "#ID: "+pedidos_id.get(5) + "  | Status: "+pedidos_status.get(5), "#ID: "+pedidos_id.get(6) + "  | Status: "+pedidos_status.get(6), "#ID: "+pedidos_id.get(7) + "  | Status: "+pedidos_status.get(7), "#ID: "+pedidos_id.get(8) + "  | Status: "+pedidos_status.get(8)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Propostas.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    } else if (i == 9) {
                        String[] lista = {"#ID: "+pedidos_id.get(0) + "  | Status: "+pedidos_status.get(0), "#ID: "+pedidos_id.get(1) + "  | Status: "+pedidos_status.get(1), "#ID: "+pedidos_id.get(2) + "  | Status: "+pedidos_status.get(2), "#ID: "+pedidos_id.get(3) + "  | Status: "+pedidos_status.get(3), "#ID: "+pedidos_id.get(4) + "  | Status: "+pedidos_status.get(4), "#ID: "+pedidos_id.get(5) + "  | Status: "+pedidos_status.get(5), "#ID: "+pedidos_id.get(6) + "  | Status: "+pedidos_status.get(6), "#ID: "+pedidos_id.get(7) + "  | Status: "+pedidos_status.get(7), "#ID: "+pedidos_id.get(8) + "  | Status: "+pedidos_status.get(8), "#ID: "+pedidos_id.get(9) + "  | Status: "+pedidos_status.get(9)};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Propostas.this, android.R.layout.simple_list_item_1, lista);
                        listaPedido.setAdapter(adapter1);
                    }

                    listaPedido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> lista, View item, int posicao, long id) {
                            listaPedido.getItemAtPosition(posicao);
                            if (posicao == 0 ) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(0));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(0) + "\nData de entrega: " + pedidos_data.get(0) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 1){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(1));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(1) + "\nData de entrega: " + pedidos_data.get(1) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 2){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(2));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(2) + "\nData de entrega: " + pedidos_data.get(2) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 3){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(3));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(3) + "\nData de entrega: " + pedidos_data.get(3) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 4){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(4));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(4) + "\nData de entrega: " + pedidos_data.get(4) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 5){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(5));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(5) + "\nData de entrega: " + pedidos_data.get(5) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 6){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(6));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(6) + "\nData de entrega: " + pedidos_data.get(6) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 7){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(7));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(7) + "\nData de entrega: " + pedidos_data.get(7) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 8){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(8));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(8) + "\nData de entrega: " + pedidos_data.get(8) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 9){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(9));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(9) + "\nData de entrega: " + pedidos_data.get(9) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 10){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(10));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(10) + "\nData de entrega: " + pedidos_data.get(10) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 11){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(11));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(11) + "\nData de entrega: " + pedidos_data.get(11) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 12){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(12));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(12) + "\nData de entrega: " + pedidos_data.get(12) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 13){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(13));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(13) + "\nData de entrega: " + pedidos_data.get(13) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 14){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(14));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(14) + "\nData de entrega: " + pedidos_data.get(14) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 15){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(15));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(15) + "\nData de entrega: " + pedidos_data.get(15) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 16){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(16));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(16) + "\nData de entrega: " + pedidos_data.get(16) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 17){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(17));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(17) + "\nData de entrega: " + pedidos_data.get(17) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 18){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(18));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(18) + "\nData de entrega: " + pedidos_data.get(18) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 19){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(19));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(19) + "\nData de entrega: " + pedidos_data.get(19) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                alerta = builder.create();
                                alerta.show();
                            } else if(posicao == 20){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Propostas.this);//Cria o gerador do AlertDialog
                                builder.setTitle("Pedido: " + pedidos_id.get(20));
                                builder.setMessage("Valor da proposta: " + pedidos_valor.get(20) + "\nData de entrega: " + pedidos_data.get(20) + "\n\nOfertante dentro da área de 5km");
                                builder.setNegativeButton("Fechar", null);
                                builder.setPositiveButton("Aceitar Proposta", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent3 = new Intent(Propostas.this, AceitaProposta.class);
                                        Bundle params = new Bundle();
                                        String id = pedidos_ofcid.get(0).toString();
                                        params.putString("id", id);
                                        intent3.putExtras(params);
                                        startActivity(intent3);
                                        Toast.makeText(Propostas.this, "ID"+id, Toast.LENGTH_SHORT).show();
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
                            Intent intent = new Intent(Propostas.this, DetailActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });*/

                }
            }else{
                Toast.makeText(Propostas.this, "Nenhuma proposta salva!", Toast.LENGTH_SHORT).show();
            }
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.meuPerfil:
                UserAccessSession userAccess = UserAccessSession.getInstance(Propostas.this);
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
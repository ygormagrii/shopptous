package com.projects.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.Session;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.projects.buysell.MainActivity;
import com.projects.buysell.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;


/**
 * Created by Ygor on 02/06/2016.
 */
public class Cep extends Activity {
    String cep,logradouro,bairro,cidade,complemento,uf,resultado,chave,numero;
    EditText etCep, etLogradouro, etBairro, etCidade, etUf,etComplemento,etNumero;
    Button btnBuscar;
    private ProgressDialog dialog;
    String qtd;
    private AlertDialog alerta;
    private EditText edtEnd, edtCidade, edtEstado,edtData, edtHora, edtTroco;
    private List<String> pagamento = new ArrayList<String>();
    private List<String> horarios = new ArrayList<String>();
    private Spinner spn1,spn2;
    private String nomePagamento,data,end,estado,hora,troco;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    //to determine JSON signal insert success/fail
    private int success;
    // url to insert new idiom (change accordingly)
    private static String url_insert_new = "http://shopptous.compedidos_prod_insert.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cep);

        btnBuscar = (Button) findViewById(R.id.btnBuscar);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HttpGetter get = new HttpGetter();
                get.execute();

                etCep = (EditText) findViewById(R.id.etCep);
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(etCep.getWindowToken(), 0);

            }
        });

        this.getActionBar().setTitle("Shopptous");

        //Adicionando Nomes no ArrayList
        pagamento.add("Forma de Pagamento");
        pagamento.add("Dinheiro");
        pagamento.add("Cartão de Crédito");
        pagamento.add("Cartão de Débito");
        pagamento.add("Cheque");

        //Identifica o Spinner no layout
        spn1 = (Spinner) findViewById(R.id.Pagamento);
        //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, pagamento);
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spn1.setAdapter(spinnerArrayAdapter);

        //Método do Spinner para capturar o item selecionado
        spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                //pega nome pela posição
                nomePagamento = parent.getItemAtPosition(posicao).toString();
                //imprime um Toast na tela com o nome que foi selecionado
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Adicionando Nomes no ArrayList
        horarios.add("Selecione o horário de entrega");
        horarios.add("00:00");
        horarios.add("01:00");
        horarios.add("02:00");
        horarios.add("03:00");
        horarios.add("04:00");
        horarios.add("05:00");
        horarios.add("06:00");
        horarios.add("07:00");
        horarios.add("08:00");
        horarios.add("09:00");
        horarios.add("10:00");
        horarios.add("11:00");
        horarios.add("12:00");
        horarios.add("13:00");
        horarios.add("14:00");
        horarios.add("15:00");
        horarios.add("16:00");
        horarios.add("17:00");
        horarios.add("18:00");
        horarios.add("19:00");
        horarios.add("20:00");
        horarios.add("21:00");
        horarios.add("22:00");
        horarios.add("23:00");

        //Identifica o Spinner no layout
        spn2 = (Spinner) findViewById(R.id.edtHora);
        //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, horarios);
        spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spn2.setAdapter(spinnerArrayAdapter);

        //Método do Spinner para capturar o item selecionado
        spn2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                //pega nome pela posição
                hora = parent.getItemAtPosition(posicao).toString();
                //imprime um Toast na tela com o nome que foi selecionado
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btnFim = (Button) findViewById(R.id.btnEnd);
        edtData=(EditText) findViewById(R.id.edtData);
        MaskEditTextChangedListener maskData = new MaskEditTextChangedListener("##/##/####", edtData);
        edtData.addTextChangedListener(maskData);
        etCep = (EditText) findViewById(R.id.etCep);
        etLogradouro = (EditText) findViewById(R.id.etLogradouro);
        etBairro = (EditText) findViewById(R.id.etBairro);
        etCidade = (EditText) findViewById(R.id.etCidade);
        etUf = (EditText) findViewById(R.id.etUf);
        etComplemento = (EditText) findViewById(R.id.etComplemento);
        etNumero = (EditText) findViewById(R.id.etNumero);
        edtTroco=(EditText) findViewById(R.id.edtTroco);

        edtTroco.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtTroco.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            // Pega a formatacao do sistema, se for brasil R$ se EUA US$
            private NumberFormat nf = NumberFormat.getCurrencyInstance();

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int after) {
                // Evita que o método seja executado varias vezes.
                // Se tirar ele entre em loop
                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                isUpdating = true;
                String str = s.toString();
                // Verifica se já existe a máscara no texto.
                boolean hasMask = ((str.indexOf("R$") > -1 || str.indexOf("$") > -1) &&
                        (str.indexOf(".") > -1 || str.indexOf(",") > -1));
                // Verificamos se existe máscara
                if (hasMask) {
                    // Retiramos a máscara.
                    str = str.replaceAll("[R$]", "").replaceAll("[,]", "")
                            .replaceAll("[.]", "");
                }

                try {
                    // Transformamos o número que está escrito no EditText em
                    // monetário.
                    str = nf.format(Double.parseDouble(str) / 100);
                    edtTroco.setText(str);
                    edtTroco.setSelection(edtTroco.getText().length());
                } catch (NumberFormatException e) {
                    s = "";
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Não utilizamos
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Cep.this);//Cria o gerador do AlertDialog
                builder.setTitle("Confirma os dados enviados ?");//define o titulo
                builder.setMessage("OBS: Sempre coloque data superior ou igual ao dia atual.");
                //define um botão como positivo
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (edtData.getText().toString().length() > 0 && etLogradouro.getText().toString().length() != 0) {
                            Toast.makeText(Cep.this, "Pedido enviado com sucesso!", Toast.LENGTH_SHORT).show();
                            data = edtData.getText().toString();
                            end = etLogradouro.getText().toString();
                            cidade = etCidade.getText().toString();
                            complemento = etComplemento.getText().toString();
                            numero = etNumero.getText().toString();
                            estado = etUf.getText().toString();
                            troco = edtTroco.getText().toString();

                            new loadData().execute();

                        } else {
                            if (etLogradouro.getText().length() == 0) {//como o tamanho é zero é nulla aresposta
                                etLogradouro.setError("Campo vazio");
                            }

                            if (edtData.getText().length() == 0) {//como o tamanho é zero é nulla aresposta
                                edtData.setError("Campo vazio");
                            }

                            if (etCidade.getText().length() == 0) {//como o tamanho é zero é nulla aresposta
                                etCidade.setError("Campo vazio");
                            }

                            if (etNumero.getText().length() == 0) {//como o tamanho é zero é nulla aresposta
                                etNumero.setError("Campo vazio");
                            }

                            if (etComplemento.getText().length() == 0) {//como o tamanho é zero é nulla aresposta
                                etComplemento.setError("Campo vazio");
                            }

                            if (etUf.getText().length() == 0) {//como o tamanho é zero é nulla aresposta
                                etUf.setError("Campo vazio");
                            }

                            if (edtTroco.getText().length() == 0) {//como o tamanho é zero é nulla aresposta
                                edtTroco.setError("Campo vazio");
                            }

                        }

                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (edtData.getText().toString().length() > 0 && edtEnd.getText().toString().length() != 0) {
                            Toast.makeText(Cep.this, "Pedido salvo com sucesso!", Toast.LENGTH_SHORT).show();
                            data = edtData.getText().toString();
                            end = etLogradouro.getText().toString();
                            cidade = etCidade.getText().toString();
                            complemento = etComplemento.getText().toString();
                            numero = etNumero.getText().toString();
                            estado = etUf.getText().toString();
                            troco = edtTroco.getText().toString();

                            new loadSave().execute();

                        } else {
                            if (etLogradouro.getText().length() == 0) {//como o tamanho é zero é nulla aresposta
                                etLogradouro.setError("Campo vazio");
                            }

                            if (edtData.getText().length() == 0) {//como o tamanho é zero é nulla aresposta
                                edtData.setError("Campo vazio");
                            }

                            if (etCidade.getText().length() == 0) {//como o tamanho é zero é nulla aresposta
                                etCidade.setError("Campo vazio");
                            }

                            if (etNumero.getText().length() == 0) {//como o tamanho é zero é nulla aresposta
                                etNumero.setError("Campo vazio");
                            }

                            if (etComplemento.getText().length() == 0) {//como o tamanho é zero é nulla aresposta
                                etComplemento.setError("Campo vazio");
                            }

                            if (etUf.getText().length() == 0) {//como o tamanho é zero é nulla aresposta
                                etUf.setError("Campo vazio");
                            }

                            if (edtTroco.getText().length() == 0) {//como o tamanho é zero é nulla aresposta
                                edtTroco.setError("Campo vazio");
                            }

                        }

                    }
                });
                alerta = builder.create();//cria o AlertDialog
                alerta.show();//Exibe
            }
        });

        Button btnBack = (Button) findViewById(R.id.buttonCancelar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Cep.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private class HttpGetter extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {

            if (Cep.this.dialog != null) {
                Cep.this.dialog.dismiss();
            }
            Cep.this.dialog = ProgressDialog.show(Cep.this, "Aguarde", "Processando", true, false);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();

            cep = etCep.getText().toString();
            chave = "TCC4R3GWV";

            HttpGet httpGet = new HttpGet("https://viacep.com.br/ws/"+cep+"/json");

            try {
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    JSONObject jsonObj = new JSONObject(builder.toString());
                    logradouro = jsonObj.getString("logradouro");
                    bairro = jsonObj.getString("bairro");
                    cidade = jsonObj.getString("localidade");
                    complemento = jsonObj.getString("complemento");
                    uf = jsonObj.getString("uf");


                } else {
                    Log.e("Falha", "Falha ao acessar servico.");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void r) {
            Cep.this.dialog.dismiss();

                etLogradouro.setText(logradouro);
                etBairro.setText(bairro);
                etCidade.setText(cidade);
                etComplemento.setText(complemento);
                etUf.setText(uf);
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
            pDialog = new ProgressDialog(Cep.this);
            pDialog.setMessage("Enviando pedido ...");
            pDialog.setIndeterminate(false); pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            UserAccessSession userAccess = UserAccessSession.getInstance(Cep.this);
            UserSession userSession = userAccess.getUserSession();
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("data", data));
            nameValuePairs.add(new BasicNameValuePair("end", end));
            nameValuePairs.add(new BasicNameValuePair("cidade", cidade));
            nameValuePairs.add(new BasicNameValuePair("complemento", complemento));
            nameValuePairs.add(new BasicNameValuePair("numero", numero));
            nameValuePairs.add(new BasicNameValuePair("estado", estado));
            nameValuePairs.add(new BasicNameValuePair("hora", hora));
            nameValuePairs.add(new BasicNameValuePair("troco", troco));
            nameValuePairs.add(new BasicNameValuePair("nomePagamento", nomePagamento));
            nameValuePairs.add(new BasicNameValuePair("user_id", String.valueOf(userSession.getUser_id())));

            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://shopptous.compedidos_prod_insert.php");
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
            pDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Pedido enviado com sucesso", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Cep.this, PedidosPre.class);
            startActivity(intent);
            finish();
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.meuPerfil:
                UserAccessSession userAccess = UserAccessSession.getInstance(Cep.this);
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

    class loadSave extends android.os.AsyncTask<String, Integer, String> {
        private StringBuilder sb;
        private ProgressDialog pr;
        private HttpResponse req;
        private InputStream is;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Cep.this);
            pDialog.setMessage("Enviando pedido ...");
            pDialog.setIndeterminate(false); pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            UserAccessSession userAccess = UserAccessSession.getInstance(Cep.this);
            UserSession userSession = userAccess.getUserSession();
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("data", data));
            nameValuePairs.add(new BasicNameValuePair("end", end));
            nameValuePairs.add(new BasicNameValuePair("cidade", cidade));
            nameValuePairs.add(new BasicNameValuePair("complemento", complemento));
            nameValuePairs.add(new BasicNameValuePair("estado", estado));
            nameValuePairs.add(new BasicNameValuePair("hora", hora));
            nameValuePairs.add(new BasicNameValuePair("troco", troco));
            nameValuePairs.add(new BasicNameValuePair("nomePagamento", nomePagamento));
            nameValuePairs.add(new BasicNameValuePair("user_id", String.valueOf(userSession.getUser_id())));

            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://shopptous.compedidos_prod_save.php");
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
            pDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Pedido salvo com sucesso", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Cep.this, PedidosSalvos.class);
            startActivity(intent);
            finish();
        }

    }

}

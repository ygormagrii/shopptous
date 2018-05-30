package com.projects.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.db.DbHelper;
import com.db.Queries;
import com.facebook.Session;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.projects.buysell.MainActivity;
import com.projects.buysell.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FormCategorias extends Activity {

    String qtd;
    String descricao;
    InputStream is=null;
    String result=null;
    String line=null;
    int code;
    private Spinner spn1;
    private Spinner spn2;
    private List<String> categorias = new ArrayList<String>();
    private String nome;
    private String nomeUnidade;
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
        setContentView(R.layout.form_categorias);
        dbHelper = new DbHelper(this);
        q = new Queries(db, dbHelper);
        final Queries q = getQueries();

        this.getActionBar().setTitle("Shopptous");

        //Identifica o Spinner no layout
        ArrayList<String> categories = q.getCategoryNames();
        String allCategories = "Todas categorias";
        categories.add(0, allCategories);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                FormCategorias.this, android.R.layout.simple_spinner_item, categories);

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

        Button btnBack = (Button) findViewById(R.id.buttonCancelar);
        Button insert=(Button) findViewById(R.id.buttonCategoria);

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nome != "Todas categorias"){
                Bundle b = new Bundle();
                b.putString("nome", nome);
                Intent intent = new Intent(FormCategorias.this, InsertNewPedidos.class);
                intent.putExtras(b);
                startActivity(intent);
                }else{
                    Toast.makeText(FormCategorias.this, "Por favor selecione uma categoria", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormCategorias.this, MainActivity.class);
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
                UserAccessSession userAccess = UserAccessSession.getInstance(FormCategorias.this);
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
        getMenuInflater().inflate(R.menu.menu_form_pedido, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        // if nav drawer is opened, the action items
        return super.onPrepareOptionsMenu(menu);
    }

}
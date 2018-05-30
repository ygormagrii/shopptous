package com.projects.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.db.DbHelper;
import com.db.Queries;
import com.projects.fragments.SearchFragment;
import com.projects.buysell.R;

public class AcessoVendas extends FragmentActivity {

    private static SQLiteDatabase db;
    private static DbHelper dbHelper;
    private static Queries q;

    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new DbHelper(this);
        q = new Queries(db, dbHelper);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.acesso_vendas);

        SearchFragment frag;
        FragmentManager fm1 = AcessoVendas.this
                .getSupportFragmentManager();
        FragmentTransaction ft1 = fm1.beginTransaction();
        frag = new SearchFragment();


        ft1.replace(R.id.frame_container, new SearchFragment());
        ft1.commit();

    }
    public Queries getQueries() {
        return q;
    }



}

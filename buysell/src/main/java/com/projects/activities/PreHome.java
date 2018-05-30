package com.projects.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.projects.buysell.MainActivity;
import com.projects.buysell.R;

/**
 * Created by Ygor on 11/02/2016.
 */

public class PreHome extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_home);

        Button btnC = (Button) findViewById(R.id.buttonC);
        Button btnV= (Button) findViewById(R.id.buttonV);
        Button btnCad= (Button) findViewById(R.id.btnCadastro);

        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAccessSession userAccess = UserAccessSession.getInstance(PreHome.this);
                UserSession userSession = userAccess.getUserSession();
                if(userSession != null) {
                    Intent intent = new Intent(PreHome.this, FormCategorias.class);
                    startActivity(intent);

                }else{
                    Intent intent = new Intent(PreHome.this, LoginActivity.class);
                    startActivity(intent);
                }

            }
        });

        btnV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserAccessSession userAccess = UserAccessSession.getInstance(PreHome.this);
                UserSession userSession = userAccess.getUserSession();
                if(userSession != null) {
                    Intent intent = new Intent(PreHome.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(PreHome.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        btnCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreHome.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        UserAccessSession userAccess = UserAccessSession.getInstance(PreHome.this);
        UserSession userSession = userAccess.getUserSession();
        if(userSession != null) {
            btnCad.setVisibility(View.INVISIBLE);
        }

    }



}
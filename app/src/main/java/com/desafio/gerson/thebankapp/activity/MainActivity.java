package com.desafio.gerson.thebankapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.desafio.gerson.thebankapp.R;
import com.desafio.gerson.thebankapp.fragment.FragmentExtrato;
import com.desafio.gerson.thebankapp.fragment.FragmentMain;
import com.desafio.gerson.thebankapp.fragment.FragmentSaldo;
import com.desafio.gerson.thebankapp.fragment.FragmentServicos;
import com.desafio.gerson.thebankapp.fragment.FragmentTransacoes;
import com.desafio.gerson.thebankapp.model.Cliente;
import com.desafio.gerson.thebankapp.util.TheBankUtil;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment contentFragment;
    private Fragment fragment;

    Cliente cliente;
    String contaCorrente;
    String perfil;
    

    Bundle args = new Bundle();

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle bundle = getIntent().getExtras();
        contaCorrente = bundle.getString("contacorrente");
        perfil = bundle.getString("perfil");

        realm = Realm.getDefaultInstance();
        Log.i("---------->>>>>>", realm.getPath());
        realm.close();

        args.putString("contacorrente", contaCorrente);

        if (perfil.equals("normal")){
            setTheme(R.style.perfil_normal);
        } else {
            setTheme(R.style.perfil_vip);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cliente = Cliente.getClienteByContaCorrente(contaCorrente);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if(cliente.getPerfil().equals("vip")){
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    requisitaVisita();

                }
            });



        } else {
            fab.setVisibility(View.INVISIBLE);
        }


        //Starts first fragment
        if (findViewById(R.id.fragment_container) != null){
            if (savedInstanceState!=null){
                return;
            }
            contentFragment = new FragmentSaldo();

            contentFragment.setArguments(getIntent().getExtras());
            //switchContent(contentFragment, FragmentSaldo.FRAG_ID);
            TheBankUtil.switchContent(this, contentFragment, FragmentSaldo.FRAG_ID);
        }

        //Setup Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    //Set toolbar title
    public void setActionBarTitle(String title){

        getSupportActionBar().setTitle(title);

    }



    //Method to change fragments
    public void switchContent(Fragment fragment, String tag)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        while(fragmentManager.popBackStackImmediate());
        if(fragment != null)
        {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment, tag);

            //Adiciona apenas o product detail ao backstack
            if(!(fragment instanceof FragmentSaldo))
            {
                transaction.addToBackStack(tag);
            }

            transaction.commit();
            contentFragment = fragment;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_saldo) {

            contentFragment = new FragmentSaldo();
            contentFragment.setArguments(args);
            switchContent(contentFragment, FragmentSaldo.FRAG_ID);

        } else if (id == R.id.nav_extrato) {

            contentFragment = new FragmentExtrato();
            contentFragment.setArguments(args);
            //switchContent(contentFragment, FragmentExtrato.FRAG_ID);
            TheBankUtil.switchContent(this, contentFragment, FragmentExtrato.FRAG_ID);
        } else if (id == R.id.nav_transacoes) {

            contentFragment = new FragmentTransacoes();
            contentFragment.setArguments(args);
            //switchContent(contentFragment, FragmentTransacoes.FRAG_ID);
            TheBankUtil.switchContent(this, contentFragment, FragmentTransacoes.FRAG_ID);

        } else if (id == R.id.nav_servicos) {

            contentFragment = new FragmentServicos();
            contentFragment.setArguments(args);
            switchContent(contentFragment, FragmentServicos.FRAG_ID);
        } else if (id == R.id.nav_sair_do_aplicativo){

            logout();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.sair, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                //clear backstack on logout
                clearBackStack();
                startActivity(intent);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });

        builder.setTitle(R.string.titulo_sair_aplicacao);
        builder.setMessage(R.string.mensagem_sair_aplicacao);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void clearBackStack() {
        FragmentManager fm = this.getSupportFragmentManager();
        if(fm.getBackStackEntryCount()>0)
        {
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
        }
        else
        {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }

    public void requisitaVisita(){
        if(cliente.getPerfil().equals("vip")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton(R.string.confirma, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Snackbar.make(getWindow().getDecorView().getRootView(), R.string.visita_confirmada, Snackbar.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    return;
                }
            });

            builder.setTitle(R.string.titulo_solicita_visita);
            builder.setMessage(R.string.mensagem_solicita_visita);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


}


package com.desafio.gerson.thebankapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;

import com.desafio.gerson.thebankapp.R;
import com.desafio.gerson.thebankapp.fragment.FragmentSaldo;
import com.desafio.gerson.thebankapp.fragment.FragmentTransacoes;
import com.desafio.gerson.thebankapp.model.Cliente;
import com.desafio.gerson.thebankapp.model.Transacao;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by gerso on 5/24/2017.
 */

public class TheBankUtil {

    public static enum TipoTransacao{
        Saque,
        Deposito,
        Transferencia
    }

    public static final String TAG = TheBankUtil.class.getName();
    public static final String PREFERENCE_NAME = "TheBankPrefs";


//
//    //Atualiza cliente no BD
//    public static void atualizaSaldoCliente(Cliente cliente) {
//        Realm realm  = Realm.getDefaultInstance();
//        realm.beginTransaction();
//        Cliente c = realm.copyToRealmOrUpdate(cliente);
//        realm.commitTransaction();
//    }

    public static boolean isNetworkAvailable(Context ctx){
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo!=null && activeNetworkInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }

    public static void switchContent(FragmentActivity activity, Fragment fragment, String tag)
    {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        while(fragmentManager.popBackStackImmediate());
        if(fragment != null)
        {
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment, tag);

            //Adiciona apenas o product detail ao backstack
            if(!(fragment instanceof FragmentSaldo))
            {
                transaction.addToBackStack(tag);
            }

            transaction.commit();
        }
    }

    public static void alertBuilderTransacoes(final FragmentActivity activity, String tituloMensagem, String corpoMensagem){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(tituloMensagem);
        builder.setMessage(corpoMensagem)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}

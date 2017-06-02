package com.desafio.gerson.thebankapp.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.desafio.gerson.thebankapp.R;
import com.desafio.gerson.thebankapp.activity.MainActivity;
import com.desafio.gerson.thebankapp.model.Cliente;
import com.desafio.gerson.thebankapp.model.Transacao;
import com.desafio.gerson.thebankapp.util.TheBankUtil;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static java.math.BigDecimal.valueOf;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSaldo extends Fragment {

    public static final String FRAG_ID = "SALDO";
    private Bundle args;
    private String mContaCorrente;

    private FloatingActionButton fab;

    Cliente cliente;

    FragmentActivity activity;

    FragmentManager fragmentManager;

    @BindView(R.id.textview_saldo_nome)
    TextView saldoNome;

    @BindView(R.id.textview_saldo_valor_saldo)
    TextView valorSaldo;


    public FragmentSaldo() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        args = getArguments();
        mContaCorrente = args.getString("contacorrente");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        activity = getActivity();



    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_saldo, container, false);
        cliente = Cliente.getClienteByContaCorrente(mContaCorrente);
        ButterKnife.bind(this, view);

        String saldoAtual = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(cliente.getSaldo());

        if(cliente.getPerfil().equals("normal")){
            saldoNome.setText(cliente.getNome());
            valorSaldo.setText(saldoAtual);


        } else{

            Realm realm = Realm.getDefaultInstance();
            RealmList<Transacao> transacoes = cliente.getTransacoes();
            realm.close();

            Transacao transacao = transacoes.last();

            Log.i("transacoes=========>", transacao.toString());

            if (cliente.getSaldo() < 0){
                valorSaldo.setTextColor(ContextCompat.getColor(activity, R.color.red));
            } else {
                valorSaldo.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            }



        }
        return view;













    }



}

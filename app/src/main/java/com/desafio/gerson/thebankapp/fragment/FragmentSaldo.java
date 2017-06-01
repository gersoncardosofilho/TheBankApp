package com.desafio.gerson.thebankapp.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.desafio.gerson.thebankapp.R;
import com.desafio.gerson.thebankapp.activity.MainActivity;
import com.desafio.gerson.thebankapp.model.Cliente;
import com.desafio.gerson.thebankapp.util.TheBankUtil;

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
        ((MainActivity) getActivity()).setActionBarTitle(FRAG_ID);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_saldo, container, false);

        ButterKnife.bind(this, view);

        activity = getActivity();

        cliente = Cliente.getClienteByContaCorrente(mContaCorrente);

        saldoNome.setText(cliente.getNome());
        valorSaldo.setText(valueOf(cliente.getSaldo()).toString());

        Realm realm = Realm.getDefaultInstance();

        //RealmList<Cliente> transacoes = cliente.getTransacoes();

        realm.close();
        return view;
    }

}

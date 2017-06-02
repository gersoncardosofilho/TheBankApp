package com.desafio.gerson.thebankapp.fragment;


import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.desafio.gerson.thebankapp.R;
import com.desafio.gerson.thebankapp.activity.MainActivity;
import com.desafio.gerson.thebankapp.adapter.ClienteExtratoAdapter;
import com.desafio.gerson.thebankapp.model.Cliente;
import com.desafio.gerson.thebankapp.model.Transacao;
import com.desafio.gerson.thebankapp.util.TheBankUtil;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentExtrato extends Fragment {

    public static final String FRAG_ID = "EXTRATO";

    private Bundle args;
    private String mContaCorrente;
    private FloatingActionButton fab;

    NumberFormat nf = NumberFormat.getCurrencyInstance();



    public FragmentExtrato() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).setActionBarTitle(FRAG_ID);

        ((MainActivity) getActivity()).setActionBarTitle(FRAG_ID);
        fab = ((MainActivity) getActivity()).getFab();

        //hides fab in fragments
        if (fab!= null){
            fab.setVisibility(View.INVISIBLE);
        }

        args = getArguments();
        mContaCorrente = args.getString("contacorrente");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_extrato, container, false);
        ListView listView = (ListView) view.findViewById(R.id.list_transacoes);
        Cliente cliente = Cliente.getClienteByContaCorrente(mContaCorrente);
        TextView textviewSaldoAtual = (TextView) view.findViewById(R.id.textview_extrato_saldoatual);
        String saldoAtual = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(cliente.getSaldo());

        RealmResults<Transacao> transacoes = (RealmResults<Transacao>) Cliente.listaExtratoCliente(cliente);
        listView.setAdapter(new ClienteExtratoAdapter(getContext(), transacoes));
        textviewSaldoAtual.setText(saldoAtual);

        return view;
    }

}

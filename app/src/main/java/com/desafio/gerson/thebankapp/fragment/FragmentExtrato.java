package com.desafio.gerson.thebankapp.fragment;


import android.os.Bundle;

import android.support.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentExtrato extends Fragment {

    public static final String FRAG_ID = "EXTRATO";

    private Bundle args;
    private String mContaCorrente;

    public FragmentExtrato() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).setActionBarTitle(FRAG_ID);

        args = getArguments();
        mContaCorrente = args.getString("contacorrente");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_extrato, container, false);
        ListView listView = (ListView) view.findViewById(R.id.list_transacoes);
        Cliente cliente = Cliente.getClienteByContaCorrente(mContaCorrente);

        RealmResults<Transacao> transacoes = (RealmResults<Transacao>) Cliente.listaExtratoCliente(cliente);
        listView.setAdapter(new ClienteExtratoAdapter(getContext(), transacoes));

        return view;
    }

}

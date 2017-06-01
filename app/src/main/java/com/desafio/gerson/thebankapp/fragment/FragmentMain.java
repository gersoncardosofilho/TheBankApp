package com.desafio.gerson.thebankapp.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.desafio.gerson.thebankapp.R;
import com.desafio.gerson.thebankapp.activity.MainActivity;
import com.desafio.gerson.thebankapp.adapter.ClienteAdapter;
import com.desafio.gerson.thebankapp.model.Cliente;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMain extends Fragment {


    public static final String FRAG_ID = "USUÁRIOS";

    LinearLayoutManager linearLayoutManager;
    FragmentActivity activity;

    RecyclerView recyclerView;

    private Bundle args;
    private String mContaCorrente;


    Realm realm;

    private ArrayList<Cliente> clientes = new ArrayList<>();

    public FragmentMain() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).setActionBarTitle(FRAG_ID);
        activity = getActivity();

        args = getArguments();
        mContaCorrente = args.getString("contacorrente");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_lista_clientes);

        linearLayoutManager = new LinearLayoutManager(activity, 1, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        realm = Realm.getDefaultInstance();

        clientes = getClientes();

        recyclerView.setAdapter(new ClienteAdapter(activity, clientes));

        return view;
    }

    private ArrayList<Cliente> getClientes(){

        ArrayList<Cliente> clientesList = new ArrayList<>();

        realm.beginTransaction();

        RealmResults<Cliente> results = realm.where(Cliente.class)
                .findAll();

        realm.commitTransaction();

        clientesList.addAll(results);

        return clientesList;
    }

}

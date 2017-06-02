package com.desafio.gerson.thebankapp.fragment;


import android.content.Context;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.desafio.gerson.thebankapp.R;
import com.desafio.gerson.thebankapp.activity.MainActivity;
import com.desafio.gerson.thebankapp.util.TheBankUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTransacoes extends Fragment {

    public static final String FRAG_ID = "TRANSAÇÕES";

    private Bundle args;
    private String mContaCorrente;

//    @BindView(R.id.btnSaque)
//    Button buttonSaque;
//
    @BindView(R.id.framelayout_saque)
    FrameLayout frameLayoutSaque;

    @BindView(R.id.framelayout_deposito)
    FrameLayout frameLayoutDeposito;

    @BindView(R.id.framelayout_transferencia)
    FrameLayout frameLayoutTransferencia;

    private Fragment contentFragment;

    Bundle contentFragmentArgs;


    FragmentActivity activity;


    public FragmentTransacoes() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).setActionBarTitle(FRAG_ID);

        args = getArguments();
        mContaCorrente = args.getString("contacorrente");
        contentFragmentArgs = args;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_transacoes, container, false);

        ButterKnife.bind(this, view);

        frameLayoutSaque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                contentFragment = new FragmentTransacaoSaque();
                contentFragment.setArguments(contentFragmentArgs);
                TheBankUtil.switchContent(activity, contentFragment, FragmentTransacaoSaque.FRAG_ID);
            }
        });

        frameLayoutDeposito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentFragment = new FragmentTransacaoDeposito();
                contentFragment.setArguments(contentFragmentArgs);
                TheBankUtil.switchContent(activity, contentFragment, FragmentTransacaoDeposito.FRAG_ID);
            }
        });

        frameLayoutTransferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentFragment = new FragmentTransacaoTransferencia();
                contentFragment.setArguments(contentFragmentArgs);
                TheBankUtil.switchContent(activity, contentFragment, FragmentTransacaoTransferencia.FRAG_ID);
            }
        });


        return view;
    }



}

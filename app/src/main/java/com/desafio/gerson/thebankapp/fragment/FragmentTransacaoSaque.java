package com.desafio.gerson.thebankapp.fragment;


import android.content.Context;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.desafio.gerson.thebankapp.R;
import com.desafio.gerson.thebankapp.activity.MainActivity;
import com.desafio.gerson.thebankapp.model.Cliente;
import com.desafio.gerson.thebankapp.model.Transacao;
import com.desafio.gerson.thebankapp.util.TheBankUtil;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTransacaoSaque extends Fragment implements View.OnClickListener {

    private Bundle args, contentFragmentArgs;
    private String mContaCorrente;

    private FragmentActivity activity;

    Cliente cliente;

    public static final String FRAG_ID = "Transacao_Saque";

    private Fragment contentFragment;

    TextView textviewSaqueSaldo;
    EditText  edittextSaqueValor;
    Button buttonConfirmaSaque;
    View root = null;


    public FragmentTransacaoSaque() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();

        args = getArguments();
        mContaCorrente = args.getString("contacorrente");
        contentFragmentArgs = args;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) getActivity()).setActionBarTitle("Saque");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transacao_saque, container, false);

        root = view;

        textviewSaqueSaldo = (TextView) view.findViewById(R.id.textview_saque_saldo);
        edittextSaqueValor = (EditText) view.findViewById(R.id.editText_saque_valor);
        buttonConfirmaSaque = (Button) view.findViewById(R.id.btn_saque_confirma);

        cliente = Cliente.getClienteByContaCorrente(mContaCorrente);
        String saldoAtual = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(cliente.getSaldo());
        textviewSaqueSaldo.setText(saldoAtual);

        buttonConfirmaSaque.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        if (TextUtils.isEmpty(edittextSaqueValor.getText().toString())){
            edittextSaqueValor.setError(getString(R.string.campo_obrigatorio));
            root = edittextSaqueValor;
            root.requestFocus();
        } else {
            boolean response;
            Double valorSaque = Double.parseDouble(edittextSaqueValor.getText().toString());
            String tipoTransacao = "saque";
            Date dataTransacao = Calendar.getInstance().getTime();
            Double valorAtualizado = cliente.getSaldo() + valorSaque;

            response = Cliente.debitaContaCliente(cliente, valorSaque);

            if (response){
                //cria objeto transacao origem
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                Transacao transacaoOrigem = new Transacao(
                        tipoTransacao,
                        valorSaque,
                        valorAtualizado,
                        cliente.getSaldo(),
                        dataTransacao,
                        cliente.getNumeroConta(),
                        null);
                realm.commitTransaction();
                realm.close();

                Transacao.adicionaTransacaoBD(cliente, transacaoOrigem);

                String titulo =(String) activity.getResources().getText(R.string.mensagem_titulo);
                String mensagem =(String) activity.getResources().getText(R.string.saque_efetuado_com_sucesso);

                TheBankUtil.alertBuilderTransacoes(activity, titulo, mensagem);
            } else
            {
                String mensagemTitulo = (String) activity.getResources().getText(R.string.mensagem_titulo);
                String corpoMensagem = (String) activity.getResources().getText(R.string.saldo_insuficiente);
                TheBankUtil.alertBuilderTransacoes(activity,mensagemTitulo, corpoMensagem);
            }

            contentFragment = new FragmentSaldo();
            contentFragment.setArguments(contentFragmentArgs);
            TheBankUtil.switchContent(activity, contentFragment, FragmentTransacaoSaque.FRAG_ID);
        }
    }
}

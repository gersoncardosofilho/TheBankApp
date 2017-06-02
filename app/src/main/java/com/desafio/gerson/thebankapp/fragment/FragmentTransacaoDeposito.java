package com.desafio.gerson.thebankapp.fragment;


import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import java.util.UUID;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTransacaoDeposito extends Fragment implements View.OnClickListener {

    public static final String FRAG_ID = "Transacao_Deposito";

    private Bundle args, contentFragmentArgs;
    private String mContaCorrente;

    private FragmentActivity activity;

    Fragment contentFragment;

    Cliente cliente;

    TextView textviewDepositoSaldo;
    EditText  edittextDepositoValor;
    Button buttonConfirmaDeposito;
    View root;

    public FragmentTransacaoDeposito() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();

        ((MainActivity) getActivity()).setActionBarTitle("Depósito");

        args = getArguments();
        mContaCorrente = args.getString("contacorrente");
        contentFragmentArgs = args;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_transacao_deposito, container, false);

        root = view;

        textviewDepositoSaldo = (TextView) view.findViewById(R.id.textview_deposito_saldo);
        edittextDepositoValor = (EditText) view.findViewById(R.id.editText_deposito_valor);
        buttonConfirmaDeposito = (Button) view.findViewById(R.id.btn_deposito_confirma);

        cliente = Cliente.getClienteByContaCorrente(mContaCorrente);

        String saldoAtual = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(cliente.getSaldo());

        textviewDepositoSaldo.setText(saldoAtual);

        buttonConfirmaDeposito.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        boolean response;

        Double valorDeposito = Double.parseDouble(edittextDepositoValor.getText().toString());
        String tipoTransacao = "depósito";
        Date dataTransacao = Calendar.getInstance().getTime();
        Double saldoAtual = cliente.getSaldo();
        Double saldoAtualizado = saldoAtual + valorDeposito;

        response = Cliente.executaDepositoCliente(valorDeposito, cliente);

        //cria objeto transacao origem
        Realm  realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Transacao transacaoOrigem = new Transacao(
                tipoTransacao,
                valorDeposito,
                saldoAtual,
                saldoAtualizado,
                dataTransacao,
                cliente.getNumeroConta(),
                null);
        realm.commitTransaction();
        realm.close();

        Transacao.adicionaTransacaoBD(cliente, transacaoOrigem);


        if (response) {
            String titulo = (String) activity.getResources().getText(R.string.mensagem_titulo);
            String mensagem = (String) activity.getResources().getText(R.string.saque_efetuado_com_sucesso);

            TheBankUtil.alertBuilderTransacoes(activity, titulo, mensagem);

            contentFragment = new FragmentSaldo();
            contentFragment.setArguments(contentFragmentArgs);
            TheBankUtil.switchContent(activity, contentFragment, FragmentTransacaoSaque.FRAG_ID);
        }
    }
}

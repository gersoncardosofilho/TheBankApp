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
import com.desafio.gerson.thebankapp.model.Cliente;
import com.desafio.gerson.thebankapp.util.TheBankUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTransacaoTransferencia extends Fragment implements View.OnClickListener {

    public static final String FRAG_ID = "Transacao_Transferencia";

    private Bundle args, contentFragmentArgs;
    private String mContaCorrente;

    private FragmentActivity activity;

    Fragment contentFragment;

    Cliente cliente;

    TextView textviewDepositoNome, textviewDepositoSaldo;
    EditText edittextDepositoValor, editTextDepositoContaDestino;
    Button buttonConfirmaDeposito;

    View root;

    public FragmentTransacaoTransferencia() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();

        args = getArguments();
        mContaCorrente = args.getString("contacorrente");
        contentFragmentArgs = args;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transacao_transferencia, container, false);
        root = view;

        textviewDepositoNome =(TextView) view.findViewById(R.id.textview_transferencia_nome);
        textviewDepositoSaldo = (TextView) view.findViewById(R.id.textview_transferencia_saldo);
        edittextDepositoValor = (EditText) view.findViewById(R.id.editText_transferencia_valor);
        editTextDepositoContaDestino = (EditText) view.findViewById(R.id.editText_transferencia_conta_destino);
        buttonConfirmaDeposito = (Button) view.findViewById(R.id.btn_deposito_confirma);

        cliente = Cliente.getClienteByContaCorrente(mContaCorrente);
        textviewDepositoNome.setText(cliente.getNome());
        textviewDepositoSaldo.setText(new Double(cliente.getSaldo()).toString());




        buttonConfirmaDeposito.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Double valorTransferencia = Double.parseDouble(edittextDepositoValor.getText().toString());
        String contaDestino = editTextDepositoContaDestino.getText().toString();

        if (cliente.getPerfil().equals("normal")){
            //transfer from normal account
            if( valorTransferencia > 1000){
                String titulo =(String) activity.getResources().getText(R.string.atencao);
                String mensagem =(String) activity.getResources().getText(R.string.mensagem_transferencia_acima_limite);

                TheBankUtil.alertBuilderTransacoes(activity, titulo, mensagem);
            } else if (cliente.getNumeroConta().equals(contaDestino)){
                String titulo =(String) activity.getResources().getText(R.string.atencao);
                String mensagem =(String) activity.getResources().getText(R.string.mensagem_transferencia_mesma_conta);

                TheBankUtil.alertBuilderTransacoes(activity, titulo, mensagem);
            } else {
                Cliente.executaTransferenciaEntreContas(valorTransferencia,contaDestino,cliente);

                String titulo =(String) activity.getResources().getText(R.string.mensagem_titulo);
                String mensagem =(String) activity.getResources().getText(R.string.mensagem_transferencia_sucesso);

                TheBankUtil.alertBuilderTransacoes(activity, titulo, mensagem);

                contentFragment = new FragmentSaldo();
                contentFragment.setArguments(contentFragmentArgs);
                TheBankUtil.switchContent(activity, contentFragment, FragmentTransacaoSaque.FRAG_ID);
            }
        } else {
            //transfer from vip account

        }
    }
}
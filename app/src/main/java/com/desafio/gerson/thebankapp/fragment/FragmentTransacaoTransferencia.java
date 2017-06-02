package com.desafio.gerson.thebankapp.fragment;


import android.os.Bundle;

import android.support.annotation.Nullable;
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

import static com.desafio.gerson.thebankapp.model.Cliente.getClienteByContaCorrente;

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

    TextView textviewDepositoSaldo;
    EditText edittextDepositoValor, editTextDepositoContaDestino;
    Button buttonConfirmaTransferencia;

    View root = null;

    public FragmentTransacaoTransferencia() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();

        ((MainActivity) getActivity()).setActionBarTitle("Transferência");

        args = getArguments();
        mContaCorrente = args.getString("contacorrente");
        contentFragmentArgs = args;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transacao_transferencia, container, false);
        root = view;

        textviewDepositoSaldo = (TextView) view.findViewById(R.id.textview_transferencia_saldo);
        edittextDepositoValor = (EditText) view.findViewById(R.id.editText_transferencia_valor);
        editTextDepositoContaDestino = (EditText) view.findViewById(R.id.editText_transferencia_conta_destino);
        buttonConfirmaTransferencia = (Button) view.findViewById(R.id.button_transferencia_confirma);

        cliente = getClienteByContaCorrente(mContaCorrente);

        String saldoAtual = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(cliente.getSaldo());

        textviewDepositoSaldo.setText(saldoAtual);

        buttonConfirmaTransferencia.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {


        boolean cancel = false;

        if (TextUtils.isEmpty(edittextDepositoValor.getText().toString())){
            edittextDepositoValor.setError(getString(R.string.campo_obrigatorio));
            root = edittextDepositoValor;
            cancel = true;
        } else if(TextUtils.isEmpty(editTextDepositoContaDestino.getText().toString())) {
            editTextDepositoContaDestino.setError(getString(R.string.campo_obrigatorio));
            root = editTextDepositoContaDestino;
            cancel = true;
        }

        if (cancel){
            root.requestFocus();
        } else {
            Double valorTransferencia = Double.parseDouble(edittextDepositoValor.getText().toString());
            String contaDestino = editTextDepositoContaDestino.getText().toString();

            if (Cliente.eValido(contaDestino)) {
                if (cliente.getPerfil().equals("normal")) {
                    //transfer from normal account
                    if (valorTransferencia > 1000) {
                        String titulo = (String) activity.getResources().getText(R.string.atencao);
                        String mensagem = (String) activity.getResources().getText(R.string.mensagem_transferencia_acima_limite);

                        TheBankUtil.alertBuilderTransacoes(activity, titulo, mensagem);
                    } else if (cliente.getNumeroConta().equals(contaDestino)) {
                        String titulo = (String) activity.getResources().getText(R.string.atencao);
                        String mensagem = (String) activity.getResources().getText(R.string.mensagem_transferencia_mesma_conta);

                        TheBankUtil.alertBuilderTransacoes(activity, titulo, mensagem);
                    } else if(cliente.getSaldo() - valorTransferencia < 0) {
                        String titulo = (String) activity.getResources().getText(R.string.atencao);
                        String mensagem = (String) activity.getResources().getText(R.string.saldo_insuficiente);

                        TheBankUtil.alertBuilderTransacoes(activity, titulo, mensagem);
                        return;
                    }

                    Double saldoAtualOrigem = cliente.getSaldo();
                    Double saltoAtualizadoOrigem = saldoAtualOrigem - valorTransferencia;
                    Date dataTransacao = Calendar.getInstance().getTime();
                    String tipoTransacaoOrigem = "transferencia p/ CC " + contaDestino;
                    String tipoTransacaoDestino = "transferencia da CC " + cliente.getNumeroConta();
                    Realm realm = Realm.getDefaultInstance();

                    Cliente.debitaContaCliente(cliente, valorTransferencia);

                    //cria objeto transacao origem
                    realm.beginTransaction();
                    Transacao transacaoOrigem = new Transacao(
                            tipoTransacaoOrigem,
                            valorTransferencia,
                            saldoAtualOrigem,
                            saltoAtualizadoOrigem,
                            dataTransacao,
                            cliente.getNumeroConta(),
                            contaDestino);
                    realm.commitTransaction();
                    realm.close();

                    Transacao.adicionaTransacaoBD(cliente, transacaoOrigem);

                    Cliente clienteDestino = Cliente.getClienteByContaCorrente(contaDestino);


                    Double saldoAtualDestino = clienteDestino.getSaldo();
                    Double saldoAtualizadoDestino = saldoAtualDestino + valorTransferencia;

                    Cliente.executaDepositoCliente(valorTransferencia, clienteDestino);

                    //cria objeto transacao destino
                    realm.beginTransaction();
                    Transacao transacaoDestino = new Transacao(
                            tipoTransacaoDestino,
                            valorTransferencia,
                            saldoAtualDestino,
                            saldoAtualizadoDestino,
                            dataTransacao,
                            cliente.getNumeroConta(),
                            contaDestino);
                    realm.commitTransaction();
                    realm.close();

                    Transacao.adicionaTransacaoBD(clienteDestino  , transacaoDestino);


                    String titulo = (String) activity.getResources().getText(R.string.mensagem_titulo);
                    String mensagem = (String) activity.getResources().getText(R.string.mensagem_transferencia_sucesso);

                    TheBankUtil.alertBuilderTransacoes(activity, titulo, mensagem);

                    contentFragment = new FragmentSaldo();
                    contentFragment.setArguments(contentFragmentArgs);
                    TheBankUtil.switchContent(activity, contentFragment, FragmentTransacaoSaque.FRAG_ID);

                } else {
                    //transfer from vip account

                }
            } else {
                String titulo = (String) activity.getResources().getText(R.string.mensagem_titulo);
                String mensagem = (String) activity.getResources().getText(R.string.usuario_nao_encontrado);

                TheBankUtil.alertBuilderTransacoes(activity, titulo, mensagem);
            }
        }




    }
}

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
import java.util.Calendar;
import java.util.Date;
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

    String saldoVipAtualFormatado;

    Transacao ultimaTransacao;

    @BindView(R.id.textview_saldo_nome)
    TextView saldoNome;

    @BindView(R.id.textview_saldo_valor_saldo)
    TextView valorSaldo;

    View view = null;


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

        if (cliente.getPerfil().equals("normal")) {

            saldoNome.setText(cliente.getNome());
            valorSaldo.setText(NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(cliente.getSaldo()));
        } else {

            RealmList<Transacao> transacoes = cliente.getTransacoes();

            if (transacoes.isEmpty()) {

                saldoNome.setText(cliente.getNome());
                valorSaldo.setText(NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(cliente.getSaldo()));

            } else {

                ultimaTransacao = cliente.getTransacoes().last();
                Log.i("Ultima transacao", ultimaTransacao.toString());

                if (ultimaTransacao.getSaldoAtual() > 0) {
                    saldoNome.setText(cliente.getNome());
                    valorSaldo.setText(NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(cliente.getSaldo()));
                } else {

                    calculaSaldo();
                }
            }
        }

        return view;
    }


    public void calculaSaldo() {
        if(cliente.getSaldo() < 0 )
        {
            saldoNome.setText(cliente.getNome());
            valorSaldo.setText(NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(cliente.getSaldo()));
        } else {
            Realm realm = Realm.getDefaultInstance();
            RealmList<Transacao> transacoes = cliente.getTransacoes();
            realm.close();

            Transacao transacao = transacoes.last();

            if (transacao == null) {
                String titulo = (String) activity.getResources().getText(R.string.mensagem_titulo);
                String mensagem = (String) activity.getResources().getText(R.string.usuario_nao_encontrado);

                TheBankUtil.alertBuilderTransacoes(activity, titulo, "Voce ainda nao tem movimentacoes");


            } else {

                Double saldoVipAtual = transacao.getSaldoAtualizado();
                Double saldoVipAnterior = transacao.getSaldoAtual();
                String tipoTransacao = "tarifa a descoberto";
                Date dataTransacao = Calendar.getInstance().getTime();

                //Transacao ultimaTransacaoSaldoNegativo = transacoes.where().equalTo("SaldoAtualizado", saldoVipAnterior).findFirst();

                RealmResults<Transacao> results = realm.where(Transacao.class).equalTo("SaldoAtualizado",saldoVipAnterior).findAll();

                Transacao ultimaTransacaoSaldoNegativo = results.first();

                Date dataUltimoSaldoNegativo = ultimaTransacaoSaldoNegativo.getDataTransacao();

                Date dataAtual = transacao.getDataTransacao();

                long baseCalculo = (dataAtual.getTime() / 60000) - (dataUltimoSaldoNegativo.getTime() / 60000);

                Double valorADebitar = baseCalculo * 0.1;

                Double valorDebitado = cliente.getSaldo() - valorADebitar;

                Log.i("Saldo novo ===>", valorADebitar.toString());

                Cliente.debitaContaCliente(cliente, valorADebitar);

                //cria objeto transacao debito
                realm.beginTransaction();
                Transacao transacaoOrigem = new Transacao(
                        tipoTransacao,
                        valorADebitar,
                        valorDebitado,
                        cliente.getSaldo(),
                        dataTransacao,
                        cliente.getNumeroConta(),
                        null);
                realm.commitTransaction();
                realm.close();

                Transacao.adicionaTransacaoBD(cliente, transacaoOrigem);

                saldoNome.setText(cliente.getNome());
                valorSaldo.setText(NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valorDebitado));

            }
        }
    }
}






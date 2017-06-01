package com.desafio.gerson.thebankapp.model;

import android.icu.math.BigDecimal;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by gerso on 5/24/2017.
 */

public class Transacao extends RealmObject {

    @PrimaryKey
    private String Id;

    private String TipoTransacao;

    private double ValorTransacao;

    private Date DataTransacao;

    private String ContaCorrenteOrigem;

    private String ContaCorrenteDestino;

    private double SaldoAnterior;

    private double SaldoAtual;


    //constructor
    public Transacao(){}

    public Transacao(String tipoTransacao, double saldoAnterior,
                     double valorTransacao, double saldoAtual,
                     Date dataTransacao,
                     String contaCorrenteOrigem,
                     String contaCorrenteDestino){
        this.Id = UUID.randomUUID().toString();
        this.TipoTransacao = tipoTransacao;
        this.ValorTransacao = valorTransacao;
        this.DataTransacao = dataTransacao;
        this.ContaCorrenteOrigem = contaCorrenteOrigem;
        this.ContaCorrenteDestino = contaCorrenteDestino;
    }

    public String getId() {
        return Id;
    }

    public double getSaldoAnterior() {
        return SaldoAnterior;
    }

    public void setSaldoAnterior(double saldoAnterior) {
        SaldoAnterior = saldoAnterior;
    }

    public double getSaldoAtual() {
        return SaldoAtual;
    }

    public void setSaldoAtual(double saldoAtual) {
        SaldoAtual = saldoAtual;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getIdTransacao() {
        return Id;
    }

    public String getTipoTransacao() {
        return TipoTransacao;
    }

    public double getValorTransacao() {
        return ValorTransacao;
    }

    public Date getDataTransacao() {
        return DataTransacao;
    }

    public void setTipoTransacao(String tipoTransacao) {
        TipoTransacao = tipoTransacao;
    }

    public void setValorTransacao(double ValorTransacao) {
        this.ValorTransacao = ValorTransacao;
    }

    public void setDataTransacao(Date DataTransacao) {
        this.DataTransacao = DataTransacao;
    }

    public String getContaCorrenteOrigem() {
        return ContaCorrenteOrigem;
    }

    public void setContaCorrenteOrigem(String contaCorrenteOrigem) {
        ContaCorrenteOrigem = contaCorrenteOrigem;
    }

    public String getContaCorrenteDestino() {
        return ContaCorrenteDestino;
    }

    public void setContaCorrenteDestino(String contaCorrenteDestino) {
        ContaCorrenteDestino = contaCorrenteDestino;
    }

    public static void adicionaTransacaoBD(final Cliente cliente, final Transacao transacao){

        Realm realm = Realm.getDefaultInstance();
        final Calendar calendar = Calendar.getInstance();
        final String contaOrigem = cliente.getNumeroConta();
        final Cliente clienteToUpdate = cliente;


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                realm.copyToRealmOrUpdate(transacao);
                clienteToUpdate.transacoes.add(transacao);
            }
        });
    }
}

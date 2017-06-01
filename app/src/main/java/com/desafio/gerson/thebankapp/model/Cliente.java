package com.desafio.gerson.thebankapp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Created by gerso on 5/24/2017.
 */

public class Cliente extends RealmObject {


    @PrimaryKey
    private String Id;
    private String Nome;
    private String Senha;
    private String Perfil;
    private String Cpf;
    private double Saldo;
    private String NumeroConta;
    private String NumeroAgencia;
    public RealmList<Transacao> transacoes;

    //constructor
    public Cliente(){}

    public Cliente(String nome, String senha, String perfil, String cpf, Double saldo, String numeroConta, String numeroAgencia)
    {
        this.Id = UUID.randomUUID().toString();
        this.Nome = nome;
        this.Senha = senha;
        this.Perfil = perfil;
        this.Cpf = cpf;
        this.Saldo = saldo;
        this.NumeroAgencia = numeroAgencia;
        this.NumeroConta = numeroConta;
    }

    //Add client to DB
    public static void addCliente(Cliente cliente) {


        Realm realm  = Realm.getDefaultInstance();
        realm.beginTransaction();
        Cliente c = realm.copyToRealm(cliente);
        realm.commitTransaction();
    }

    //Search for client on DB
    public static Cliente getClienteByContaCorrente(String contaCorrente) {
        Realm realm = Realm.getDefaultInstance();
        Cliente result = realm.where(Cliente.class).equalTo("NumeroConta", contaCorrente).findFirst();

        return result;
    }


    //Atualiza cliente no BD
    public static void atualizaSaldoCliente(Cliente clienteNovoSaldo) {
        Realm realm  = Realm.getDefaultInstance();
        realm.beginTransaction();
        Cliente c = realm.copyToRealmOrUpdate(clienteNovoSaldo);
        realm.commitTransaction();
    }

    public static boolean executaSaqueCliente(FragmentActivity activity, Cliente cliente, Double valorSaque){

        Calendar calendar = Calendar.getInstance();
        Realm realm = Realm.getDefaultInstance();
        Transacao transacao = new Transacao();
        Double saldoAnterior;
        Double saldoAtual = cliente.getSaldo();
        Date dataSaque = calendar.getTime();

        saldoAnterior = saldoAtual;

        if(cliente.getPerfil().equals("normal")){
            if (saldoAtual > 0 && (saldoAtual - valorSaque > 0)){
                //withdraw normal client
                saldoAtual = saldoAtual - valorSaque;

                realm.beginTransaction();
                cliente.setSaldo(saldoAtual);
                realm.commitTransaction();

                atualizaSaldoCliente(cliente);

                //cria objeto transacao
                realm.beginTransaction();
                transacao.setId(UUID.randomUUID().toString());
                transacao.setValorTransacao(valorSaque);
                transacao.setTipoTransacao("saque");
                transacao.setDataTransacao(dataSaque);
                transacao.setContaCorrenteOrigem(cliente.getNumeroConta());
                transacao.setSaldoAnterior(saldoAnterior);
                transacao.setSaldoAtual(saldoAtual);
                realm.commitTransaction();
                realm.close();

                Transacao.adicionaTransacaoBD(cliente, transacao);

                return true;

            } else {

                return false;
            }
        }else{
                //withdraw vip client
            Date dataCorrente = calendar.getTime();
            saldoAtual = saldoAtual - valorSaque;

            realm.beginTransaction();
            cliente.setSaldo(saldoAtual);
            realm.commitTransaction();

            atualizaSaldoCliente(cliente);

            //cria objeto transacao
            realm.beginTransaction();
            transacao.setId(UUID.randomUUID().toString());
            transacao.setValorTransacao(valorSaque);
            transacao.setTipoTransacao("saque");
            transacao.setDataTransacao(dataSaque);
            transacao.setContaCorrenteOrigem(cliente.getNumeroConta());
            transacao.setSaldoAnterior(saldoAnterior);
            transacao.setSaldoAtual(saldoAtual);
            realm.commitTransaction();
            realm.close();


            Transacao.adicionaTransacaoBD(cliente, transacao);

            return true;

        }
    }

    public static boolean executaDepositoCliente(Double valorDeposito, Cliente cliente){

        Realm realm = Realm.getDefaultInstance();
        Double saldoAtual = cliente.getSaldo();
        saldoAtual = saldoAtual + valorDeposito;

        realm.beginTransaction();
        cliente.setSaldo(saldoAtual);
        realm.commitTransaction();

        atualizaSaldoCliente(cliente);
        //Transacao.adicionaTransacaoBD(cliente, "deposito", valorDeposito);

        return true;
    }

    public static void debitaContaCliente(final Cliente cliente, Double valorADebitar){

        Realm realm = Realm.getDefaultInstance();

        final Double novoSaldo = cliente.getSaldo() - valorADebitar;

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                cliente.setSaldo(novoSaldo);
            }
        });

    }

    public static void executaTransferenciaEntreContas(Double valorASerTransferido, String contaDestino, Cliente cliente){

        Realm realm = Realm.getDefaultInstance();

        Cliente clienteDestino = getClienteByContaCorrente(contaDestino);
        executaDepositoCliente(valorASerTransferido, clienteDestino);
        debitaContaCliente(cliente, valorASerTransferido);

        String tipoTransacaoOrigem = "transfencia p/ CC " + contaDestino;
        String tipoTransacaoDestino = "transferencia da CC " + cliente.getNumeroConta();

        //Transacao.adicionaTransacaoBD(cliente, tipoTransacaoOrigem, valorASerTransferido);
        //Transacao.adicionaTransacaoBD(clienteDestino, tipoTransacaoDestino, valorASerTransferido);
    }

    public static List<Transacao> listaExtratoCliente(final Cliente cliente){

        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        RealmList<Transacao> transacoes = cliente.getTransacoes();
        RealmResults<Transacao> results = transacoes.where().findAll();
        realm.commitTransaction();
        realm.close();

        Log.i("Results --------->>", results.toString());

        return results;
    }


    public void setId(String id) {
        Id = id;
    }

    public String getId() {
        return Id;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getSenha() {
        return Senha;
    }

    public void setSenha(String senha) {
        Senha = senha;
    }

    public String getPerfil() {
        return Perfil;
    }

    public void setPerfil(String perfil) {
        Perfil = perfil;
    }

    public String getCpf() {
        return Cpf;
    }

    public void setCpf(String cpf) {
        Cpf = cpf;
    }

    public double getSaldo() {
        return Saldo;
    }

    public void setSaldo(double Saldo) {
        this.Saldo = Saldo;
    }

    public String getNumeroConta() {
        return NumeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        NumeroConta = numeroConta;
    }

    public String getNumeroAgencia() {
        return NumeroAgencia;
    }

    public void setNumeroAgencia(String numeroAgencia) {
        NumeroAgencia = numeroAgencia;
    }

    public RealmList<Transacao> getTransacoes() {
        return transacoes;
    }
}


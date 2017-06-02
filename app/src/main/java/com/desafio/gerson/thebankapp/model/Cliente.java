package com.desafio.gerson.thebankapp.model;

import android.content.Context;
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
    public static void atualizaSaldoCliente(Cliente cliente) {
        Realm realm  = Realm.getDefaultInstance();
        realm.beginTransaction();
        Cliente c = realm.copyToRealmOrUpdate(cliente);
        realm.commitTransaction();
    }

    public static boolean executaDepositoCliente(Double valorDeposito, Cliente cliente){

        Realm realm = Realm.getDefaultInstance();
        Double saldoAtual = cliente.getSaldo();
        Double saldoAnterior = saldoAtual;
        saldoAtual = saldoAtual + valorDeposito;
        Transacao transacao = new Transacao();
        Calendar calendar = Calendar.getInstance();
        Date dataTransacao = calendar.getTime();

        realm.beginTransaction();
        cliente.setSaldo(saldoAtual);
        realm.commitTransaction();

        atualizaSaldoCliente(cliente);

        return true;
    }

    public static boolean debitaContaCliente(final Cliente cliente, Double valorADebitar){

        Realm realm = Realm.getDefaultInstance();
        Double saldoAtual = cliente.getSaldo();

        if(cliente.getPerfil().equals("normal")){
            if (saldoAtual > 0 && (saldoAtual - valorADebitar > 0)){
                //withdraw normal client
                saldoAtual = saldoAtual - valorADebitar;

                realm.beginTransaction();
                cliente.setSaldo(saldoAtual);
                realm.commitTransaction();

                atualizaSaldoCliente(cliente);

                return true;

            } else {

                return false;
            }
        }else{
            //withdraw vip client
            saldoAtual = saldoAtual - valorADebitar;

            realm.beginTransaction();
            cliente.setSaldo(saldoAtual);
            realm.commitTransaction();

            atualizaSaldoCliente(cliente);

            return true;

        }
    }

    public static boolean eValido(String numeroConta){
        Cliente response = Cliente.getClienteByContaCorrente(numeroConta);
        if (response != null){
            return true;
        } else {
            return false;
        }
    }

//    public static void executaTransferenciaEntreContas(Double valorASerTransferido, String contaDestino, Cliente cliente){
//
//        Realm realm = Realm.getDefaultInstance();
//
//
//        Calendar calendar = Calendar.getInstance();
//        Date dataTransacao = calendar.getTime();
//        double saldoOrigemAtual = cliente.getSaldo() - valorASerTransferido;
//
//        Cliente clienteDestino = getClienteByContaCorrente(contaDestino);
//        double saldoDestinoAtual = clienteDestino.getSaldo() + valorASerTransferido;
//        executaDepositoCliente(valorASerTransferido, clienteDestino);
//        debitaContaCliente(cliente, valorASerTransferido);
//
//        //cria objeto transacao cliente origem
//        realm.beginTransaction();
//        Transacao transacaoOrigem = new Transacao(
//                tipoTransacaoOrigem,
//                cliente.getSaldo(),
//                valorASerTransferido,
//                saldoOrigemAtual,
//                dataTransacao,
//                cliente.getNumeroConta(),
//                contaDestino);
//        realm.commitTransaction();
//        realm.close();
//
//
//        //cria objeto transacao cliente destino
//        realm.beginTransaction();
//        Transacao transacaoDestino = new Transacao(
//                tipoTransacaoDestino,
//                clienteDestino.getSaldo(),
//                valorASerTransferido,
//                saldoDestinoAtual,
//                dataTransacao,
//                cliente.getNumeroConta(),
//                clienteDestino.getNumeroConta());
//        realm.commitTransaction();
//        realm.close();
//
//
//        Transacao.adicionaTransacaoBD(cliente, transacaoOrigem);
//        Transacao.adicionaTransacaoBD(clienteDestino, transacaoDestino);
//    }

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


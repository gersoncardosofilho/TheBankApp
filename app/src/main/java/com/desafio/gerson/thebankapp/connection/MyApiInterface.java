package com.desafio.gerson.thebankapp.connection;

import com.desafio.gerson.thebankapp.model.Cliente;
import com.desafio.gerson.thebankapp.model.Transacao;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by gerso on 5/24/2017.
 */

public interface MyApiInterface {

    //Clients
    /**
     * Get user from API
     * @return code 200 - success operation
     */
    //login
    @GET("api/cliente/login/")
    Call<Cliente> getUserLogin(@QueryMap Map<String,String> data);

    @GET("api/cliente/{contaCorrente}")
    Call<Cliente> getUserByContaCorrente(@Path("contaCorrente") String contaCorrente);

    //logout
    @GET("api/users/logout")
    Call<Cliente> getUserLogout();

    //edit saldo
    @PUT("api/users")
    Call<Cliente> putSaldo(@Body Cliente cliente);


    //Transacoes
    //insert payment
    @POST("api/payments")
    Call<Transacao> postTransacao(@Body Transacao transacao);

    //Extratos

}

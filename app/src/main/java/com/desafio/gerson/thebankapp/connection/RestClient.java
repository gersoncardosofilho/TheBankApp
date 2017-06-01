package com.desafio.gerson.thebankapp.connection;

import com.desafio.gerson.thebankapp.model.Cliente;
import com.desafio.gerson.thebankapp.model.Transacao;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gerso on 5/24/2017.
 */

public class RestClient {

    private static final String TAG = RestClient.class.getName();
    private static final String BASE_URL = "http://gersondeveloper-com.umbler.net/";
    private static final int TIMEOUT = 60000;

    private static RestClient instance;
    private Retrofit retrofit;
    MyApiInterface  apiService;

    //Singleton Initializer
    public static void initialize()
    {
        if(instance == null)
        {
            instance = new RestClient();
        }
    }

    //Singleton Instance Getter
    public static RestClient getInstance()
    {
        initialize();

        return instance;
    }

    //Constructor
    private RestClient()
    {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();

        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.apiService = retrofit.create(MyApiInterface.class);
    }

    public void getUserLogin(Map<String,String> data, Callback<Cliente> callback)
    {
        Call<Cliente> call = apiService.getUserLogin(data);
        call.enqueue(callback);

    }

    public void registraTransacao(Transacao transacao, Callback<Transacao> callback)
    {
        Call<Transacao>call = apiService.postTransacao(transacao);
        call.enqueue(callback);
    }
}

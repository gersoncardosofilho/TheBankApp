package com.desafio.gerson.thebankapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.desafio.gerson.thebankapp.MyApplication;
import com.desafio.gerson.thebankapp.R;
import com.desafio.gerson.thebankapp.connection.RestClient;
import com.desafio.gerson.thebankapp.model.Cliente;
import com.desafio.gerson.thebankapp.util.TheBankUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();

    @BindView(R.id.activity_login)
    View root;

    @BindView(R.id.login_edit_text_conta_corrente)
    EditText numeroContaEditText;

    @BindView(R.id.login_edit_text_senha)
    EditText senhaEditText;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    Cliente cliente;

    Long nextKey;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);


    }

    @OnClick(R.id.btnLogar)
    public void login(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);

        if (TheBankUtil.isNetworkAvailable(this)){

            String numeroConta = numeroContaEditText.getText().toString().trim();
            String senha = senhaEditText.getText().toString().trim();

            boolean cancel = false;
            View focusView = null;

            if (TextUtils.isEmpty(numeroConta)){
                numeroContaEditText.setError(getString(R.string.campo_obrigatorio));
                focusView = numeroContaEditText;
                cancel = true;
            }

            else if (TextUtils.isEmpty(senha)){
                senhaEditText.setError(getString(R.string.campo_obrigatorio));
                focusView = senhaEditText;
                cancel = true;
            }

            if (cancel){
                focusView.requestFocus();
            }else{
                attemptLogin(numeroConta, senha);
            }
        } else{
            Snackbar.make(root, R.string.sem_conexao, Snackbar.LENGTH_LONG).show();
        }
    }

    private void attemptLogin(String numeroConta, String senha)
    {

            progressBar.setVisibility(View.VISIBLE);

            Map<String,String> data = new HashMap<>();
            data.put("contaCorrente", numeroConta);
            data.put("senha", senha);

            RestClient.getInstance().getUserLogin(data, loginCallback);
    }

    private Callback<Cliente> loginCallback = new Callback<Cliente>() {
        @Override
        public void onResponse(Call<Cliente> call, final Response<Cliente> response) {
            //login succeeded logic
            int statusCode = response.code();
            final Cliente clienteResponse = response.body();
            //status code 200 = success operation
            //open mainactivity passing user

            if(statusCode == 200)
            {
                Log.d(TAG,"Login realizado com sucesso");
                progressBar.setVisibility(View.GONE);
                Snackbar.make(root, R.string.login_sucesso, Snackbar.LENGTH_LONG).show();

                //Checks for client

                cliente = Cliente.getClienteByContaCorrente(clienteResponse.getNumeroConta());

                // if client do not exists save him to db else, move on
                if (cliente == null){
                    realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                        Cliente newCliente = new Cliente();
                        newCliente.setNome(clienteResponse.getNome());
                        newCliente.setCpf(clienteResponse.getCpf());
                        newCliente.setId(UUID.randomUUID().toString());
                        newCliente.setNumeroAgencia(clienteResponse.getNumeroAgencia());
                        newCliente.setPerfil(clienteResponse.getPerfil());
                        newCliente.setSenha(clienteResponse.getSenha());
                        newCliente.setSaldo(clienteResponse.getSaldo());
                        newCliente.setNumeroConta(clienteResponse.getNumeroConta());
                    realm.commitTransaction();
                    realm.close();

                    Cliente.addCliente(newCliente);
                }
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("contacorrente", clienteResponse.getNumeroConta());
                    intent.putExtra("perfil", clienteResponse.getPerfil());
                    startActivity(intent);
            }
            //status code 400 = invalid username/password suplied
            //launch snackbar with invalid username/password message
            else if(statusCode == 404){
                progressBar.setVisibility(View.GONE);
                Snackbar.make(root, R.string.usuario_nao_encontrado, Snackbar.LENGTH_LONG).show();
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, R.string.sem_conexao, Toast.LENGTH_LONG).show();
                return;
            }

        }

        @Override
        public void onFailure(Call<Cliente> call, Throwable t) {
            //login failed logic
            Log.d(TAG, "Falha de acesso ao banco. Tente novamente." + t.getMessage());
        }
    };
}

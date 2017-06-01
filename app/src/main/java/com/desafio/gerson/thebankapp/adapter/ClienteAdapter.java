package com.desafio.gerson.thebankapp.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.desafio.gerson.thebankapp.R;
import com.desafio.gerson.thebankapp.model.Cliente;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by gerso on 5/27/2017.
 */

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.MyViewHolder> {

    private ArrayList<Cliente> dataSet;
    private FragmentActivity activity;
    private LayoutInflater inflater;
    View root;

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nomeTextView;
        TextView numeroContaTextView;

        public MyViewHolder(View itemView){
            super(itemView);

        }
    }

    public ClienteAdapter(FragmentActivity activity, ArrayList<Cliente> data){
        this.activity = activity;
        this.dataSet = data;
    }

    @Override
    public ClienteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.clientes_listitem, parent, false);
        root = view.findViewById(R.id.cliente_list_nome);

        ClienteAdapter.MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(ClienteAdapter.MyViewHolder holder, int position) {
        TextView nomeTextView = holder.nomeTextView;
        TextView numeroContaCorrente = holder.numeroContaTextView;

        nomeTextView.setText(dataSet.get(position).getNome());
        numeroContaCorrente.setText(dataSet.get(position).getNumeroConta());
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

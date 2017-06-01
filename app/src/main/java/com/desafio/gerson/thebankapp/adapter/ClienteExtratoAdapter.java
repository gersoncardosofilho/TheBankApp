package com.desafio.gerson.thebankapp.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.desafio.gerson.thebankapp.R;
import com.desafio.gerson.thebankapp.model.Transacao;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by gerso on 5/31/2017.
 */

public class ClienteExtratoAdapter extends RealmBaseAdapter<Transacao> implements ListAdapter {

    private Context context;

    private static class ViewHolder{
        TextView dataTransacao, descricaoTransacao, valorTransacao;
    }


    public ClienteExtratoAdapter(Context context, @Nullable OrderedRealmCollection<Transacao> data) {
        super(data);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.extrato_row_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.dataTransacao = (TextView) convertView.findViewById(R.id.textview_extrato_data);
            viewHolder.descricaoTransacao = (TextView) convertView.findViewById(R.id.textview_extrato_descricao);
            viewHolder.valorTransacao = (TextView) convertView.findViewById(R.id.textview_extrato_valor);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(adapterData !=null){

            DateFormat df = new SimpleDateFormat("dd/mm");

            String data = DateFormat.getDateInstance(DateFormat.DATE_FIELD, Locale.forLanguageTag("pt-BR)")).format(getItem(position).getDataTransacao());
            //String data = df.format(getItem(position).getDataTransacao());
            String descricao = getItem(position).getTipoTransacao().toString();
            String valor = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(getItem(position).getValorTransacao());



            if (descricao.equals("saque") || descricao.contains("ncia pa") || descricao.contains("taxa")){
                viewHolder.valorTransacao.setTextColor(ContextCompat.getColor(context, R.color.red));

            } else {
                viewHolder.valorTransacao.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

            }

            viewHolder.dataTransacao.setText(data);
            viewHolder.descricaoTransacao.setText(descricao);
            viewHolder.valorTransacao.setText(valor);
        }

        return convertView;
    }
}

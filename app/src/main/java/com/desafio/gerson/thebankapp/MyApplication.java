package com.desafio.gerson.thebankapp;

import android.app.Application;

import com.desafio.gerson.thebankapp.model.Cliente;
import com.desafio.gerson.thebankapp.util.PrimaryKeyFactory;

import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by gerso on 5/24/2017.
 */

public class MyApplication extends Application {



    @Override
    public void onCreate() {
        super.onCreate();

        //Initialize Realm Database
        Realm.init(this);
//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
//                .name("Database.realm")
//                .schemaVersion(8)
//                .build();
//
//        //Realm.setDefaultConfiguration(realmConfiguration);
//
//        Realm realm = Realm.getInstance(realmConfiguration);
//
//        primaryKeyFactory.initialize(realm);
//
//        realm.close();

//        Realm realm = Realm.getDefaultInstance();
//
//        PrimaryKeyFactory pkf = PrimaryKeyFactory.getInstance();
//        pkf.initialize(realm);
//
//        realm.close();
//
  }



}

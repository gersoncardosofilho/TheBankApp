package com.desafio.gerson.thebankapp;

import com.desafio.gerson.thebankapp.model.Cliente;

import org.junit.Test;

import io.realm.Realm;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TheBankUnitTest {



    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void TestaSaqueClienteNormal(){

        boolean success;

        Cliente cliente = new Cliente();
        cliente.setNome("Nome Cliente");
        cliente.setPerfil("normal");
        cliente.setSaldo(1000.00);
        cliente.setCpf("888888888-88");
        cliente.setNumeroAgencia("0001");
        cliente.setNumeroConta("12345");
        cliente.setSenha("Cliente senha");

        //success = Cliente.executaSaqueCliente(null, cliente, 2000.00);

        assertTrue("saldo insuficiente", false);
    }

    @Test
    public void TestaSaqueClienteVip(){

    }
}
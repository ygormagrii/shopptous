package com.models;

import java.io.Serializable;

public class Pedidos implements Serializable{


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Integer pedidos_id;
    private String pedidos_email;
    private String pedidos_nome;

    public Integer getPedidos_id() {
        return pedidos_id;
    }

    public void setPedidos_id(Integer pedidos_id) {
        this.pedidos_id = pedidos_id;
    }


    public String getPedidos_email() {
        return pedidos_email;
    }

    public void setPedidos_email(String pedidos_email) {
        this.pedidos_email = pedidos_email;
    }

    public String getPedidos_nome() {
        return pedidos_nome;
    }

    public void setPedidos_nome(String pedidos_nome) {
        this.pedidos_nome = pedidos_nome;
    }
}

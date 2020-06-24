package com.example.myapplication.ui;

public class Contacto {
    String nombre;
    String numero;
    boolean check;

    public Contacto(String nombre, String numero, boolean check) {
        this.nombre = nombre;
        this.numero = numero;
        this.check = check;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}

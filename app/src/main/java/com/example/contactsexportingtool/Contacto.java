package com.example.contactsexportingtool;

class Contacto {
    String id;
    String nombre;
    String telefonos;

    public Contacto(String id, String nombre, String listaTelefonos) {
        this.id = id;
        this.nombre = nombre;
        this.telefonos = listaTelefonos;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getTelefonos() {
        return this.telefonos;
    }
}


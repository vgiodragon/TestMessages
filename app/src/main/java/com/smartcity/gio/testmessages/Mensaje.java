package com.smartcity.gio.testmessages;


/**
 * Created by gio on 9/13/17.
 */

public class Mensaje {
    String json;
    String hora;

    public Mensaje(String json, String hora) {
        this.json = json;
        this.hora = hora;
    }

    public String getJson() {
        return json;
    }

    public String getHora() {
        return hora;
    }
}

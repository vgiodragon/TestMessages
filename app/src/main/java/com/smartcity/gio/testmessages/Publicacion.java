package com.smartcity.gio.testmessages;

/**
 * Created by gio on 11/14/17.
 */

public class Publicacion {
    String fecha_llegada;
    String hora_llegada;
    String fecha_llegada_ntp;
    String hora_llegada_ntp;
    String hora_envio;
    String fecha_envio;
    double value;


    public Publicacion(String fecha_llegada, String hora_llegada, String fecha_llegada_ntp, String hora_llegada_ntp,
                       String hora_envio, String fecha_envio, double value) {
        this.fecha_llegada = fecha_llegada;
        this.hora_llegada = hora_llegada;
        this.fecha_llegada_ntp = fecha_llegada_ntp;
        this.hora_llegada_ntp = hora_llegada_ntp;
        this.hora_envio = hora_envio;
        this.fecha_envio = fecha_envio;
        this.value = value;
    }

    @Override
    public String toString() {
        return  fecha_llegada + ", " + hora_llegada + ", " + fecha_llegada_ntp + ", " + hora_llegada_ntp
                + ", " +hora_envio +", "+ fecha_envio +", "+ value+"\n";
    }
}

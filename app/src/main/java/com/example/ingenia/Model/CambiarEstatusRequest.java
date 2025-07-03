package com.example.ingenia.Model;

public class CambiarEstatusRequest {
    private int idEstatus;

    public CambiarEstatusRequest(int idEstatus) {
        this.idEstatus = idEstatus;
    }

    public int getIdEstatus() {
        return idEstatus;
    }

    public void setIdEstatus(int idEstatus) {
        this.idEstatus = idEstatus;
    }
}

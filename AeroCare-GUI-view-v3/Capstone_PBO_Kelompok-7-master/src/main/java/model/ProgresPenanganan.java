package model;

import java.sql.Timestamp;

public class ProgresPenanganan {

    private int idProgres;
    private Timestamp waktuUpdate;
    private String statusProgres;
    private String tindakanPenanganan;
    private int penugasanIdPenugasan;

    public ProgresPenanganan() {
    }

    public ProgresPenanganan(int idProgres, Timestamp waktuUpdate,
            String statusProgres, String tindakanPenanganan,
            int penugasanIdPenugasan) {

        this.idProgres = idProgres;
        this.waktuUpdate = waktuUpdate;
        this.statusProgres = statusProgres;
        this.tindakanPenanganan = tindakanPenanganan;
        this.penugasanIdPenugasan = penugasanIdPenugasan;
    }

    public int getIdProgres() {
        return idProgres;
    }

    public void setIdProgres(int idProgres) {
        this.idProgres = idProgres;
    }

    public Timestamp getWaktuUpdate() {
        return waktuUpdate;
    }

    public void setWaktuUpdate(Timestamp waktuUpdate) {
        this.waktuUpdate = waktuUpdate;
    }

    public String getStatusProgres() {
        return statusProgres;
    }

    public void setStatusProgres(String statusProgres) {
        this.statusProgres = statusProgres;
    }

    public String getTindakanPenanganan() {
        return tindakanPenanganan;
    }

    public void setTindakanPenanganan(String tindakanPenanganan) {
        this.tindakanPenanganan = tindakanPenanganan;
    }

    public int getPenugasanIdPenugasan() {
        return penugasanIdPenugasan;
    }

    public void setPenugasanIdPenugasan(int penugasanIdPenugasan) {
        this.penugasanIdPenugasan = penugasanIdPenugasan;
    }
}
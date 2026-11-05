package model;

import java.sql.Date;

public class Penugasan {

    private int idPenugasan;
    private Date waktuPenugasan;
    private String catatanPenugasan;
    private int supervisorIdPengguna;
    private int laporanIdLaporan;
    private int idPengguna;

    public Penugasan() {
    }

    public Penugasan(int idPenugasan, Date waktuPenugasan,
            String catatanPenugasan, int supervisorIdPengguna,
            int laporanIdLaporan, int idPengguna) {

        this.idPenugasan = idPenugasan;
        this.waktuPenugasan = waktuPenugasan;
        this.catatanPenugasan = catatanPenugasan;
        this.supervisorIdPengguna = supervisorIdPengguna;
        this.laporanIdLaporan = laporanIdLaporan;
        this.idPengguna = idPengguna;
    }

    public int getIdPenugasan() {
        return idPenugasan;
    }

    public void setIdPenugasan(int idPenugasan) {
        this.idPenugasan = idPenugasan;
    }

    public Date getWaktuPenugasan() {
        return waktuPenugasan;
    }

    public void setWaktuPenugasan(Date waktuPenugasan) {
        this.waktuPenugasan = waktuPenugasan;
    }

    public String getCatatanPenugasan() {
        return catatanPenugasan;
    }

    public void setCatatanPenugasan(String catatanPenugasan) {
        this.catatanPenugasan = catatanPenugasan;
    }

    public int getSupervisorIdPengguna() {
        return supervisorIdPengguna;
    }

    public void setSupervisorIdPengguna(int supervisorIdPengguna) {
        this.supervisorIdPengguna = supervisorIdPengguna;
    }

    public int getLaporanIdLaporan() {
        return laporanIdLaporan;
    }

    public void setLaporanIdLaporan(int laporanIdLaporan) {
        this.laporanIdLaporan = laporanIdLaporan;
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }
}
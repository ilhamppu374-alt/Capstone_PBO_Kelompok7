package model;

import java.sql.Date;

public class LaporanKerusakan {

    private int idLaporan;
    private Date tanggalLaporan;
    private String deskripsiKerusakan;
    private String prioritas;
    private String status;
    private int fasilitasIdFasilitas;
    private int adminIdPengguna;

    public LaporanKerusakan() {
    }

    public LaporanKerusakan(int idLaporan, Date tanggalLaporan,
            String deskripsiKerusakan, String prioritas,
            String status, int fasilitasIdFasilitas,
            int adminIdPengguna) {

        this.idLaporan = idLaporan;
        this.tanggalLaporan = tanggalLaporan;
        this.deskripsiKerusakan = deskripsiKerusakan;
        this.prioritas = prioritas;
        this.status = status;
        this.fasilitasIdFasilitas = fasilitasIdFasilitas;
        this.adminIdPengguna = adminIdPengguna;
    }

    public int getIdLaporan() {
        return idLaporan;
    }

    public void setIdLaporan(int idLaporan) {
        this.idLaporan = idLaporan;
    }

    public Date getTanggalLaporan() {
        return tanggalLaporan;
    }

    public void setTanggalLaporan(Date tanggalLaporan) {
        this.tanggalLaporan = tanggalLaporan;
    }

    public String getDeskripsiKerusakan() {
        return deskripsiKerusakan;
    }

    public void setDeskripsiKerusakan(String deskripsiKerusakan) {
        this.deskripsiKerusakan = deskripsiKerusakan;
    }

    public String getPrioritas() {
        return prioritas;
    }

    public void setPrioritas(String prioritas) {
        this.prioritas = prioritas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFasilitasIdFasilitas() {
        return fasilitasIdFasilitas;
    }

    public void setFasilitasIdFasilitas(int fasilitasIdFasilitas) {
        this.fasilitasIdFasilitas = fasilitasIdFasilitas;
    }

    public int getAdminIdPengguna() {
        return adminIdPengguna;
    }

    public void setAdminIdPengguna(int adminIdPengguna) {
        this.adminIdPengguna = adminIdPengguna;
    }
}
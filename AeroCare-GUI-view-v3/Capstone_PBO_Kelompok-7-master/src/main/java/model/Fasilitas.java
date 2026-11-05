package model;

public class Fasilitas {

    private int idFasilitas;
    private String namaFasilitas;
    private String jenisFasilitas;
    private String lokasi;
    private String kondisi;
    private String keterangan;
    private int adminIdPengguna;

    public Fasilitas() {
    }

    public Fasilitas(int idFasilitas, String namaFasilitas,
            String jenisFasilitas, String lokasi, String kondisi,
            String keterangan, int adminIdPengguna) {

        this.idFasilitas = idFasilitas;
        this.namaFasilitas = namaFasilitas;
        this.jenisFasilitas = jenisFasilitas;
        this.lokasi = lokasi;
        this.kondisi = kondisi;
        this.keterangan = keterangan;
        this.adminIdPengguna = adminIdPengguna;
    }

    public int getIdFasilitas() {
        return idFasilitas;
    }

    public void setIdFasilitas(int idFasilitas) {
        this.idFasilitas = idFasilitas;
    }

    public String getNamaFasilitas() {
        return namaFasilitas;
    }

    public void setNamaFasilitas(String namaFasilitas) {
        this.namaFasilitas = namaFasilitas;
    }

    public String getJenisFasilitas() {
        return jenisFasilitas;
    }

    public void setJenisFasilitas(String jenisFasilitas) {
        this.jenisFasilitas = jenisFasilitas;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getKondisi() {
        return kondisi;
    }

    public void setKondisi(String kondisi) {
        this.kondisi = kondisi;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public int getAdminIdPengguna() {
        return adminIdPengguna;
    }

    public void setAdminIdPengguna(int adminIdPengguna) {
        this.adminIdPengguna = adminIdPengguna;
    }
}
package model;

public class DetailPenugasan {

    private int idDetail;
    private String statusKeterlibatan;
    private int petugasIdPengguna;
    private int penugasanIdPenugasan;

    public DetailPenugasan() {
    }

    public DetailPenugasan(int idDetail, String statusKeterlibatan,
            int petugasIdPengguna, int penugasanIdPenugasan) {

        this.idDetail = idDetail;
        this.statusKeterlibatan = statusKeterlibatan;
        this.petugasIdPengguna = petugasIdPengguna;
        this.penugasanIdPenugasan = penugasanIdPenugasan;
    }

    public int getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(int idDetail) {
        this.idDetail = idDetail;
    }

    public String getStatusKeterlibatan() {
        return statusKeterlibatan;
    }

    public void setStatusKeterlibatan(String statusKeterlibatan) {
        this.statusKeterlibatan = statusKeterlibatan;
    }

    public int getPetugasIdPengguna() {
        return petugasIdPengguna;
    }

    public void setPetugasIdPengguna(int petugasIdPengguna) {
        this.petugasIdPengguna = petugasIdPengguna;
    }

    public int getPenugasanIdPenugasan() {
        return penugasanIdPenugasan;
    }

    public void setPenugasanIdPenugasan(int penugasanIdPenugasan) {
        this.penugasanIdPenugasan = penugasanIdPenugasan;
    }
}
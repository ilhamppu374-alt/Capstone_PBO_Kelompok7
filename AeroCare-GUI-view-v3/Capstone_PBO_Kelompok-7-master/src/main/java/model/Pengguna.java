package model;

public class Pengguna {

    private int idPengguna;
    private String namaLengkap;
    private String username;
    private String password;
    private String email;
    private String noTelepon;
    private String peran;
    private String statusAktif;

    public Pengguna() {
    }

    public Pengguna(int idPengguna, String namaLengkap,
            String username, String password, String email,
            String noTelepon, String peran, String statusAktif) {

        this.idPengguna = idPengguna;
        this.namaLengkap = namaLengkap;
        this.username = username;
        this.password = password;
        this.email = email;
        this.noTelepon = noTelepon;
        this.peran = peran;
        this.statusAktif = statusAktif;
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public void setNoTelepon(String noTelepon) {
        this.noTelepon = noTelepon;
    }

    public String getPeran() {
        return peran;
    }

    public void setPeran(String peran) {
        this.peran = peran;
    }

    public String getStatusAktif() {
        return statusAktif;
    }

    public void setStatusAktif(String statusAktif) {
        this.statusAktif = statusAktif;
    }
}
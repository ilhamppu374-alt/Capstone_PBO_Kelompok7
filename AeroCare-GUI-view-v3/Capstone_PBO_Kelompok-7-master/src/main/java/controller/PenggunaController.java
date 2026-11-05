package controller;

import dao.PenggunaDAO;
import model.Pengguna;
import java.util.List;

public class PenggunaController {

    private final PenggunaDAO penggunaDAO;

    public PenggunaController() {
        penggunaDAO = new PenggunaDAO();
    }

    public List<Pengguna> tampilkanSemuaPengguna() {
        return penggunaDAO.getAllPengguna();
    }

    public boolean tambahPengguna(
            String namaLengkap,
            String username,
            String password,
            String email,
            String noTelepon,
            String peran,
            String statusAktif,
            String dataTambahan1,
            String dataTambahan2) {

        return penggunaDAO.tambahPengguna(
                namaLengkap,
                username,
                password,
                email,
                noTelepon,
                peran,
                statusAktif,
                dataTambahan1,
                dataTambahan2
        );
    }

    public boolean updatePengguna(
            int idPengguna,
            String namaLengkap,
            String username,
            String password,
            String email,
            String noTelepon,
            String statusAktif,
            String dataTambahan1,
            String dataTambahan2) {

        return penggunaDAO.updatePengguna(
                idPengguna,
                namaLengkap,
                username,
                password,
                email,
                noTelepon,
                statusAktif,
                dataTambahan1,
                dataTambahan2
        );
    }

    public boolean hapusPengguna(int idPengguna) {
        return penggunaDAO.hapusPengguna(idPengguna);
    }

    public List<Pengguna> cariPengguna(String keyword) {
        return penggunaDAO.searchPengguna(keyword);
    }
}
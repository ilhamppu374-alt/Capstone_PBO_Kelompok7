package controller;

import dao.PenggunaDAO;
import model.Pengguna;

public class LoginController {

    private final PenggunaDAO penggunaDAO;

    public LoginController() {
        penggunaDAO = new PenggunaDAO();
    }

    public Pengguna prosesLogin(String username, String password) {

        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            return null;
        }

        Pengguna pengguna = penggunaDAO.login(username.trim());

        if (pengguna == null) {
            return null;
        }

        if (pengguna.getPassword() == null
                || !pengguna.getPassword().equals(password)) {
            return null;
        }

        if (!"Aktif".equals(pengguna.getStatusAktif())) {
            return null;
        }

        return pengguna;
    }
}
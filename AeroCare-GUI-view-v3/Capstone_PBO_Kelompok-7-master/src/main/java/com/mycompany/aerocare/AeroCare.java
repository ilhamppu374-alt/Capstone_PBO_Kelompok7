
package com.mycompany.aerocare;

import view.LoginView;

public class AeroCare {

    public static void main(String[] args) {

        // Menampilkan GUI (package view). Layar pertama: LoginView.
        // Demo login sementara: username diawali "admin", "supervisor", atau "petugas"
        // (password bebas) untuk membuka dashboard sesuai peran.
        java.awt.EventQueue.invokeLater(() -> new LoginView().setVisible(true));
    }
}

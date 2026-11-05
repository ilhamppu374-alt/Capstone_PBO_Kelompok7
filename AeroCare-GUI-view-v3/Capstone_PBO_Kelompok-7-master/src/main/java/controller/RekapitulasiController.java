package controller;

import dao.RekapitulasiDAO;
import java.sql.Date;
import java.util.Map;

public class RekapitulasiController {

    private final RekapitulasiDAO rekapitulasiDAO;

    public RekapitulasiController() {
        rekapitulasiDAO = new RekapitulasiDAO();
    }

    public Map<String, Integer> getRekapLaporan(
            Date tanggalAwal,
            Date tanggalAkhir) {

        if (tanggalAwal == null || tanggalAkhir == null) {
            throw new IllegalArgumentException(
                    "Tanggal awal dan tanggal akhir harus diisi."
            );
        }

        if (tanggalAwal.after(tanggalAkhir)) {
            throw new IllegalArgumentException(
                    "Tanggal awal tidak boleh melebihi tanggal akhir."
            );
        }

        return rekapitulasiDAO.getRekapLaporan(
                tanggalAwal,
                tanggalAkhir
        );
    }
}
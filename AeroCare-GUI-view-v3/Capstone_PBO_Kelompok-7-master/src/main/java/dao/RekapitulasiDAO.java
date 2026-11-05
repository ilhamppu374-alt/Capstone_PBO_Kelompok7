package dao;

import config.Koneksi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class RekapitulasiDAO {

    public Map<String, Integer> getRekapLaporan(
            Date tanggalAwal, Date tanggalAkhir) {

        Map<String, Integer> rekap = new LinkedHashMap<>();

        // Nilai awal
        rekap.put("totalLaporan", 0);
        rekap.put("menunggu", 0);
        rekap.put("diproses", 0);
        rekap.put("selesai", 0);
        rekap.put("prioritasRendah", 0);
        rekap.put("prioritasSedang", 0);
        rekap.put("prioritasTinggi", 0);

        if (tanggalAwal == null || tanggalAkhir == null
                || tanggalAwal.after(tanggalAkhir)) {
            return rekap;
        }

        String sql = "SELECT "
                + "COUNT(*) AS totalLaporan, "
                + "SUM(CASE WHEN status = 'Menunggu' "
                + "THEN 1 ELSE 0 END) AS menunggu, "
                + "SUM(CASE WHEN status = 'Diproses' "
                + "THEN 1 ELSE 0 END) AS diproses, "
                + "SUM(CASE WHEN status = 'Selesai' "
                + "THEN 1 ELSE 0 END) AS selesai, "
                + "SUM(CASE WHEN prioritas = 'Rendah' "
                + "THEN 1 ELSE 0 END) AS prioritasRendah, "
                + "SUM(CASE WHEN prioritas = 'Sedang' "
                + "THEN 1 ELSE 0 END) AS prioritasSedang, "
                + "SUM(CASE WHEN prioritas = 'Tinggi' "
                + "THEN 1 ELSE 0 END) AS prioritasTinggi "
                + "FROM laporan_kerusakan "
                + "WHERE tanggal_laporan BETWEEN ? AND ?";

        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) {
                return rekap;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDate(1, tanggalAwal);
                ps.setDate(2, tanggalAkhir);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        rekap.put("totalLaporan",
                                rs.getInt("totalLaporan"));
                        rekap.put("menunggu",
                                rs.getInt("menunggu"));
                        rekap.put("diproses",
                                rs.getInt("diproses"));
                        rekap.put("selesai",
                                rs.getInt("selesai"));
                        rekap.put("prioritasRendah",
                                rs.getInt("prioritasRendah"));
                        rekap.put("prioritasSedang",
                                rs.getInt("prioritasSedang"));
                        rekap.put("prioritasTinggi",
                                rs.getInt("prioritasTinggi"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(
                    "Gagal mengambil rekapitulasi: " + e.getMessage());
        }

        return rekap;
    }
}

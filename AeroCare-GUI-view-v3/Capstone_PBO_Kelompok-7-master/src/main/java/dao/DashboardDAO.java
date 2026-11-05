package dao;

import config.Koneksi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardDAO {

    public Map<String, Integer> getRingkasanDashboard() {
        Map<String, Integer> data = new LinkedHashMap<>();

        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM pengguna "
                + "WHERE status_aktif = 'Aktif') AS total_pengguna, "

                + "(SELECT COUNT(*) FROM fasilitas) AS total_fasilitas, "

                + "(SELECT COUNT(*) FROM fasilitas "
                + "WHERE kondisi = 'Baik') AS fasilitas_baik, "

                + "(SELECT COUNT(*) FROM fasilitas "
                + "WHERE kondisi = 'Rusak') AS fasilitas_rusak, "

                + "(SELECT COUNT(*) FROM fasilitas "
                + "WHERE kondisi = 'Perlu Perbaikan') "
                + "AS fasilitas_perlu_perbaikan, "

                + "(SELECT COUNT(*) FROM laporan_kerusakan) "
                + "AS total_laporan, "

                + "(SELECT COUNT(*) FROM laporan_kerusakan "
                + "WHERE status = 'Menunggu') AS laporan_menunggu, "

                + "(SELECT COUNT(*) FROM laporan_kerusakan "
                + "WHERE status = 'Diproses') AS laporan_diproses, "

                + "(SELECT COUNT(*) FROM laporan_kerusakan "
                + "WHERE status = 'Selesai') AS laporan_selesai, "

                + "(SELECT COUNT(*) FROM penugasan) "
                + "AS total_penugasan, "

                + "(SELECT COUNT(*) FROM progres_penanganan "
                + "WHERE status_progres = 'Selesai') "
                + "AS total_progres_selesai";

        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) {
                return data;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    data.put("totalPengguna", rs.getInt("total_pengguna"));
                    data.put("totalFasilitas", rs.getInt("total_fasilitas"));
                    data.put("fasilitasBaik", rs.getInt("fasilitas_baik"));
                    data.put("fasilitasRusak", rs.getInt("fasilitas_rusak"));
                    data.put("fasilitasPerluPerbaikan",
                            rs.getInt("fasilitas_perlu_perbaikan"));
                    data.put("totalLaporan", rs.getInt("total_laporan"));
                    data.put("laporanMenunggu", rs.getInt("laporan_menunggu"));
                    data.put("laporanDiproses", rs.getInt("laporan_diproses"));
                    data.put("laporanSelesai", rs.getInt("laporan_selesai"));
                    data.put("totalPenugasan", rs.getInt("total_penugasan"));
                    data.put("totalProgresSelesai",
                            rs.getInt("total_progres_selesai"));
                }
            }
        } catch (SQLException e) {
            System.out.println(
                    "Gagal mengambil data dashboard: " + e.getMessage());
        }

        return data;
    }
}

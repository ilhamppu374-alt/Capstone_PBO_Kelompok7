package dao;

import config.Koneksi;
import model.LaporanKerusakan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LaporanKerusakanDAO {

    // READ: Mengambil seluruh laporan
    public List<LaporanKerusakan> getAllLaporan() {
        List<LaporanKerusakan> daftar = new ArrayList<>();

        String sql = "SELECT * FROM laporan_kerusakan "
                + "ORDER BY id_laporan DESC";

        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) {
                return daftar;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    daftar.add(ambilDataLaporan(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal mengambil data laporan: " + e.getMessage());
        }

        return daftar;
    }

    // CREATE: Menambahkan laporan baru
    public boolean tambahLaporan(LaporanKerusakan laporan) {
        if (!validasiLaporan(laporan)) {
            return false;
        }

        String sql = "INSERT INTO laporan_kerusakan "
                + "(tanggal_laporan, deskripsi_kerusakan, prioritas, "
                + "status, FASILITAS_id_fasilitas, ADMIN_id_pengguna) "
                + "VALUES (?, ?, ?, 'Menunggu', ?, ?)";

        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) {
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDate(1, laporan.getTanggalLaporan());
                ps.setString(2, laporan.getDeskripsiKerusakan());
                ps.setString(3, laporan.getPrioritas());
                ps.setInt(4, laporan.getFasilitasIdFasilitas());
                ps.setInt(5, laporan.getAdminIdPengguna());

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Gagal menambahkan laporan: " + e.getMessage());
            return false;
        }
    }

    // UPDATE: Mengubah laporan yang masih Menunggu dan belum ditugaskan
    public boolean updateLaporan(LaporanKerusakan laporan) {
        if (laporan == null || laporan.getIdLaporan() <= 0
                || !validasiLaporan(laporan)) {
            return false;
        }

        String sql = "UPDATE laporan_kerusakan SET "
                + "tanggal_laporan = ?, "
                + "deskripsi_kerusakan = ?, "
                + "prioritas = ?, "
                + "FASILITAS_id_fasilitas = ? "
                + "WHERE id_laporan = ? "
                + "AND status = 'Menunggu' "
                + "AND NOT EXISTS ("
                + "SELECT 1 FROM penugasan "
                + "WHERE LAPORAN_KERUSAKAN_id_laporan = id_laporan"
                + ")";

        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) {
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDate(1, laporan.getTanggalLaporan());
                ps.setString(2, laporan.getDeskripsiKerusakan());
                ps.setString(3, laporan.getPrioritas());
                ps.setInt(4, laporan.getFasilitasIdFasilitas());
                ps.setInt(5, laporan.getIdLaporan());

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Gagal memperbarui laporan: " + e.getMessage());
            return false;
        }
    }

    // DELETE: Menghapus laporan yang masih Menunggu dan belum ditugaskan
    public boolean hapusLaporan(int idLaporan) {
        if (idLaporan <= 0) {
            return false;
        }

        String sql = "DELETE FROM laporan_kerusakan "
                + "WHERE id_laporan = ? "
                + "AND status = 'Menunggu' "
                + "AND NOT EXISTS ("
                + "SELECT 1 FROM penugasan "
                + "WHERE LAPORAN_KERUSAKAN_id_laporan = id_laporan"
                + ")";

        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) {
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idLaporan);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Gagal menghapus laporan: " + e.getMessage());
            return false;
        }
    }

    // SEARCH: Mencari laporan berdasarkan data laporan dan fasilitas
    public List<LaporanKerusakan> searchLaporan(String keyword) {
        List<LaporanKerusakan> daftar = new ArrayList<>();

        String sql = "SELECT lr.* FROM laporan_kerusakan lr "
                + "JOIN fasilitas f "
                + "ON lr.FASILITAS_id_fasilitas = f.id_fasilitas "
                + "WHERE CAST(lr.id_laporan AS CHAR) LIKE ? "
                + "OR f.nama_fasilitas LIKE ? "
                + "OR lr.deskripsi_kerusakan LIKE ? "
                + "OR lr.prioritas LIKE ? "
                + "OR lr.status LIKE ? "
                + "OR f.lokasi LIKE ? "
                + "ORDER BY lr.id_laporan DESC";

        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) {
                return daftar;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String parameter = "%" + (keyword == null ? "" : keyword.trim()) + "%";

                for (int i = 1; i <= 6; i++) {
                    ps.setString(i, parameter);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        daftar.add(ambilDataLaporan(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal mencari laporan: " + e.getMessage());
        }

        return daftar;
    }

    // Mengubah hasil query menjadi objek LaporanKerusakan
    private LaporanKerusakan ambilDataLaporan(ResultSet rs) throws SQLException {
        LaporanKerusakan laporan = new LaporanKerusakan();

        laporan.setIdLaporan(rs.getInt("id_laporan"));
        laporan.setTanggalLaporan(rs.getDate("tanggal_laporan"));
        laporan.setDeskripsiKerusakan(rs.getString("deskripsi_kerusakan"));
        laporan.setPrioritas(rs.getString("prioritas"));
        laporan.setStatus(rs.getString("status"));
        laporan.setFasilitasIdFasilitas(rs.getInt("FASILITAS_id_fasilitas"));
        laporan.setAdminIdPengguna(rs.getInt("ADMIN_id_pengguna"));

        return laporan;
    }

    // Validasi data laporan
    private boolean validasiLaporan(LaporanKerusakan laporan) {
        if (laporan == null) {
            return false;
        }

        String deskripsi = laporan.getDeskripsiKerusakan();
        String prioritas = laporan.getPrioritas();

        return laporan.getTanggalLaporan() != null
                && deskripsi != null
                && !deskripsi.trim().isEmpty()
                && deskripsi.length() <= 150
                && prioritas != null
                && (prioritas.equals("Rendah")
                || prioritas.equals("Sedang")
                || prioritas.equals("Tinggi"))
                && laporan.getFasilitasIdFasilitas() > 0
                && laporan.getAdminIdPengguna() > 0;
    }
}

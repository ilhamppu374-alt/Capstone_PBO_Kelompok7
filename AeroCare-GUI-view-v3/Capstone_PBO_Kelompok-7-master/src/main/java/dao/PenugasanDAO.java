package dao;

import config.Koneksi;
import model.Penugasan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PenugasanDAO {

    // READ: Mengambil seluruh data penugasan
    public List<Penugasan> getAllPenugasan() {
        List<Penugasan> daftar = new ArrayList<>();

        String sql = "SELECT * FROM penugasan ORDER BY id_penugasan DESC";

        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) {
                return daftar;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    daftar.add(ambilDataPenugasan(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal mengambil data penugasan: " + e.getMessage());
        }

        return daftar;
    }

    // CREATE: Membuat penugasan beserta daftar Petugas
    public boolean tambahPenugasan(
            Penugasan penugasan, List<Integer> daftarIdPetugas) {

        if (!validasiPenugasan(penugasan)
                || daftarIdPetugas == null
                || daftarIdPetugas.isEmpty()) {
            return false;
        }

        String sqlPenugasan = "INSERT INTO penugasan "
                + "(waktu_penugasan, catatan_penugasan, "
                + "SUPERVISOR_id_pengguna, "
                + "LAPORAN_KERUSAKAN_id_laporan, id_pengguna) "
                + "VALUES (?, ?, ?, ?, ?)";

        String sqlDetail = "INSERT INTO detail_penugasan "
                + "(status_keterlibatan, PETUGAS_id_pengguna, "
                + "PENUGASAN_id_penugasan) VALUES ('Aktif', ?, ?)";

        Connection conn = null;

        try {
            conn = Koneksi.getConnection();
            if (conn == null) {
                return false;
            }

            conn.setAutoCommit(false);

            validasiDaftarPetugas(conn, daftarIdPetugas);

            int idPenugasan;

            try (PreparedStatement ps = conn.prepareStatement(
                    sqlPenugasan, Statement.RETURN_GENERATED_KEYS)) {

                ps.setDate(1, penugasan.getWaktuPenugasan());
                ps.setString(2, penugasan.getCatatanPenugasan());
                ps.setInt(3, penugasan.getSupervisorIdPengguna());
                ps.setInt(4, penugasan.getLaporanIdLaporan());
                ps.setInt(5, penugasan.getIdPengguna());

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("ID penugasan tidak ditemukan.");
                    }
                    idPenugasan = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlDetail)) {
                for (Integer idPetugas : daftarIdPetugas) {
                    ps.setInt(1, idPetugas);
                    ps.setInt(2, idPenugasan);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            rollbackTransaksi(conn);
            System.out.println("Gagal menambahkan penugasan: " + e.getMessage());
            return false;
        } finally {
            tutupKoneksi(conn);
        }
    }

    // UPDATE: Memperbarui penugasan dan Petugas yang terlibat
    public boolean updatePenugasan(
            Penugasan penugasan, List<Integer> daftarIdPetugas) {

        if (penugasan == null || penugasan.getIdPenugasan() <= 0
                || !validasiPenugasan(penugasan)
                || daftarIdPetugas == null
                || daftarIdPetugas.isEmpty()) {
            return false;
        }

        String sqlUpdate = "UPDATE penugasan SET "
                + "waktu_penugasan = ?, catatan_penugasan = ? "
                + "WHERE id_penugasan = ?";

        String sqlAmbilAktif = "SELECT PETUGAS_id_pengguna "
                + "FROM detail_penugasan "
                + "WHERE PENUGASAN_id_penugasan = ? "
                + "AND status_keterlibatan = 'Aktif'";

        String sqlGantiStatus = "UPDATE detail_penugasan "
                + "SET status_keterlibatan = 'Digantikan' "
                + "WHERE PENUGASAN_id_penugasan = ? "
                + "AND PETUGAS_id_pengguna = ? "
                + "AND status_keterlibatan = 'Aktif'";

        String sqlTambahDetail = "INSERT INTO detail_penugasan "
                + "(status_keterlibatan, PETUGAS_id_pengguna, "
                + "PENUGASAN_id_penugasan) VALUES ('Aktif', ?, ?)";

        Connection conn = null;

        try {
            conn = Koneksi.getConnection();
            if (conn == null) {
                return false;
            }

            conn.setAutoCommit(false);

            validasiDaftarPetugas(conn, daftarIdPetugas);

            int idPenugasan = penugasan.getIdPenugasan();

            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setDate(1, penugasan.getWaktuPenugasan());
                ps.setString(2, penugasan.getCatatanPenugasan());
                ps.setInt(3, idPenugasan);

                if (ps.executeUpdate() == 0) {
                    throw new SQLException("Penugasan tidak ditemukan.");
                }
            }

            Set<Integer> petugasLama = new HashSet<>();

            try (PreparedStatement ps = conn.prepareStatement(sqlAmbilAktif)) {
                ps.setInt(1, idPenugasan);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        petugasLama.add(rs.getInt("PETUGAS_id_pengguna"));
                    }
                }
            }

            Set<Integer> petugasBaru = new HashSet<>(daftarIdPetugas);

            // Petugas yang tidak dipilih lagi ditandai Digantikan
            for (Integer idLama : petugasLama) {
                if (!petugasBaru.contains(idLama)) {
                    try (PreparedStatement ps = conn.prepareStatement(sqlGantiStatus)) {
                        ps.setInt(1, idPenugasan);
                        ps.setInt(2, idLama);
                        ps.executeUpdate();
                    }
                }
            }

            // Petugas baru ditambahkan sebagai Aktif
            for (Integer idBaru : petugasBaru) {
                if (!petugasLama.contains(idBaru)) {
                    try (PreparedStatement ps = conn.prepareStatement(sqlTambahDetail)) {
                        ps.setInt(1, idBaru);
                        ps.setInt(2, idPenugasan);
                        ps.executeUpdate();
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            rollbackTransaksi(conn);
            System.out.println("Gagal memperbarui penugasan: " + e.getMessage());
            return false;
        } finally {
            tutupKoneksi(conn);
        }
    }

    // DELETE: Menghapus penugasan yang belum memiliki progres
    public boolean hapusPenugasan(int idPenugasan) {
        if (idPenugasan <= 0) {
            return false;
        }

        String sqlCek = "SELECT COUNT(*) FROM progres_penanganan "
                + "WHERE PENUGASAN_id_penugasan = ?";

        String sqlDetail = "DELETE FROM detail_penugasan "
                + "WHERE PENUGASAN_id_penugasan = ?";

        String sqlPenugasan = "DELETE FROM penugasan WHERE id_penugasan = ?";

        Connection conn = null;

        try {
            conn = Koneksi.getConnection();
            if (conn == null) {
                return false;
            }

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlCek)) {
                ps.setInt(1, idPenugasan);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new SQLException(
                                "Penugasan memiliki riwayat progres dan tidak dapat dihapus.");
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlDetail)) {
                ps.setInt(1, idPenugasan);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlPenugasan)) {
                ps.setInt(1, idPenugasan);

                if (ps.executeUpdate() == 0) {
                    throw new SQLException("Penugasan tidak ditemukan.");
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            rollbackTransaksi(conn);
            System.out.println("Gagal menghapus penugasan: " + e.getMessage());
            return false;
        } finally {
            tutupKoneksi(conn);
        }
    }

    // SEARCH: Mencari data penugasan
    public List<Penugasan> searchPenugasan(String keyword) {
        List<Penugasan> daftar = new ArrayList<>();

        String sql = "SELECT DISTINCT p.* FROM penugasan p "
                + "LEFT JOIN pengguna s "
                + "ON p.SUPERVISOR_id_pengguna = s.id_pengguna "
                + "LEFT JOIN detail_penugasan dp "
                + "ON p.id_penugasan = dp.PENUGASAN_id_penugasan "
                + "LEFT JOIN pengguna pt "
                + "ON dp.PETUGAS_id_pengguna = pt.id_pengguna "
                + "WHERE CAST(p.id_penugasan AS CHAR) LIKE ? "
                + "OR CAST(p.LAPORAN_KERUSAKAN_id_laporan AS CHAR) LIKE ? "
                + "OR p.catatan_penugasan LIKE ? "
                + "OR s.nama_lengkap LIKE ? "
                + "OR pt.nama_lengkap LIKE ? "
                + "ORDER BY p.id_penugasan DESC";

        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) {
                return daftar;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String parameter = "%" + (keyword == null ? "" : keyword.trim()) + "%";

                for (int i = 1; i <= 5; i++) {
                    ps.setString(i, parameter);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        daftar.add(ambilDataPenugasan(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal mencari penugasan: " + e.getMessage());
        }

        return daftar;
    }

    // Mengubah hasil query menjadi objek Penugasan
    private Penugasan ambilDataPenugasan(ResultSet rs) throws SQLException {
        Penugasan p = new Penugasan();

        p.setIdPenugasan(rs.getInt("id_penugasan"));
        p.setWaktuPenugasan(rs.getDate("waktu_penugasan"));
        p.setCatatanPenugasan(rs.getString("catatan_penugasan"));
        p.setSupervisorIdPengguna(rs.getInt("SUPERVISOR_id_pengguna"));
        p.setLaporanIdLaporan(rs.getInt("LAPORAN_KERUSAKAN_id_laporan"));
        p.setIdPengguna(rs.getInt("id_pengguna"));

        return p;
    }

    // Validasi data utama penugasan
    private boolean validasiPenugasan(Penugasan p) {
        return p != null
                && p.getWaktuPenugasan() != null
                && p.getSupervisorIdPengguna() > 0
                && p.getLaporanIdLaporan() > 0
                && p.getIdPengguna() > 0
                && (p.getCatatanPenugasan() == null
                || p.getCatatanPenugasan().length() <= 150);
    }

    // Memastikan daftar Petugas valid dan tidak duplikat
    private void validasiDaftarPetugas(
            Connection conn, List<Integer> daftarIdPetugas) throws SQLException {

        Set<Integer> unik = new HashSet<>();

        for (Integer id : daftarIdPetugas) {
            if (id == null || id <= 0 || !unik.add(id)) {
                throw new SQLException("Daftar ID Petugas tidak valid atau duplikat.");
            }

            String sql = "SELECT COUNT(*) FROM pengguna p "
                    + "JOIN petugas pt ON p.id_pengguna = pt.id_pengguna "
                    + "WHERE p.id_pengguna = ? "
                    + "AND p.peran = 'Petugas' "
                    + "AND p.status_aktif = 'Aktif'";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) != 1) {
                        throw new SQLException(
                                "Petugas tidak ditemukan atau tidak aktif: " + id);
                    }
                }
            }
        }
    }

    private void rollbackTransaksi(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.out.println("Rollback gagal: " + e.getMessage());
            }
        }
    }

    private void tutupKoneksi(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                System.out.println("Gagal menutup koneksi: " + e.getMessage());
            }
        }
    }
}

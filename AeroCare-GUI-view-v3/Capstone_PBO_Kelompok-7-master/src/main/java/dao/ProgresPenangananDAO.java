package dao;

import config.Koneksi;
import model.ProgresPenanganan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProgresPenangananDAO {

    private static final List<String> STATUS_VALID = Arrays.asList(
            "Pemeriksaan",
            "Perbaikan",
            "Pengujian",
            "Selesai"
    );

    // READ: Mengambil seluruh progres berdasarkan ID Penugasan
    public List<ProgresPenanganan> getAllProgres(int idPenugasan) {

        List<ProgresPenanganan> daftarProgres = new ArrayList<>();

        String sql = "SELECT * FROM progres_penanganan "
                + "WHERE PENUGASAN_id_penugasan = ? "
                + "ORDER BY waktu_update ASC, id_progres ASC";

        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPenugasan);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftarProgres.add(ambilDataProgres(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Gagal mengambil data progres: " + e.getMessage()
            );
        }

        return daftarProgres;
    }

    // CREATE: Menambahkan progres penanganan
    public boolean tambahProgres(ProgresPenanganan progres) {

        if (!validasiProgres(progres)) {
            System.out.println("Data progres tidak valid.");
            return false;
        }

        String sql = "INSERT INTO progres_penanganan "
                + "(waktu_update, status_progres, "
                + "tindakan_penanganan, PENUGASAN_id_penugasan) "
                + "VALUES (?, ?, ?, ?)";

        Connection conn = null;

        try {
            conn = Koneksi.getConnection();

            if (conn == null) {
                return false;
            }

            conn.setAutoCommit(false);

            Timestamp waktu = progres.getWaktuUpdate();

            if (waktu == null) {
                waktu = new Timestamp(System.currentTimeMillis());
            }

            // Memastikan penugasan tersedia
            if (!penugasanTersedia(conn, progres.getPenugasanIdPenugasan())) {
                throw new SQLException(
                        "Data penugasan tidak ditemukan."
                );
            }

            // Menambahkan data progres
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setTimestamp(1, waktu);
                ps.setString(2, progres.getStatusProgres());
                ps.setString(3, progres.getTindakanPenanganan());
                ps.setInt(4, progres.getPenugasanIdPenugasan());

                ps.executeUpdate();
            }

            // Memperbarui status laporan berdasarkan progres terakhir
            perbaruiStatusLaporan(
                    conn,
                    progres.getPenugasanIdPenugasan()
            );

            conn.commit();
            return true;

        } catch (SQLException e) {
            rollbackTransaksi(conn);

            System.out.println(
                    "Gagal menambahkan progres: " + e.getMessage()
            );

            return false;

        } finally {
            tutupKoneksi(conn);
        }
    }

    // UPDATE: Memperbarui data progres
    public boolean updateProgres(ProgresPenanganan progres) {

        if (progres == null
                || progres.getIdProgres() <= 0
                || !statusValid(progres.getStatusProgres())
                || progres.getTindakanPenanganan() == null
                || progres.getTindakanPenanganan().trim().isEmpty()) {

            System.out.println("Data progres tidak valid.");
            return false;
        }

        String sqlAmbilPenugasan =
                "SELECT PENUGASAN_id_penugasan "
                + "FROM progres_penanganan "
                + "WHERE id_progres = ?";

        String sqlUpdate = "UPDATE progres_penanganan "
                + "SET waktu_update = ?, "
                + "status_progres = ?, "
                + "tindakan_penanganan = ? "
                + "WHERE id_progres = ?";

        Connection conn = null;

        try {
            conn = Koneksi.getConnection();

            if (conn == null) {
                return false;
            }

            conn.setAutoCommit(false);

            int idPenugasan;

            // Mengambil ID penugasan dari data progres
            try (PreparedStatement ps = conn.prepareStatement(sqlAmbilPenugasan)) {
                ps.setInt(1, progres.getIdProgres());

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException(
                                "Data progres tidak ditemukan."
                        );
                    }

                    idPenugasan = rs.getInt("PENUGASAN_id_penugasan");
                }
            }

            Timestamp waktu = progres.getWaktuUpdate();

            if (waktu == null) {
                waktu = new Timestamp(System.currentTimeMillis());
            }

            // Memperbarui data progres
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setTimestamp(1, waktu);
                ps.setString(2, progres.getStatusProgres());
                ps.setString(3, progres.getTindakanPenanganan());
                ps.setInt(4, progres.getIdProgres());

                if (ps.executeUpdate() == 0) {
                    throw new SQLException(
                            "Data progres gagal diperbarui."
                    );
                }
            }

            // Menghitung ulang status laporan
            perbaruiStatusLaporan(conn, idPenugasan);

            conn.commit();
            return true;

        } catch (SQLException e) {
            rollbackTransaksi(conn);

            System.out.println(
                    "Gagal memperbarui progres: " + e.getMessage()
            );

            return false;

        } finally {
            tutupKoneksi(conn);
        }
    }

    // DELETE: Menghapus data progres
    public boolean hapusProgres(int idProgres) {

        String sqlAmbilPenugasan =
                "SELECT PENUGASAN_id_penugasan "
                + "FROM progres_penanganan "
                + "WHERE id_progres = ?";

        String sqlHapus =
                "DELETE FROM progres_penanganan "
                + "WHERE id_progres = ?";

        Connection conn = null;

        try {
            conn = Koneksi.getConnection();

            if (conn == null) {
                return false;
            }

            conn.setAutoCommit(false);

            int idPenugasan;

            // Mengambil ID penugasan sebelum progres dihapus
            try (PreparedStatement ps = conn.prepareStatement(sqlAmbilPenugasan)) {
                ps.setInt(1, idProgres);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException(
                                "Data progres tidak ditemukan."
                        );
                    }

                    idPenugasan = rs.getInt("PENUGASAN_id_penugasan");
                }
            }

            // Menghapus data progres
            try (PreparedStatement ps = conn.prepareStatement(sqlHapus)) {
                ps.setInt(1, idProgres);

                if (ps.executeUpdate() == 0) {
                    throw new SQLException(
                            "Data progres gagal dihapus."
                    );
                }
            }

            // Memperbarui kembali status laporan
            perbaruiStatusLaporan(conn, idPenugasan);

            conn.commit();
            return true;

        } catch (SQLException e) {
            rollbackTransaksi(conn);

            System.out.println(
                    "Gagal menghapus progres: " + e.getMessage()
            );

            return false;

        } finally {
            tutupKoneksi(conn);
        }
    }

    // Mengubah ResultSet menjadi objek ProgresPenanganan
    private ProgresPenanganan ambilDataProgres(ResultSet rs)
            throws SQLException {

        ProgresPenanganan progres = new ProgresPenanganan();

        progres.setIdProgres(rs.getInt("id_progres"));
        progres.setWaktuUpdate(rs.getTimestamp("waktu_update"));
        progres.setStatusProgres(rs.getString("status_progres"));
        progres.setTindakanPenanganan(
                rs.getString("tindakan_penanganan")
        );
        progres.setPenugasanIdPenugasan(
                rs.getInt("PENUGASAN_id_penugasan")
        );

        return progres;
    }

    // Memastikan data progres memenuhi ketentuan
    private boolean validasiProgres(ProgresPenanganan progres) {

        return progres != null
                && progres.getPenugasanIdPenugasan() > 0
                && statusValid(progres.getStatusProgres())
                && progres.getTindakanPenanganan() != null
                && !progres.getTindakanPenanganan().trim().isEmpty();
    }

    // Memastikan status progres sesuai ENUM database
    private boolean statusValid(String status) {
        return status != null && STATUS_VALID.contains(status);
    }

    // Memastikan penugasan tersedia di database
    private boolean penugasanTersedia(
            Connection conn,
            int idPenugasan) throws SQLException {

        String sql = "SELECT COUNT(*) FROM penugasan "
                + "WHERE id_penugasan = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPenugasan);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // Memperbarui status laporan berdasarkan progres paling akhir
    private void perbaruiStatusLaporan(
            Connection conn,
            int idPenugasan) throws SQLException {

        String sql = "SELECT "
                + "p.status_progres, "
                + "pk.LAPORAN_KERUSAKAN_id_laporan, "
                + "lk.FASILITAS_id_fasilitas "
                + "FROM penugasan pk "
                + "JOIN laporan_kerusakan lk "
                + "ON pk.LAPORAN_KERUSAKAN_id_laporan = lk.id_laporan "
                + "LEFT JOIN progres_penanganan p "
                + "ON pk.id_penugasan = p.PENUGASAN_id_penugasan "
                + "WHERE pk.id_penugasan = ? "
                + "ORDER BY p.waktu_update DESC, p.id_progres DESC "
                + "LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPenugasan);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException(
                            "Data penugasan atau laporan tidak ditemukan."
                    );
                }

                String statusProgres = rs.getString("status_progres");
                int idLaporan = rs.getInt(
                        "LAPORAN_KERUSAKAN_id_laporan"
                );
                int idFasilitas = rs.getInt("FASILITAS_id_fasilitas");

                String statusLaporan;

                if (statusProgres == null) {
                    statusLaporan = "Menunggu";
                } else if ("Selesai".equals(statusProgres)) {
                    statusLaporan = "Selesai";
                } else {
                    statusLaporan = "Diproses";
                }

                // Memperbarui status laporan kerusakan
                String sqlUpdateLaporan =
                        "UPDATE laporan_kerusakan "
                        + "SET status = ? "
                        + "WHERE id_laporan = ?";

                try (PreparedStatement psUpdate =
                        conn.prepareStatement(sqlUpdateLaporan)) {

                    psUpdate.setString(1, statusLaporan);
                    psUpdate.setInt(2, idLaporan);
                    psUpdate.executeUpdate();
                }

                // Jika progres terakhir selesai, fasilitas menjadi Baik
                if ("Selesai".equals(statusProgres)) {
                    String sqlUpdateFasilitas =
                            "UPDATE fasilitas "
                            + "SET kondisi = 'Baik' "
                            + "WHERE id_fasilitas = ?";

                    try (PreparedStatement psFasilitas =
                            conn.prepareStatement(sqlUpdateFasilitas)) {

                        psFasilitas.setInt(1, idFasilitas);
                        psFasilitas.executeUpdate();
                    }
                }
            }
        }
    }

    // Membatalkan transaksi ketika terjadi kesalahan
    private void rollbackTransaksi(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.out.println(
                        "Gagal melakukan rollback: " + e.getMessage()
                );
            }
        }
    }

    // Mengembalikan auto-commit dan menutup koneksi
    private void tutupKoneksi(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                System.out.println(
                        "Gagal menutup koneksi: " + e.getMessage()
                );
            }
        }
    }
}

package dao;

import config.Koneksi;
import model.Pengguna;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PenggunaDAO {

    // 1. LOGIN PENGGUNA
    public Pengguna login(String username) {

        String sql = "SELECT * FROM pengguna "
                + "WHERE username = ? "
                + "AND password = ? "
                + "AND status_aktif = 'Aktif'";

        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            String password = null;
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return ambilDataPengguna(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Gagal melakukan login: "
                    + e.getMessage());
        }

        return null;
    }

    // 2. MENAMPILKAN SELURUH DATA PENGGUNA
    public List<Pengguna> getAllPengguna() {

        List<Pengguna> daftarPengguna = new ArrayList<>();

        String sql = "SELECT id_pengguna, nama_lengkap, username, "
                + "email, no_telepon, peran, status_aktif "
                + "FROM pengguna ORDER BY id_pengguna ASC";

        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                daftarPengguna.add(ambilDataPengguna(rs));
            }

        } catch (SQLException e) {
            System.out.println("Gagal mengambil data pengguna: "
                    + e.getMessage());
        }

        return daftarPengguna;
    }

    // 3. MENAMBAHKAN PENGGUNA BARU
    public boolean tambahPengguna(
            String namaLengkap,
            String username,
            String password,
            String email,
            String noTelepon,
            String peran,
            String statusAktif,
            String dataTambahan1,
            String dataTambahan2
    ) {

        if (!validasiDataPengguna(namaLengkap, username,
                password, email, noTelepon, peran, statusAktif,
                dataTambahan1, dataTambahan2, true)) {
            return false;
        }

        String sqlPengguna = "INSERT INTO pengguna "
                + "(nama_lengkap, username, password, email, "
                + "no_telepon, peran, status_aktif) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        String sqlAdmin = "INSERT INTO admin "
                + "(id_pengguna, hak_akses) VALUES (?, ?)";

        String sqlSupervisor = "INSERT INTO supervisor "
                + "(id_pengguna, area_pengawasan) VALUES (?, ?)";

        String sqlPetugas = "INSERT INTO petugas "
                + "(id_pengguna, spesialisasi, shift_kerja) "
                + "VALUES (?, ?, ?)";

        Connection conn = null;

        try {
            conn = Koneksi.getConnection();

            if (conn == null) {
                return false;
            }

            conn.setAutoCommit(false);

            int idPengguna;

            try (PreparedStatement ps = conn.prepareStatement(
                    sqlPengguna, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, namaLengkap.trim());
                ps.setString(2, username.trim());
                ps.setString(3, password);

                if (email == null || email.trim().isEmpty()) {
                    ps.setNull(4, Types.VARCHAR);
                } else {
                    ps.setString(4, email.trim());
                }

                ps.setString(5, noTelepon.trim());
                ps.setString(6, peran);
                ps.setString(7, statusAktif);

                if (ps.executeUpdate() != 1) {
                    conn.rollback();
                    return false;
                }

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }

                    idPengguna = rs.getInt(1);
                }
            }

            if ("Admin".equals(peran)) {

                try (PreparedStatement ps =
                        conn.prepareStatement(sqlAdmin)) {

                    ps.setInt(1, idPengguna);
                    ps.setString(2, dataTambahan1);

                    ps.executeUpdate();
                }

            } else if ("Supervisor".equals(peran)) {

                try (PreparedStatement ps =
                        conn.prepareStatement(sqlSupervisor)) {

                    ps.setInt(1, idPengguna);
                    ps.setString(2, dataTambahan1);

                    ps.executeUpdate();
                }

            } else if ("Petugas".equals(peran)) {

                try (PreparedStatement ps =
                        conn.prepareStatement(sqlPetugas)) {

                    ps.setInt(1, idPengguna);
                    ps.setString(2, dataTambahan1);
                    ps.setString(3, dataTambahan2);

                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            rollbackTransaksi(conn);

            System.out.println("Gagal menambahkan pengguna: "
                    + e.getMessage());

            return false;

        } finally {
            tutupKoneksi(conn);
        }
    }

    // 4. MEMPERBARUI DATA PENGGUNA
    public boolean updatePengguna(
            int idPengguna,
            String namaLengkap,
            String username,
            String password,
            String email,
            String noTelepon,
            String statusAktif,
            String dataTambahan1,
            String dataTambahan2
    ) {

        if (idPengguna <= 0
                || !validasiDataUtama(namaLengkap, username,
                        password, email, noTelepon, statusAktif)) {
            return false;
        }

        Connection conn = null;

        try {
            conn = Koneksi.getConnection();

            if (conn == null) {
                return false;
            }

            conn.setAutoCommit(false);

            String peran;

            String sqlPeran = "SELECT peran FROM pengguna "
                    + "WHERE id_pengguna = ? FOR UPDATE";

            try (PreparedStatement ps =
                    conn.prepareStatement(sqlPeran)) {

                ps.setInt(1, idPengguna);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }

                    peran = rs.getString("peran");
                }
            }

            if (!validasiDataTambahan(peran,
                    dataTambahan1, dataTambahan2)) {
                conn.rollback();
                return false;
            }

            String sqlPengguna = "UPDATE pengguna SET "
                    + "nama_lengkap = ?, "
                    + "username = ?, "
                    + "password = COALESCE(NULLIF(?, ''), password), "
                    + "email = ?, "
                    + "no_telepon = ?, "
                    + "status_aktif = ? "
                    + "WHERE id_pengguna = ?";

            try (PreparedStatement ps =
                    conn.prepareStatement(sqlPengguna)) {

                ps.setString(1, namaLengkap.trim());
                ps.setString(2, username.trim());
                ps.setString(3, password);

                if (email == null || email.trim().isEmpty()) {
                    ps.setNull(4, Types.VARCHAR);
                } else {
                    ps.setString(4, email.trim());
                }

                ps.setString(5, noTelepon.trim());
                ps.setString(6, statusAktif);
                ps.setInt(7, idPengguna);

                if (ps.executeUpdate() != 1) {
                    conn.rollback();
                    return false;
                }
            }

            String sqlTambahan;

            if ("Admin".equals(peran)) {
                sqlTambahan = "UPDATE admin SET hak_akses = ? "
                        + "WHERE id_pengguna = ?";

                try (PreparedStatement ps =
                        conn.prepareStatement(sqlTambahan)) {

                    ps.setString(1, dataTambahan1);
                    ps.setInt(2, idPengguna);

                    if (ps.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                }

            } else if ("Supervisor".equals(peran)) {
                sqlTambahan = "UPDATE supervisor "
                        + "SET area_pengawasan = ? "
                        + "WHERE id_pengguna = ?";

                try (PreparedStatement ps =
                        conn.prepareStatement(sqlTambahan)) {

                    ps.setString(1, dataTambahan1);
                    ps.setInt(2, idPengguna);

                    if (ps.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                }

            } else if ("Petugas".equals(peran)) {
                sqlTambahan = "UPDATE petugas SET "
                        + "spesialisasi = ?, shift_kerja = ? "
                        + "WHERE id_pengguna = ?";

                try (PreparedStatement ps =
                        conn.prepareStatement(sqlTambahan)) {

                    ps.setString(1, dataTambahan1);
                    ps.setString(2, dataTambahan2);
                    ps.setInt(3, idPengguna);

                    if (ps.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                }

            } else {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            rollbackTransaksi(conn);

            System.out.println("Gagal memperbarui pengguna: "
                    + e.getMessage());

            return false;

        } finally {
            tutupKoneksi(conn);
        }
    }

    // 5. MENONAKTIFKAN PENGGUNA
    public boolean hapusPengguna(int idPengguna) {

        if (idPengguna <= 0) {
            return false;
        }

        String sql = "UPDATE pengguna "
                + "SET status_aktif = 'Tidak Aktif' "
                + "WHERE id_pengguna = ? "
                + "AND status_aktif = 'Aktif'";

        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPengguna);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Gagal menonaktifkan pengguna: "
                    + e.getMessage());
            return false;
        }
    }

    // 6. MENCARI DATA PENGGUNA
    public List<Pengguna> searchPengguna(String keyword) {

        List<Pengguna> daftarPengguna = new ArrayList<>();

        if (keyword == null) {
            keyword = "";
        }

        String sql = "SELECT id_pengguna, nama_lengkap, username, "
                + "email, no_telepon, peran, status_aktif "
                + "FROM pengguna "
                + "WHERE CAST(id_pengguna AS CHAR) LIKE ? "
                + "OR nama_lengkap LIKE ? "
                + "OR username LIKE ? "
                + "OR no_telepon LIKE ? "
                + "OR peran LIKE ? "
                + "OR status_aktif LIKE ? "
                + "ORDER BY id_pengguna ASC";

        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String parameter = "%" + keyword.trim() + "%";

            for (int i = 1; i <= 6; i++) {
                ps.setString(i, parameter);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    daftarPengguna.add(ambilDataPengguna(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Gagal mencari pengguna: "
                    + e.getMessage());
        }

        return daftarPengguna;
    }

    // MEMBENTUK OBJEK PENGGUNA DARI RESULTSET
    private Pengguna ambilDataPengguna(ResultSet rs)
            throws SQLException {

        Pengguna pengguna = new Pengguna();

        pengguna.setIdPengguna(rs.getInt("id_pengguna"));
        pengguna.setNamaLengkap(rs.getString("nama_lengkap"));
        pengguna.setUsername(rs.getString("username"));

        // Password hanya diambil jika kolom tersedia
        try {
            pengguna.setPassword(rs.getString("password"));
        } catch (SQLException e) {
            pengguna.setPassword(null);
        }

        pengguna.setEmail(rs.getString("email"));
        pengguna.setNoTelepon(rs.getString("no_telepon"));
        pengguna.setPeran(rs.getString("peran"));
        pengguna.setStatusAktif(rs.getString("status_aktif"));

        return pengguna;
    }

    // VALIDASI DATA UTAMA
    private boolean validasiDataUtama(
            String namaLengkap,
            String username,
            String password,
            String email,
            String noTelepon,
            String statusAktif) {

        if (namaLengkap == null || namaLengkap.trim().isEmpty()
                || namaLengkap.trim().length() > 60) {
            System.out.println("Nama lengkap tidak valid.");
            return false;
        }

        if (username == null || username.trim().isEmpty()
                || username.trim().length() > 30) {
            System.out.println("Username tidak valid.");
            return false;
        }

        if (password != null && password.length() > 30) {
            System.out.println("Password maksimal 30 karakter.");
            return false;
        }

        if (email != null && email.trim().length() > 50) {
            System.out.println("Email maksimal 50 karakter.");
            return false;
        }

        if (noTelepon == null || noTelepon.trim().isEmpty()
                || noTelepon.trim().length() > 20) {
            System.out.println("Nomor telepon tidak valid.");
            return false;
        }

        if (!"Aktif".equals(statusAktif)
                && !"Tidak Aktif".equals(statusAktif)) {
            System.out.println("Status pengguna tidak valid.");
            return false;
        }

        return true;
    }

    // VALIDASI DATA PENGGUNA SAAT PENAMBAHAN
    private boolean validasiDataPengguna(
            String namaLengkap,
            String username,
            String password,
            String email,
            String noTelepon,
            String peran,
            String statusAktif,
            String dataTambahan1,
            String dataTambahan2,
            boolean tambah) {

        if (!validasiDataUtama(namaLengkap, username,
                password, email, noTelepon, statusAktif)) {
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            System.out.println("Password wajib diisi.");
            return false;
        }

        if (!validasiDataTambahan(peran,
                dataTambahan1, dataTambahan2)) {
            return false;
        }

        return true;
    }

    // VALIDASI DATA TAMBAHAN BERDASARKAN PERAN
    private boolean validasiDataTambahan(
            String peran,
            String dataTambahan1,
            String dataTambahan2) {

        if ("Admin".equals(peran)) {
            return dataTambahan1 == null
                    || dataTambahan1.length() <= 50;
        }

        if ("Supervisor".equals(peran)) {
            return dataTambahan1 == null
                    || dataTambahan1.length() <= 50;
        }

        if ("Petugas".equals(peran)) {

            boolean spesialisasiValid =
                    "Mekanikal".equals(dataTambahan1)
                    || "Elektrikal".equals(dataTambahan1)
                    || "Plumbing dan Perpipaan".equals(dataTambahan1)
                    || "Pemeliharaan Bangunan".equals(dataTambahan1)
                    || "HVAC (Tata Udara)".equals(dataTambahan1);

            boolean shiftValid =
                    "Pagi".equals(dataTambahan2)
                    || "Malam".equals(dataTambahan2);

            if (!spesialisasiValid || !shiftValid) {
                System.out.println(
                        "Spesialisasi atau shift Petugas tidak valid.");
                return false;
            }

            return true;
        }

        System.out.println("Peran pengguna tidak valid.");
        return false;
    }

    // ROLLBACK TRANSAKSI
    private void rollbackTransaksi(Connection conn) {

        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.out.println("Gagal melakukan rollback: "
                        + e.getMessage());
            }
        }
    }

    // MENUTUP KONEKSI
    private void tutupKoneksi(Connection conn) {

        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                System.out.println("Gagal menutup koneksi: "
                        + e.getMessage());
            }
        }
    }
}

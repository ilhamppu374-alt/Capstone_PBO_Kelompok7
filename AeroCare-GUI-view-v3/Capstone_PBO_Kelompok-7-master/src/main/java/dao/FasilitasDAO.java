package dao;

import config.Koneksi;
import model.Fasilitas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FasilitasDAO {

    // READ
    public List<Fasilitas> getAllFasilitas() {
        List<Fasilitas> daftar = new ArrayList<>();

        String sql = "SELECT * FROM fasilitas ORDER BY id_fasilitas ASC";

        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) {
                return daftar;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    daftar.add(ambilDataFasilitas(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal mengambil data fasilitas: " + e.getMessage());
        }

        return daftar;
    }

    // CREATE
    public boolean tambahFasilitas(Fasilitas fasilitas) {
        if (!validasiFasilitas(fasilitas)) {
            return false;
        }

        String sql = "INSERT INTO fasilitas "
                + "(nama_fasilitas, jenis_fasilitas, lokasi, kondisi, "
                + "keterangan, ADMIN_id_pengguna) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) {
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, fasilitas.getNamaFasilitas());
                ps.setString(2, fasilitas.getJenisFasilitas());
                ps.setString(3, fasilitas.getLokasi());
                ps.setString(4, fasilitas.getKondisi());
                ps.setString(5, fasilitas.getKeterangan());
                ps.setInt(6, fasilitas.getAdminIdPengguna());

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Gagal menambahkan fasilitas: " + e.getMessage());
            return false;
        }
    }

    // UPDATE
    public boolean updateFasilitas(Fasilitas fasilitas) {
        if (fasilitas == null || fasilitas.getIdFasilitas() <= 0
                || !validasiFasilitas(fasilitas)) {
            return false;
        }

        String sql = "UPDATE fasilitas SET "
                + "nama_fasilitas = ?, jenis_fasilitas = ?, "
                + "lokasi = ?, kondisi = ?, keterangan = ? "
                + "WHERE id_fasilitas = ?";

        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) {
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, fasilitas.getNamaFasilitas());
                ps.setString(2, fasilitas.getJenisFasilitas());
                ps.setString(3, fasilitas.getLokasi());
                ps.setString(4, fasilitas.getKondisi());
                ps.setString(5, fasilitas.getKeterangan());
                ps.setInt(6, fasilitas.getIdFasilitas());

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Gagal memperbarui fasilitas: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean hapusFasilitas(int idFasilitas) {
        if (idFasilitas <= 0) {
            return false;
        }

        String sql = "DELETE FROM fasilitas "
                + "WHERE id_fasilitas = ? "
                + "AND NOT EXISTS ("
                + "SELECT 1 FROM laporan_kerusakan "
                + "WHERE FASILITAS_id_fasilitas = ?"
                + ")";

        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) {
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idFasilitas);
                ps.setInt(2, idFasilitas);

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Gagal menghapus fasilitas: " + e.getMessage());
            return false;
        }
    }

    // SEARCH
    public List<Fasilitas> searchFasilitas(String keyword) {
        List<Fasilitas> daftar = new ArrayList<>();

        String sql = "SELECT * FROM fasilitas "
                + "WHERE CAST(id_fasilitas AS CHAR) LIKE ? "
                + "OR nama_fasilitas LIKE ? "
                + "OR jenis_fasilitas LIKE ? "
                + "OR lokasi LIKE ? "
                + "OR kondisi LIKE ? "
                + "OR keterangan LIKE ? "
                + "ORDER BY id_fasilitas ASC";

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
                        daftar.add(ambilDataFasilitas(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal mencari fasilitas: " + e.getMessage());
        }

        return daftar;
    }

    // Mengubah hasil query menjadi objek Fasilitas
    private Fasilitas ambilDataFasilitas(ResultSet rs) throws SQLException {
        Fasilitas fasilitas = new Fasilitas();

        fasilitas.setIdFasilitas(rs.getInt("id_fasilitas"));
        fasilitas.setNamaFasilitas(rs.getString("nama_fasilitas"));
        fasilitas.setJenisFasilitas(rs.getString("jenis_fasilitas"));
        fasilitas.setLokasi(rs.getString("lokasi"));
        fasilitas.setKondisi(rs.getString("kondisi"));
        fasilitas.setKeterangan(rs.getString("keterangan"));
        fasilitas.setAdminIdPengguna(rs.getInt("ADMIN_id_pengguna"));

        return fasilitas;
    }

    // Validasi data fasilitas
    private boolean validasiFasilitas(Fasilitas fasilitas) {
        if (fasilitas == null) {
            return false;
        }

        String nama = fasilitas.getNamaFasilitas();
        String jenis = fasilitas.getJenisFasilitas();
        String lokasi = fasilitas.getLokasi();
        String kondisi = fasilitas.getKondisi();
        String keterangan = fasilitas.getKeterangan();

        return nama != null && !nama.trim().isEmpty() && nama.length() <= 30
                && jenis != null && !jenis.trim().isEmpty() && jenis.length() <= 30
                && lokasi != null && !lokasi.trim().isEmpty() && lokasi.length() <= 50
                && kondisi != null
                && (kondisi.equals("Baik")
                || kondisi.equals("Rusak")
                || kondisi.equals("Perlu Perbaikan"))
                && (keterangan == null || keterangan.length() <= 60)
                && fasilitas.getAdminIdPengguna() > 0;
    }
}

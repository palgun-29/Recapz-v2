/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Peminjam;

import Login.DatabaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SITI NURLENY
 */
public class PanelPeminjam extends javax.swing.JPanel {
private DefaultTableModel tableModel;
 private JTable table;

 private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
 
    public PanelPeminjam() {
        initComponents();
        populateComboBoxes(); // Isi JComboBox Anggota dan Buku
        loadDataPeminjamanToTable(); // Muat data peminjaman ke tabel saat panel dibuka
        clearForm();
    }
    
    private void populateComboBoxes() {
        jComboBox2.removeAllItems();
        jComboBox3.removeAllItems();

        // Tambahkan placeholder item
        jComboBox2.addItem("-- Pilih Anggota --");
        jComboBox3.addItem("-- Pilih Buku --");
        
        ResultSet rs = null;

        try(Connection conn = DatabaseConnection.connect()) {

            // Isi ComboBox Nama Anggota
            String sqlAnggota = "SELECT id_anggota, nama FROM anggota ORDER BY nama";
            PreparedStatement stmt = conn.prepareStatement(sqlAnggota);
            rs = stmt.executeQuery();
            while (rs.next()) {
                // Untuk kasus sederhana, kita tampilkan nama. Jika nama tidak unik, pertimbangkan objek kustom atau Map.
                jComboBox2.addItem(rs.getString("nama"));
            }
            rs.close();
            stmt.close(); // Tutup pstmt setelah selesai digunakan

            // Isi ComboBox Judul Buku
            String sqlBuku = "SELECT id_buku, judul, stok FROM buku ORDER BY judul"; // Menggunakan 'judul' sesuai ERD
            stmt = conn.prepareStatement(sqlBuku);
            rs = stmt.executeQuery();
            while (rs.next()) {
                // Untuk kasus sederhana, kita tampilkan judul.
                jComboBox3.addItem(rs.getString("judul"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error memuat data untuk combobox: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } 
    }

private void clearForm() {
        jComboBox2.setSelectedIndex(0); // Pilih item pertama (placeholder)
        jComboBox3.setSelectedIndex(0);   // Pilih item pertama (placeholder)
        jTextField1.setText("");
        jTextField1.setText("");
        jComboBox1.setSelectedItem("Dipinjam"); // Asumsi status default "Dipinjam"
        jTable1.clearSelection();
}

    private void loadDataPeminjamanToTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Peminjaman");
        model.addColumn("Nama Anggota");
        model.addColumn("Judul Buku");
        model.addColumn("Tgl Pinjam");
        model.addColumn("Tgl Kembali");
        model.addColumn("Status");

        ResultSet rs = null;

        try (Connection conn = DatabaseConnection.connect()){
            // SQL Query dengan INNER JOIN untuk menghubungkan 3 tabel
            String sql = "SELECT " +
                         "p.id_peminjam, " +
                         "a.nama, " +
                         "b.judul, " + // Gunakan 'judul' sesuai ERD
                         "p.tangga_pinjam, " +
                         "p.tangga_kembali, " +
                         "p.status " + // Gunakan 'status' sesuai ERD
                         "FROM peminjam p " +
                         "INNER JOIN anggota a ON p.id_anggota = a.id_anggota " +
                         "INNER JOIN buku b ON p.id_buku = b.id_buku " +
                         "ORDER BY p.tangga_pinjam DESC, p.id_peminjam DESC"; // Urutkan data terbaru di atas

            PreparedStatement stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                // Format tanggal dari java.sql.Date ke String untuk tampilan di JTable
                String tglPinjam = (rs.getDate("tangga_pinjam") != null) ? dateFormat.format(rs.getDate("tangga_pinjam")) : "";
                String tglKembali = (rs.getDate("tangga_kembali") != null) ? dateFormat.format(rs.getDate("tangga_kembali")) : "";

                model.addRow(new Object[]{
                    rs.getObject("id_peminjam"),
                    rs.getString("nama"),
                    rs.getString("judul"), // Ambil dari kolom 'judul' tabel buku
                    tglPinjam,
                    tglKembali,
                    rs.getString("status") // Ambil dari kolom 'status' tabel peminjaman
                });
            }
            jTable1.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error memuat data peminjaman: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void tambahPeminjaman() {
        String namaAnggota = (String) jComboBox2.getSelectedItem();
        String judulBuku = (String) jComboBox3.getSelectedItem();
        String tglPinjamStr = jTextField1.getText().trim();
        String tglKembaliStr = jTextField2.getText().trim();
        String statusPeminjaman = (String) jComboBox1.getSelectedItem();

        // Validasi input kosong, termasuk placeholder JComboBox
        if (namaAnggota == null || namaAnggota.equals("-- Pilih Anggota --") ||
            judulBuku == null || judulBuku.equals("-- Pilih Buku --") ||
            tglPinjamStr.isEmpty() || tglKembaliStr.isEmpty() || statusPeminjaman == null) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validasi format tanggal secara ketat
        java.util.Date parsedTglPinjam;
        java.util.Date parsedTglKembali;
        try {
            dateFormat.setLenient(false); // Penting! Agar tidak mengizinkan tanggal tidak valid (misal: 2025-02-30)
            parsedTglPinjam = dateFormat.parse(tglPinjamStr);
            parsedTglKembali = dateFormat.parse(tglKembaliStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Format tanggal tidak valid (harus YYYY-MM-DD) atau tanggal tidak ada!", "Error Input Tanggal", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Konversi ke java.sql.Date untuk database
        Date sqlTglPinjam = new Date(parsedTglPinjam.getTime());
        Date sqlTglKembali = new Date(parsedTglKembali.getTime());

        int idAnggota = -1;
        int idBuku = -1;

        ResultSet rs = null;

        try (Connection conn = DatabaseConnection.connect()){
            conn.setAutoCommit(false); // Mulai transaksi untuk memastikan atomicity operasi

            // 1. Dapatkan id_anggota dari nama_anggota yang dipilih
            String sqlGetAnggotaId = "SELECT id_anggota FROM anggota WHERE nama = ?";
            PreparedStatement stmt = conn.prepareStatement(sqlGetAnggotaId);
            stmt.setString(1, namaAnggota);
            rs = stmt.executeQuery();
            if (rs.next()) {
                idAnggota = rs.getInt("id_anggota"); 
            } else {
                JOptionPane.showMessageDialog(this, "Nama Anggota '" + namaAnggota + "' tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                conn.rollback(); // Batalkan transaksi jika anggota tidak ditemukan
                return;
            }
            rs.close();
            stmt.close();

            // 2. Dapatkan id_buku dan stok saat ini dari judul_buku yang dipilih
            String sqlGetBukuInfo = "SELECT id_buku, stok FROM buku WHERE judul = ?"; // Menggunakan 'judul' dan 'stok' sesuai ERD
            stmt = conn.prepareStatement(sqlGetBukuInfo);
            stmt.setString(1, judulBuku);
            rs = stmt.executeQuery();
            int currentStok = 0;
            if (rs.next()) {
                idBuku = rs.getInt("id_buku");
                currentStok = rs.getInt("stok");
            } else {
                JOptionPane.showMessageDialog(this, "Judul Buku '" + judulBuku + "' tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                conn.rollback(); // Batalkan transaksi jika buku tidak ditemukan
                return;
            }
            rs.close();
            stmt.close();

            // Validasi Stok Buku
            if (currentStok <= 0) {
                JOptionPane.showMessageDialog(this, "Stok buku '" + judulBuku + "' tidak tersedia!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                conn.rollback(); // Batalkan transaksi jika stok habis
                return;
            }
            
            // 3. Insert data ke tabel peminjaman
            // Asumsi id_peminjaman adalah AUTO_INCREMENT
            String sqlInsertPeminjaman = "INSERT INTO peminjam (id_anggota, id_buku, tangga_pinjam, tangga_kembali, status) VALUES (?, ?, ?, ?, ?)"; // Menggunakan 'status' sesuai ERD
            stmt = conn.prepareStatement(sqlInsertPeminjaman);
            stmt.setInt(1, idAnggota); 
            stmt.setInt(2, idBuku);     
            stmt.setDate(3, sqlTglPinjam);
            stmt.setDate(4, sqlTglKembali);
            stmt.setString(5, statusPeminjaman);

            int rowsAffectedPeminjaman = stmt.executeUpdate();
            
            if (rowsAffectedPeminjaman > 0) {
                // 4. Update stok buku (kurangi 1)
                String sqlUpdateStok = "UPDATE buku SET stok = ? WHERE id_buku = ?"; // Menggunakan 'stok' sesuai ERD
                stmt = conn.prepareStatement(sqlUpdateStok);
                stmt.setInt(1, currentStok - 1);
                stmt.setInt(2, idBuku);
                int rowsAffectedStok = stmt.executeUpdate();

                if (rowsAffectedStok > 0) {
                    conn.commit(); // Jika kedua operasi berhasil, commit transaksi
                    JOptionPane.showMessageDialog(this, "Peminjaman berhasil ditambahkan dan stok diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadDataPeminjamanToTable(); // Refresh tabel peminjaman
                    populateComboBoxes(); // Refresh combobox buku (untuk update stok yang terlihat)
                } else {
                    conn.rollback(); // Jika update stok gagal, batalkan peminjaman juga
                    JOptionPane.showMessageDialog(this, "Gagal memperbarui stok buku, peminjaman dibatalkan.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                conn.rollback(); // Jika insert peminjaman gagal, batalkan semua
                JOptionPane.showMessageDialog(this, "Gagal menambahkan peminjaman.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat menambahkan peminjaman: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } 
    }

    private void kembalikanBuku() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data peminjaman dari tabel yang akan dikembalikan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idPeminjaman = jTable1.getValueAt(selectedRow, 0).toString(); // ID Peminjaman dari kolom pertama
        String judulBuku = jTable1.getValueAt(selectedRow, 2).toString(); // Judul Buku dari tabel
        String statusSaatIni = jTable1.getValueAt(selectedRow, 5).toString(); // Status saat ini

        if (statusSaatIni.equalsIgnoreCase("Dikembalikan")) {
            JOptionPane.showMessageDialog(this, "Buku ini sudah dikembalikan.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Anda yakin ingin mengembalikan buku: " + judulBuku + "?", "Konfirmasi Pengembalian", JOptionPane.YES_NO_OPTION);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            ResultSet rs = null;

            try (Connection conn = DatabaseConnection.connect()){
                
                conn.setAutoCommit(false); // Mulai transaksi

                // 1. Dapatkan id_buku dan stok saat ini dari buku
                int idBuku = -1;
                int currentStok = 0;
                String sqlGetBukuInfo = "SELECT id_buku, stok FROM buku WHERE judul = ?"; // Menggunakan 'judul' dan 'stok' sesuai ERD
                PreparedStatement stmt = conn.prepareStatement(sqlGetBukuInfo);
                stmt.setString(1, judulBuku);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    idBuku = rs.getInt("id_buku");
                    currentStok = rs.getInt("stok");
                } else {
                    JOptionPane.showMessageDialog(this, "Data buku tidak ditemukan untuk pengembalian.", "Error", JOptionPane.ERROR_MESSAGE);
                    conn.rollback();
                    return;
                }
                rs.close();
                stmt.close();

                // 2. Update status peminjaman di tabel peminjaman
                // Gunakan CURRENT_DATE() untuk mendapatkan tanggal saat ini dari MySQL untuk tanggal_kembali
                String sqlUpdatePeminjaman = "UPDATE peminjam SET status = 'Dikembalikan', tangga_kembali = CURRENT_DATE() WHERE id_peminjam = ?"; // Menggunakan 'status' sesuai ERD
                stmt = conn.prepareStatement(sqlUpdatePeminjaman);
                stmt.setString(1, idPeminjaman);

                int rowsAffectedPeminjaman = stmt.executeUpdate();

                if (rowsAffectedPeminjaman > 0) {
                    // 3. Update stok buku (tambah 1)
                    String sqlUpdateStok = "UPDATE buku SET stok = ? WHERE id_buku = ?"; // Menggunakan 'stok' sesuai ERD
                    stmt = conn.prepareStatement(sqlUpdateStok);
                    stmt.setInt(1, currentStok + 1);
                    stmt.setInt(2, idBuku);
                    int rowsAffectedStok = stmt.executeUpdate();

                    if (rowsAffectedStok > 0) {
                        conn.commit(); // Commit transaksi jika semua berhasil
                        JOptionPane.showMessageDialog(this, "Buku berhasil dikembalikan dan stok diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        clearForm();
                        loadDataPeminjamanToTable();
                        populateComboBoxes(); // Refresh combobox buku
                    } else {
                        conn.rollback(); // Rollback jika update stok gagal
                        JOptionPane.showMessageDialog(this, "Gagal memperbarui stok buku, pengembalian dibatalkan.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    conn.rollback(); // Rollback jika update peminjaman gagal
                    JOptionPane.showMessageDialog(this, "Gagal memperbarui status peminjaman. ID Peminjaman mungkin tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error saat mengembalikan buku: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 100, 100));
        jLabel1.setText("Form Peminjaman Buku");

        jLabel2.setText("Judul Buku");

        jLabel3.setText("Tanggal Pinjam");

        jLabel4.setText("Tanggal Kembali");

        jLabel5.setText("Status");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Dipinjam", "Dikembalikan" }));
        jComboBox1.setSelectedIndex(-1);

        jLabel6.setText("Nama Angggota");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Nama Anggota", "Judul Buku", "Tgl Pinjam", "Tgl Kembali", "Status"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton1.setBackground(new java.awt.Color(0, 100, 100));
        jButton1.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(204, 204, 204));
        jButton1.setText("Pinjam Buku");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 100, 100));
        jButton2.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(204, 204, 204));
        jButton2.setText("Kembalikan Buku");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jTextField1.setText("jTextField1");

        jTextField2.setText("jTextField2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(44, 44, 44)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1)
                            .addComponent(jTextField2)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 779, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addGap(35, 35, 35))
            .addGroup(layout.createSequentialGroup()
                .addGap(183, 183, 183)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1)
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        tambahPeminjaman();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        kembalikanBuku();
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}

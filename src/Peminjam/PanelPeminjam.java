/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Peminjam;

import DashboardMenuUtama.DatabaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SITI NURLENY
 */
public class PanelPeminjam extends javax.swing.JPanel {
    private DefaultTableModel tableModel;
    
    public PanelPeminjam() {
        initComponents();
        loadComboBoxData();
    }
    
    private void loadComboBoxData() {
    jComboBox2.removeAllItems();
    jComboBox3.removeAllItems();       

    try (Connection conn = DatabaseConnection.connect()) {
        // Load anggota
        Statement stmtAnggota = conn.createStatement();
        ResultSet rsAnggota = stmtAnggota.executeQuery("SELECT id_anggota, nama FROM anggota");
        while (rsAnggota.next()) {
            int id = rsAnggota.getInt("id_anggota");
            String nama = rsAnggota.getString("nama");
            jComboBox2.addItem(nama);
        }

        // Load buku
        Statement stmtBuku = conn.createStatement();
        ResultSet rsBuku = stmtBuku.executeQuery("SELECT id_buku, judul FROM buku WHERE stok > 0");
        while (rsBuku.next()) {
            int id = rsBuku.getInt("id_buku");
            String judul = rsBuku.getString("judul");
            jComboBox3.addItem(judul);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal load ComboBox: " + e.getMessage());
    }
}

   private void pinjamBuku() {
    try (Connection conn = DatabaseConnection.connect()) {
        String sql = "INSERT INTO peminjam (id_anggota, id_buku, tangga_pinjam, tangga_kembali, status) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, getSelectedId(jComboBox2)); // jComboBox2 untuk Anggota
        ps.setInt(2, getSelectedId(jComboBox3)); // jComboBox3 untuk Buku
        ps.setDate(3, Date.valueOf(jTextField1.getText().trim())); // jTextField1 untuk Tanggal Pinjam
        ps.setDate(4, Date.valueOf(jTextField2.getText().trim())); // jTextField2 untuk Tanggal Kembali
        ps.setString(5, jComboBox1.getSelectedItem().toString()); // jComboBox1 untuk Status (seperti "Dipinjam")
        ps.executeUpdate();

        // Kurangi stok buku
        PreparedStatement psUpdateStok = conn.prepareStatement("UPDATE buku SET stok = stok - 1 WHERE id_buku = ?");
        psUpdateStok.setInt(1, getSelectedId(jComboBox3));
        psUpdateStok.executeUpdate();

        JOptionPane.showMessageDialog(this, "Data peminjaman berhasil disimpan.");        
        // Opsional: Bersihkan inputan setelah berhasil
        jTextField1.setText(""); // Bersihkan tanggal pinjam
        jTextField2.setText(""); // Bersihkan tanggal kembali
        jComboBox1.setSelectedIndex(-1);
        jComboBox2.setSelectedIndex(-1);
        jComboBox3.setSelectedIndex(-1);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + e.getMessage());
    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this, "Format tanggal salah. Gunakan format YYYY-MM-DD.");
    }
}
   private void loadTable() {
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel(); // Asumsikan jTable1 adalah nama JTable Anda
    model.setRowCount(0); // Bersihkan semua baris yang ada di tabel

    String sql = "SELECT p.id_peminjam, a.nama, b.judul, p.tangga_pinjam, p.tangga_kembali, p.status " +
                 "FROM peminjam p " +
                 "JOIN anggota a ON p.id_anggota = a.id_anggota " +
                 "JOIN buku b ON p.id_buku = b.id_buku";

    try (Connection conn = DatabaseConnection.connect();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            // Ambil data dari ResultSet
            // int idPeminjam = rs.getInt("id_peminjam"); // Jika Anda ingin menampilkan ID peminjam
            String namaAnggota = rs.getString("nama");
            String judulBuku = rs.getString("judul");
            Date tanggalPinjam = rs.getDate("tangga_pinjam");
            Date tanggalKembali = rs.getDate("tangga_kembali");
            String status = rs.getString("status");

            // Tambahkan data ke model tabel
            model.addRow(new Object[]{namaAnggota, judulBuku, tanggalPinjam, tanggalKembali, status});
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error memuat data tabel: " + e.getMessage());
    }
}

    private void kembalikanBuku() {
    int selectedRow = jTable1.getSelectedRow(); // Ambil baris yang dipilih di JTable

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih baris peminjaman yang akan dikembalikan terlebih dahulu.");
        return;
    }

    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    String namaAnggotaTabel = (String) model.getValueAt(selectedRow, 0); // Kolom Nama Anggota
    String judulBukuTabel = (String) model.getValueAt(selectedRow, 1);    // Kolom Judul Buku
    java.sql.Date tanggalPinjamTabel = (java.sql.Date) model.getValueAt(selectedRow, 2); // Kolom Tanggal Pinjam
    
    int idPeminjam = -1;
    int idBuku = -1; // Untuk mengembalikan stok

    try (Connection conn = DatabaseConnection.connect()) {
        // 1. Dapatkan id_peminjam dan id_buku berdasarkan data yang dipilih di tabel
        String getIdsSql = "SELECT p.id_peminjam, p.id_buku FROM peminjam p " +
                           "JOIN anggota a ON p.id_anggota = a.id_anggota " +
                           "JOIN buku b ON p.id_buku = b.id_buku " +
                           "WHERE a.nama = ? AND b.judul = ? AND p.tangga_pinjam = ? AND p.status = 'Dipinjam'"; // Hanya yang masih dipinjam
        
        PreparedStatement psGetIds = conn.prepareStatement(getIdsSql);
        psGetIds.setString(1, namaAnggotaTabel);
        psGetIds.setString(2, judulBukuTabel);
        psGetIds.setDate(3, tanggalPinjamTabel);
        
        ResultSet rs = psGetIds.executeQuery();
        if (rs.next()) {
            idPeminjam = rs.getInt("id_peminjam");
            idBuku = rs.getInt("id_buku");
        } else {
            JOptionPane.showMessageDialog(this, "Data peminjaman tidak ditemukan atau sudah dikembalikan.");
            return;
        }

        // 2. Ubah status peminjaman menjadi "Dikembalikan"
        String updatePeminjamanSql = "UPDATE peminjam SET status = 'Dikembalikan' WHERE id_peminjam = ?";
        PreparedStatement psUpdatePeminjaman = conn.prepareStatement(updatePeminjamanSql);
        psUpdatePeminjaman.setInt(1, idPeminjam);
        psUpdatePeminjaman.executeUpdate();

        // 3. Tambahkan kembali stok buku
        String updateStokSql = "UPDATE buku SET stok = stok + 1 WHERE id_buku = ?";
        PreparedStatement psUpdateStok = conn.prepareStatement(updateStokSql);
        psUpdateStok.setInt(1, idBuku);
        psUpdateStok.executeUpdate();

        JOptionPane.showMessageDialog(this, "Buku berhasil dikembalikan dan stok diperbarui.");
        
        // 4. Perbarui tabel GUI agar menampilkan perubahan
        loadTable(); // Memuat ulang semua data dari database
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal mengembalikan buku: " + e.getMessage());
        e.printStackTrace(); // Cetak stack trace untuk debugging
    }
}

    private int getSelectedId(JComboBox<String> comboBox) {
        String item = (String) comboBox.getSelectedItem();
        return Integer.parseInt(item.split(" - ")[0]);
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
        jButton3 = new javax.swing.JButton();

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

        jComboBox3.setSelectedIndex(-1);

        jButton3.setBackground(new java.awt.Color(0, 100, 100));
        jButton3.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(204, 204, 204));
        jButton3.setText("Lihat");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

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
                .addGap(81, 81, 81)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(104, 104, 104)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(117, 117, 117)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       pinjamBuku();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        kembalikanBuku();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        loadTable();
    }//GEN-LAST:event_jButton3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
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

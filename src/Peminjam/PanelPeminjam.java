

package Peminjam;

import DashboardMenuUtama.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SITI NURLENY
 */
public class PanelPeminjam extends javax.swing.JPanel {
    
    public PanelPeminjam() {
        initComponents();
        loadAnggota();
        loadBuku();
        loadTable();
        clearForm();
    }
    
    private void loadAnggota() {
    jComboBox2.removeAllItems();
    try (Connection conn = DatabaseConnection.connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT nama FROM anggota")) {
        while (rs.next()) {
            jComboBox2.addItem(rs.getString("nama"));
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal load anggota: " + e.getMessage());
    }
}

private void loadBuku() {
    jComboBox3.removeAllItems();
    try (Connection conn = DatabaseConnection.connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT judul FROM buku")) {
        while (rs.next()) {
            jComboBox3.addItem(rs.getString("judul"));
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal load buku: " + e.getMessage());
    }
}

private void loadTable() {
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("Nama Anggota");
    model.addColumn("Judul Buku");
    model.addColumn("Tanggal Pinjam");
    model.addColumn("Tanggal Kembali");
    model.addColumn("Status");

    try (Connection conn = DatabaseConnection.connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(
             "SELECT a.nama, b.judul, p.tangga_pinjam, p.tangga_kembali, p.status " +
             "FROM peminjam p " +
             "JOIN anggota a ON p.id_anggota = a.id_anggota " +
             "JOIN buku b ON p.id_buku = b.id_buku")) {

        while (rs.next()) {
            model.addRow(new Object[] {
                rs.getString("nama"),
                rs.getString("judul"),
                rs.getString("tangga_pinjam"),
                rs.getString("tangga_kembali"),
                rs.getString("status")
            });
        }

        jTable1.setModel(model);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
    }
}


private void pinjamBuku() {
    try (Connection conn = DatabaseConnection.connect()) {
        String namaAnggota = jComboBox2.getSelectedItem().toString();  // Nama Anggota
        String judulBuku = jComboBox3.getSelectedItem().toString();    // Judul Buku
        String tanggalPinjam = jTextField1.getText().trim();           // Tanggal Pinjam
        String tanggalKembali = jTextField2.getText().trim();          // Tanggal Kembali
        String status = jComboBox1.getSelectedItem().toString();       // Status

        // Validasi form
        if (tanggalPinjam.isEmpty() || tanggalKembali.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tanggal pinjam dan kembali harus diisi!");
            return;
        }

        // Ambil id_anggota dari nama
        int idAnggota = -1;
        PreparedStatement stmt1 = conn.prepareStatement("SELECT id_anggota FROM anggota WHERE nama = ?");
        stmt1.setString(1, namaAnggota);
        ResultSet rs1 = stmt1.executeQuery();
        if (rs1.next()) {
            idAnggota = rs1.getInt("id_anggota");
        } else {
            JOptionPane.showMessageDialog(this, "Anggota tidak ditemukan!");
            return;
        }

        // Ambil id_buku dari judul
        int idBuku = -1;
        PreparedStatement stmt2 = conn.prepareStatement("SELECT id_buku FROM buku WHERE judul = ?");
        stmt2.setString(1, judulBuku);
        ResultSet rs2 = stmt2.executeQuery();
        if (rs2.next()) {
            idBuku = rs2.getInt("id_buku");
        } else {
            JOptionPane.showMessageDialog(this, "Buku tidak ditemukan!");
            return;
        }

        // Simpan ke peminjam
        String sql = "INSERT INTO peminjam (id_anggota, id_buku, tangga_pinjam, tangga_kembali, status) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idAnggota);
        ps.setInt(2, idBuku);
        ps.setString(3, tanggalPinjam);
        ps.setString(4, tanggalKembali);
        ps.setString(5, status);
        ps.executeUpdate();

        // Kurangi stok
        PreparedStatement psUpdate = conn.prepareStatement("UPDATE buku SET stok = stok - 1 WHERE id_buku = ?");
        psUpdate.setInt(1, idBuku);
        psUpdate.executeUpdate();

        JOptionPane.showMessageDialog(this, "Data peminjaman berhasil disimpan!");
        clearForm();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + e.getMessage());
    } catch (NullPointerException e) {
        JOptionPane.showMessageDialog(this, "Pastikan semua data dipilih!");
    }
}

private void kembalikanBuku() {
    int selectedRow = jTable1.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih salah satu baris untuk dikembalikan!");
        return;
    }

    String namaAnggota = jTable1.getValueAt(selectedRow, 0).toString();
    String judulBuku = jTable1.getValueAt(selectedRow, 1).toString();

    try (Connection conn = DatabaseConnection.connect()) {
        // Ambil id anggota
        int idAnggota = -1;
        PreparedStatement stmt1 = conn.prepareStatement("SELECT id_anggota FROM anggota WHERE nama = ?");
        stmt1.setString(1, namaAnggota);
        ResultSet rs1 = stmt1.executeQuery();
        if (rs1.next()) {
            idAnggota = rs1.getInt("id_anggota");
        }

        // Ambil id buku
        int idBuku = -1;
        PreparedStatement stmt2 = conn.prepareStatement("SELECT id_buku FROM buku WHERE judul = ?");
        stmt2.setString(1, judulBuku);
        ResultSet rs2 = stmt2.executeQuery();
        if (rs2.next()) {
            idBuku = rs2.getInt("id_buku");
        }

        if (idAnggota != -1 && idBuku != -1) {
            // Update status peminjaman jadi "Dikembalikan"
            PreparedStatement updateStatus = conn.prepareStatement(
                "UPDATE peminjam SET status = 'Dikembalikan' WHERE id_anggota = ? AND id_buku = ? AND status = 'Dipinjam'");
            updateStatus.setInt(1, idAnggota);
            updateStatus.setInt(2, idBuku);
            int rows = updateStatus.executeUpdate();

            if (rows > 0) {
                // Tambah stok buku
                PreparedStatement tambahStok = conn.prepareStatement(
                    "UPDATE buku SET stok = stok + 1 WHERE id_buku = ?");
                tambahStok.setInt(1, idBuku);
                tambahStok.executeUpdate();

                JOptionPane.showMessageDialog(this, "Buku berhasil dikembalikan!");

            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengubah status. Mungkin buku sudah dikembalikan.");
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal mengembalikan buku: " + e.getMessage());
    }
}

private void clearForm() {
    jComboBox1.setSelectedIndex(0); // Status
    jComboBox2.setSelectedIndex(0); // Nama anggota
    jComboBox3.setSelectedIndex(0); // Judul buku
    jTextField1.setText("");        // Tanggal pinjam
    jTextField2.setText("");        // Tanggal kembali
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
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 779, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(35, 35, 35))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(104, 104, 104)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(117, 117, 117)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(281, 281, 281)
                        .addComponent(jLabel1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1)
                .addGap(32, 32, 32)
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

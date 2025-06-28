/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Anggota;

import Login.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SITI NURLENY
 */
public class PanelAnggota extends javax.swing.JPanel {
    private String JenisKelamin;
    private String sql;

    /**
     * Creates new form PanelAnggota
     */
    public PanelAnggota() {
        initComponents();
        tambahDataAnggota();
    }
    
    private void clearForm() {
        jTextField1.setText("");
        buttonGroup1.clearSelection(); // Membersihkan pilihan radio button
        jTextField2.setText("");
        jTextField3.setText("");
        jComboBox1.setSelectedIndex(0); // Pilih item pertama "-- Pilih Kategori --"
        jTextArea1.setText(""); 
    }
    
    private void loadDataAnggota() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Anggota");
        model.addColumn("Nama");
        model.addColumn("No.Handphone");
        model.addColumn("Email");
        model.addColumn("Jenis Kelamin");
        model.addColumn("Kategori");
        model.addColumn("Alamat");
        
        ResultSet rs = null;
        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "SELECT id_anggota, nama, no_telpon, email, jenis_kelamin, kategori, alamat FROM anggota";
            PreparedStatement stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_anggota"),
                    rs.getString("nama"),
                    rs.getString("no_telpon"),
                    rs.getString("email"),
                    rs.getString("jenis_kelamin"),
                    rs.getString("kategori"),
                    rs.getString("alamat")
                });
            }
            jTable1.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error memuat data anggota: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                System.err.println("Error menutup resource: " + e.getMessage());
            }
        }
    }

    private void tambahDataAnggota() {
        String nama = jTextField1.getText().trim();
        String noHandphone = jTextField2.getText().trim();
        String email = jTextField3.getText().trim();
        String alamat = jTextArea1.getText().trim();
        String kategori = (String) jComboBox1.getSelectedItem(); // Pastikan tidak null

        String jenisKelamin = "";
        if (jRadioButton1.isSelected()) {
            jenisKelamin = "Laki-laki";
        } else if (jRadioButton2.isSelected()) {
            jenisKelamin = "Perempuan";
        }

        // Validasi input
        if (nama.isEmpty() || noHandphone.isEmpty() || email.isEmpty() || alamat.isEmpty() || kategori.isEmpty() || jenisKelamin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try(Connection conn = DatabaseConnection.connect()){
            String sql = "INSERT INTO anggota ( nama, no_telpon, email, jenis_kelamin kategori, alamat) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nama);
            stmt.setString(2, noHandphone); // Parameter untuk ENUM
            stmt.setString(3, email);
            stmt.setString(4, jenisKelamin);
            stmt.setString(5, kategori);
            stmt.setString(6, alamat);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data anggota berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadDataAnggota(); // Refresh tabel setelah tambah
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan data anggota.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error menambahkan data: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Cetak stack trace untuk debug lebih lanjut
        }
    }
    
    private void editDataAnggota() {
        // Ganti jTextField1, jTextField2, dll. dengan nama variabel komponen GUI Anda
        
        String nama = jTextField1.getText().trim();
        String noHandphone = jTextField2.getText().trim();
        String email = jTextField3.getText().trim();
        String alamat = jTextArea1.getText().trim();
        String kategori = jComboBox1.getSelectedItem() != null ? jComboBox1.getSelectedItem().toString() : "";

        String jenisKelamin = "";
        if (jRadioButton1.isSelected()) {
            jenisKelamin = "Laki-laki";
        } else if (jRadioButton2.isSelected()) {
            jenisKelamin = "Perempuan";
        }

        // Validasi input
        if (nama.isEmpty() || noHandphone.isEmpty() || email.isEmpty() || alamat.isEmpty() || kategori.equals("-- Pilih Kategori --") || jenisKelamin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.connect()){
            // PERBAIKI INI: Gunakan 'no_telpon' di SQL query
            String sql = "UPDATE anggota SET nama = ?, no_telpon = ?, email = ?, jenis_kelamin = ?, kategori = ?, alamat = ? WHERE id_anggota = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nama);
            stmt.setString(2, noHandphone);
            stmt.setString(3, email); // Variabel Java 'noHandphone' ke kolom DB 'no_telpon'
            stmt.setString(4, jenisKelamin);
            stmt.setString(5, kategori);
            stmt.setString(6, alamat);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data anggota berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadDataAnggota(); // Refresh tabel
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui data anggota. ID mungkin tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error memperbarui data: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void hapusDataAnggota() {
        // Ganti jTextField1 dengan txtIdAnggota jika Anda sudah mengganti namanya
        String nama = (jTextField1 != null) ? jTextField1.getText().trim() : "";

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data anggota yang akan dihapus dari tabel atau masukkan Nama.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Anda yakin ingin menghapus data anggota dengan Nama: " + nama + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (konfirmasi == JOptionPane.YES_OPTION) {

            try (Connection conn = DatabaseConnection.connect()){

                String sql = "DELETE FROM anggota WHERE nama = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nama);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Data anggota berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadDataAnggota(); // Refresh tabel
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data anggota. nama tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error menghapus data: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jComboBox1 = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        buttonTambah = new javax.swing.JButton();
        buttonLihat = new javax.swing.JButton();
        buttonEdit = new javax.swing.JButton();
        buttonHapus = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 100, 100));
        jLabel1.setText("Form Data Anggota ");

        jLabel2.setText("Nama");

        jLabel3.setText("No.Handphone");

        jLabel4.setText("Email");

        jLabel5.setText("Jenis Kelamin");

        jLabel6.setText("Kategori");

        jLabel7.setText("Alamat");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Laki-laki");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Perempuan");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        jComboBox1.setBackground(new java.awt.Color(255, 255, 255));
        jComboBox1.setForeground(new java.awt.Color(255, 255, 255));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Pelajar", "Non-Pelajar" }));
        jComboBox1.setSelectedIndex(-1);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Nama", "No.Handphone", "Email", "Jenis Kelamin", "Kategori", "Alamat"
            }
        ));
        jScrollPane2.setViewportView(jTable1);

        buttonTambah.setBackground(new java.awt.Color(0, 100, 100));
        buttonTambah.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        buttonTambah.setForeground(new java.awt.Color(204, 204, 204));
        buttonTambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IconGambar/add_2_20dp_FFFFFF_FILL0_wght400_GRAD0_opsz20.png"))); // NOI18N
        buttonTambah.setText("Tambah");
        buttonTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTambahActionPerformed(evt);
            }
        });

        buttonLihat.setBackground(new java.awt.Color(0, 100, 100));
        buttonLihat.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        buttonLihat.setForeground(new java.awt.Color(204, 204, 204));
        buttonLihat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IconGambar/visibility_20dp_FFFFFF_FILL0_wght400_GRAD0_opsz20.png"))); // NOI18N
        buttonLihat.setText("Lihat");
        buttonLihat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLihatActionPerformed(evt);
            }
        });

        buttonEdit.setBackground(new java.awt.Color(0, 100, 100));
        buttonEdit.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        buttonEdit.setForeground(new java.awt.Color(204, 204, 204));
        buttonEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IconGambar/edit_square_20dp_FFFFFF_FILL0_wght400_GRAD0_opsz20.png"))); // NOI18N
        buttonEdit.setText("Edit");
        buttonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditActionPerformed(evt);
            }
        });

        buttonHapus.setBackground(new java.awt.Color(0, 100, 100));
        buttonHapus.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        buttonHapus.setForeground(new java.awt.Color(204, 204, 204));
        buttonHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IconGambar/delete_forever_20dp_FFFFFF_FILL0_wght400_GRAD0_opsz20.png"))); // NOI18N
        buttonHapus.setText("Hapus");
        buttonHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonHapusActionPerformed(evt);
            }
        });

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IconGambar/arrow_back_20dp_1F1F1F_FILL0_wght400_GRAD0_opsz20.png"))); // NOI18N
        jLabel8.setText("Kembali");
        jLabel8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(buttonTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(48, 48, 48)
                                .addComponent(buttonLihat, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(49, 49, 49)
                                .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(49, 49, 49)
                                .addComponent(buttonHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 752, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(33, 33, 33)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel5))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(33, 33, 33)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jRadioButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(260, 260, 260)
                        .addComponent(jLabel1)))
                .addContainerGap(131, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonLihat, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buttonHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buttonTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        // TODO add your handling code here:
        JenisKelamin = "Perempuan";
        jRadioButton1.setSelected(false);
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jLabel8MouseClicked

    private void buttonTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTambahActionPerformed
        // TODO add your handling code here:
        tambahDataAnggota();
    }//GEN-LAST:event_buttonTambahActionPerformed

    private void buttonLihatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLihatActionPerformed
        // TODO add your handling code here:
        loadDataAnggota();
    }//GEN-LAST:event_buttonLihatActionPerformed

    private void buttonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditActionPerformed
        // TODO add your handling code here:
        editDataAnggota();
    }//GEN-LAST:event_buttonEditActionPerformed

    private void buttonHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonHapusActionPerformed
        // TODO add your handling code here:
        hapusDataAnggota();
    }//GEN-LAST:event_buttonHapusActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        // TODO add your handling code here:
        JenisKelamin = "Laki-laki";
        jRadioButton2.setSelected(false);
    }//GEN-LAST:event_jRadioButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonEdit;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton buttonHapus;
    private javax.swing.JButton buttonLihat;
    private javax.swing.JButton buttonTambah;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}

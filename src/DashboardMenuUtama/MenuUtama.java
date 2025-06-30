
package DashboardMenuUtama;

import Anggota.PanelAnggota;
import Buku.PanelBuku;
import Peminjam.PanelPeminjam;
import java.awt.CardLayout;

public class MenuUtama extends javax.swing.JFrame {

    private CardLayout cardLayout;
    
    private PanelAnggota panelAnggota;
    private PanelBuku panelBuku;
    private PanelPeminjam panelPeminjam;
    
    public MenuUtama(String kategori) {
 
        initComponents();
        

        cardLayout = (CardLayout)jPanel2.getLayout();
        
        panelAnggota = new PanelAnggota();
        panelBuku = new PanelBuku();
        panelPeminjam = new PanelPeminjam();
        
        jPanel2.add(panelAnggota, "anggotaCard");
        jPanel2.add(panelBuku, "bukuCard");
        jPanel2.add(panelPeminjam, "peminjamCard");
        
        //Tampilan default saat user berhasil login masuk
        cardLayout.show(jPanel2, "anggotaCard");
        
        if (!"Admin".equalsIgnoreCase(kategori)) {
            labelBuku.setEnabled(false);
            labelBuku.setVisible(false);
        }
        
    }

    public MenuUtama() {
        this("Admin"); // Default ke admin jika konstruktor tanpa parameter dipakai
    }

    // Metode untuk mengatur hak akses UI di MenuUtama
    


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        labelJudul = new javax.swing.JLabel();
        labelAnggota = new javax.swing.JLabel();
        labelBuku = new javax.swing.JLabel();
        labelPinjam = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        mainContentPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 100, 100));

        labelJudul.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        labelJudul.setForeground(new java.awt.Color(255, 255, 255));
        labelJudul.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IconGambar/library (1).png"))); // NOI18N
        labelJudul.setText("RECAPZ Perpustakaan");

        labelAnggota.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        labelAnggota.setForeground(new java.awt.Color(204, 204, 204));
        labelAnggota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IconGambar/group (1).png"))); // NOI18N
        labelAnggota.setText("Anggota");
        labelAnggota.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelAnggota.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelAnggotaMouseClicked(evt);
            }
        });

        labelBuku.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        labelBuku.setForeground(new java.awt.Color(204, 204, 204));
        labelBuku.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IconGambar/books.png"))); // NOI18N
        labelBuku.setText("Buku");
        labelBuku.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelBuku.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelBukuMouseClicked(evt);
            }
        });

        labelPinjam.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        labelPinjam.setForeground(new java.awt.Color(204, 204, 204));
        labelPinjam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IconGambar/book.png"))); // NOI18N
        labelPinjam.setText("Pinjam");
        labelPinjam.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelPinjam.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelPinjamMouseClicked(evt);
            }
        });

        jSeparator1.setForeground(new java.awt.Color(204, 204, 204));

        jLabel1.setFont(new java.awt.Font("sansserif", 0, 10)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("© 2025 Tim RECAPZ. All Rights Reserved.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelJudul, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addComponent(labelAnggota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelBuku, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelPinjam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(labelJudul, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelAnggota, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelBuku, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelPinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 296, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(14, 14, 14))
        );

        labelJudul.getAccessibleContext().setAccessibleName("RECAPZ \nPerpustakaan");

        mainContentPanel.setLayout(new java.awt.CardLayout());

        jPanel2.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 861, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(1203, Short.MAX_VALUE)
                    .addComponent(mainContentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(mainContentPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void labelAnggotaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelAnggotaMouseClicked

        cardLayout.show(jPanel2, "anggotaCard");
    }//GEN-LAST:event_labelAnggotaMouseClicked

    private void labelBukuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelBukuMouseClicked
        if (labelBuku.isEnabled()) {
        cardLayout.show(jPanel2, "bukuCard");
        }
    }//GEN-LAST:event_labelBukuMouseClicked

    private void labelPinjamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelPinjamMouseClicked

        cardLayout.show(jPanel2, "peminjamCard");
    }//GEN-LAST:event_labelPinjamMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MenuUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuUtama().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel labelAnggota;
    private javax.swing.JLabel labelBuku;
    private javax.swing.JLabel labelJudul;
    private javax.swing.JLabel labelPinjam;
    private javax.swing.JPanel mainContentPanel;
    // End of variables declaration//GEN-END:variables
}

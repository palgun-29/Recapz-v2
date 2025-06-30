/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DashboardMenuUtama;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author SITI NURLENY
 */
public class DatabaseConnection {
    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/db_recapz_perpustakaan", "root", "lupagw1910"); // Ubah sesuai
            System.out.println("Koneksi berhasil");
        } catch (Exception e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        }
        return conn;
    }
}

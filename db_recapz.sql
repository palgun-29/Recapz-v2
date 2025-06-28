SELECT p.id_pinjam, a.anggota, b.judul, p.tangga_pinjam, p.tangga_kembali, p.status 
FROM peminjam p
JOIN anggota a ON p.id_anggota = a.id_anggota
JOIN buku b ON p.id_buku = b.id_buku

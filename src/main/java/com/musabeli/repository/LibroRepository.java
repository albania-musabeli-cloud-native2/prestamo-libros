package com.musabeli.repository;

import com.musabeli.config.DatabaseConfig;
import com.musabeli.entities.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibroRepository {

    public List<Libro> findAll() throws Exception {
        String sql = "SELECT ID, TITULO, AUTOR, ISBN, STOCK, CREATED_AT FROM LIBROS ORDER BY ID";
        List<Libro> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        }
        return result;
    }

    public Optional<Libro> findById(long id) throws Exception {
        String sql = "SELECT ID, TITULO, AUTOR, ISBN, STOCK, CREATED_AT FROM LIBROS WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Libro create(Libro libro) throws Exception {
        String sql = "INSERT INTO LIBROS (TITULO, AUTOR, ISBN, STOCK) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setInt(4, libro.getStock() != null ? libro.getStock() : 1);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    libro.setId(rs.getLong(1));
                }
            }
        }
        return libro;
    }

    public boolean update(long id, Libro libro) throws Exception {
        String sql = "UPDATE LIBROS SET TITULO = ?, AUTOR = ?, ISBN = ?, STOCK = ? WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setInt(4, libro.getStock());
            ps.setLong(5, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws Exception {
        String sql = "DELETE FROM LIBROS WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Libro mapRow(ResultSet rs) throws SQLException {
        Timestamp createdAt = rs.getTimestamp("CREATED_AT");
        return Libro.builder()
                .id(rs.getLong("ID"))
                .titulo(rs.getString("TITULO"))
                .autor(rs.getString("AUTOR"))
                .isbn(rs.getString("ISBN"))
                .stock(rs.getInt("STOCK"))
                .createdAt(createdAt != null ? createdAt.toLocalDateTime() : null)
                .build();
    }
}

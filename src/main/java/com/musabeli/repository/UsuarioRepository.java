package com.musabeli.repository;

import com.musabeli.config.DatabaseConfig;
import com.musabeli.entities.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {

    public List<Usuario> findAll() throws Exception {
        String sql = "SELECT ID, NOMBRE, EMAIL, CREATED_AT FROM USUARIOS ORDER BY ID";
        List<Usuario> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        }
        return result;
    }

    public Optional<Usuario> findById(long id) throws Exception {
        String sql = "SELECT ID, NOMBRE, EMAIL, CREATED_AT FROM USUARIOS WHERE ID = ?";
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

    public Usuario create(Usuario usuario) throws Exception {
        String sql = "INSERT INTO USUARIOS (NOMBRE, EMAIL) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getLong(1));
                }
            }
        }
        return usuario;
    }

    public boolean update(long id, Usuario usuario) throws Exception {
        String sql = "UPDATE USUARIOS SET NOMBRE = ?, EMAIL = ? WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setLong(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws Exception {
        String sql = "DELETE FROM USUARIOS WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Usuario mapRow(ResultSet rs) throws SQLException {
        Timestamp createdAt = rs.getTimestamp("CREATED_AT");
        return Usuario.builder()
                .id(rs.getLong("ID"))
                .nombre(rs.getString("NOMBRE"))
                .email(rs.getString("EMAIL"))
                .createdAt(createdAt != null ? createdAt.toLocalDateTime() : null)
                .build();
    }
}

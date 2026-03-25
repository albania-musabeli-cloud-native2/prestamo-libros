package com.musabeli.repository;

import com.musabeli.config.DatabaseConfig;
import com.musabeli.entities.Prestamo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrestamoRepository {

    public List<Prestamo> findAll() throws Exception {
        String sql = "SELECT ID, ID_USUARIO, ID_LIBRO, FECHA_INICIO, FECHA_FIN, ESTADO, CREATED_AT FROM PRESTAMOS ORDER BY ID";
        List<Prestamo> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        }
        return result;
    }

    public Optional<Prestamo> findById(long id) throws Exception {
        String sql = "SELECT ID, ID_USUARIO, ID_LIBRO, FECHA_INICIO, FECHA_FIN, ESTADO, CREATED_AT FROM PRESTAMOS WHERE ID = ?";
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

    public Prestamo create(Prestamo prestamo) throws Exception {
        String sql = "INSERT INTO PRESTAMOS (ID_USUARIO, ID_LIBRO, FECHA_INICIO, FECHA_FIN, ESTADO) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
            ps.setLong(1, prestamo.getIdUsuario());
            ps.setLong(2, prestamo.getIdLibro());
            ps.setDate(3, Date.valueOf(prestamo.getFechaInicio()));
            ps.setDate(4, Date.valueOf(prestamo.getFechaFin()));
            ps.setString(5, prestamo.getEstado() != null ? prestamo.getEstado() : "ACTIVO");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    prestamo.setId(rs.getLong(1));
                }
            }
        }
        return prestamo;
    }

    public boolean update(long id, Prestamo prestamo) throws Exception {
        String sql = "UPDATE PRESTAMOS SET ID_USUARIO = ?, ID_LIBRO = ?, FECHA_INICIO = ?, FECHA_FIN = ?, ESTADO = ? WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, prestamo.getIdUsuario());
            ps.setLong(2, prestamo.getIdLibro());
            ps.setDate(3, Date.valueOf(prestamo.getFechaInicio()));
            ps.setDate(4, Date.valueOf(prestamo.getFechaFin()));
            ps.setString(5, prestamo.getEstado());
            ps.setLong(6, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws Exception {
        String sql = "DELETE FROM PRESTAMOS WHERE ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Prestamo mapRow(ResultSet rs) throws SQLException {
        return Prestamo.builder()
                .id(rs.getLong("ID"))
                .idUsuario(rs.getLong("ID_USUARIO"))
                .idLibro(rs.getLong("ID_LIBRO"))
                .fechaInicio(rs.getDate("FECHA_INICIO").toLocalDate())
                .fechaFin(rs.getDate("FECHA_FIN").toLocalDate())
                .estado(rs.getString("ESTADO"))
                .createdAt(rs.getTimestamp("CREATED_AT").toLocalDateTime())
                .build();
    }
}

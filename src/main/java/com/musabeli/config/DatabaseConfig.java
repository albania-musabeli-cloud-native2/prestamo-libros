package com.musabeli.config;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConfig {

    private static volatile String walletPath;

    private DatabaseConfig() {}

    public static Connection getConnection() throws Exception {
        String tnsName  = System.getenv("ORACLE_TNS_NAME");
        String username = System.getenv("ORACLE_USERNAME");
        String password = System.getenv("ORACLE_PASSWORD");

        configureWallet();

        String url = "jdbc:oracle:thin:@" + tnsName;
        return DriverManager.getConnection(url, username, password);
    }

    private static void configureWallet() throws Exception {
        if (walletPath != null) return;

        URL walletUrl = DatabaseConfig.class.getClassLoader().getResource("wallet");
        if (walletUrl == null) {
            throw new IllegalStateException("Wallet no encontrado en el classpath (resources/wallet)");
        }

        String resolvedPath;
        if ("file".equals(walletUrl.getProtocol())) {
            resolvedPath = Paths.get(walletUrl.toURI()).toString();
        } else {
            resolvedPath = extractWalletToTemp();
        }

        walletPath = resolvedPath;
        System.setProperty("oracle.net.tns_admin", walletPath);
        System.setProperty("oracle.net.wallet_location",
                "(SOURCE=(METHOD=FILE)(METHOD_DATA=(DIRECTORY=" + walletPath + ")))");
    }

    private static String extractWalletToTemp() throws Exception {
        Path tempDir = Files.createTempDirectory("oracle-wallet");
        String[] files = {
            "cwallet.sso", "ewallet.p12", "ewallet.pem",
            "keystore.jks", "ojdbc.properties", "sqlnet.ora",
            "tnsnames.ora", "truststore.jks"
        };
        for (String file : files) {
            try (InputStream is = DatabaseConfig.class.getClassLoader()
                    .getResourceAsStream("wallet/" + file)) {
                if (is != null) {
                    Files.copy(is, tempDir.resolve(file), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        return tempDir.toString();
    }
}

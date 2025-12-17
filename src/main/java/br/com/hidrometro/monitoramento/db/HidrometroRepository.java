package br.com.hidrometro.monitoramento.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HidrometroRepository {

    private static final String URL = "jdbc:sqlite:hidrometro_data.db";

    public HidrometroRepository() {
        criarTabela();
    }

    private void criarTabela() {
        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement()) {

            // Cria tabela base se não existir
            String sqlCreate = "CREATE TABLE IF NOT EXISTS leituras (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "hidrometro_id TEXT NOT NULL," +
                    "origem TEXT DEFAULT 'Interno'," +
                    "timestamp TEXT NOT NULL," +
                    "valor_ocr TEXT," +
                    "valor_real REAL" +
                    ");";
            stmt.execute(sqlCreate);

            // Tenta adicionar a coluna 'origem' caso a tabela já exista do passo anterior
            // sem ela.
            // SQLite ignora se tentarmos adicionar uma coluna que ja existe? Não, da erro.
            // Mas podemos fazer um try-catch silencioso para essa "migration" simples.
            try {
                stmt.execute("ALTER TABLE leituras ADD COLUMN origem TEXT DEFAULT 'Interno';");
            } catch (SQLException ignored) {
                // Coluna provavelmente ja existe
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco: " + e.getMessage());
        }
    }

    public void salvarLeitura(String hidrometroId, String origem, String valorOCR, double valorReal) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String sql = "INSERT INTO leituras(hidrometro_id, origem, timestamp, valor_ocr, valor_real) VALUES(?,?,?,?,?)";

        // Salvar no Banco
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hidrometroId);
            pstmt.setString(2, origem);
            pstmt.setString(3, timestamp);
            pstmt.setString(4, valorOCR);
            pstmt.setDouble(5, valorReal);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao salvar leitura no banco: " + e.getMessage());
        }

        // Salvar no Arquivo TXT
        try (java.io.FileWriter fw = new java.io.FileWriter("leituras.txt", true);
             java.io.PrintWriter out = new java.io.PrintWriter(fw)) {
            
            out.printf("[%s] Fonte: %s (%s) | OCR: %s | Real: %.2f%n", 
                    timestamp, hidrometroId, origem, valorOCR, valorReal);
            
        } catch (java.io.IOException e) {
            System.err.println("Erro ao salvar leitura no arquivo: " + e.getMessage());
        }
    }
}

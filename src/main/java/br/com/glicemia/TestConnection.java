package br.com.glicemia;

import br.com.glicemia.util.DatabaseConnection;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║  Teste de Conexão GlicemIA + NeonDB   ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        System.out.println("Testando conexão com PostgreSQL (NeonDB)...\n");

        if (DatabaseConnection.testConnection()) {
            System.out.println("✓ Conexão com banco de dados OK!");
            System.out.println("✓ SSL habilitado");
            System.out.println("\nVersão do banco: " + DatabaseConnection.getDatabaseVersion());
            System.out.println("\n🎉 Setup concluído com sucesso!");
        } else {
            System.out.println("✗ Falha na conexão com banco de dados");
            System.out.println("\nVerifique:");
            System.out.println("1. Connection string no database.properties");
            System.out.println("2. Usuário e senha do NeonDB");
            System.out.println("3. Conexão com a internet (NeonDB é cloud)");
            System.out.println("4. Driver PostgreSQL nas dependências");
        }
    }
}

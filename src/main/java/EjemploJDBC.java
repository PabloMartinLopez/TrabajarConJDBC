import java.util.Scanner;

public class EjemploJDBC {

    // --- Constantes de Conexión ---
    // Cambia estos valores para que coincidan con tu configuración de MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/mi_bbdd_demo?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root";

    /**
     * Método principal que orquesta todas las demostraciones en orden.
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        do {
            System.out.println("\n--- MENÚ DE DEMOSTRACIONES JDBC ---");
            System.out.println("1. (DDL) Crear/Reiniciar Tablas");
            System.out.println("2. (DML) Insertar con PreparedStatement");
            System.out.println("3. (DQL) Consultar y Mapear a POJOs");
            System.out.println("4. (Batch) Inserción por Lotes");
            System.out.println("5. (Transacción) Demostración de Commit/Rollback");
            System.out.println("6. (Callable) Llamar a Stored Procedure");
            System.out.println("-------------------------------------");
            System.out.println("0. Salir");
            System.out.print("Selecciona una opción: ");

            try {
                // Leemos la línea completa y la convertimos
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                opcion = -1; // Fuerza el default en el switch
            }

            switch (opcion) {
                case 1:
                    System.out.println("\n--- Demo 1: DDL con Statement ---");
                    demoDDL();
                    break;
                case 2:
                    System.out.println("\n--- Demo 2: DML con PreparedStatement ---");
                    demoInsertPreparedStatement();
                    break;
                case 3:
                    System.out.println("\n--- Demo 3: DQL y Mapeo a POJOs ---");
                    demoSelectConMapeo();
                    break;
                case 4:
                    System.out.println("\n--- Demo 4: Ejecución por Lotes (Batch) ---");
                    demoBatchInsert();
                    break;
                case 5:
                    System.out.println("\n--- Demo 5: Gestión de Transacciones ---");
                    demoTransaccion();
                    break;
                case 6:
                    System.out.println("\n--- Demo 6: CallableStatement (Stored Procedure) ---");
                    demoCallableStatement();
                    break;
                case 0:
                    System.out.println("\nSaliendo...");
                    break;
                default:
                    System.out.println("Opción no válida. Inténtalo de nuevo.");
            }

        } while (opcion != 0);

        scanner.close();
        System.out.println("\n--- Fin de las demostraciones ---");
    }
}
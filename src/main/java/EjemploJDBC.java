import model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
//                    demoBatchInsert();
                    break;
                case 5:
                    System.out.println("\n--- Demo 5: Gestión de Transacciones ---");
                    demoTransaccion();
                    break;
                case 6:
                    System.out.println("\n--- Demo 6: CallableStatement (Stored Procedure) ---");
//                    demoCallableStatement();
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

    //* Transacciones
    private static void demoTransaccion() {
        String sqlRestarA   = "UPDATE cuentas SET saldo = saldo - 100 where id='A'";
        String sqlSumaB     = "UPDATE cuentas SET saldo = saldo + 100 where id='B'";
//        ! Consulta pra que falle la transaccion
//        String sqlSumaB     = "UPDATE cuentasasdasdasd SET saldo = saldo + 100 where id='B'";

        Connection conn = null;


        try{
            conn = DriverManager.getConnection(URL, USER, PASS);
            conn.setAutoCommit(false);

            try(Statement stmt = conn.createStatement()){
                stmt.executeUpdate(sqlRestarA);
                stmt.executeUpdate(sqlSumaB);
            }

            conn.commit();

        } catch (SQLException e) {
//            Si la conn falla no ay que deshacer el rollback
            if (conn != null) {
                try{
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

    }

    //* Inserciones masivas
    private static void demoBatchInsert() {
//        String sqlInseert ="INSERT INTO usuarios(nombre, email) VALUES (?,?);";
//
//        try(Connection conn = DriverManager.getConnection(URL, USER, PASS);
//            PreparedStatement pstmt = conn.prepareStatement(sqlInseert)){
//
//        }catch (SQLException e){
//            System.err.println(e.getMessage());
//            e.printStackTrace();
//        }


    }

    //* Select
    private static void demoSelectConMapeo() {
        String sqlSelect ="Select * from usuarios WHERE nombre like ?";

        List<Usuario> usuarios = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {

            pstmt.setString(1, "P%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String email = rs.getString("email");

                Usuario user = new Usuario(id, nombre, email);
                usuarios.add(user);
            }

        }catch (SQLException e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        for (Usuario usuario : usuarios) {
            System.out.println(usuario);
        }


    }

    //* Insert
    private static void demoInsertPreparedStatement() {

        String sqlInseert ="INSERT INTO usuarios(nombre, email) VALUES (?,?);";

        try(Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement pstmt = conn.prepareStatement(sqlInseert)){

            pstmt.setString(1, "Pepito Perez");
            pstmt.setString(2, "pepitoperez@gmail.com");

            int filasAfectadas = pstmt.executeUpdate();

            pstmt.setString(1, "Luisa Perez");
            pstmt.setString(2, "luisaperez@gmail.com");

            filasAfectadas+=pstmt.executeUpdate();

        }catch (SQLException e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }


    }

    //* Levantar BD
    private static void demoDDL() {
        // SQL para DDL
        String sqlDropUsuarios = "DROP TABLE IF EXISTS usuarios";
        String sqlCreateUsuarios = "CREATE TABLE usuarios (" +
                " id INT AUTO_INCREMENT PRIMARY KEY," +
                " nombre VARCHAR(100) NOT NULL," +
                " email VARCHAR(100) NOT NULL UNIQUE" +
                ")";

        String sqlDropCuentas = "DROP TABLE IF EXISTS cuentas";
        String sqlCreateCuentas = "CREATE TABLE cuentas (" +
                " id VARCHAR(10) PRIMARY KEY," +
                " saldo DECIMAL(10, 2) NOT NULL" +
                ")";

        String sqlInsertCuentas = "INSERT INTO cuentas(id, saldo) VALUES ('A', 1000.00), ('B', 500.00)";

        try(Connection conn = DriverManager.getConnection(URL, USER, PASS);
            Statement stmt = conn.createStatement()){

            //!  Drop tablas
            stmt.execute(sqlDropUsuarios);
            stmt.execute(sqlDropCuentas);

            //* Crear tablas
            stmt.execute(sqlCreateUsuarios);
            stmt.execute(sqlCreateCuentas);

            //* Insert
            stmt.execute(sqlInsertCuentas);

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
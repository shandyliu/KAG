package KAG;

import java.sql.*;
//import net.ucanaccess.jdbc.UcanaccessDriver;

public class dbAccess {
    static Connection con = null;
    static Statement st = null;

    public dbAccess()
    {
        init();
        openDB();
        buildStatement();
    }

    public static void init() {
        // Step 1: Loading or registering Oracle JDBC driver class
        try {

            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            System.out.println("DriverLoaded");
        } catch (ClassNotFoundException cnfex) {

            System.out.println("Problem in loading or " + "registering MS Access JDBC driver");
            cnfex.printStackTrace();
        }

        //connect DB
        //openDB();
       // buildStatement();

        //close DB
        //closeDB();

    }

    public static void openDB() {
        try {
            //home/vagrant/work/KAG/src
            String msAccDB = "D:/work/vm_project/KAG.accdb";
            String dbURL = "jdbc:ucanaccess://" + msAccDB;
            // Step 2.A: Create and get connection using DriverManager class
            con = DriverManager.getConnection(dbURL);
            System.out.println("Connection Established Successfully");
        } catch (SQLException e) {
            e.printStackTrace();
             System.out.println("Could Not Connect to Database");

        }
    }

    public static void buildStatement(){
        try {
            st = con.createStatement();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void closeDB()
    {
        // Step 3: Closing database connection
        try {
            if(null != con) {
                // and then finally close connection
                con.close();
                System.out.println("Database Close Successfully");
            }
        }
        catch (SQLException sqlex) {
            sqlex.printStackTrace();
            System.out.println("Could Not Close Database");
        }
    }

}

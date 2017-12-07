package utils;

import play.db.DB;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DBGetter {

    public static String getJsonFromDB(String query) {
        String result = "";

        List<Employee> employeeList = selectFromDB(query);
        if (employeeList.size() == 0) {
            return "{}";
        }
        int count = 1;
        if(employeeList.size() > 1) {
            for (Employee e : employeeList.subList(0, employeeList.size() - 1)) {
                result += "\"" + count + "\":" + e.toJSON() + ",";
                count++;
            }
        }
        result += "\"" + count + "\":" + employeeList.get(employeeList.size()-1).toJSON();
        return "{" + result + "}";
    }

    public static boolean insertIntoDB(String name, String date, String dept) {
        int id = getMaxID();
        if(id <= 0) {
            return false;
        }
        id++;
        DecimalFormat df = new DecimalFormat("0000");
        name = sanitizeCriteria(name);
        date = sanitizeCriteria(date);
        dept = sanitizeCriteria(dept).toUpperCase();
        try {
            Connection connection = DB.getConnection();
            String insertQuery = "INSERT INTO test.employees (id,code,name,join_at,department_code) " + "VALUES(?,?,?,?,?)";
            PreparedStatement stmt = connection.prepareStatement(insertQuery);
            stmt.setInt(1,id);
            stmt.setString(2,df.format(id));
            stmt.setString(3,name);
            stmt.setString(4,date);
            stmt.setString(5,dept);
            stmt.execute();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static List<Employee> selectAll(){
        String query = "SELECT * FROM test.employees;";
        return selectFromDB(query);
    }

    public static Employee selectUnique(String name) {
        String query = "SELECT * FROM test.employees WHERE name Like \"" + sanitizeCriteria(name) + "\";";
        List<Employee> employeeList = selectFromDB(query);
        if (employeeList.size() == 0) {
            return null;
        } else {
            return employeeList.get(0);

        }
    }
    public static Employee selectUnique(int id) {
        String query = "SELECT * FROM test.employees WHERE id = " + id;
        List<Employee> employeeList = selectFromDB(query);
        if (employeeList.size() == 0) {
            return null;
        } else {
            return employeeList.get(0);
        }
    }

    public static boolean update(int id, String name, String date, String dept) {
        name = sanitizeCriteria(name);
        date = sanitizeCriteria(date);
        dept = sanitizeCriteria(dept).toUpperCase();

        try {
            Connection connection = DB.getConnection();
            String query = "UPDATE test.employees SET name = ?, join_at = ?, department_code = ?  WHERE id= ? ;";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, date);
            stmt.setString(3, dept);
            stmt.setInt(4, id);
            stmt.execute();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String sanitizeCriteria(String input) {
        input = input.replaceAll("[\";\\\\]","");
        return input;
    }

    private static List<Employee> selectFromDB(String sanitizedQuery) {
        List<Employee> employeeList = new ArrayList<>();
        try {
            //以下ががdeprecateされてるんですが、新しいオブジェクトDatabaseからディフォルトデータベースのインスタンスをもらう方法がよくわかりません
            Connection connection = DB.getConnection();
            Statement stmt = null;
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sanitizedQuery);
            System.out.println(sanitizedQuery);
//            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                employeeList.add(
                        new Employee (rs.getInt(1),rs.getNString(2),rs.getNString(3),
                                rs.getNString(4),rs.getNString(5)));
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return employeeList;
        }
        return employeeList;
    }

    private static int getMaxID() {
        String selectAll = "SELECT MAX(id) FROM test.employees;";
        int id = -1;
        System.out.println(id);
        try {
            Connection connection = DB.getConnection();
            Statement stmt = null;
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(selectAll);
            while(rs.next()) {
                id = rs.getInt(1);
            }
            connection.close();
        } catch (SQLException e) {
            id = -2;
        }
        return id;
    }


}

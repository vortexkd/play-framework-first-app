package utils;

import play.db.DB;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DBGetter {

    public static String getJsonFromDB(String sanitizedQuery) {
        String result = "";
        List<Employee> employeeList = selectFromDB(sanitizedQuery);
        if (employeeList.size() == 0) {
            return "{}";
        }
        int count = 1;
        if(employeeList.size() > 1) {
            for (Employee e : employeeList.subList(0, employeeList.size() - 1)) {
                result += "\"" + count + "\":" + e.toString() + ",";
                count++;
            }
        }
        result += "\"" + count + "\":" + employeeList.get(employeeList.size()-1).toString();
        return "{" + result + "}";
    }

    public static boolean insertIntoDB(String name, String date, String dept) {
        int id = getMaxID();
        if(id <= 0) {
            return false;
        }
        id++;
        DecimalFormat df = new DecimalFormat("0000");
//        String insertQuery = "INSERT INTO test.employees VALUES(\"" + id +
//                "\",\"" + df.format(id) +
//                "\",\"" + name + "\",\"" + date + "\",\"" + dept + "\")";
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

    private static List<Employee> selectFromDB(String query) {
        List<Employee> employeeList = new ArrayList<>();
        try {
            //以下ががdeprecateされてるんですが、新しいオブジェクトDatabaseからディフォルトデータベースのインスタンスをもらう方法がよくわかりません
            Connection connection = DB.getConnection();
            Statement stmt = null;
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
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

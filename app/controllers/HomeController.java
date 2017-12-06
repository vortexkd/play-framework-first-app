package controllers;

import play.data.DynamicForm;
import play.data.Form;
import play.db.DB;
import play.mvc.*;

import utils.Employee;
import views.html.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        String query = "SELECT * FROM test.employees ORDER BY department_code;";
        String json = getJsonFromDB(query);
        return ok(index.render(json));
    }

    public Result query() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        if(dynamicForm.get("queryCriteria") == null || dynamicForm.get("column") == null) {
            return ok(index.render("No results"));
        }
        String criteria = sanitizeCriteria((dynamicForm.get("queryCriteria")));
        switch (dynamicForm.get("column")) {
            case "1":
                return queryDepartment(criteria);
            case "2":
                return queryName(criteria);
            case "3":
                return queryAfterDate(criteria);
            default:
                return ok(index.render("No results"));
        }
    }

    private Result queryDepartment(String criteria) {
        String query = "SELECT * FROM test.employees WHERE department_code=\"" + criteria + "\";";
        String json = getJsonFromDB(query);
        return ok(index.render(json));
    }

    private Result queryName (String criteria) {
        String query = "SELECT * FROM test.employees WHERE name=\"" + criteria + "\";";
        String json = getJsonFromDB(query);
        return ok(index.render(json));
    }

    private Result queryAfterDate(String criteria) {
        Pattern p = Pattern.compile("\\d\\d\\d\\d/\\d\\d/\\d\\d");
        Matcher m = p.matcher(criteria);
        if(m.find()){
            String query = "SELECT * FROM test.employees WHERE join_at>=\"" + criteria + "\";";
            String json = getJsonFromDB(query);
            return ok(index.render(json));
        }
        return ok(index.render("No results"));
    }

    private String sanitizeCriteria(String input) {
        input = input.replaceAll("[\";\\\\]","");
        return input;
    }

    private String getJsonFromDB(String query) {
        String result = "";
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
        }
        if (employeeList.size() == 0) {
            return "{}";
        }
        int count = 1;
        if(employeeList.size() > 1) {
            for (Employee e : employeeList.subList(0, employeeList.size() - 2)) {
                result += "\"" + count + "\":" + e.toString() + ",";
                count++;
            }
        }
        result += "\"" + count + "\":" + employeeList.get(employeeList.size()-1).toString();
        return "{" + result + "}";
    }


}

package controllers;

import play.db.DB;
import play.mvc.*;

import views.html.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        String query = "SELECT * FROM test.employees;";
        int i = 0;
        try {
            Connection connection = DB.getConnection();
            Statement stmt = null;
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                i++;
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ok(index.render("Hello Goalist"+i));
    }


}

package controllers;

import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;

import utils.DBGetter;
import views.html.*;

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
        return ok(index.render(""));
    }

    public Result selectAll() {
        String query = "SELECT * FROM test.employees";
        String output = DBGetter.getJsonFromDB(query);
        return ok(output);
    }

    public Result query() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        if(dynamicForm.get("queryCriteria") == null || dynamicForm.get("column") == null) {
            return ok(index.render("No results"));
        }
        String criteria = dynamicForm.get("queryCriteria");
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
        String query = "SELECT * FROM test.employees WHERE department_code=\"" + DBGetter.sanitizeCriteria(criteria) + "\";";
        String json = DBGetter.getJsonFromDB(query);
        return ok(index.render(json));
    }

    private Result queryName (String criteria) {
        String query = "SELECT * FROM test.employees WHERE name LIKE \"" + DBGetter.sanitizeCriteria(criteria) + "\";";
        String json = DBGetter.getJsonFromDB(query);
        return ok(index.render(json));
    }

    private Result queryAfterDate(String criteria) {
        Pattern p = Pattern.compile("\\d\\d\\d\\d/\\d\\d/\\d\\d");
        Matcher m = p.matcher(criteria);
        if(m.find()){
            String query = "SELECT * FROM test.employees WHERE join_at>=\"" + criteria + "\";";
            String json = DBGetter.getJsonFromDB(query);
            return ok(index.render(json));
        }
        return ok(index.render("No results"));
    }
}

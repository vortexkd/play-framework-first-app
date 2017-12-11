package controllers;

import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.Database;
import play.mvc.*;

import utils.DBGetter;
import utils.Employee;
import views.html.*;

import javax.inject.Inject;
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
    private static final String STATUS_OK = "OK";
    private static final String STATUS_ERROR = "ERROR";
    private static final String NO_MESSAGE = "";

    @Inject
    FormFactory formFactory;

    @Inject
    Database db;

    public Result index() {
        return ok(index.render(""));
    }

    public Result selectAll() {
        DBGetter dbGetter = new DBGetter(db);
        String query = "SELECT * FROM test.employees";
        return ok(packageReturnData(STATUS_OK, NO_MESSAGE, dbGetter.getJsonFromDB(query)));
    }
    

    public Result query() {
        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
        if(dynamicForm.get("queryCriteria") == null || dynamicForm.get("column") == null) {
            return ok(packageReturnData(STATUS_ERROR, "null検索しようとしています。",""));
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
                return ok(packageReturnData(STATUS_ERROR, "サーチ変数に間違いがあります。",""));
        }
    }

    public Result makeNew(){
        return ok(data.render());
    }

    public Result add() {
        DBGetter dbGetter = new DBGetter(db);
        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
        if(dynamicForm.get("name") == null || dynamicForm.get("department") == null || dynamicForm.get("join_date") == null) {
            return ok(packageReturnData(STATUS_ERROR,"入力失敗。",""));
        }
        String name = dynamicForm.get("name");
        String dept = dynamicForm.get("department");
        String date = dynamicForm.get("join_date");

        if(dbGetter.insertIntoDB(name,date,dept)) {
            return ok(packageReturnData(STATUS_OK,"データベース入力成功！",""));
        } else {
            return ok(packageReturnData(STATUS_ERROR,"入力失敗。",""));
        }
    }

    public Result editChoice() {
        DBGetter dbGetter = new DBGetter(db);
        List<Employee> employeeList = dbGetter.selectAll();
        return ok(edit.render(employeeList));
    }

    public Result edit() { // must return json
        DBGetter dbGetter = new DBGetter(db);
        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
        String criteria = dynamicForm.get("choice");
        Pattern p = Pattern.compile("^[\\d]+$");
        Matcher m = p.matcher(criteria);
        Employee employee;
        if (m.find()) {
            //is id
            employee = dbGetter.selectUnique(Integer.valueOf(criteria));
        } else {
            employee = dbGetter.selectUnique(criteria);
        }
        if (employee != null) {
            return ok(editInfo.render(employee.getName(), employee.getJoin_date(), employee.getDepartment_code(), employee.getCode()));
        } else {
            return ok(packageReturnData(STATUS_ERROR,"社員が登録しておりません",""));
        }
    }

    public Result update() {
        DBGetter dbGetter = new DBGetter(db);
        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
        String name = dynamicForm.get("name");
        String date = dynamicForm.get("date");
        String dept = dynamicForm.get("dept");
        String code = dynamicForm.get("token");//IDがずるされてないのがどうやって確かめるんでしょう
        int id;
        try {
            id = Integer.parseInt(code);
        } catch (NumberFormatException e) {
            return ok(packageReturnData(STATUS_ERROR,"code " + code + "の社員がございません",""));
        }

        if(dbGetter.update(id, name, date, dept)) {
            return ok(packageReturnData(STATUS_OK,"編集しました。",""));
        }
        return ok(packageReturnData(STATUS_ERROR,"編集失敗",""));
    }

    private Result queryDepartment(String criteria) {
        DBGetter dbGetter = new DBGetter(db);
        String query = "SELECT * FROM test.employees WHERE department_code=\"" + dbGetter.sanitizeCriteria(criteria) + "\";";
        String json = dbGetter.getJsonFromDB(query);
        return ok(packageReturnData(STATUS_OK, NO_MESSAGE, json));
    }

    private Result queryName (String criteria) {
        DBGetter dbGetter = new DBGetter(db);
        String query = "SELECT * FROM test.employees WHERE name LIKE \"" + dbGetter.sanitizeCriteria(criteria) + "\";";
        String json = dbGetter.getJsonFromDB(query);
        if(emptyJson(json)) {
            return ok(packageReturnData(STATUS_ERROR, "この社員は存在しません","{}"));
        }
        return ok(packageReturnData(STATUS_OK, NO_MESSAGE, json));
    }

    private Result queryAfterDate(String criteria) {
        DBGetter dbGetter = new DBGetter(db);
        Pattern p = Pattern.compile("\\d\\d\\d\\d/\\d\\d/\\d\\d");
        Matcher m = p.matcher(criteria);
        if(m.find()){
            String query = "SELECT * FROM test.employees WHERE join_at>=\"" + criteria + "\";";
            String json = dbGetter.getJsonFromDB(query);
            if(emptyJson(json)) {
                return ok(packageReturnData(STATUS_ERROR, "この時間から入社してる人はいません。","{}"));
            }
            return ok(packageReturnData(STATUS_OK, NO_MESSAGE, json));

        }
        return ok(packageReturnData(STATUS_ERROR, "yyyy/mm/ddのような日付を入力してください。","{}"));
    }

    private String packageReturnData(String status, String message, String retValue) {
        String output = "{\"status\":\"" + status + "\",\"message\":\"" + message + "\"";
        if (retValue.equals("")) {
            output += "}";
        } else {
            output += ",\"ret\":" + retValue + "}";
        }
        return output;
    }

    private boolean emptyJson(String json) {
        return json.equals("{}");
    }
}

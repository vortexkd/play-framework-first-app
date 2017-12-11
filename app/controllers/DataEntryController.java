//package controllers;
//
//import play.data.DynamicForm;
//import play.data.FormFactory;
//import play.db.Database;
//import play.mvc.Controller;
//import play.mvc.Result;
//
//import utils.DBGetter;
//import utils.Employee;
//import views.html.index;
//import views.html.data;
//import views.html.edit;
//import views.html.editInfo;
//
//import javax.inject.Inject;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class DataEntryController extends Controller{
//
//    private static final String STATUS_OK = "OK";
//    private static final String STATUS_ERROR = "ERROR";
//    private static final String NO_MESSAGE = "";
//
//    @Inject
//    FormFactory formFactory;
//
//    @Inject
//    Database db;
//
//    public Result makeNew(){
//        return ok(data.render());
//    }
//
//    public Result add() {
//        DBGetter dbGetter = new DBGetter(db);
//        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
//        if(dynamicForm.get("name") == null || dynamicForm.get("department") == null || dynamicForm.get("join_date") == null) {
//            return ok(index.render("入力失敗。"));
//        }
//        String name = dynamicForm.get("name");
//        String dept = dynamicForm.get("department");
//        String date = dynamicForm.get("join_date");
//
//        if(dbGetter.insertIntoDB(name,date,dept)) {
//            return ok(index.render("データベース入力成功！"));
//        } else {
//            return ok(index.render(String.valueOf("入力失敗。")));
//        }
//    }
//
//    public Result editChoice() {
//        DBGetter dbGetter = new DBGetter(db);
//        List<Employee> employeeList = dbGetter.selectAll();
//        return ok(edit.render(employeeList)); //
//    }
//
//    public Result edit() {
//        DBGetter dbGetter = new DBGetter(db);
//        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
//        String criteria = dynamicForm.get("choice");
//        Pattern p = Pattern.compile("^[\\d]+$");
//        Matcher m = p.matcher(criteria);
//        Employee employee;
//        if (m.find()) {
//            //is id
//            employee = dbGetter.selectUnique(Integer.valueOf(criteria));
//        } else {
//            employee = dbGetter.selectUnique(criteria);
//        }
//        if (employee != null) {
//            return ok(editInfo.render(employee.getName(), employee.getJoin_date(), employee.getDepartment_code(), employee.getCode()));
//        } else {
//            return ok(index.render("社員が登録しておりません"));
//        }
//    }
//
//    public Result update() {
//        DBGetter dbGetter = new DBGetter(db);
//        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
//        String name = dynamicForm.get("name");
//        String date = dynamicForm.get("date");
//        String dept = dynamicForm.get("dept");
//        String code = dynamicForm.get("token");//IDがずるされてないのがどうやって確かめるんでしょう
//        int id;
//        try {
//            id = Integer.parseInt(code);
//        } catch (NumberFormatException e) {
//            return ok(index.render("編集失敗"));
//        }
//
//        if(dbGetter.update(id, name, date, dept)) {
//            return ok(index.render(name+" "+date+" "+dept+" "+id));
//        }
//        return ok(index.render("編集失敗"));
//    }
//
//}

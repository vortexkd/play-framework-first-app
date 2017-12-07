package controllers;

import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import utils.DBGetter;
import utils.Employee;
import views.html.index;
import views.html.data;
import views.html.edit;
import views.html.editInfo;

import javax.swing.text.html.HTML;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataEntryController extends Controller{

    public Result makeNew(){
        return ok(data.render());
    }

    public Result add() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        if(dynamicForm.get("name") == null || dynamicForm.get("department") == null || dynamicForm.get("join_date") == null) {
            return ok(index.render("Failed"));
        }
        String name = dynamicForm.get("name");
        String dept = dynamicForm.get("department");
        String date = dynamicForm.get("join_date");

        return ok(index.render(String.valueOf(DBGetter.insertIntoDB(name,date,dept))));
    }

    public Result editChoice() {
        List<Employee> employeeList = DBGetter.selectAll();
        return ok(edit.render(employeeList)); //
    }

    public Result edit() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        String criteria = dynamicForm.get("choice");
        String search = "";
        Pattern p = Pattern.compile("^[\\d]+$");
        Matcher m = p.matcher(criteria);
        Employee employee;
        if (m.find()) {
            //is id
            employee = DBGetter.selectUnique(Integer.valueOf(criteria));
        } else {
            employee = DBGetter.selectUnique(criteria);
        }
        if (employee != null) {
            return ok(editInfo.render(employee.getName(), employee.getJoin_date(), employee.getDepartment_code(), employee.getCode()));
        } else {
            return ok(index.render("No such employee"));
        }
    }

    public Result update() {
        //UPDATE `Table A`
//        SET `text`= value
//        WHERE `Table A`.`A-num` = `Table B`.`A-num`
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        String name = dynamicForm.get("name");
        String date = dynamicForm.get("date");
        String dept = dynamicForm.get("dept");
        String code = dynamicForm.get("token");//IDがずるされてないのがどうやって確かめるんでしょう
        int id;
        try {
            id = Integer.parseInt(code);
        } catch (NumberFormatException e) {
            return ok(index.render("Failed"));
        }

        if(DBGetter.update(id, name, date, dept)) {
            return ok(index.render(name+" "+date+" "+dept+" "+id));
        }
        return ok(index.render("Failed"));
    }




    private String sanitizeCriteria(String input) {
        input = input.replaceAll("[\";\\\\]","");
        return input;
    }

}

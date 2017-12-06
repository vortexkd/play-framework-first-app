package controllers;

import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import utils.DBGetter;
import views.html.index;
import views.html.data;

public class DataEntryController extends Controller{

    public Result makeNew(){
        return ok(data.render());
    }

    public Result add() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        if(dynamicForm.get("name") == null || dynamicForm.get("department") == null || dynamicForm.get("join_date") == null) {
            return ok(index.render("Failed"));
        }
        String name = sanitizeCriteria(dynamicForm.get("name"));
        String dept = sanitizeCriteria(dynamicForm.get("department"));
        String date = sanitizeCriteria(dynamicForm.get("join_date"));

        //INSERT INTO test.employees VALUES("27","0027","すごい","2017/05/11","MNG")
        String query = "INSERT INTO test.employees VALUES(\"27\",\"0027\",\"" + name + "\",\"" + date + "\",\"" + dept + "\")";
        //return ok(index.render("hi"));

        return ok(index.render(String.valueOf(DBGetter.insertIntoDB(name,date,dept))));
    }
//    public Result add(){
//
//    }


    private String sanitizeCriteria(String input) {
        input = input.replaceAll("[\";\\\\]","");
        return input;
    }

}

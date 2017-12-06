package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.data;

public class DataEntryController extends Controller{

    public Result makeNew(){
        return ok(data.render("Hi"));
    }

    public Result add() {
        return ok(index.render("Done"));
    }
//    public Result add(){
//
//    }

}

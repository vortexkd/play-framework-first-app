package utils;

public class Employee {
    private int id;
    private String code;
    private String name;
    private String join_date;
    private String department_code;

    public Employee(int id, String code, String name, String join_date, String department_code) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.join_date = join_date;
        this.department_code = department_code;
    }

    @Override
    public String toString(){
        return "{\"id\": \"" + id +
                "\",\n\"code\": \"" + code +
                "\",\n\"name\": \"" + name +
                "\",\n\"join_at\": \"" + join_date +
                "\",\n\"department_code\": \"" + department_code +
                "\"}\n";
    }
}

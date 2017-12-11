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

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getJoin_date() {
        return join_date;
    }

    public String getDepartment_code() {
        return department_code;
    }

    public String toJSON(){
        return "{\"id\": \"" + id +
                "\",\"code\": \"" + code +
                "\",\"name\": \"" + name +
                "\",\"join_at\": \"" + join_date +
                "\",\"department_code\": \"" + department_code +
                "\"}";
    }

    @Override
    public String toString() {
        return this.id + " - " + this.code + " - " + this.name + " - " + this.join_date + " - " + this.department_code;
    }
}

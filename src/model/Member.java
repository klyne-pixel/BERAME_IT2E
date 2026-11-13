
package model;

public class Member {

    private int id;
    private String name;
    private int age;
    private String gender;
    private String phone;
    private String email;
    private String status;

    public Member(int id, String name, int age, String gender,
                  String phone, String email, String status) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }
}

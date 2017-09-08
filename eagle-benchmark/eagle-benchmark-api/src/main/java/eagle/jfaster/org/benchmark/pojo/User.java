package eagle.jfaster.org.benchmark.pojo;

import java.io.Serializable;

/**
 * Created by fangyanpeng on 2017/9/8.
 */
public class User implements Serializable {

    private int id;

    private String name;

    private int age;

    private Sex sex;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }
}

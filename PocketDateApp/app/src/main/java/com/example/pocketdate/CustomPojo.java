package com.example.pocketdate;

import java.util.ArrayList;

/**
 * Created by Austin on 3/20/2018.
 */

public class CustomPojo {

    //POJO class consists of get method and set method
    private String name;
    private String time,content;
    private int type;
    private ArrayList<CustomPojo> customPojo = new ArrayList<>();

    public CustomPojo() {

    }
    //getting content value
    public String getContent(){return content;}
    //setting content value
    public void setContent(String content){this.content=content;}

    public String getTime(){return time;}
    public void setTime(String time){this.time=time;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
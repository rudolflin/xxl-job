package com.xxl.job.executor.service.jobhandler;

public class ContentModel  {

    private int age;
    private String name;

    public ContentModel(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public ContentModel() {
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ContentModel{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}

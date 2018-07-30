package vc.c4.deadletter.domain;

import java.util.Date;

public class ExampleObject {

  private Date date = new Date();

  private String name;

  @Override
  public String toString() {
    return "ExampleObject{" +
        "date= " + date + ", " + "name= " + name +
        '}';
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}

package com.stefan.springjwt.models;

import javax.persistence.*;

@Entity
@Table(name = "shoelasts")
public class Shoelast {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "shoesize")
  private int shoesize;

  public Shoelast() {

  }

  public Shoelast(String title, String description, int shoesize) {
    this.title = title;
    this.description = description;
    this.shoesize = shoesize;
  }

  public long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getShoesize() {
    return shoesize;
  }

  public void setShoesize(int shoesize) {
    this.shoesize = shoesize;
  }


  @Override
  public String toString() {
    return "Shoelast [id=" + id + ", title=" + title + ", desc=" + description + ", shoesize=" + shoesize + "]";
  }

}

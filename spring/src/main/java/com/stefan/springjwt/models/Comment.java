package com.stefan.springjwt.models;

import javax.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(name = "tutid")
  private Long tutid;

  @Column(name = "description")
  private String description;

  public Comment() {

  }

  public Comment(Long tutid, String description) {
    this.tutid = tutid;
    this.description = description;
  }

  public long getId() {
    return id;
  }
  
  public Long getTutid() {
    return tutid;
  }

  public void setTutid(Long tutid) {
    this.tutid = tutid;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
  

  @Override
  public String toString() {
    //return "Comment [id=" + this.tutid + ", desc=" + this.description  + "]";
    return "Comment [id=" + id + ", tutid=" + this.tutid + ", desc=" + description + "]";
  }

}

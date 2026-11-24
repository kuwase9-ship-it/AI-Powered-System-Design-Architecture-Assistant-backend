package com.archpilot.model;

import jakarta.persistence.*;

@Entity
@Table
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String ownerid;
    private String name;
    private String description;
    private String requirements;
    private String createdAt;
    private String updateAt;

public Project(int id, String ownerid, String name, String description, String requirements, String createdAt, String updateAt){
    this.id= id;
    this.ownerid= ownerid;
    this.name= name;
    this.description= description;
    this.requirements= requirements;
    this.createdAt= createdAt;
    this.updateAt= updateAt;
    }

    public Project(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwnerid() {
        return ownerid;
    }
    public void setOwnerid(){
        this.ownerid=ownerid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }
}

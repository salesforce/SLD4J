package com.salesforce.sld.parser.xml.xstream;

import java.util.ArrayList;

public class ClassMock {
    private String name;
    private int id;
    ArrayList<String> names;
    
    public ClassMock(String name, int id){
        this.name = name;
        this.id = id;
        names = new ArrayList<String>();
        names.add(name);
    }
    
    public String getName(){
        return this.name;
    }
    
    public int getID(){
        return id;
    }
    
    public ArrayList<String> getArray(){
        return names;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setID(int id){
        this.id = id;
    }
    
    public void setArray(ArrayList<String> names){
        this.names = names;
    }
}
import java.io.*;
import java.net.*;
import java.util.*;

public class User {
    private String name;

    public User(String name){
        this.name = name;
    }

    public void speak()
    {
        System.out.println("Hello From Manager" + this.name);
    }

    public String toString(){
        return this.name;
    }
}

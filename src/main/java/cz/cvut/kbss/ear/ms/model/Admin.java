package cz.cvut.kbss.ear.ms.model;

import javax.persistence.*;

@Entity
@Table(name = "MS_ADMIN")
@DiscriminatorValue("ADMIN")
public class Admin extends Account {

    public Admin(){
        setRole(Role.ADMIN);
    }

    public Admin(String username, String password){
        super(username, password);
        setRole(Role.ADMIN);
    }

    public String toString() {
        return "Admin{" +
                "\n    id = " + getId() +
                "\n    username = " + getUsername() +
                "}";
    }
}

package cz.cvut.kbss.ear.ms.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "MS_ACCOUNT")
@NamedQueries({
        @NamedQuery(name = "Person.findByUsername", query = "SELECT e FROM Account e WHERE e.username = :username")
})

@DiscriminatorColumn(name = "PERSON_TYPE",
        discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Admin.class, name = "admin"),
        @JsonSubTypes.Type(value = User.class, name = "user1")
})

public abstract class Account extends AbstractEntity {

    @Basic(optional = false)
    @Column(nullable = false, unique = true)
    @Setter @Getter
    private String username;

    @Basic(optional = false)
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Setter @Getter
    private Role role;

    public Account() {
        setRole(Role.PERSON);
    }

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }

    public void erasePassword() {
        this.password = null;
    }

    public String toString() {
        return "Person{" +
                "\n    id = " + getId() +
                "\n    username = " + username +
                "}";
    }
}
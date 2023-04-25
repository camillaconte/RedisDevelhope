package co.develhope.redisCache.entities.jpa;

import co.develhope.redisCache.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
//occhio che Lorenzo se li era dimenticati i constructors
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class UserJPA {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String firstName;
    private String lastName;
    private String profilePicture;
    private String email;
    private String passwordEncrypted;

    private String domicileAddress;
    private String domicileCity;
    private String domicileNumber;
    private String domicileState;

    private String fiscalCode;

    public UserJPA(String firstName, String lastName, String profilePicture, String email, String passwordEncrypted,
                   String domicileAddress, String domicileCity, String domicileNumber,
                   String domicileState, String fiscalCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
        this.email = email;
        this.passwordEncrypted = passwordEncrypted;
        this.domicileAddress = domicileAddress;
        this.domicileCity = domicileCity;
        this.domicileNumber = domicileNumber;
        this.domicileState = domicileState;
        this.fiscalCode = fiscalCode;
    }
}

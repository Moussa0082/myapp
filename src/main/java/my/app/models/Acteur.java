package my.app.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Acteur {

    @Id
    private String idActeur;

    @Column(nullable = true)
    private String nomActeur;

    @Column(nullable = true)
    private String codeActeur;

    @Column(nullable = true)
    private String roleActeur;

    @Column(nullable = true)
    private String emailActeur;

    @Column(nullable = false)
    private Boolean statutActeur;

    @Column(nullable = true)
    private String password;

    @Column(name = "reset_token", nullable = true)
	private String resetToken;


    @Column(nullable = true)
    private String tokenCreationDate;

    @Column(nullable = true)
    private String photoActeur;    

    @Column(nullable = true)
    private String numero;
    
    @Column(nullable = true)
    private String dateAjout;

    @Column(nullable = true)
    private String latitude;

    @Column(nullable = true)
    private String longitude;

    @Column(nullable = true)
    private String adresse;

    @OneToMany
    (mappedBy = "acteur")
    @JsonIgnore
    private List<Stock> stockList;


    @OneToMany
    (mappedBy = "acteur")
    @JsonIgnore
    private List<Commande> commandeList;

    
    
}

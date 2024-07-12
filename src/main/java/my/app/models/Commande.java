package my.app.models;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Entity
@Data
public class Commande {

    @Id
    private String idCommande;

    @Column(nullable = true)
    private String codeCommande;
    
    @Column(nullable = true)
    private String descriptionCommande;

    @Column
    private Boolean statutCommandeLivrer = false;

    @Column(nullable = true)
    private String statutCommande;
    
    private String dateCommande;

    private Boolean isProprietaire;
    
    @Column(nullable = true)
    private int quantiteDemande;

    @Column(nullable = true)
    private int quantiteLivrer;

    @Column(nullable = true)
    private int quantiteNonLivrer;
    
    @ManyToOne
    @JoinColumn(name = "idActeur")
    private Acteur acteur;

    @ManyToOne
    @JoinColumn(name = "acteurProprietaire")
    private Acteur acteurProprietaire;


    @ManyToMany
    @JoinTable(name = "commande_stock",
    joinColumns = @JoinColumn(name = "id_commande"),
    inverseJoinColumns = @JoinColumn(name = "id_stock"))
    @JsonIgnore
    private List<Stock> stock;

   

    @OneToMany
    (mappedBy = "commande")
    @JsonIgnore
    private List<DetailCommande> detailCommandeList;

    public Commande() {}

}
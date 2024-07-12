package my.app.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Stock {

    @Id
    private String idStock;

    @Column(nullable = true)
    private String nomProduit;

    @Column(nullable = true)
    private String codeProduit;

    @Column(nullable = true)
    private String dateAjout;

    @Lob
    @Column(nullable = true, columnDefinition = "TEXT")
    private String description;


    @Column(nullable = true)
    private String photo;

    
    @Column(nullable = false)
    private int quantiteStock;

    @Column(nullable = false)
    private Boolean statutStock;

    @Column(nullable = false)
    private int prix;

    @ManyToOne
    @JoinColumn(name = "idCategorieProduit") 
    private CategorieProduit categorieProduit;
    
    @ManyToOne
    @JoinColumn(name = "idActeur") 
    private Acteur acteur;
    
    
}

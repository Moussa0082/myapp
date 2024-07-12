package my.app.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;


@Entity
@Data
public class DetailCommande {

    @Id
    private String idDetailCommande;

    @Column(nullable = true)
    private String codeProduit;
    
    @Column(nullable = false)
    private int quantiteDemande;
    
    @Column(nullable = true)
    private int quantiteLivree;

    @Column(nullable = true)
     private String description;

    @Column(nullable = true)
    private int quantiteNonLivree; // Utiliser la classe d'enveloppe Double au lieu du type primitif double

    private String dateAjout;


    @ManyToOne
    @JoinColumn(name = "idCommande")
    private Commande commande;

    @ManyToOne
    @JoinColumn(name = "idStock")
    private Stock stock;
    
 
    
}

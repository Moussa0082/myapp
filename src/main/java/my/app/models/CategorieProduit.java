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
public class CategorieProduit {

    @Id
    private String idCategorieProduit;

    @Column(nullable = false)
    private String libelleCategorie;

    @Column(nullable = false)
    private String photoCategorie;

    @Column(nullable = false)
    private String descriptionCategorie;

    @Column(nullable = false)
    private boolean statutCategorie = true;

    @OneToMany
    (mappedBy = "categorieProduit")
    @JsonIgnore
    private List<Stock> stockList;
    
}

package my.app.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class TypeActeur {

    @Id
    private String idTypeActeur;

    @Column(nullable = true)
    private String libelle;

    @Column(nullable = true)
    private String description;
    
}

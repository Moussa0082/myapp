package my.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import my.app.models.CategorieProduit;


public interface CategorieProduitRepository extends JpaRepository<CategorieProduit, String>{

    CategorieProduit findByLibelleCategorie(String libelleCategorie);
    CategorieProduit findByIdCategorieProduit(String idCategorieProduit);
    
}

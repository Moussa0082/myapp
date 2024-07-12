package my.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import my.app.models.Commande;


public interface CommandeRepository extends JpaRepository<Commande, String>{

    List<Commande> findByActeurProprietaireIdActeur(String acteurProprietaire);

    Commande findByIdCommande(String idCommande);
    
}

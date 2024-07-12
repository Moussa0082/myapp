package my.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import my.app.models.DetailCommande;


public interface DetailCommandeRepository extends JpaRepository<DetailCommande, String>{

    List<DetailCommande> findByCommandeIdCommande(String idCommande);
    List<DetailCommande> findAllByCommandeIdCommande(String idCommande);

    
}

package my.app.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import my.app.models.Acteur;
import my.app.models.Rating;
import my.app.models.Stock;

public interface RatingRepository extends JpaRepository<Rating, String>{
    Optional<Rating> findByStockAndActeur(Stock stock, Acteur acteur);

        Page<Rating> findByStock(Stock stock, Pageable pageable);

        
        String countByStockIdStock(String idStock);
    
}

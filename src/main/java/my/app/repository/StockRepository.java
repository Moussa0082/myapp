package my.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import my.app.models.Stock;


public interface StockRepository extends JpaRepository<Stock, String>{

    Stock findByIdStock(String idStock);



    Page<Stock> findAllByStatutStock(boolean statutStock, Pageable pageable);
    List<Stock> findAllByIdStockIn(List<String> idStock);

    Page<Stock> findByCategorieProduit_IdCategorieProduitAndStatutStock(String idCategorieProduit, boolean statutStock,
            Pageable pageable);
    
}

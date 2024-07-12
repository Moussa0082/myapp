package my.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import my.app.models.Stock;
import my.app.services.StockService;

@RestController
public class StockController {
    

    @Autowired
    StockService stockService;


        //Create banque
    @PostMapping("/create")
    @Operation(summary = "Création d'un produit")
    public ResponseEntity<Stock> createStock(
        @Valid @RequestParam("stock") String stockString,
        @RequestParam(value = "image", required = false) MultipartFile imageFile)
        throws Exception {

    Stock stock = new Stock();
    try {
        stock = new JsonMapper().readValue(stockString, Stock.class);
    } catch (JsonProcessingException e) {
        throw new Exception(e.getMessage());
    }

    Stock savedStock = stockService.createStock(stock, imageFile);

    return new ResponseEntity<>(savedStock, HttpStatus.CREATED);
}


     
    //Mettre à jour un user
      @PutMapping("/update/{id}")
    @Operation(summary = "Mise à jour  d'un  produit par son Id ")
    public ResponseEntity<Stock> updateStock(
            @PathVariable String id,
            @Valid @RequestParam("stock") String stockString,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        
        Stock stock = new Stock();
        
        try {
            stock = new JsonMapper().readValue(stockString, Stock.class);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Stock stockMisAjour = stockService.updateStock(id, stock, imageFile);
            return new ResponseEntity<>(stockMisAjour, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @GetMapping("/getAllStocksByCategorieWithPagination")
    public ResponseEntity<Page<Stock>> getStocksByCategorieWithPagination(
            @RequestParam String idCategorie,
            @RequestParam int page,
            @RequestParam int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Stock> stocks = stockService.getStockByCategorieWithPagination(idCategorie, pageable);

        return ResponseEntity.ok().body(stocks);
    }


    @GetMapping("/getAllStockWithPagination")
    public ResponseEntity<Page<Stock>> getStocksWithPagination(
            @RequestParam int page,
            @RequestParam int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Stock> stocks = stockService.getAllStocksPageable(pageable);

        return ResponseEntity.ok().body(stocks);
    }


    @PutMapping("/activer/{id}")
    public ResponseEntity<Stock> activeStock(@PathVariable String id) throws Exception {
        return new ResponseEntity<>(stockService.active(id), HttpStatus.OK);
    }

        @PutMapping("/desactiver/{id}")
    public ResponseEntity<Stock> desactiveStock(@PathVariable String id) throws Exception {
        return new ResponseEntity<>(stockService.desactive(id), HttpStatus.OK);
    }


    @PutMapping("/updateQuantiteStock/{idStock}")
    @Operation(summary = "Modification de la quantite d'un produit")
    public ResponseEntity<Stock> updatedQuantiteStock(@RequestBody Stock stock ,@PathVariable String idStock) throws Exception {
        return new ResponseEntity<>(stockService.updateQteStock(stock, idStock), HttpStatus.OK);
    }


    @DeleteMapping("/deleteStocks/{id}")
        @Operation(summary = "Suppression des stocks")
        public String supprimer(@PathVariable String id){
            return stockService.deleteStock(id);
        }

}

package my.app.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import my.app.exception.NotFoundException;
import my.app.functions.CodeGenerator;
import my.app.functions.IdGenerator;
import my.app.models.CategorieProduit;
import my.app.models.Stock;
import my.app.repository.CategorieProduitRepository;
import my.app.repository.StockRepository;

@Service
public class StockService {


    @Autowired
    StockRepository stockRepository;
    
    @Autowired
    IdGenerator idGenerator;
    
    @Autowired
    CodeGenerator codeGenerator;

    @Autowired
    CategorieProduitRepository categorieProduitRepository;


    public Stock createStock(Stock stock, MultipartFile imageFile) throws Exception {
        CategorieProduit cat = categorieProduitRepository.findByIdCategorieProduit(stock.getCategorieProduit().getIdCategorieProduit());
       
        
        if(cat == null)
            throw new IllegalStateException("Veuiller choisir une categorie");
        
           
            String codes = codeGenerator.genererCode();
            String idCode = idGenerator.genererCode();

            stock.setIdStock(idCode);
            stock.setCodeProduit(codes);

            
            String pattern = "yyyy-MM-dd HH:mm";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = now.format(formatter);

        stock.setDateAjout(formattedDateTime);
        stock.setStatutStock(true);

           // Traitement image
        if (imageFile != null) {
            String imageLocation = "C:\\xampp\\htdocs\\ecom";
            try {
                Path imageRootLocation = Paths.get(imageLocation);
                if (!Files.exists(imageRootLocation)) {
                    Files.createDirectories(imageRootLocation);
                }
    
                String imageName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path imagePath = imageRootLocation.resolve(imageName);
                Files.copy(imageFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                stock.setPhoto("ecom/" + imageName);
            } catch (IOException e) {
                throw new Exception("Error processing the image file: " + e.getMessage());
            }
        }

        Stock st = stockRepository.save(stock);

    try {
        //  sendMessageToAllActeur(st);
    } catch (Exception e) {
        System.out.println(e.getMessage());
    }
    
        return st;
    }


    public Stock updateStock(String id ,Stock stock, MultipartFile imageFile) throws Exception {
        Stock st = new Stock();

        CategorieProduit cat = categorieProduitRepository.findByIdCategorieProduit(stock.getCategorieProduit().getIdCategorieProduit());
       Stock stockExistant = stockRepository.findByIdStock(stock.getIdStock());
        
        if(cat == null)
            throw new IllegalStateException("Veuiller choisir une categorie");
        
           
            if(stockExistant != null){

                stockExistant.setNomProduit(stock.getNomProduit());
                stockExistant.setDescription(stock.getDescription());
                stockExistant.setQuantiteStock(stock.getQuantiteStock());
                stockExistant.setPrix(stock.getPrix());
                if(stock.getCategorieProduit() != null){
                    stockExistant.setCategorieProduit(stock.getCategorieProduit());
                }
                // Traitement image
             if (imageFile != null) {
                 String imageLocation = "C:\\xampp\\htdocs\\ecom";
                 try {
                     Path imageRootLocation = Paths.get(imageLocation);
                     if (!Files.exists(imageRootLocation)) {
                         Files.createDirectories(imageRootLocation);
                     }
         
                     String imageName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                     Path imagePath = imageRootLocation.resolve(imageName);
                     Files.copy(imageFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                     st.setPhoto("ecom/" + imageName);
                 } catch (IOException e) {
                     throw new Exception("Error processing the image file: " + e.getMessage());
                 }
             }
     
         st = stockRepository.save(stockExistant);
     
         try {
             //  sendMessageToAllActeur(st);
         } catch (Exception e) {
             System.out.println(e.getMessage());
         }
            }else{
                throw new NotFoundException("Stock non trouver");
            }
    
        return st;
    }

    public Page<Stock> getAllStocksPageable(Pageable pageable) {
        return stockRepository.findAllByStatutStock(true,pageable);
    }


    public Stock updateQteStock(Stock stock,String id) throws Exception {
        Stock stocks = stockRepository.findById(id).orElseThrow(null);

        if(stocks != null){
       int  qt = stock.getQuantiteStock();
            stocks.setQuantiteStock(stocks.getQuantiteStock() + qt);
        }
        
            return stockRepository.save(stocks);
    }


      // recuperer les stock par  categorie avec pagination
      public Page<Stock> getStockByCategorieWithPagination(String idCategorieProduit,Pageable pageable) {
        return stockRepository.findByCategorieProduit_IdCategorieProduitAndStatutStock(idCategorieProduit, true, pageable);
    }

    
    public String deleteStock(String id){
        Stock stock = stockRepository.findById(id).orElseThrow(null);

        stockRepository.delete(stock);

        return "Supprimé avec success";
    }

    public Stock active(String id) throws Exception{
        Stock stock = stockRepository.findById(id).orElseThrow(null);

        try {
            stock.setStatutStock(true);
        } catch (Exception e) {
            throw new Exception("Erreur lors de l'activation : " + e.getMessage());
        }
        return stockRepository.save(stock);
    }

    public Stock desactive(String id) throws Exception{
        Stock stock = stockRepository.findById(id).orElseThrow(null);

        try {
            stock.setStatutStock(false);
        } catch (Exception e) {
            throw new Exception("Erreur lors de l'activation : " + e.getMessage());
        }
        return stockRepository.save(stock);
    }

    
}

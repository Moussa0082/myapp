package my.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import my.app.models.CategorieProduit;
import my.app.services.CategorieProduitService;

@RestController
@RequestMapping("/categorie")
public class CategorieProduitController {

    @Autowired
    private CategorieProduitService categorieProduitService;

     //Create banque
    @PostMapping("/create")
    @Operation(summary = "Création d'une categorie")
    public ResponseEntity<CategorieProduit> createCategorie(
        @Valid @RequestParam("categorie") String categorieString,
        @RequestParam(value = "image", required = false) MultipartFile imageFile)
        throws Exception {

    CategorieProduit categorieProduit = new CategorieProduit();
    try {
        categorieProduit = new JsonMapper().readValue(categorieString, CategorieProduit.class);
    } catch (JsonProcessingException e) {
        throw new Exception(e.getMessage());
    }

    CategorieProduit savedCategorieProduit = categorieProduitService.createCategorieProduit(categorieProduit, imageFile);

    return new ResponseEntity<>(savedCategorieProduit, HttpStatus.CREATED);
}


     
    //Mettre à jour d'une categorie
      @PutMapping("/update/{id}")
    @Operation(summary = "Mise à jour du type d'une categorie par son Id ")
    public ResponseEntity<CategorieProduit> updateCategorie(
            @PathVariable String id,
            @Valid @RequestParam("categorie") String categorieString,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        
        CategorieProduit categorieProduit = new CategorieProduit();
        
        try {
            categorieProduit = new JsonMapper().readValue(categorieString, CategorieProduit.class);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            CategorieProduit categorieMisAjour = categorieProduitService.modifierCategorieProduit(id, categorieProduit, imageFile);
            return new ResponseEntity<>(categorieMisAjour, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


         @GetMapping("/getAllCategorie")
    public ResponseEntity<List<CategorieProduit>> getAllSpeculation() {
        List<CategorieProduit> categories = categorieProduitService.getAllCategorie();

        if (categories.isEmpty()) {
            throw new EntityNotFoundException("Categorie non trouvé");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        return ResponseEntity.ok()
                .headers(headers)
                .body(categories);
    }
    
     

    @PutMapping("/activer/{id}")
    @Operation(summary="Activation de categorie de produit à travers son id")
    public ResponseEntity<CategorieProduit> activeCategories(@PathVariable String id) throws Exception {
        return new ResponseEntity<>(categorieProduitService.active(id), HttpStatus.OK);
    }


    @PutMapping("/desactiver/{id}")
    @Operation(summary="Desactivation de categorie de produit à travers son id")
    public ResponseEntity<CategorieProduit> desactiveCategories(@PathVariable String id) throws Exception {
        return new ResponseEntity<>(categorieProduitService.desactive(id), HttpStatus.OK);
    }


     @DeleteMapping("/delete/{id}")
    @Operation(summary="Supprimé de catégories de produit en fonction de l'id ")
    public String deleteCategorie(@PathVariable String id) {
        return categorieProduitService.deleteCategorie(id);
    }


    
}

package my.app.controllers;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import my.app.models.CommandeAvecStocks;
import my.app.models.Stock;
import my.app.services.CommandeService;

@RestController
public class CommandeController {

    @Autowired
    CommandeService commandeService;


    @PostMapping("/add")
    public ResponseEntity<?> ajouterStocksACommande(@RequestBody CommandeAvecStocks commandeAvecStocks ) {
        try {
            // Envelopper les listes dans des objets Optional
    List<Stock> stocks = commandeAvecStocks.getStocks();
    Optional<List<Integer>> optionalQuantitesDemandees = Optional.ofNullable(commandeAvecStocks.getQuantitesDemandees());

    // Appeler la méthode ajouterStocksACommande en passant les objets Optional
    commandeService.ajouterStocksACommande(
        commandeAvecStocks.getActeur(),
        stocks,
        optionalQuantitesDemandees
    );
          
        return ResponseEntity.status(HttpStatus.OK).body("Commande passer avec succes");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création des commandes : " + e.getMessage());
        }
    }
   



     @PutMapping("/confirmerLivraison/{idDetailCommande}/{quantiteLivree}")
     public ResponseEntity<Map<String, String>> confirmerLivrasonProduit(@PathVariable String idDetailCommande, @PathVariable int quantiteLivree) {
    Map<String, String> response = new HashMap<>();
    try {
        commandeService.confirmerCommandeParProduit(idDetailCommande, quantiteLivree);
        response.put("message", "Commande confirmée avec succès pour le produit : " + idDetailCommande);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        response.put("error", "Erreur lors de la confirmation de la commande pour le produit : " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

     @PutMapping("/confirmerLivraisonForAllProducts/{idCommande}")
     public ResponseEntity<Map<String, String>> confirmerLivrasonProduitForAllProduct(@PathVariable String idCommande) {
    Map<String, String> response = new HashMap<>();
    try {
        commandeService.confirmerCommandeForAllProducts(idCommande);
        response.put("message", "Commande confirmée avec succès pour tout les produit : " );
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        response.put("error", "Erreur lors de la confirmation de la commande pour tout les produit : " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}


     @PutMapping("annulerLivraison/{idDetailCommande}")
  public ResponseEntity<Map<String, String>> annulerLivrasonProduit(@PathVariable String idDetailCommande, @RequestBody(required = false) Map<String, String> description) {
    Map<String, String> response = new HashMap<>();
    try {
        String desc = description != null ? description.get("description") : null;
        commandeService.annulerCommandeParProduit(idDetailCommande, desc);
        response.put("message", "Commande annulée avec succès pour le produit : " + idDetailCommande);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        response.put("error", "Erreur lors de l'annulation de la commande pour le produit : " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}


        //Annuler commande
        @PutMapping("/{id}/disable")
        public ResponseEntity<String> disableCommande(@PathVariable("id") String id) {
            try {
                return commandeService.disableCommande(id);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de l'annulation de la commande : " + e.getMessage());
            }
        }

        @DeleteMapping("/delete/{id}")
    @Operation(summary="Supprimé de commande de produit en fonction de l'id ")
    public String deleteFilieres(@PathVariable String id) {
        return commandeService.deleteCommande(id);
    }
    
}

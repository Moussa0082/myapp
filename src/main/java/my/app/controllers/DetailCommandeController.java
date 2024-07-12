package my.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import my.app.models.DetailCommande;
import my.app.services.DetailCommandeService;

@RestController
public class DetailCommandeController {

    
    @Autowired
    DetailCommandeService detailCommandeService;


    @GetMapping("/getAllDetailByCommande/{idCommande}")
    @Operation(summary = "Liste des details commandes by commande")
    public ResponseEntity<List<DetailCommande>> listeDetailCommandeByCommande(@PathVariable String idCommande){
        return new ResponseEntity<>(detailCommandeService.getAllDetailCommandeByIdCommande(idCommande), HttpStatus.OK);
    }
    
}

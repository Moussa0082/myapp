package my.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import my.app.config.ResponseHandler;
import my.app.models.Acteur;
import my.app.services.ActeurService;

@RestController
@RequestMapping("/acteur")
public class ActeurController {

  @Autowired
  private ActeurService acteurService;


      //Create banque
    @PostMapping("/create")
    @Operation(summary = "Création d'un acteur")
    public ResponseEntity<Acteur> createActeur(
        @Valid @RequestParam("acteur") String acteurString,
        @RequestParam(value = "image", required = false) MultipartFile imageFile)
        throws Exception {

    Acteur acteur = new Acteur();
    try {
        acteur = new JsonMapper().readValue(acteurString, Acteur.class);
    } catch (JsonProcessingException e) {
        throw new Exception(e.getMessage());
    }

    Acteur savedActeur = acteurService.createActeur(acteur, imageFile);

    return new ResponseEntity<>(savedActeur, HttpStatus.OK);
}


  //Mettre à jour un user
      @PutMapping("/update/{id}")
    @Operation(summary = "Mise à jour du type d'un acteur  par son Id ")
    public ResponseEntity<Acteur> updateActeur(
            @PathVariable String id,
            @Valid @RequestParam("acteur") String acteurString,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        
        Acteur acteur = new Acteur();
        
        try {
            acteur = new JsonMapper().readValue(acteurString, Acteur.class);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Acteur acteurMisAjour = acteurService.modifierActeur(id, acteur, imageFile);
            return new ResponseEntity<>(acteurMisAjour, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/verifierOtpCodeEmail")
    public ResponseEntity<String> verifyOtpCodeEmail(@RequestParam("emailActeur") String emailActeur, @RequestParam(required = false) String resetToken) {
        try {
            boolean isVerified = acteurService.verifyOtpCodeEmail(emailActeur,resetToken);
            if (isVerified) {
                return ResponseEntity.ok("Code vérifié avec succès");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Code incorrect ou expiré");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue : " + e.getMessage());
        }
    }

    @PutMapping("/resetPasswordEmail")
    @Operation(summary = "Réinitialise le mot de passe de l'utilisateur via email et nouveau mot de passe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mot de passe réinitialisé avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<String> resetPasswordEmail(@RequestParam("emailActeur") String emailActeur, @RequestParam("password") String password) {
        try {
            acteurService.resetPasswordEmail(emailActeur, password);
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue : " + e.getMessage());
        }
    }


     @GetMapping("/sendOtpCodeEmail")
    @Operation(summary = "Verifier l'email de l'utilisateur en lui envoyant un code de verification à son adresse email pour la procedure de changement de son mot de pass")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "L'email exist et le code a été envoyer avec succès", content = {
                    @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
            }),
            @ApiResponse(responseCode = "500",description = "Erreur serveur", content = @Content),
    })
    public ResponseEntity<Object> sendOtpCodeEmail(@RequestParam("emailActeur") String emailActeur) throws Exception {
        return ResponseHandler.generateResponse(acteurService.sendOtpCodeEmail(emailActeur), HttpStatus.OK,null);
    }


         // Get Liste des  aceturs
         @GetMapping("/read")
         @Operation(summary = "Liste globale des acteurs")
       public ResponseEntity<List<Acteur>> getAllActeur() {
           return new ResponseEntity<>(acteurService.getAllActeur(), HttpStatus.OK);
       }


               //Supprimer un acteur
           @DeleteMapping("/delete/{id}")
    @Operation(summary = "Suppression d'un acteur")
    public ResponseEntity<String> deleteActeur(@PathVariable String id){
        return new ResponseEntity<>(acteurService.deleteByIdActeur(id), HttpStatus.OK);
    }

    //Se connecter 
    @GetMapping("/login")
    @Operation(summary = "Connexion d'un Acteur ")
    public Acteur connexion(@RequestParam("emailActeur")  String emailActeur,
                            @RequestParam("password")  String password) {
        return acteurService.connexionActeur(emailActeur, password);
    }

    //Se connecter 
    @GetMapping("/pinLogin")
    @Operation(summary = "Connexion d'un Acteur")
    public Acteur connexionActeurWithPin(
        @RequestParam("codeActeur")  String codeActeur,
        @RequestParam("password")  String password
    ) {
    return acteurService.connexionActeurWithPin(codeActeur,password);
    }

    @GetMapping("/codeAndNomActeurLogin")
    @Operation(summary = "Connexion d'un Acteur via code et nomActeur")
    public Acteur connexionActeurWithCodeAndNomAceur(
        @RequestParam("codeActeur")  String codeActeur,
        @RequestParam("nomActeur")  String nomActeur
    ) {
    return acteurService.connexionActeurWithCodeAndNomActeur(codeActeur,nomActeur);
    }



    
}

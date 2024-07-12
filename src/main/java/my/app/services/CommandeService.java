package my.app.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import my.app.functions.CodeGenerator;
import my.app.functions.IdGenerator;
import my.app.models.Acteur;
import my.app.models.Alerte;
import my.app.models.CategorieProduit;
import my.app.models.Commande;
import my.app.models.DetailCommande;
import my.app.models.Stock;
import my.app.repository.AlerteRepository;
import my.app.repository.CategorieProduitRepository;
import my.app.repository.CommandeRepository;
import my.app.repository.DetailCommandeRepository;
import my.app.repository.StockRepository;

@Service
public class CommandeService {

    @Autowired
    CommandeRepository commandeRepository;

    @Autowired
    DetailCommandeRepository detailCommandeRepository;

    @Autowired
    private AlerteRepository alerteRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private CodeGenerator codeGenerator;
    
    
     public Commande ajouterStocksACommande(Acteur acteur, List<Stock> stocks, Optional<List<Integer>>  quantitesDemandees) throws Exception {
    
        Commande commande = new Commande();


   // Récupération des stocks correspondant aux identifiants fournis
   List<Stock> stocksFound = stockRepository.findAllByIdStockIn(
    stocks.stream()
          .map(Stock::getIdStock)
          .collect(Collectors.toList()));


   
    

    // Date et heure actuelles formatées
    String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        for (int i = 0; i < stocksFound.size(); i++) {
            Stock stock = stocksFound.get(i);
            int quantiteDemandee = quantitesDemandees.orElse(Collections.emptyList()).get(i);
        // Vérifier si la quantité est égale à 1 ou si la quantité demandée est supérieure à celle restante
        if (quantiteDemandee >= stock.getQuantiteStock()) {
            throw new Exception("La quantité rentant pour le produit " + stock.getNomProduit() + "est insuffisant");
        }
    }
    
    // Mise à jour des informations de la commande
    commande.setIdCommande(idGenerator.genererCode());
    commande.setCodeCommande(codeGenerator.genererCode());
    commande.setDateCommande(formattedDateTime);
    commande.setStatutCommande("en attente");
    commande.setActeur(acteur);
    Acteur acteurProprietaire = new Acteur();
    if (!stocksFound.isEmpty()) {
        acteurProprietaire = stocksFound.get(0).getActeur();
    } 
    
    // Utiliser l'acteur propriétaire trouvé pour définir l'acteur de la commande
    if (acteurProprietaire != null) {
    commande.setActeurProprietaire(acteurProprietaire);
    } else {
        // Si aucun acteur propriétaire n'est trouvé, utilisez l'acteur passé en paramètre
        System.out.println("aucun acteur trouver");
    }
   Commande savedCommande = commandeRepository.save(commande);
   // Enregistrement des détails de la commande pour chaque produit
   // Récupérer l'acteur propriétaire à partir des stocks ou des intrants
    for (int i = 0; i < stocksFound.size(); i++) {
        Stock stock = stocksFound.get(i);
        // double quantiteDemandee = quantitesDemandees.get(i);
        int quantiteDemandee = quantitesDemandees.orElse(Collections.emptyList()).get(i);
  

   
        // Création d'une nouvelle instance de DetailCommande
        DetailCommande detailCommande = new DetailCommande();
        detailCommande.setIdDetailCommande(idGenerator.genererCode());
        detailCommande.setCodeProduit(stock.getCodeProduit());
        detailCommande.setQuantiteDemande(quantiteDemandee);
        detailCommande.setQuantiteLivree(0); // Initialement aucun n'a été livré
        detailCommande.setQuantiteNonLivree(quantiteDemandee); // Initialement aucun n'a été livré
        detailCommande.setDateAjout(formattedDateTime);
        detailCommande.setCommande(savedCommande);
        detailCommande.setStock(stock);

        // Enregistrement du détail de la commande
         detailCommandeRepository.save(detailCommande);

        // Mise à jour de la quantité en stock
        int quantiteRestante = stock.getQuantiteStock() - quantiteDemandee;
        stock.setQuantiteStock(quantiteRestante);
        stockRepository.save(stock);

        // Mise à jour de la quantité demandée totale dans la commande
        savedCommande.setQuantiteDemande(savedCommande.getQuantiteDemande() + quantiteDemandee);
    }
    // Enregistrement des détails de la commande pour chaque intrant
   
    // Envoi de notifications aux propriétaires des stocks
       Acteur proprietaire = stocksFound.get(0).getActeur();
        String message = "Une commande a été  passé par " + savedCommande.getActeur().getNomActeur() + " code commande " + savedCommande.getCodeCommande().toUpperCase() + "rendez vous sur l'application pour voir les details et confirmer la commande";
       
        // Envoi d'un e-mail uniquement si le propriétaire a une adresse e-mail
        if (proprietaire != null && proprietaire.getEmailActeur() != null) {
            Alerte al = new Alerte(proprietaire.getEmailActeur(), message, "Nouvelle commande de produits");
            al.setId(idGenerator.genererCode());
            al.setDateAjout(formattedDateTime);
            al.setActeur(proprietaire);
            alerteRepository.save(al);
            emailService.sendSimpleMail(al);
        } else {
            System.out.println("Adresse e-mail introuvable pour le propriétaire du stock : " + proprietaire);
        }
    
    return savedCommande;
   }

   public void confirmerCommandeParProduit(String idDetailCommande, int quantiteLivree) throws Exception {
    // Rechercher le détail de la commande par l'ID
    Optional<DetailCommande> optionalDetailCommande = detailCommandeRepository.findById(idDetailCommande);
    String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

    if (optionalDetailCommande.isPresent()) {
        DetailCommande detailCommande = optionalDetailCommande.get();

        // Mettre à jour la quantité livrée
        int quantiteNonLivree = detailCommande.getQuantiteDemande() - quantiteLivree;
        if (quantiteNonLivree < 0) {
            throw new Exception("La quantité livrée ne peut dépasser la quantité demandée");
        }

        detailCommande.setQuantiteLivree(quantiteLivree);
        detailCommande.setQuantiteNonLivree(quantiteNonLivree);

        String msg = "La livraison de votre commande de " + optionalDetailCommande.get().getStock().getNomProduit().toUpperCase() + " passé le " + optionalDetailCommande.get().getCommande().getDateCommande()  + " a été confirmer avec succès par le proprietaire en cas de retard de livraison vous pouvez le contacter à son numéro " + optionalDetailCommande.get().getCommande().getActeurProprietaire().getNumero() ;
        // Enregistrer les modifications dans la base de données
        detailCommandeRepository.save(detailCommande);
        Alerte al = new Alerte(detailCommande.getCommande().getActeur().getEmailActeur(), msg, "Commande de produits confirmer");
        al.setId(idGenerator.genererCode());
        al.setDateAjout(formattedDateTime);
        emailService.sendSimpleMail(al);
        alerteRepository.save(al);
        // Récupérer tous les détails de commande liés à la même commande
        List<DetailCommande> allDetailsForCommande = detailCommandeRepository.findByCommandeIdCommande(detailCommande.getCommande().getIdCommande());

        // Vérifier si la somme des quantités livrées pour tous les détails de commande est égale à la quantité demandée
        int quantiteTotaleLivree = allDetailsForCommande.stream().mapToInt(DetailCommande::getQuantiteLivree).sum();
        int quantiteDemandee = allDetailsForCommande.stream().mapToInt(DetailCommande::getQuantiteDemande).sum();

        if (quantiteTotaleLivree == quantiteDemandee) {
            // Mettre à jour le statut de la commande à "confirmé"
            Commande commande = detailCommande.getCommande();
            commande.setStatutCommandeLivrer(true);
            commandeRepository.save(commande);
           
        }
    } else {
        // Lever une exception si le détail de la commande n'est pas trouvé
        throw new Exception("Détail de la commande non trouvé");
    }
}

   public void confirmerCommandeForAllProducts(String idCommande) throws Exception {
    // Rechercher le détail de la commande par l'ID

    List<DetailCommande> detailCommandes = detailCommandeRepository.findAllByCommandeIdCommande(idCommande);
    String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

    String msg = "La livraison de votre commande de " + detailCommandes.get(0).getStock().getNomProduit().toUpperCase() + " passé le " + detailCommandes.get(0).getCommande().getDateCommande()  + " a été confirmer avec succès par le proprietaire en cas de retard de livraison vous pouvez le contacter à son numéro " + detailCommandes.get(0).getCommande().getActeurProprietaire().getNumero() ;
   
    for (DetailCommande detailCommande : detailCommandes) {

        detailCommande.setQuantiteLivree(detailCommande.getQuantiteNonLivree());

        // Enregistrer les modifications dans la base de données
        detailCommandeRepository.save(detailCommande);
    } 
    Alerte al = new Alerte(detailCommandes.get(0).getCommande().getActeur().getEmailActeur(), msg, "Commande de produits confirmer");
    al.setId(idGenerator.genererCode());
    al.setDateAjout(formattedDateTime);
    emailService.sendSimpleMail(al);
    alerteRepository.save(al);
        // Mettre à jour le statut de la commande à "confirmé"
        Commande commande = detailCommandes.get(0).getCommande();
        commande.setStatutCommandeLivrer(true);
        commandeRepository.save(commande);
       
}


    //Annuler commande en tant qu'acheteur
   public ResponseEntity<String> disableCommande(String id) throws Exception {
    Commande commande = commandeRepository.findByIdCommande(id);

    if (commande != null) {
        // Mettre à jour le statut de la commande
        
        commande.setStatutCommande("annulée");
        commandeRepository.save(commande);

        // Récupérer les détails de commande de la commande
        List<DetailCommande> detailsCommande = commande.getDetailCommandeList();

        // Récupérer la liste des acteurs propriétaires des produits commandés
        // List<Acteur> acteursProprietaires = detailsCommande.stream()
        // .map(detail -> detail.getNomProduit()) // Récupérer le nom du produit de chaque détail
        // .flatMap(nomProduit -> stockRepository.findByNomProduit(nomProduit).stream()) // Convertir la collection de Stock en un flux
        // .map(stock -> stock.getActeur()) // Récupérer l'acteur associé à chaque stock
        // .distinct()
        // .collect(Collectors.toList());
     
        Acteur acteurProprietaire = commande.getActeurProprietaire();
        // Informer chaque acteur propriétaire
        // for (Acteur acteurProprietaire : acteursProprietaires) {
            // Construire le message pour l'acteur propriétaire
            String message = "Commande annulé " + commande.getActeur().getNomActeur().toUpperCase() + " a annulé la " +
                    " (Commande n° " + commande.getCodeCommande() + ")  .  ";


            // Créer et sauvegarder une alerte
            String pattern = "yyyy-MM-dd HH:mm";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = now.format(formatter);
            Alerte alerte = new Alerte(acteurProprietaire.getEmailActeur(), message, "Commande de vos produits annulé");
            alerte.setId(idGenerator.genererCode());
            alerte.setDateAjout(formattedDateTime);
            alerte.setActeur(acteurProprietaire);
            alerteRepository.save(alerte);

            // Envoyer un e-mail à l'acteur propriétaire
            emailService.sendSimpleMail(alerte);
        // }

        return new ResponseEntity<>("La commande a été annulé avec succès.", HttpStatus.OK);
    } else {
        return new ResponseEntity<>("Commande non trouvée avec l'ID " + id, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


      //Annuler
      public void annulerCommandeParProduit(String idDetailCommande, String description) throws Exception {
        // Rechercher le détail de la commande par l'ID
        Optional<DetailCommande> optionalDetailCommande = detailCommandeRepository.findById(idDetailCommande);
        String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        if (optionalDetailCommande.isPresent()) {
            DetailCommande detailCommande = optionalDetailCommande.get();
    
            // Mettre à jour la quantité livrée
    
            detailCommande.setDescription(description);
            detailCommande.setQuantiteLivree(0);
    
            String msg = "La livraison de votre commande de " + optionalDetailCommande.get().getStock().getNomProduit().toUpperCase() + " passé le " + optionalDetailCommande.get().getCommande().getDateCommande()  + " a été annulée  par le proprietaire  vous pouvez le contacter à son numéro " + optionalDetailCommande.get().getCommande().getActeurProprietaire().getNumero() ;
            // Enregistrer les modifications dans la base de données
            detailCommandeRepository.save(detailCommande);
            Alerte al = new Alerte(detailCommande.getCommande().getActeur().getEmailActeur(), msg, "Commande de produits confirmer");
        al.setId(idGenerator.genererCode());
        al.setDateAjout(formattedDateTime);
        emailService.sendSimpleMail(al);
        alerteRepository.save(al);
          
        } else {
            // Lever une exception si le détail de la commande n'est pas trouvé
            throw new Exception("Détail de la commande non trouvé");
        }
    }


    public List<Commande> getAllCommandeByActeurProprietaire(String acteurProprietaire) {
        // Récupérer tout les commandes de l'acteur proprietaire depuis la base de données
        List<Commande> commande = commandeRepository.findByActeurProprietaireIdActeur(acteurProprietaire);

        commande.sort(Comparator.comparing(Commande::getDateCommande).reversed());

        return commande;
    }

    public List<Commande> getCommandeByActeur(String id){
        List<Commande> commandeList =commandeRepository.findAll();

        if(commandeList.isEmpty())
            throw new EntityNotFoundException("Aucune commande trouvé");

            commandeList.sort(Comparator.comparing(Commande::getDateCommande).reversed());
        return commandeList;
    }


    public String deleteCommande(String id){
        Commande commande = commandeRepository.findById(id).orElseThrow(null);

        commandeRepository.delete(commande); 
        return "Commande supprimé avec succèss";
    }


}

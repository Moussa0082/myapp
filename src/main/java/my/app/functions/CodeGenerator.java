package my.app.functions;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class CodeGenerator {

    public String genererCode() {
    // Générer 2 lettres aléatoires
    String lettresAleatoires = genererLettresAleatoires(2);

    // Générer 3 chiffres aléatoires
    String chiffresAleatoires = genererChiffresAleatoires(3);



    // Concaténer les parties pour former le code final
    String codeFinal = lettresAleatoires + chiffresAleatoires ;

    return codeFinal;
}

private String genererLettresAleatoires(int longueur) {
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    return genererChaineAleatoire(alphabet, longueur);
}

private String genererChiffresAleatoires(int longueur) {
    String chiffres = "0123456789";
    return genererChaineAleatoire(chiffres, longueur);
}

private String genererChaineAleatoire(String source, int longueur) {
    Random random = new Random();
    StringBuilder resultat = new StringBuilder();
    for (int i = 0; i < longueur; i++) {
        int index = random.nextInt(source.length());
        resultat.append(source.charAt(index));
    }
    return resultat.toString();
}



   /*
    * 
    Stock mis a jour et enregistrer dans detail commande pour chaque stock et sa quantite demané
    public Commande ajouterStocksACommande(Commande commande, List<Stock> stocks, List<Double> quantitesDemandees) throws Exception {
    // Récupération des stocks correspondant aux identifiants fournis
    List<Stock> stocksFound = stockRepository.findByIdStockIn(
        stocks.stream().map(Stock::getIdStock).collect(Collectors.toList())
    );

    // Date et heure actuelles formatées
    String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

    // Mise à jour des informations de la commande
    commande.setIdCommande(idGenerator.genererCode());
    commande.setCodeCommande(codeGenerator.genererCode());
    commande.setDateCommande(formattedDateTime);
    commande.setStatutCommande(true);
    Commande savedCommande = commandeRepository.save(commande);

    // Enregistrement des détails de la commande pour chaque produit
    for (int i = 0; i < stocksFound.size(); i++) {
        Stock stock = stocksFound.get(i);
        double quantiteDemandee = quantitesDemandees.get(i);

        // Création d'une nouvelle instance de DetailCommande
        DetailCommande detailCommande = new DetailCommande();
        detailCommande.setIdDetailCommande(idGenerator.genererCode());
        detailCommande.setCodeProduit(stock.getCodeStock());
        detailCommande.setQuantiteDemande(quantiteDemandee);
        detailCommande.setQuantiteLivree(0); // Initialement aucun n'a été livré
        detailCommande.setNomProduit(stock.getNomProduit());
        detailCommande.setDateAjout(formattedDateTime);
        detailCommande.setCommande(savedCommande);

        // Enregistrement du détail de la commande
        detailCommandeRepository.save(detailCommande);

        // Mise à jour de la quantité en stock
        double quantiteRestante = stock.getQuantiteStock() - quantiteDemandee;
        stock.setQuantiteStock(quantiteRestante);
        stockRepository.save(stock);

        // Mise à jour de la quantité demandée totale dans la commande
        savedCommande.setQuantiteDemande(savedCommande.getQuantiteDemande() + quantiteDemandee);
    }

    // Envoi de notifications aux propriétaires des stocks
    for (Stock stock : stocksFound) {
        Acteur proprietaire = stock.getActeur();
        String message = "Les produits suivants ont été commandés par " + savedCommande.getActeur().getNomActeur() + " :\n" +
                         "- " + stock.getNomProduit() + " : quantités " + savedCommande.getQuantiteDemande() + "\n" +
                         "Veuillez lui livrer sa commande dans les plus brefs délais";
        System.out.println( "Message : " + message);
        // Envoi d'un e-mail uniquement si le propriétaire a une adresse e-mail
        if (proprietaire != null && proprietaire.getEmailActeur() != null) {
            Alerte al = new Alerte(proprietaire.getEmailActeur(), message, "Nouvelle commande de produits");
            al.setId(idGenerator.genererCode());
            al.setDateAjout(formattedDateTime);
            al.setActeur(proprietaire);
            alerteRepository.save(al);
            // emailService.sendSimpleMail(al);
        } else {
            System.out.println("Adresse e-mail introuvable pour le propriétaire du stock : " + proprietaire);
        }
    }

    return savedCommande;
}

    */

}

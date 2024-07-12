package my.app.models;

import java.util.List;

import lombok.Data;

@Data
public class CommandeAvecStocks {
    private  Commande commande;
    private  Acteur acteur;
    private List<Stock> stocks;
    private List<Integer> quantitesDemandees;
}

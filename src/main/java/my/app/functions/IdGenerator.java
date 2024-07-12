package my.app.functions;

import java.util.*;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class IdGenerator {

     public String genererCode() {
        // Générer 2 lettres aléatoires
        String lettresAleatoires = genererLettresAleatoires(4);

        // Générer 3 chiffres aléatoires
        String chiffresAleatoires = genererChiffresAleatoires(4);

        // Générer une combinaison de 9 caractères aléatoires (14 - 2 - 3)
        String combinaisonAleatoire = genererCombinaisonAleatoire(12);

        // Concaténer les parties pour former le code final
        String codeFinal = lettresAleatoires + chiffresAleatoires + combinaisonAleatoire;

        // Mélanger les caractères dans le code final
        codeFinal = melangerCaracteres(codeFinal);

        return codeFinal;
    }

    private String genererLettresAleatoires(int longueur) {
        String alphabet = "azertyuiopqsdfghjklwxcvbn";
        return genererChaineAleatoire(alphabet, longueur);
    }

    private String genererChiffresAleatoires(int longueur) {
        String chiffres = "0123456789";
        return genererChaineAleatoire(chiffres, longueur);
    }

    private String genererCombinaisonAleatoire(int longueur) {
        String caracteres = "abcdefghijklmnopqrstuvwxyz0123456789";
        return genererChaineAleatoire(caracteres, longueur);
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

    private String melangerCaracteres(String chaine) {
        List<Character> caracteres = new ArrayList<>();
        for (char c : chaine.toCharArray()) {
            caracteres.add(c);
        }
        Collections.shuffle(caracteres);
        StringBuilder resultat = new StringBuilder();
        for (char c : caracteres) {
            resultat.append(c);
        }
        return resultat.toString();
    }
    
}

package my.app.services;

import java.util.stream.Collectors;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import my.app.models.DetailCommande;
import my.app.repository.DetailCommandeRepository;

@Service
public class DetailCommandeService {

    @Autowired
    DetailCommandeRepository detailCommandeRepository;

     public List<DetailCommande> getAllDetailCommandeByIdCommande(String idCommande) {
        List<DetailCommande> detailCommandeList = detailCommandeRepository.findByCommandeIdCommande(idCommande);

        if(detailCommandeList.isEmpty())
            throw new IllegalStateException("Aucun detail trouvé");
        
            detailCommandeList = detailCommandeList.
        stream().sorted((m1,m2) -> m2.getDateAjout().compareTo(m1.getDateAjout()))
        .collect(Collectors.toList());

        return detailCommandeList;
    }
    
}

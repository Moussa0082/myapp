package my.app.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;

import java.nio.file.Path;
import java.nio.file.Paths;

import my.app.functions.IdGenerator;
import my.app.models.CategorieProduit;
import my.app.repository.CategorieProduitRepository;

@Service
public class CategorieProduitService {



    @Autowired
    private CategorieProduitRepository categorieProduitRepository;

    @Autowired
    private IdGenerator idGenerator;


    public CategorieProduit createCategorieProduit(CategorieProduit categorieProduit, MultipartFile imageFile) throws Exception {

        // Verifie si la categorie existe
        CategorieProduit cat = categorieProduitRepository.findByLibelleCategorie(categorieProduit.getLibelleCategorie());
    
        if (cat != null) {
            throw new IllegalArgumentException("La categorie " + cat.getLibelleCategorie() + " existe déjà" );
        }
        categorieProduit.setIdCategorieProduit(idGenerator.genererCode());
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
                categorieProduit.setPhotoCategorie("ecom/" + imageName);
            } catch (IOException e) {
                throw new Exception("Error processing the image file: " + e.getMessage());
            }
        }
        // sauvegarde du type de la banque
        return categorieProduitRepository.save(categorieProduit);
    }


       //Modifier un categorieproduit
    public CategorieProduit modifierCategorieProduit(String id, CategorieProduit categorieProduit, MultipartFile imageFile) throws Exception {
        try {
            CategorieProduit categorieProduitExistant = categorieProduitRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("CategorieProduit non trouvé avec l'ID: " + id));
    
            // Mettre à jour les champs
            categorieProduitExistant.setLibelleCategorie(categorieProduit.getLibelleCategorie());
            categorieProduitExistant.setDescriptionCategorie(categorieProduit.getDescriptionCategorie());
            
    
            // Mettre à jour l'image si fournie
            if (imageFile != null) {
                String emplacementImage = "C:\\xampp\\htdocs\\ecom";
                String nomImage = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path cheminImage = Paths.get(emplacementImage).resolve(nomImage);
    
                Files.copy(imageFile.getInputStream(), cheminImage, StandardCopyOption.REPLACE_EXISTING);
                categorieProduitExistant.setPhotoCategorie("ecom/" + nomImage);
            }
    
            // Enregistrer le type de banque mis à jour
            return categorieProduitRepository.save(categorieProduitExistant);
        } catch (NoSuchElementException ex) {
            throw new NoSuchElementException("Une erreur s'est produite lors de la mise à jour de la categporie avec l'ID : " + id);
        }
    }


        public List<CategorieProduit> getAllCategorie() {
        List<CategorieProduit> categorieProduits = categorieProduitRepository.findAll();

        if (categorieProduits.isEmpty()) {
            throw new EntityNotFoundException("CategorieProduit vide");
        }

        categorieProduits = categorieProduits
            .stream()
            .map(this::ensureUtf8Encoding)
            .sorted((s1, s2) -> s2.getLibelleCategorie().compareTo(s1.getLibelleCategorie()))
            .collect(Collectors.toList());

        return categorieProduits;
    }

    private CategorieProduit ensureUtf8Encoding(CategorieProduit categorieProduit) {
        String libelleCategorie = categorieProduit.getLibelleCategorie();
        if (!StandardCharsets.UTF_8.newEncoder().canEncode(libelleCategorie)) {
            byte[] bytes = libelleCategorie.getBytes(StandardCharsets.ISO_8859_1);
            libelleCategorie = new String(bytes, StandardCharsets.UTF_8);
            categorieProduit.setLibelleCategorie(libelleCategorie);
        }
        return categorieProduit;
    }


    public String deleteCategorie(String id){
        CategorieProduit categorieProduit = categorieProduitRepository.findById(id).orElseThrow(null);

        categorieProduitRepository.delete(categorieProduit); 
        return "Supprimé avec succèss";
    }

    public CategorieProduit active(String id) throws Exception{
        CategorieProduit cat = categorieProduitRepository.findById(id).orElseThrow(null);

        try {
            cat.setStatutCategorie(true);
        } catch (Exception e) {
            throw new Exception("Erreur lors de l'activation  de la categorie : " + e.getMessage());
        }
        return categorieProduitRepository.save(cat);
    }

    public CategorieProduit desactive(String id) throws Exception{
        CategorieProduit cat = categorieProduitRepository.findById(id).orElseThrow(null);

        try {
            cat.setStatutCategorie(false);
        } catch (Exception e) {
            throw new Exception("Erreur lors de l'activation  de la categorie : " + e.getMessage());
        }
        return categorieProduitRepository.save(cat);
    }

    
}

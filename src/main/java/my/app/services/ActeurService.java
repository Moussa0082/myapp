package my.app.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import my.app.exception.NoContentException;
import my.app.functions.IdGenerator;
import my.app.models.Acteur;
import my.app.models.Alerte;
import org.springframework.beans.factory.annotation.Value;

import java.nio.file.Path;
import java.nio.file.Paths;
import my.app.repository.ActeurRepository;
import my.app.repository.AlerteRepository;

@Service
public class ActeurService {

    @Autowired
    private ActeurRepository acteurRepository;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    AlerteRepository alerteRepository;

     @Autowired
     private JavaMailSender javaMailSender;


    @Value("bane8251@gmail.com")
     String sender;

     String code = getRandomNumberString();


     //créer un acteur
      public Acteur createActeur(Acteur acteur, MultipartFile imageFile) throws Exception {
        
        Acteur id = acteurRepository.findByIdActeur(acteur.getIdActeur());
        if(id != null){

            throw new IllegalArgumentException("Un acteur avec l'id " + id + " existe déjà");
        }
        
        // Vérifier si l'acteur a le même mail et le même type
        // Acteur existingActeurAvecMemeType = acteurRepository.findByEmailActeurAndTypeActeurIn(acteur.getEmailActeur(), acteur.getTypeActeur());
        Acteur existingActeurAvecMemeType = acteurRepository.findByEmailActeur(acteur.getEmailActeur());
        if (existingActeurAvecMemeType != null) {
            // Si un acteur avec le même email et type existe déjà
            throw new IllegalArgumentException("Un compte avec le même email existe déjà");
        }

        Acteur existingActeur = acteurRepository.findByNumero(acteur.getNumero());
        if (existingActeur != null) {
            // Si un acteur avec le même email et type existe déjà
            throw new IllegalArgumentException("Un compte avec le même numero de téléphone existe déjà");
        }
        acteur.setIdActeur(idGenerator.genererCode());
            
        if(acteur.getRoleActeur() == null){
            acteur.setRoleActeur("user");
        }
            //On hashe le mot de passe
            String passWordHasher = passwordEncoder.encode(acteur.getPassword());
            acteur.setPassword(passWordHasher);

            // Traitement du fichier image siege acteur
            // Traitement image
        if (imageFile != null) {
            // String imageLocation = "C:\\Users\\bane8\\OneDrive\\Bureau\\Express Solution\\api_solution_express";
            String imageLocation = "C:\\xampp\\htdocs\\ecom";
            try {
                Path imageRootLocation = Paths.get(imageLocation);
                if (!Files.exists(imageRootLocation)) {
                    Files.createDirectories(imageRootLocation);
                }
    
                String imageName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path imagePath = imageRootLocation.resolve(imageName);
                Files.copy(imageFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                acteur.setPhotoActeur("ecom/" + imageName);
            } catch (IOException e) {
                throw new Exception("Error processing the image file: " + e.getMessage());
            }
        }
       

     
            String pattern = "yyyy-MM-dd HH:mm";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = now.format(formatter);
            acteur.setDateAjout(formattedDateTime);
            acteur.setIdActeur(idGenerator.genererCode());
        
            Acteur savedActeur = acteurRepository.save(acteur);
        
                     
            return savedActeur;
               
    }

    //Modifier un user
    public Acteur modifierActeur(String id, Acteur acteur, MultipartFile imageFile) throws Exception {
        try {
            Acteur acteurExistant = acteurRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Acteur non trouvé avec l'ID: " + id));
    
         
            // Vérifier si le type de banque existe dans la banque en question pour éviter les doublons de types dans la même banque
            if (acteurExistant == null) {
                throw new IllegalArgumentException("L'utilisateur avec le nom " + acteur.getNomActeur() + " n'existe pas  " );
            }
    
            // Mettre à jour les champs
            acteurExistant.setNomActeur(acteur.getNomActeur());
            acteurExistant.setAdresse(acteur.getAdresse());
            acteurExistant.setEmailActeur(acteur.getEmailActeur());
            acteurExistant.setNumero(acteur.getNumero());

    
            // Mettre à jour l'image si fournie
            if (imageFile != null) {
                // String emplacementImage = "C:\\Users\\bane8\\OneDrive\\Bureau\\Express Solution\\api_solution_express";
                String emplacementImage = "C:\\xampp\\htdocs\\ecom";
                String nomImage = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path cheminImage = Paths.get(emplacementImage).resolve(nomImage);
    
                Files.copy(imageFile.getInputStream(), cheminImage, StandardCopyOption.REPLACE_EXISTING);
                acteurExistant.setPhotoActeur("ecom/" + nomImage);
            }
    
            // Enregistrer l'acteur mis à jour
            return acteurRepository.save(acteurExistant);
        } catch (NoSuchElementException ex) {
            throw new NoSuchElementException("Une erreur s'est produite lors de la mise à jour d'un acteur avec l'ID : " + id);
        }
    }


    //activer un acteur
      public ResponseEntity<String> enableActeur(String id) throws Exception {
        Optional<Acteur> acteur = acteurRepository.findById(id);
        if (acteur.isPresent()) {
            acteur.get().setStatutActeur(true);
            acteurRepository.save(acteur.get());
            Alerte alerte = new Alerte(acteur.get().getEmailActeur(), "Votre compte a été activé par l'administrateur vous ne pouvez plus acceder à votre compte veuillez contacter l'administrateur " , "Activation de compte ");
            alerte.setId(idGenerator.genererCode());
            alerteRepository.save(alerte);
            // emailService.sendSimpleMail(alerte);
            return new ResponseEntity<>("L'acteur " + acteur.get().getNomActeur() + " a été activé avec succès", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Acteur non trouvé avec l'ID " + id, HttpStatus.BAD_REQUEST);
        }
    }

    //desactiver un acteur
      public ResponseEntity<String> disableActeur(String id) throws Exception {
        Optional<Acteur> acteur = acteurRepository.findById(id);
        if (acteur.isPresent()) {
            acteur.get().setStatutActeur(false);
            acteurRepository.save(acteur.get());
            Alerte alerte = new Alerte(acteur.get().getEmailActeur(), "Votre compte a été desactivé par l'administrateur vous ne pouvez plus acceder à votre compte veuillez contacter l'administrateur " , "Desactivation de compte par l'administrateur de koumi");
            alerte.setId(idGenerator.genererCode());
            alerteRepository.save(alerte);
            // emailService.sendSimpleMail(alerte);
            return new ResponseEntity<>("L'acteur " + acteur.get().getNomActeur() + " a été désactivé avec succès", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Acteur non trouvé avec l'ID " + id, HttpStatus.BAD_REQUEST);
        }
    }



    //Fonction pour générer 6 chiffres en chaîne de caractère
    private String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(9999);

        // this will convert any number sequence into 4 character.
        return String.format("%04d", number);
    }

    private void sendMail(Acteur acteur, String code) throws Exception {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        try {
            mailMessage.setFrom(sender);
            mailMessage.setTo(acteur.getEmailActeur());
            mailMessage.setText("Votre code de verification est "+code);
            mailMessage.setSubject("Validation email");

            javaMailSender.send(mailMessage);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }



        // Mot de passe oublié : envoyer un code à l'utilisateur par e-mail
        public String sendOtpCodeEmail(String email) throws Exception {
            Acteur userVerif = acteurRepository.findByEmailActeur(email);
            if (userVerif == null) {
                throw new Exception("Cet email n'existe pas, veuillez vérifier l'email saisi");
            }
            
            // Générez le code
            
            // Enregistrez le code et son horodatage dans la base de données
            userVerif.setResetToken(code);
            // Définir la date d'expiration du token (après 2 minutes)
            LocalDateTime tokenExpiryDate = LocalDateTime.now().plusMinutes(2);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = tokenExpiryDate.format(formatter);
            userVerif.setTokenCreationDate(formattedDate);    acteurRepository.save(userVerif);
            
            // Envoyez le code par e-mail
            sendMail(userVerif, code);
            
            return code;
        }
    

        // Vérifier le code envoyé par e-mail
    public boolean verifyOtpCodeEmail(String emailActeur, String resetToken) {
        Acteur userVerif = acteurRepository.findByEmailActeurAndResetToken(emailActeur, resetToken);
        if (userVerif == null) {
            throw new RuntimeException("Code incorrect ou expiré");
        }
        
        // Vérifiez si le code est expiré
        // Convertir la date de création du token en LocalDateTime pour la vérification
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime tokenCreationDate = LocalDateTime.parse(userVerif.getTokenCreationDate(), formatter);
    
        if (tokenCreationDate == null || tokenCreationDate.isBefore(LocalDateTime.now().minusMinutes(2))) {
            // Code expiré
            throw new RuntimeException("Code expiré");
        }
    
        // Vérifier si le code correspond
        if (!userVerif.getResetToken().equals(code)) {
            throw new RuntimeException("Code incorrect");
        }
    
        
        // Réinitialisez le token et la date de création
        userVerif.setResetToken(null);
        userVerif.setTokenCreationDate(null);
        acteurRepository.save(userVerif);
        
        return true; // Code vérifié avec succès
    }

       //Recuperer la liste des Admins
     public List<Acteur> getAllActeur(){

        List<Acteur> acteurList = acteurRepository.findAll();

        acteurList = acteurList
                .stream().sorted((d1, d2) -> d2.getDateAjout().compareTo(d1.getDateAjout()))
                .collect(Collectors.toList());
        return acteurList;
    }



        //Fonction pour reinitialiser le mot de passe par email
        public Acteur resetPasswordEmail(String email, String password) throws Exception{
            Acteur userVerif = acteurRepository.findByEmailActeur(email);
            // Vérifier si le code est expiré
            // if (isCodeExpired(code)) {
            //     throw new Exception("Code expiré");
            // }
            
            userVerif.setPassword(passwordEncoder.encode(password));
    
            return acteurRepository.save(userVerif);
        }


         public String deleteByIdActeur(String id){
        Acteur acteur = acteurRepository.findByIdActeur(id);
        if(acteur == null){
            throw new EntityNotFoundException("Désolé l'acteur à supprimer n'existe pas");
        }
        acteurRepository.delete(acteur);
        return "Acteur supprimé avec succèss";
    }


     //Se connecter 
      public Acteur connexionActeur(String emailActeur, String password){
        Acteur acteur = acteurRepository.findByEmailActeur(emailActeur);
        if (acteur == null || !passwordEncoder.matches(password, acteur.getPassword())) {
            throw new EntityNotFoundException("Email ou mot de passe incorrect");
        }
        
        if(acteur.getStatutActeur()==false){
            throw new NoContentException("Connexion échoué votre compte  est desactivé \n veuillez contacter l'administrateur pour la procedure d'activation de votre compte !");
        }
         return acteur;
        }

     //Se connecter avec  code pin
        public Acteur connexionActeurWithPin(String codeActeur,String password){
            // String hashedPassword = passwordEncoder.encode(password); // Hasher le mot de passe saisi par l'utilisateur
            Acteur acteur = acteurRepository.findByCodeActeur(codeActeur);
          
            
            // Comparer les mots de passe hachés
            if (acteur == null || !passwordEncoder.matches(password, acteur.getPassword())) {
                throw new EntityNotFoundException("Code Pin incorrect");
            }
            
            if (!acteur.getStatutActeur()) {
                throw new NoContentException("Connexion échouée : votre compte est désactivé. Veuillez contacter l'administrateur pour la procédure d'activation de votre compte !");
            }
            
            return acteur;
        }

        //Se connecter avec  code pin
        public Acteur connexionActeurWithCodeAndNomActeur(String codeActeur,String nomActeur){
            Acteur acteur = acteurRepository.findByCodeActeurAndNomActeur(codeActeur,nomActeur);
          
            
            // Comparer les mots de passe hachés
            if (acteur == null) {
                throw new EntityNotFoundException("Acteur non existant");
            }
            
            if (!acteur.getStatutActeur()) {
                throw new NoContentException("Connexion échouée : votre compte est désactivé. Veuillez contacter l'administrateur pour la procédure d'activation de votre compte !");
            }
            
            return acteur;
        }
    


}

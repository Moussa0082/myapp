package my.app.services;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;





@Service
public class EmailService {

     @Autowired
     my.app.repository.AlerteRepository alerteRepository;

     @Autowired private JavaMailSender javaMailSender;
 
    @Value("bane8251@gmail.com") private String sender;
  

    public String sendSimpleMail(my.app.models.Alerte alerte) {
       // Method 1
    // To send a simple email
    
 
        // Try block to check for exceptions
        try {
 
            // Creating a simple mail message
            SimpleMailMessage mailMessage
                = new SimpleMailMessage();
 
            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(alerte.getEmail());
            mailMessage.setText(alerte.getMessage());
            mailMessage.setSubject(alerte.getSujet());
 
            // Sending the mail
            javaMailSender.send(mailMessage);
            return "Email envoyer avec succès...";
        }
 
        // Catch block to handle the exceptions
        catch (Exception e) {
            return "Erreur lors de l'envoi de l'email ";
        }
    }   
    
    
}

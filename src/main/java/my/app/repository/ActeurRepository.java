package my.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import my.app.models.Acteur;

public interface ActeurRepository extends JpaRepository<Acteur, String> {

    Acteur findByEmailActeur(String emailActeur);

    Acteur findByNumero(String numero);

    Acteur findByIdActeur(String idActeur);

    Acteur findByEmailActeurAndResetToken(String emailActeur, String resetToken);

    Acteur findByCodeActeur(String codeActeur);

    Acteur findByCodeActeurAndNomActeur(String codeActeur, String nomActeur);

    
}

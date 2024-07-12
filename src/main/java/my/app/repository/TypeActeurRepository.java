package my.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import my.app.models.TypeActeur;

public interface TypeActeurRepository extends JpaRepository<TypeActeur, String>{
    
}

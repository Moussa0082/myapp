package my.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import my.app.models.Alerte;



public interface AlerteRepository extends JpaRepository<Alerte, String>{
    
}

package org.frank.test.springboot.app.repositories;

import org.frank.test.springboot.app.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
//    List<Cuenta> findAll();
//    Cuenta findById(Long id);
//    void update(Cuenta cuenta);

    Optional<Cuenta> findByPersona(String persona);

    @Query("Select c from Cuenta c where c.persona = ?1")
    Optional<Cuenta> findPorPersona(String persona);
}

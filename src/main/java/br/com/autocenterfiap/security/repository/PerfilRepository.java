package br.com.autocenterfiap.security.repository;

import br.com.autocenterfiap.security.entity.Perfil;
import br.com.autocenterfiap.security.enums.PerfilType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    Optional<Perfil> findByNome(PerfilType nome);
}


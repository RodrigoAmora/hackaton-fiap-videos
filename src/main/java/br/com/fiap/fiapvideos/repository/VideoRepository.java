package br.com.fiap.fiapvideos.repository;

import br.com.fiap.fiapvideos.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    Page<Video> findByOwnerId(String ownerId, Pageable pageable);

}

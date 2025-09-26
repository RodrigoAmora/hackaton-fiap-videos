-- Criação do banco de dados
CREATE DATABASE IF NOT EXISTS fiap_videos;
USE fiap_videos;

-- Tabela de vídeos
CREATE TABLE videos (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        filename VARCHAR(255) NOT NULL,
                        owner_id BIGINT NOT NULL,
                        status VARCHAR(20) NOT NULL,
                        error_message TEXT,
                        result_zip_path VARCHAR(255),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        INDEX idx_owner_id (owner_id),
                        INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

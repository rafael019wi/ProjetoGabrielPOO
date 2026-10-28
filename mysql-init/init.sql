CREATE SCHEMA IF NOT EXISTS stock_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE stock_db;

SET NAMES utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- Drop de tabelas
SET SESSION FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `products`;
SET SESSION FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `user` (
    `id_user` INT AUTO_INCREMENT PRIMARY KEY,
    `user_name` VARCHAR(255) NOT NULL,
    `user_email` VARCHAR(255) UNIQUE NOT NULL,
    `user_phone` CHAR(11) NOT NULL,
    `user_password` VARCHAR(255) NOT NULL,
    `user_status` ENUM('active', 'blocked') NOT NULL,
    `created_at` DATETIME NOT NULL,
    INDEX `idx_user_name` (`user_name`, `id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `categories` (
    `id_category` INT AUTO_INCREMENT PRIMARY KEY,
    `fk_created_by` INT NOT NULL,
    `category_name` VARCHAR(255) NOT NULL,
    `created_at` DATETIME NOT NULL,
    FOREIGN KEY (`fk_created_by`) REFERENCES `user`(`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `products` (
    `id_products` INT AUTO_INCREMENT PRIMARY KEY,
    `fk_id_category` INT NOT NULL,
    `fk_created_by` INT NOT NULL,
    `book_author` VARCHAR(255) NOT NULL,
    `name_product` VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NULL,
    `price` DECIMAL(6,2) NOT NULL,
    `stock` INT NOT NULL CHECK (`stock` >= 0),
    `product_status` ENUM('active', 'blocked') NOT NULL,
    `created_at` DATETIME NOT NULL,
    FOREIGN KEY (`fk_id_category`) REFERENCES `categories`(`id_category`) ON DELETE RESTRICT,
    FOREIGN KEY (`fk_created_by`) REFERENCES `user`(`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `user` (id_user, user_name, user_email, user_phone, user_password, user_status, created_at) VALUES (
    1, 'Manager', 'teste@gmail.com'  , '19972735318', 'RgcNS/k0+w1LBtnixG40aUTjIkRJAKQ119mpXm10NfU=', 'active', NOW()
);
CREATE DATABASE  IF NOT EXISTS `reservas` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_spanish_ci */;
USE `reservas`;
-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: reservas
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `habitacion`
--

DROP TABLE IF EXISTS `habitacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `habitacion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `disponible_desde` date DEFAULT NULL,
  `disponible_hasta` date DEFAULT NULL,
  `precio_noche` double DEFAULT NULL,
  `reservado` bit(1) NOT NULL,
  `tipo_habitacion` varchar(255) DEFAULT NULL,
  `id_hotel` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnati1hwmlnaiemvjcwuql8web` (`id_hotel`),
  CONSTRAINT `FKnati1hwmlnaiemvjcwuql8web` FOREIGN KEY (`id_hotel`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `habitacion`
--

LOCK TABLES `habitacion` WRITE;
/*!40000 ALTER TABLE `habitacion` DISABLE KEYS */;
INSERT INTO `habitacion` VALUES (1,'2025-03-25','2025-03-26',100,_binary '','triple',1),(2,'2025-03-25','2025-03-26',100,_binary '','triple',1),(3,'2025-03-23','2025-03-25',100,_binary '\0','triple',1),(4,'2025-03-26','2025-03-27',100,_binary '\0','triple',1),(5,'2025-03-23','2025-03-27',100,_binary '\0','cuadruple',2),(6,'2025-03-01','2025-03-30',100,_binary '\0','triple',2),(7,'2025-03-01','2025-03-30',100,_binary '\0','doble',2);
/*!40000 ALTER TABLE `habitacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotel`
--

DROP TABLE IF EXISTS `hotel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `alta` bit(1) NOT NULL,
  `codigo_hotel` varchar(255) DEFAULT NULL,
  `lugar` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel`
--

LOCK TABLES `hotel` WRITE;
/*!40000 ALTER TABLE `hotel` DISABLE KEYS */;
INSERT INTO `hotel` VALUES (1,_binary '','IB-0001','Alcázar de San Juan','Insula Barataria'),(2,_binary '','DP-0001','Marbella','Don Pablo'),(3,_binary '','HG-0001','Málaga','Hotel Guadalmar');
/*!40000 ALTER TABLE `hotel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `apellido` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `id_habitacion` bigint(20) DEFAULT NULL,
  `id_vuelo` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKo6ttws6j9vit42cls6lqobqpt` (`id_habitacion`),
  KEY `FK7c7ck5wpc9fu6fixa6lh65v8s` (`id_vuelo`),
  CONSTRAINT `FK7c7ck5wpc9fu6fixa6lh65v8s` FOREIGN KEY (`id_vuelo`) REFERENCES `vuelo` (`id`),
  CONSTRAINT `FKo6ttws6j9vit42cls6lqobqpt` FOREIGN KEY (`id_habitacion`) REFERENCES `habitacion` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'Ojeda','Antonio',NULL,1),(2,'Ojeda','Antonio',2,NULL),(3,'Jara','Laura',2,NULL),(4,'Gómez','Mario',1,NULL),(5,'Millán','Josefa',1,NULL);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vuelo`
--

DROP TABLE IF EXISTS `vuelo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vuelo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `alta` bit(1) NOT NULL,
  `destino` varchar(255) DEFAULT NULL,
  `fecha` date DEFAULT NULL,
  `numero_vuelo` varchar(255) DEFAULT NULL,
  `origen` varchar(255) DEFAULT NULL,
  `precio` double NOT NULL,
  `tipo_asiento` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vuelo`
--

LOCK TABLES `vuelo` WRITE;
/*!40000 ALTER TABLE `vuelo` DISABLE KEYS */;
INSERT INTO `vuelo` VALUES (1,_binary '','Málaga','2025-03-23','MM-0001','Madrid',150.5,'Economy'),(2,_binary '','Madrid','2025-03-24','MM-0002','Málaga',150.5,'Economy'),(3,_binary '','París','2025-02-25','PB-0001','Berlín',160,'Bisness'),(4,_binary '','Toronto','2025-05-24','NT-0001','New York',150.5,'Economy'),(5,_binary '','Londres','2025-06-24','PL-0001','Paris',150.5,'Economy'),(6,_binary '','París','2025-06-24','LP-0001','Londres',150.5,'Economy'),(7,_binary '','Málaga','2025-03-23','MM-0002','Madrid',150.5,'Business');
/*!40000 ALTER TABLE `vuelo` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-26 10:50:22

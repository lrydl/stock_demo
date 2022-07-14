/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 80025
Source Host           : localhost:3306
Source Database       : lry

Target Server Type    : MYSQL
Target Server Version : 80025
File Encoding         : 65001

Date: 2022-07-14 09:22:06
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sorder
-- ----------------------------
DROP TABLE IF EXISTS `sorder`;
CREATE TABLE `sorder` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `buy_num` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2224744 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for t_product
-- ----------------------------
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product` (
  `id` int NOT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `stock` int DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 80025
Source Host           : localhost:3306
Source Database       : lry

Target Server Type    : MYSQL
Target Server Version : 80025
File Encoding         : 65001

Date: 2022-07-14 23:44:10
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
) ENGINE=InnoDB AUTO_INCREMENT=2410470 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sorder
-- ----------------------------

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

-- ----------------------------
-- Records of t_product
-- ----------------------------
INSERT INTO `t_product` VALUES ('1', '10.00', 'iphone10', '97733483', null);
INSERT INTO `t_product` VALUES ('2', '10.00', 'iphone11', '99357191', null);
INSERT INTO `t_product` VALUES ('3', '10.00', 'iphone11', '99356439', null);
INSERT INTO `t_product` VALUES ('4', '10.00', 'iphone11', '99356978', null);
INSERT INTO `t_product` VALUES ('5', '10.00', 'iphone11', '99356250', null);
INSERT INTO `t_product` VALUES ('6', '10.00', 'iphone11', '99356086', null);
INSERT INTO `t_product` VALUES ('7', '10.00', 'iphone11', '99363131', null);
INSERT INTO `t_product` VALUES ('8', '10.00', 'iphone11', '99356567', null);
INSERT INTO `t_product` VALUES ('9', '10.00', 'iphone11', '99356568', null);
INSERT INTO `t_product` VALUES ('10', '10.00', 'iphone11', '99360522', null);

/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 80025
Source Host           : localhost:3306
Source Database       : lry

Target Server Type    : MYSQL
Target Server Version : 80025
File Encoding         : 65001

Date: 2022-07-15 01:52:24
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sorder
-- ----------------------------
DROP TABLE IF EXISTS `sorder`;
CREATE TABLE `sorder` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_sn` bigint DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `buy_num` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3684634 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
INSERT INTO `t_product` VALUES ('1', '10.00', 'iphone10', '96657345', null);
INSERT INTO `t_product` VALUES ('2', '10.00', 'iphone11', '98281084', null);
INSERT INTO `t_product` VALUES ('3', '10.00', 'iphone11', '98275490', null);
INSERT INTO `t_product` VALUES ('4', '10.00', 'iphone11', '99202865', null);
INSERT INTO `t_product` VALUES ('5', '10.00', 'iphone11', '99204783', null);
INSERT INTO `t_product` VALUES ('6', '10.00', 'iphone11', '99263110', null);
INSERT INTO `t_product` VALUES ('7', '10.00', 'iphone11', '99270696', null);
INSERT INTO `t_product` VALUES ('8', '10.00', 'iphone11', '99263139', null);
INSERT INTO `t_product` VALUES ('9', '10.00', 'iphone11', '99355902', null);
INSERT INTO `t_product` VALUES ('10', '10.00', 'iphone11', '99359979', null);

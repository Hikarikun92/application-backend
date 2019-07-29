CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `username` varchar(45) NOT NULL,
  `email` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
);
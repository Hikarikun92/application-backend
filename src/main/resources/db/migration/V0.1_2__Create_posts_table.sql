CREATE TABLE `post` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) NOT NULL,
  `body` text NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),

  CONSTRAINT post_fk_user FOREIGN KEY (user_id) references user (id)
);
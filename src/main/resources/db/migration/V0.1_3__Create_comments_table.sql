CREATE TABLE `comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) NOT NULL,
  `body` text NOT NULL,
  `email` varchar(50) NOT NULL,
  `post_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),

  CONSTRAINT comment_fk_post FOREIGN KEY (post_id) references post (id)
);
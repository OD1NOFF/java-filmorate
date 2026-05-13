MERGE INTO mpa (id, name)
KEY(id)
VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

MERGE INTO genres (id, name)
KEY(id)
VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

MERGE INTO users (id, email, login, name, birthday)
KEY(id)
VALUES
(1, '1@mail.com', 'Alex', 'Alex1', '2006-04-05'),
(2, '2@mail.com', 'Alex', 'Alex2', '2006-04-06'),
(3, '3@mail.com', 'Alex', 'Alex3', '2006-04-07'),
(4, '4@mail.com', 'Alex', 'Alex4', '2006-04-08'),
(5, '5@mail.com', 'Alex', 'Alex5', '2006-04-09');

ALTER TABLE users ALTER COLUMN id RESTART WITH 6;

MERGE INTO friendships (user_id, friend_id)
KEY (user_id, friend_id)
VALUES
(1, 2),
(2, 4),
(5, 3),
(2, 3),
(1, 3);
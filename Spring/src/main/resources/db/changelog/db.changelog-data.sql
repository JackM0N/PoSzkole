--liquibase formatted sql
--changeset Przemyslaw:1 labels:init,data

INSERT INTO role (role_name)
VALUES ('STUDENT');
INSERT INTO role (role_name)
VALUES ('TEACHER');
INSERT INTO role (role_name)
VALUES ('MANAGER');
INSERT INTO role (role_name)
VALUES ('OWNER');

INSERT INTO price_list (creation_date, start_date)
VALUES
    ('2023-01-15', '2023-02-01'),
    ('2023-03-10', '2023-04-01'),
    ('2023-05-05', '2023-06-01'),
    ('2023-07-20', '2023-08-01'),
    ('2023-09-10', '2023-10-01');

INSERT INTO website_user
    (user_id,username, password, first_name, last_name, gender, email, phone, hourly_rate, level_id, guardian_phone, guardian_email, price_list_id, discount_percentage, is_cash_payment, issue_invoice)
VALUES
    (10000, 'jnowak', '$2a$10$o9OcOIN.1bmooaFQJ.1PAOyZxHBA9IXo8qj2dECXrj8Vk9YpvxstO', 'Jan', 'Nowak', 'M', 'j.nowak@example.com', '+48123456789', NULL, 'PRIMARY', '+48987654324', 'p.nowak@example.com', 1, 10, TRUE, TRUE),
    (10001, 'awisniewska', '$2a$10$o9OcOIN.1bmooaFQJ.1PAOyZxHBA9IXo8qj2dECXrj8Vk9YpvxstO', 'Anna', 'Wiśniewska', 'F', 'a.wisniewska@example.com', '+48987654321', NULL, 'HIGH_SCHOOL', '+48123456890', 'b.wisniewska@example.com', 2, 5, FALSE, FALSE),
    (10002, 'pkowalski', '$2a$10$o9OcOIN.1bmooaFQJ.1PAOyZxHBA9IXo8qj2dECXrj8Vk9YpvxstO', 'Piotr', 'Kowalski', 'M', 'p.kowalski@example.com', '+48876543210', NULL, 'HIGH_SCHOOL', NULL, NULL, 1, 15, TRUE, TRUE),
    (10003, 'mlewandowski', '$2a$10$o9OcOIN.1bmooaFQJ.1PAOyZxHBA9IXo8qj2dECXrj8Vk9YpvxstO', 'Marek', 'Lewandowski', 'M', 'm.lewandowski@example.com', '+48765432109', NULL, 'TERTIARY', NULL, NULL, 3, 20, FALSE, TRUE),
    (1000, 'ekowalczyk', '$2a$10$o9OcOIN.1bmooaFQJ.1PAOyZxHBA9IXo8qj2dECXrj8Vk9YpvxstO', 'Ewa', 'Kowalczyk', 'F', 'e.kowalczyk@example.com', '+48654321098', 48.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (1001, 'kzielinski', '$2a$10$o9OcOIN.1bmooaFQJ.1PAOyZxHBA9IXo8qj2dECXrj8Vk9YpvxstO', 'Katarzyna', 'Zielińska', 'F', 'k.zielinska@example.com', '+48678901234', 52.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (1002, 'mpietrzak', '$2a$10$o9OcOIN.1bmooaFQJ.1PAOyZxHBA9IXo8qj2dECXrj8Vk9YpvxstO', 'Michał', 'Pietrzak', 'M', 'm.pietrzak@example.com', '+48567890123', 47.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (1003, 'jgorska', '$2a$10$o9OcOIN.1bmooaFQJ.1PAOyZxHBA9IXo8qj2dECXrj8Vk9YpvxstO', 'Joanna', 'Górska', 'F', 'j.gorska@example.com', '+48789012345', 54.00, NULL, NULL, NULL, NULL, NULL, TRUE, FALSE),
    (1004, 'rnowicka', '$2a$10$o9OcOIN.1bmooaFQJ.1PAOyZxHBA9IXo8qj2dECXrj8Vk9YpvxstO', 'Renata', 'Nowicka', 'F', 'r.nowicka@example.com', '+48901234567', 60.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (100, 'adobosz', '$2a$10$o9OcOIN.1bmooaFQJ.1PAOyZxHBA9IXo8qj2dECXrj8Vk9YpvxstO', 'Adam', 'Dobosz', 'M', 'a.dobosz@example.com', '+48432109876', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO user_role (user_id, role_id)
VALUES
    (10000, 1),
    (10001, 1),
    (10002, 1),
    (10003, 1),
    (1000, 2),
    (1001, 2),
    (1002, 2),
    (1003, 2),
    (1004, 2),
    (100, 3);

INSERT INTO subject (subject_name)
VALUES
    ('Matematyka'),
    ('Fizyka'),
    ('Angielski'),
    ('Niemiecki'),
    ('Biologia'),
    ('Chemia');

INSERT INTO teacher_subject (teacher_id, subject_id)
VALUES
    (1000, 1),
    (1000, 2),
    (1001, 3),
    (1001, 4),
    (1002, 6),
    (1003, 1),
    (1004, 5);

INSERT INTO room (room_id, building, floor, room_number)
VALUES
    (1, 'Budynek A - ul. Nadrzeczna 21', 1, 101),
    (2, 'Budynek A - ul. Nadrzeczna 21', 1, 102),
    (3, 'Budynek A - ul. Nadrzeczna 21', 2, 201),
    (4, 'Budynek A - ul. Nadrzeczna 21', 2, 202),
    (5, 'Budynek A - ul. Nadrzeczna 21', 3, 301),
    (6, 'Budynek B - ul. Poznańska 77', 1, 11),
    (7, 'Budynek B - ul. Poznańska 77', 1, 12),
    (8, 'Budynek B - ul. Poznańska 77', 2, 21),
    (9, 'Budynek B - ul. Poznańska 77', 1, 101),
    (10, 'Budynek B - ul. Poznańska 77', 2, 201);

INSERT INTO room_reservation (reservation_id, room_id, teacher_id, reservation_from, reservation_to)
VALUES
    (99990, 1, 1000, '2024-11-27 08:00:00', '2024-11-27 10:00:00'),
    (99991, 2, 1001, '2024-11-27 09:00:00', '2024-11-27 11:00:00'),
    (99992, 3, 1002, '2024-11-28 10:00:00', '2024-11-28 12:00:00'),
    (99993, 4, 1003, '2024-11-28 11:00:00', '2024-11-28 13:00:00'),
    (99994, 5, 1000, '2024-11-29 14:00:00', '2024-11-29 16:00:00'),
    (99995, 6, 1001, '2024-11-29 15:00:00', '2024-11-29 17:00:00'),
    (99996, 7, 1002, '2024-11-30 09:00:00', '2024-11-30 11:00:00'),
    (99997, 8, 1003, '2024-11-30 10:00:00', '2024-11-30 12:00:00'),
    (99998, 9, 1004, '2024-12-01 08:00:00', '2024-12-01 10:00:00'),
    (99999, 10, 1000, '2024-12-01 09:00:00', '2024-12-01 11:00:00');






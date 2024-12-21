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

--changeset Przemyslaw:2 labels:init,data
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

INSERT INTO room_reservation (room_id, teacher_id, reservation_from, reservation_to)
VALUES
    (1, 1000, '2024-11-27 08:00:00', '2024-11-27 10:00:00'),
    (2, 1001, '2024-11-27 09:00:00', '2024-11-27 11:00:00'),
    (3, 1002, '2024-11-28 10:00:00', '2024-11-28 12:00:00'),
    (4, 1003, '2024-11-28 11:00:00', '2024-11-28 13:00:00'),
    (5, 1000, '2024-11-29 14:00:00', '2024-11-29 16:00:00'),
    (6, 1001, '2024-11-29 15:00:00', '2024-11-29 17:00:00'),
    (7, 1002, '2024-11-30 09:00:00', '2024-11-30 11:00:00'),
    (8, 1003, '2024-11-30 10:00:00', '2024-11-30 12:00:00'),
    (9, 1004, '2024-12-01 08:00:00', '2024-12-01 10:00:00'),
    (10, 1000, '2024-12-01 09:00:00', '2024-12-01 11:00:00');

--changeset Przemyslaw:3 labels:init,data
INSERT INTO tutoring_class (teacher_id, subject_id, class_name, is_completed)
VALUES
    (1000, 1, 'Matematyka z Janem', false),
    (1000, 2, 'Fizyka z Janem', false),
    (1000, 1, 'Matematyka gr.1', false),
    (1001, 3, 'Ang z Janem', false);


INSERT INTO course (course_name, price, max_participants, start_date, is_open_for_registration, tutoring_class_id, is_done)
VALUES
    ('Kurs Matematyki dla Początkujących', 499.99, 20, '2025-01-15', TRUE, NULL, FALSE),
    ('Zaawansowany Kurs Języka Angielskiego', 799.00, 15, '2025-02-01', TRUE, NULL, FALSE),
    ('Podstawy Programowania w Pythonie', 599.50, 25, '2025-03-10', TRUE, NULL, FALSE),
    ('Warsztaty z Fotografii Cyfrowej', 349.99, 10, '2024-01-20', FALSE, NULL, TRUE),
    ('Kurs Gotowania dla Początkujących', 299.00, 12, '2025-02-15', TRUE, NULL, FALSE),
    ('Zaawansowane Techniki Marketingowe', 899.99, 30, '2025-04-05', FALSE, NULL, FALSE),
    ('Podstawy Projektowania Graficznego', 649.50, 18, '2025-03-22', TRUE, NULL, FALSE),
    ('Warsztaty z Kreatywnego Pisania', 399.00, 20, '2025-01-30', FALSE, NULL, TRUE),
    ('Kurs Języka Niemieckiego dla Początkujących', 499.99, 25, '2025-02-28', TRUE, NULL, FALSE);

INSERT INTO student_course(student_id, course_id)
VALUES
    (10000, 1),
    (10001, 1),
    (10002, 1),
    (10000, 2),
    (10001, 2),
    (10000, 4);

--changeset Przemyslaw:4 labels:init,data
INSERT INTO request (student_id, subject_id, repeat_until, prefers_individual, prefers_location, issue_date, acceptance_date, teacher_id, class_id)
VALUES
    (10002, 1, '2025-06-23', true, 'ONLINE', '2024-12-04', null, null, null),
    (10002, 2, '2025-05-22', true, 'ONLINE', '2024-12-04', null, null, null),
    (10002, 3, '2025-06-19', false, 'INDIVIDUAL', '2024-12-04', null, null, null),
    (10003, 1, '2025-01-22', true, 'NONE', '2024-12-04', null, null, null),
    (10000, 4, '2025-06-24', true, 'NONE', '2024-12-04', null, null, null),
    (10000, 6, null, true, 'NONE', '2024-12-04', null, null, null),
    (10001, 5, '2025-03-27', true, 'ONLINE', '2024-12-04', null, null, null),
    (10001, 6, null, false, 'NONE', '2024-12-04', null, null, null),
    (10000, 2, null, false, 'ONLINE', '2024-12-04', null, null, null),
    (10002, 6, null, false, 'NONE', '2024-12-04', null, null, null),
    (10000, 1, '2025-06-24', true, 'ONLINE', '2024-12-04', null, null, null);

INSERT INTO student_class (student_id, class_id)
VALUES
    (10001, 3),
    (10000, 1),
    (10000, 2),
    (10000, 4);

INSERT INTO class_schedule (class_id, room_id, class_date_from, class_date_to, is_online, is_completed, is_canceled)
VALUES
    (1, null, '2024-12-06 11:00:00.000', '2024-12-06 12:00:00.000', true, false, false),
    (1, null, '2024-12-13 11:00:00.000', '2024-12-13 12:00:00.000', true, false, false),
    (1, null, '2024-12-20 11:00:00.000', '2024-12-20 12:00:00.000', true, false, false),
    (1, null, '2024-12-27 11:00:00.000', '2024-12-27 12:00:00.000', true, false, false),
    (1, null, '2025-01-03 11:00:00.000', '2025-01-03 12:00:00.000', true, false, false),
    (1, null, '2025-01-10 11:00:00.000', '2025-01-10 12:00:00.000', true, false, false),
    (1, null, '2025-01-17 11:00:00.000', '2025-01-17 12:00:00.000', true, false, false),
    (1, null, '2025-01-24 11:00:00.000', '2025-01-24 12:00:00.000', true, false, false),
    (1, null, '2025-01-31 11:00:00.000', '2025-01-31 12:00:00.000', true, false, false),
    (1, null, '2025-02-07 11:00:00.000', '2025-02-07 12:00:00.000', true, false, false),
    (1, null, '2025-02-14 11:00:00.000', '2025-02-14 12:00:00.000', true, false, false),
    (1, null, '2025-02-21 11:00:00.000', '2025-02-21 12:00:00.000', true, false, false),
    (1, null, '2025-02-28 11:00:00.000', '2025-02-28 12:00:00.000', true, false, false),
    (1, null, '2025-03-07 11:00:00.000', '2025-03-07 12:00:00.000', true, false, false),
    (1, null, '2025-03-14 11:00:00.000', '2025-03-14 12:00:00.000', true, false, false),
    (1, null, '2025-03-21 11:00:00.000', '2025-03-21 12:00:00.000', true, false, false),
    (1, null, '2025-03-28 11:00:00.000', '2025-03-28 12:00:00.000', true, false, false),
    (1, null, '2025-04-04 11:00:00.000', '2025-04-04 12:00:00.000', true, false, false),
    (1, null, '2025-04-11 11:00:00.000', '2025-04-11 12:00:00.000', true, false, false),
    (1, null, '2025-04-18 11:00:00.000', '2025-04-18 12:00:00.000', true, false, false),
    (1, null, '2025-04-25 11:00:00.000', '2025-04-25 12:00:00.000', true, false, false),
    (1, null, '2025-05-02 11:00:00.000', '2025-05-02 12:00:00.000', true, false, false),
    (1, null, '2025-05-09 11:00:00.000', '2025-05-09 12:00:00.000', true, false, false),
    (1, null, '2025-05-16 11:00:00.000', '2025-05-16 12:00:00.000', true, false, false),
    (1, null, '2025-05-23 11:00:00.000', '2025-05-23 12:00:00.000', true, false, false),
    (1, null, '2025-05-30 11:00:00.000', '2025-05-30 12:00:00.000', true, false, false),
    (1, null, '2025-06-06 11:00:00.000', '2025-06-06 12:00:00.000', true, false, false),
    (1, null, '2025-06-13 11:00:00.000', '2025-06-13 12:00:00.000', true, false, false),
    (1, null, '2025-06-20 11:00:00.000', '2025-06-20 12:00:00.000', true, false, false),
    (2, null, '2024-12-06 12:15:00.000', '2024-12-06 13:15:00.000', true, false, false),
    (2, null, '2024-12-13 12:15:00.000', '2024-12-13 13:15:00.000', true, false, false),
    (2, null, '2024-12-20 12:15:00.000', '2024-12-20 13:15:00.000', true, false, false),
    (2, null, '2024-12-27 12:15:00.000', '2024-12-27 13:15:00.000', true, false, false),
    (2, null, '2025-01-03 12:15:00.000', '2025-01-03 13:15:00.000', true, false, false),
    (2, null, '2025-01-10 12:15:00.000', '2025-01-10 13:15:00.000', true, false, false),
    (2, null, '2025-01-17 12:15:00.000', '2025-01-17 13:15:00.000', true, false, false),
    (2, null, '2025-01-24 12:15:00.000', '2025-01-24 13:15:00.000', true, false, false),
    (2, null, '2025-01-31 12:15:00.000', '2025-01-31 13:15:00.000', true, false, false),
    (2, null, '2025-02-07 12:15:00.000', '2025-02-07 13:15:00.000', true, false, false),
    (2, null, '2025-02-14 12:15:00.000', '2025-02-14 13:15:00.000', true, false, false),
    (2, null, '2025-02-21 12:15:00.000', '2025-02-21 13:15:00.000', true, false, false),
    (2, null, '2025-02-28 12:15:00.000', '2025-02-28 13:15:00.000', true, false, false),
    (2, null, '2025-03-07 12:15:00.000', '2025-03-07 13:15:00.000', true, false, false),
    (2, null, '2025-03-14 12:15:00.000', '2025-03-14 13:15:00.000', true, false, false),
    (2, null, '2025-03-21 12:15:00.000', '2025-03-21 13:15:00.000', true, false, false),
    (2, null, '2025-03-28 12:15:00.000', '2025-03-28 13:15:00.000', true, false, false),
    (2, null, '2025-04-04 12:15:00.000', '2025-04-04 13:15:00.000', true, false, false),
    (2, null, '2025-04-11 12:15:00.000', '2025-04-11 13:15:00.000', true, false, false),
    (2, null, '2025-04-18 12:15:00.000', '2025-04-18 13:15:00.000', true, false, false),
    (2, null, '2025-04-25 12:15:00.000', '2025-04-25 13:15:00.000', true, false, false),
    (2, null, '2025-05-02 12:15:00.000', '2025-05-02 13:15:00.000', true, false, false),
    (2, null, '2025-05-09 12:15:00.000', '2025-05-09 13:15:00.000', true, false, false),
    (2, null, '2025-05-16 12:15:00.000', '2025-05-16 13:15:00.000', true, false, false),
    (3, null, '2024-12-09 18:00:00.000', '2024-12-09 20:00:00.000', false, false, false),
    (3, null, '2024-12-16 18:00:00.000', '2024-12-16 20:00:00.000', false, false, false),
    (3, null, '2024-12-23 18:00:00.000', '2024-12-23 20:00:00.000', false, false, false),
    (3, null, '2024-12-30 18:00:00.000', '2024-12-30 20:00:00.000', false, false, false),
    (3, null, '2025-01-06 18:00:00.000', '2025-01-06 20:00:00.000', false, false, false),
    (3, null, '2025-01-13 18:00:00.000', '2025-01-13 20:00:00.000', false, false, false),
    (3, null, '2025-01-20 18:00:00.000', '2025-01-20 20:00:00.000', false, false, false),
    (4, null, '2024-12-10 15:30:00.000', '2024-12-10 17:30:00.000', true, false, false),
    (4, null, '2024-12-17 15:30:00.000', '2024-12-17 17:30:00.000', true, false, false),
    (4, null, '2024-12-24 15:30:00.000', '2024-12-24 17:30:00.000', true, false, false),
    (4, null, '2024-12-31 15:30:00.000', '2024-12-31 17:30:00.000', true, false, false),
    (4, null, '2025-01-07 15:30:00.000', '2025-01-07 17:30:00.000', true, false, false),
    (4, null, '2025-01-14 15:30:00.000', '2025-01-14 17:30:00.000', true, false, false),
    (4, null, '2025-01-21 15:30:00.000', '2025-01-21 17:30:00.000', true, false, false),
    (4, null, '2025-01-28 15:30:00.000', '2025-01-28 17:30:00.000', true, false, false),
    (4, null, '2025-02-04 15:30:00.000', '2025-02-04 17:30:00.000', true, false, false),
    (4, null, '2025-02-11 15:30:00.000', '2025-02-11 17:30:00.000', true, false, false),
    (4, null, '2025-02-18 15:30:00.000', '2025-02-18 17:30:00.000', true, false, false),
    (4, null, '2025-02-25 15:30:00.000', '2025-02-25 17:30:00.000', true, false, false),
    (4, null, '2025-03-04 15:30:00.000', '2025-03-04 17:30:00.000', true, false, false),
    (4, null, '2025-03-11 15:30:00.000', '2025-03-11 17:30:00.000', true, false, false),
    (4, null, '2025-03-18 15:30:00.000', '2025-03-18 17:30:00.000', true, false, false),
    (4, null, '2025-03-25 15:30:00.000', '2025-03-25 17:30:00.000', true, false, false),
    (4, null, '2025-04-01 15:30:00.000', '2025-04-01 17:30:00.000', true, false, false),
    (4, null, '2025-04-08 15:30:00.000', '2025-04-08 17:30:00.000', true, false, false),
    (4, null, '2025-04-15 15:30:00.000', '2025-04-15 17:30:00.000', true, false, false),
    (4, null, '2025-04-22 15:30:00.000', '2025-04-22 17:30:00.000', true, false, false),
    (4, null, '2025-04-29 15:30:00.000', '2025-04-29 17:30:00.000', true, false, false),
    (4, null, '2025-05-06 15:30:00.000', '2025-05-06 17:30:00.000', true, false, false),
    (4, null, '2025-05-13 15:30:00.000', '2025-05-13 17:30:00.000', true, false, false),
    (4, null, '2025-05-20 15:30:00.000', '2025-05-20 17:30:00.000', true, false, false),
    (4, null, '2025-05-27 15:30:00.000', '2025-05-27 17:30:00.000', true, false, false),
    (4, null, '2025-06-03 15:30:00.000', '2025-06-03 17:30:00.000', true, false, false),
    (4, null, '2025-06-10 15:30:00.000', '2025-06-10 17:30:00.000', true, false, false),
    (4, null, '2025-06-17 15:30:00.000', '2025-06-17 17:30:00.000', true, false, false);

INSERT INTO attendance (attendance_id, class_schedule_id, student_id, is_present)
VALUES
    (1, 1, 10000, true),
    (2, 30, 10000, false),
    (3, 2, 10000, true),
    (4, 31, 10000, true),
    (5, 3, 10000, false),
    (6, 32, 10000, true),
    (7, 4, 10000, true),
    (8, 33, 10000, true);
-- liquibase formatted sql

--changeset Przemyslaw:1 labels:init,tables
--Cennik
CREATE TABLE price_list
(
    price_list_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    creation_date DATE NOT NULL,
    start_date    DATE NOT NULL
);

--Przedmiot
CREATE TABLE subject
(
    subject_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    subject_name VARCHAR(50) UNIQUE NOT NULL
);

-- Role
CREATE TABLE role
(
    role_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
);

-- Uzytkownik
CREATE TABLE website_user
(
--Uzytkownik
    user_id             BIGINT PRIMARY KEY,
    username            VARCHAR(50) UNIQUE  NOT NULL,
    password            VARCHAR(255)        NOT NULL,
--Kierownik
    first_name          VARCHAR(50)         NOT NULL,
    last_name           VARCHAR(50)         NOT NULL,
    gender              CHAR(1)             NOT NULL,
    email               VARCHAR(200) UNIQUE NOT NULL,
    phone               VARCHAR(20) UNIQUE  NOT NULL,
--Nauczyciel
    hourly_rate         MONEY,
--Uczen
    level_id            VARCHAR(50),
    guardian_phone      VARCHAR(20) UNIQUE,
    guardian_email      VARCHAR(200) UNIQUE,
    price_list_id       BIGINT REFERENCES price_list (price_list_id),
    discount_percentage INT,
    is_cash_payment     BOOLEAN,
    issue_invoice       BOOLEAN
);

-- Sale
CREATE TABLE room
(
    room_id     BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    building    VARCHAR(50) UNIQUE NOT NULL,
    floor       INT                NOT NULL,
    room_number INT                NOT NULL
);

--Zajecia
CREATE TABLE tutoring_class
(
    class_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    teacher_id BIGINT       NOT NULL REFERENCES website_user (user_id),
    subject_id BIGINT       NOT NULL REFERENCES subject (subject_id),
    class_name VARCHAR(300) NOT NULL
);

--Kurs
CREATE TABLE course
(
    course_id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    course_name      VARCHAR(100) NOT NULL,
    price            MONEY        NOT NULL,
    max_participants INT          NOT NULL
);

--Dni zajete
CREATE TABLE user_busy_day
(
    user_busy_day_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    day_of_the_week  VARCHAR(20) NOT NULL,
    time_from        TIME(6)     NOT NULL,
    time_to          TIME(6)     NOT NULL
);

--Dostepnosc
CREATE TABLE user_availability
(
    user_availability_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id              BIGINT NOT NULL REFERENCES website_user (user_id),
    user_busy_day_id     BIGINT NOT NULL REFERENCES user_busy_day (user_busy_day_id)
);

--Przedmiot cennik
CREATE TABLE subject_price_list
(
    subject_price_list_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    price_level           INT    NOT NULL,
    payment_amount        MONEY  NOT NULL,
    teacher_salary        MONEY  NOT NULL,
    price_list_id         BIGINT NOT NULL REFERENCES price_list (price_list_id),
    subject_id            BIGINT NOT NULL REFERENCES subject (subject_id)
);

--Faktura
CREATE TABLE invoice
(
    invoice_number   BIGINT PRIMARY KEY,
    first_name       VARCHAR(50)  NOT NULL,
    last_name        VARCHAR(50)  NOT NULL,
    company_name     VARCHAR(200),
    nip              VARCHAR(13),
    street           VARCHAR(50)  NOT NULL,
    house_number     VARCHAR(10)  NOT NULL,
    apartment_number VARCHAR(10)  NOT NULL,
    city             VARCHAR(100) NOT NULL,
    postal_code      CHAR(6)      NOT NULL,
    issue_date       DATE         NOT NULL,
    issued_by        BIGINT REFERENCES website_user (user_id)
);

--NauczycielPrzedmiot
CREATE TABLE teacher_subject
(
    teacher_subject_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    teacher_id         BIGINT NOT NULL REFERENCES website_user (user_id),
    subject_id         BIGINT NOT NULL REFERENCES subject (subject_id)
);

--UczenKurs
CREATE TABLE student_course
(
    student_course_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    student_id        BIGINT NOT NULL REFERENCES website_user (user_id),
    course_id         BIGINT NOT NULL REFERENCES course (course_id)
);

--UczenZajecia
CREATE TABLE student_class
(
    student_class_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    student_id       BIGINT NOT NULL REFERENCES website_user (user_id),
    class_id         BIGINT NOT NULL REFERENCES tutoring_class (class_id)
);

--Termin zajec
CREATE TABLE class_schedule
(
    class_schedule_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    class_id          BIGINT       NOT NULL REFERENCES tutoring_class (class_id),
    room_id           BIGINT REFERENCES room (room_id),
    class_date_from   TIMESTAMP(3) NOT NULL,
    class_date_to     TIMESTAMP(3) NOT NULL,
    is_online         BOOLEAN      NOT NULL,
    is_completed      BOOLEAN      NOT NULL
);

--Obecnosc na zajeciach
CREATE TABLE attendance
(
    attendance_id     BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    class_schedule_id BIGINT  NOT NULL REFERENCES class_schedule (class_schedule_id),
    student_id        BIGINT  NOT NULL REFERENCES website_user (user_id),
    is_present        BOOLEAN NOT NULL
);

--Naleznosc za zajecia
CREATE TABLE payment_for_class
(
    payment_id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    student_id        BIGINT  NOT NULL REFERENCES website_user (user_id),
    class_schedule_id BIGINT  NOT NULL REFERENCES class_schedule (class_schedule_id),
    amount            MONEY   NOT NULL,
    is_paid           BOOLEAN NOT NULL
);

--Prosby
CREATE TABLE request
(
    request_id      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    student_id      BIGINT NOT NULL REFERENCES website_user (user_id),
    subject_id      BIGINT NOT NULL REFERENCES subject (subject_id),
    repeat_until    DATE,
    issue_date      DATE   NOT NULL,
    acceptance_date DATE,
    teacher_id      BIGINT REFERENCES website_user (user_id),
    class_id        BIGINT REFERENCES tutoring_class(class_id),
    CONSTRAINT  check_repeat_until CHECK ( repeat_until > CURRENT_DATE )
);

--Rejestr zmian w terminach
CREATE TABLE schedule_changes_log
(
    change_id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    class_schedule_id BIGINT       NOT NULL REFERENCES class_schedule (class_schedule_id),
    user_id           BIGINT       NOT NULL REFERENCES website_user (user_id),
    reason            VARCHAR(100) NOT NULL,
    explanation       TEXT         NOT NULL
);

-- Rezerwacje sal
CREATE TABLE room_reservation
(
    reservation_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    room_id          BIGINT       NOT NULL REFERENCES room (room_id),
    teacher_id       BIGINT       NOT NULL REFERENCES website_user (user_id),
    reservation_from TIMESTAMP(3) NOT NULL,
    reservation_to   TIMESTAMP(3) NOT NULL
);

--UzytkownikRola
CREATE TABLE user_role
(
    user_role_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES website_user (user_id),
    role_id      BIGINT NOT NULL REFERENCES role (role_id)
);
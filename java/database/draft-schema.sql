BEGIN;

DROP TABLE IF EXISTS users, class_details, client_info, teacher_details, class_attendance CASCADE;     --CASCADE for serialized foreign keys?

CREATE TABLE users (
	user_id SERIAL,
	username varchar(50) NOT NULL UNIQUE,
	password_hash varchar(200) NOT NULL,
	role varchar(50) NOT NULL,
	CONSTRAINT PK_user PRIMARY KEY (user_id)
);

CREATE TABLE teacher_details
(
    teacher_id        serial      NOT NULL,
    last_name         varchar(30) NOT NULL,
    first_name        varchar(30) NOT NULL,
    is_teacher_active boolean     NOT NULL,
	user_id			  int		  NOT NULL,
	CONSTRAINT PK_teacher_id PRIMARY KEY (teacher_id),
	CONSTRAINT FK_teacher_id_user_id FOREIGN KEY (user_id) REFERENCES user_id(users)
);

CREATE TABLE class_details
(
    class_id          serial    NOT NULL,
    teacher_id        int       NOT NULL,
    class_datetime    timestamp NOT NULL,
    class_duration    int      NOT NULL,
    is_paid           boolean   NOT NULL,  --??
    class_description text      NOT NULL,
    CONSTRAINT PK_class_details PRIMARY KEY (class_id),
    CONSTRAINT FK_teacher_id FOREIGN KEY (teacher_id) REFERENCES teacher_details (teacher_id)

);

CREATE TABLE client_info
(
    client_id               serial      NOT NULL,
    last_name               varchar(30) NOT NULL,
    first_name              varchar(30) NOT NULL,
    is_client_active        boolean     NOT NULL,
    is_constant_contact     boolean     NOT NULL,
    street_address          varchar(50) NOT NULL,
    city                    varchar(30) NOT NULL,
    state_abbreviation      varchar(2)  NOT NULL,
    zip_code                varchar(12) NOT NULL,
    phone_number            varchar(15) NOT NULL,
    is_on_email_list        boolean     NOT NULL,
    email                   varchar(30) NOT NULL,
    has_record_of_liability boolean     NOT NULL,
    date_of_entry           timestamp,
	user_id			  int		  NOT NULL,
    CONSTRAINT PK_client_id PRIMARY KEY (client_id),
	CONSTRAINT FK_client_id_user_id FOREIGN KEY (user_id) REFERENCES user_id(users)
);



CREATE TABLE class_attendance
(
    class_attendance_id serial         NOT NULL,
    class_id            int            NOT NULL,
    client_id           int            NOT NULL,
    package_id          int            NOT NULL,
    purchase_id         int            NOT NULL,
    is_new_client       boolean        NOT NULL,
    is_drop_in          boolean        NOT NULL,
    drop_in_fee         decimal(13, 2) NOT NULL,
    mat_use_fee         decimal(13, 2) NOT NULL,
    is_guest            boolean,
    attendance_count    int,
    is_private          boolean, -- what is this field for?
    is_unlimited        boolean, -- what is this field for?
    CONSTRAINT PK_class_attendance PRIMARY KEY (class_attendance_id),
    CONSTRAINT FK_class_attendance_id FOREIGN KEY (class_id) REFERENCES class_details (class_id),
    CONSTRAINT FK_class_attendance_client_id FOREIGN KEY (client_id) REFERENCES client_info (client_id)
--     CONSTRAINT FK_package_id FOREIGN KEY (package_id) REFERENCES ???

);

COMMIT;
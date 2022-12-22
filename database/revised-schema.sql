BEGIN TRANSACTION;

DROP TABLE IF EXISTS users,teacher_details,class_details,client_info,client_class,package_purchase,package_details CASCADE;

CREATE TABLE users 
(
	user_id 					serial		  NOT NULL,
	username 					varchar(50)   NOT NULL UNIQUE,
	password_hash 				varchar(200)  NOT NULL,
	role 						varchar(50)   NOT NULL,
	CONSTRAINT PK_user PRIMARY KEY (user_id)
);

CREATE TABLE teacher_details
(
    teacher_id        serial      NOT NULL,
    last_name         varchar(30) NOT NULL,
    first_name        varchar(30) NOT NULL,
    is_teacher_active boolean     NOT NULL,
	user_id 		  int 		  NOT NULL,
	CONSTRAINT PK_teacher_id PRIMARY KEY (teacher_id),
	CONSTRAINT FK_teacher_id_user_id FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE class_details
(
    class_id          serial    NOT NULL,
    teacher_id        int       NOT NULL,
    class_datetime    timestamp NOT NULL,
    class_duration    int       NOT NULL,
    is_paid           boolean   NOT NULL,           -- why this field? (Whether or not you have to pay to take the class)
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
    is_constant_contact     boolean     NOT NULL,    -- do we need this?
	is_new_client			boolean		NOT NULL,
    street_address          varchar(50) NOT NULL,
    city                    varchar(30) NOT NULL,
    state_abbreviation      varchar(2)  NOT NULL,
    zip_code                varchar(12) NOT NULL,
    phone_number            varchar(15) NOT NULL,
    is_on_email_list        boolean     NOT NULL,
    email                   varchar(30) NOT NULL,
    has_record_of_liability boolean     NOT NULL,
    date_of_entry           timestamp   NOT NULL,
	user_id 				int 		NOT NULL,
    CONSTRAINT PK_client_id PRIMARY KEY (client_id),
	CONSTRAINT FK_client_id_user_id FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE client_class
(
	client_id			int				NOT NULL,
	class_id			int				NOT NULL,
	CONSTRAINT PK_client_class PRIMARY KEY (client_id,class_id),
	CONSTRAINT FK_client_class_client_id FOREIGN KEY (client_id) REFERENCES client_info(client_id),
	CONSTRAINT FK_client_class_class_id FOREIGN KEY (class_id) REFERENCES class_details(class_id)
);


CREATE TABLE package_details
(
	package_id			serial			NOT NULL,  
	description			text			NOT NULL,
	package_cost		decimal(13, 2)	NOT NULL,
	activation_date		date, 
	expiration_date		date,
	classes_remaining	int,
--	cost_per_class		decimal(13, 2),				-- necessary?
	CONSTRAINT PK_package_details PRIMARY KEY (package_id)
);

CREATE TABLE package_purchase
(
	package_purchase_id serial	  		NOT NULL,
	client_id			int 	  		NOT NULL,
	package_id			int				NOT NULL,
	date_purchased		timestamp 		NOT NULL,
	is_expired			boolean   		NOT NULL,
	is_monthly_renew	boolean,					
--	total_amount_paid	decimal(13, 2)  NOT NULL,	-- why necessary if it's just the cost of the package
--	cost_per_class		decimal(13, 2),  		 	-- necessary?
	CONSTRAINT PK_package_purchase PRIMARY KEY (package_purchase_id),
	CONSTRAINT FK_package_purchase_client_id FOREIGN KEY (client_id) REFERENCES client_info (client_id),
	CONSTRAINT FK_package_purchase_package_id FOREIGN KEY (package_id) REFERENCES package_details (package_id)
);

-- --  Can we drop this table?
-- CREATE TABLE class_attendance
-- (
-- 	class_purchase_id   int            NOT NULL,  -- FK
--     is_drop_in          boolean        NOT NULL,  -- 
-- 	is_guest            boolean,				  -- drop in and guests too?
--     drop_in_fee         decimal(13, 2),			  -- can be null?
--     mat_use_fee         decimal(13, 2), 		  -- can be null?
-- --    attendance_count    int,					  -- what is this for?
--     CONSTRAINT PK_class_attendance PRIMARY KEY (class_attendance_id),
--     CONSTRAINT FK_class_attendance_id FOREIGN KEY (class_id) REFERENCES class_details (class_id),
--     CONSTRAINT FK_class_attendance_client_id FOREIGN KEY (client_id) REFERENCES client_info (client_id),
-- 	CONSTRAINT FK_class_attendance_class_purchase_id FOREIGN KEY (class_purchase_id) REFERENCES class_purchase (class_purchase_id),
-- 	CONSTRAINT FK_class_attendance_package_id FOREIGN KEY (package_id) REFERENCES package_details (package_id)
-- );


COMMIT TRANSACTION;
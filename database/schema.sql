BEGIN TRANSACTION;

DROP TABLE IF EXISTS users,teacher_details,class_details,client_details,client_class,package_purchase,package_details,events,client_event,client_family,families,website_descriptions, gift_card CASCADE;


CREATE TABLE users 
(
	user_id 					serial		  NOT NULL,
	username 					varchar(50)   NOT NULL UNIQUE,
	password_hash 				varchar(200)  NOT NULL,
	role 						varchar(50)   NOT NULL,
	activated                   boolean ,
	CONSTRAINT PK_user PRIMARY KEY (user_id)
);

CREATE TABLE teacher_details
(
    teacher_id        serial      NOT NULL,
    last_name         varchar(30) NOT NULL,
    first_name        varchar(30) NOT NULL, 
    is_teacher_active boolean     NOT NULL,
	CONSTRAINT PK_teacher_id PRIMARY KEY (teacher_id)
);

CREATE TABLE class_details
(
    class_id          serial    NOT NULL,
    teacher_id        int       NOT NULL,
	is_repeating	  boolean   NOT NULL,
	start_time		  text		NOT NULL,
	date_range		  text[],	
    class_duration    int       NOT NULL,
    is_paid           boolean,           
    class_description text      NOT NULL,
    CONSTRAINT PK_class_details PRIMARY KEY (class_id),
    CONSTRAINT FK_teacher_id FOREIGN KEY (teacher_id) REFERENCES teacher_details (teacher_id)

);

CREATE TABLE client_details
(
    client_id               serial      NOT NULL,
    last_name               varchar(30) NOT NULL,
    first_name              varchar(30) NOT NULL,
    is_client_active        boolean     NOT NULL,
	is_new_client			boolean		NOT NULL,
    street_address          varchar(50) ,
    city                    varchar(30) ,
    state_abbreviation      varchar(2)  ,
    zip_code                varchar(12) ,
    phone_number            varchar(15) ,
    is_on_email_list        boolean     ,
    email                   varchar(30) UNIQUE,
    has_record_of_liability boolean     ,
    date_of_entry           timestamp   ,
	is_allowed_video		boolean		NOT NULL,
	user_id 				int 		,
	customer_id				varchar(50)	,
    CONSTRAINT PK_client_id PRIMARY KEY (client_id),
	CONSTRAINT FK_client_id_user_id FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE client_class
(
	client_id			int				NOT NULL,
	class_id			int				NOT NULL,
	CONSTRAINT PK_client_class PRIMARY KEY (client_id,class_id),
	CONSTRAINT FK_client_class_client_id FOREIGN KEY (client_id) REFERENCES client_details(client_id),
	CONSTRAINT FK_client_class_class_id FOREIGN KEY (class_id) REFERENCES class_details(class_id)
);

CREATE TABLE package_details
(
	package_id			serial			NOT NULL,  
	description			text			NOT NULL,
	package_cost		decimal(13, 2)	NOT NULL,
	classes_amount		int,
	subscription_duration	int,
	is_subscription		boolean 		NOT NULL, 
	is_visible_online	boolean			NOT NULL,
	is_recurring 		boolean			NOT NULL,	
	CONSTRAINT PK_package_details PRIMARY KEY (package_id)
);

CREATE TABLE package_purchase
(
	package_purchase_id serial	  		NOT NULL,
	client_id			int 	  		NOT NULL,
	package_id			int				NOT NULL,
	date_purchased		timestamp 		NOT NULL,
	classes_remaining	int,
	activation_date		date, 
	expiration_date		date,
	is_monthly_renew	boolean,					
	total_amount_paid	decimal(13, 2),
	discount			decimal(13, 2),
	paymentId			varchar(30),
	CONSTRAINT PK_package_purchase PRIMARY KEY (package_purchase_id),
	CONSTRAINT FK_package_purchase_client_id FOREIGN KEY (client_id) REFERENCES client_details (client_id),
	CONSTRAINT FK_package_purchase_package_id FOREIGN KEY (package_id) REFERENCES package_details (package_id)
);

CREATE TABLE events (
	event_id serial NOT NULL,
	class_id int,
	event_name varchar(30) NOT NULL,
	start_time timestamp NOT NULL,
	end_time timestamp NOT NULL,
	color varchar(10) NOT NULL,
	timed boolean NOT NULL,
	is_paid boolean,
	is_visible_online boolean,
	CONSTRAINT PK_event PRIMARY KEY (event_id)
-- 	CONSTRAINT FK_event_class_id FOREIGN KEY (class_id) REFERENCES class_details (class_id)
);

CREATE TABLE client_event (
	client_id int NOT NULL,
	event_id int NOT NULL,
	package_purchase_id int,
	CONSTRAINT PK_client_event PRIMARY KEY (client_id, event_id),
	CONSTRAINT FK_client_event_client_id FOREIGN KEY (client_id) REFERENCES client_details (client_id),
	CONSTRAINT FK_client_event_event_id FOREIGN KEY (event_id) REFERENCES events(event_id)
	--CONSTRAINT FK_client_event_package_purchase_id FOREIGN KEY (package_purchase_id) REFERENCES package_purchase(package_purchase_id)
);

CREATE TABLE families (
	family_id serial NOT NULL,
	family_name varchar(30) NOT NULL,
	CONSTRAINT PK_family PRIMARY KEY (family_id)
);

CREATE TABLE client_family(
	client_id int NOT NULL,
	family_id int NOT NULL,
	CONSTRAINT PK_client_family PRIMARY KEY (client_id,family_id),
	CONSTRAINT FK_client_family_client_id FOREIGN KEY (client_id) REFERENCES client_details (client_id),
	CONSTRAINT FK_client_family_family_id FOREIGN KEY (family_id) REFERENCES families (family_id)
);

CREATE TABLE website_descriptions (
	webdescription_id serial NOT NULL,
	location_name varchar(30) NOT NULL,
	description text NOT NULL,
	CONSTRAINT PK_website_descriptions PRIMARY KEY (webdescription_id)
);

CREATE TABLE gift_card (
	code VARCHAR(7) NOT NULL,
	amount decimal(13, 2),
	client_id int,
	CONSTRAINT PK_gift_card PRIMARY KEY (code),
	CONSTRAINT FK_client_id_client_id FOREIGN KEY (client_id) REFERENCES client_details(client_id)
);
	

COMMIT TRANSACTION;


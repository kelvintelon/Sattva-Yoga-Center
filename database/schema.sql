BEGIN TRANSACTION;

DROP TABLE IF EXISTS users,teacher_details,class_details,client_details,client_class,package_purchase,package_details,sales,transactions,events,client_event,client_family,families,website_descriptions, gift_card CASCADE;


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
    last_name         varchar(30) ,
    first_name        varchar(30) NOT NULL, 
    is_teacher_active boolean     NOT NULL,
	CONSTRAINT PK_teacher_id PRIMARY KEY (teacher_id)
);

CREATE SEQUENCE teacher_details_id_seq START 200001 OWNED BY teacher_details.teacher_id;

ALTER TABLE teacher_details ALTER COLUMN teacher_id SET DEFAULT nextval('teacher_details_id_seq');

CREATE TABLE class_details
(
    class_id          serial    NOT NULL,
    teacher_id        int       ,
	is_repeating	  boolean   NOT NULL,
	start_time		  text		NOT NULL,
	date_range		  text[],	
    class_duration    int       NOT NULL,
    is_paid           boolean,           
    class_description text      NOT NULL,
    CONSTRAINT PK_class_details PRIMARY KEY (class_id)
);

CREATE TABLE client_details
(
    client_id               serial      NOT NULL,
	user_id 				int 		NOT NULL,
	customer_id				varchar(50)	,
    last_name               varchar(60) NOT NULL,
    first_name              varchar(60) NOT NULL,
    is_client_active        boolean     NOT NULL,
	is_new_client			boolean		NOT NULL,
    street_address          varchar(100) ,
    city                    varchar(30) ,
    state_abbreviation      varchar(2)  ,
	country					varchar(30) ,
    zip_code                varchar(30) ,
    phone_number            varchar(30) ,
    is_on_email_list        boolean     ,
    email                   varchar(50) ,
    has_record_of_liability boolean     ,
    date_of_entry           timestamp   ,
	is_allowed_video		boolean		NOT NULL,
    CONSTRAINT PK_client_id PRIMARY KEY (client_id),
	CONSTRAINT FK_client_id_user_id FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE SEQUENCE client_details_id_seq START 200001 OWNED BY client_details.client_id;

ALTER TABLE client_details ALTER COLUMN client_id SET DEFAULT nextval('client_details_id_seq');

-- CREATE TABLE client_class
-- (
-- 	client_id			int				NOT NULL,
-- 	class_id			int				NOT NULL,
-- 	CONSTRAINT PK_client_class PRIMARY KEY (client_id,class_id),
-- 	CONSTRAINT FK_client_class_client_id FOREIGN KEY (client_id) REFERENCES client_details(client_id),
-- 	CONSTRAINT FK_client_class_class_id FOREIGN KEY (class_id) REFERENCES class_details(class_id)
-- );

CREATE TABLE package_details
(
	package_id			serial			NOT NULL,  
	description			text			NOT NULL,
	package_cost		decimal(13, 2)	NOT NULL,
	classes_amount		int,
	package_duration	int,
	unlimited			boolean 		NOT NULL, 
	is_visible_online	boolean			NOT NULL,
	is_recurring 		boolean			,
	active				boolean			,
	package_order		int				,	
	CONSTRAINT PK_package_details PRIMARY KEY (package_id)
);

CREATE SEQUENCE package_details_id_seq START 1001 OWNED BY package_details.package_id;

ALTER TABLE package_details ALTER COLUMN package_id SET DEFAULT nextval('package_details_id_seq');

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
	paymentId			text,
	CONSTRAINT PK_package_purchase PRIMARY KEY (package_purchase_id)
);

CREATE TABLE sales
(
	sale_id						serial NOT NULL,
	client_id					int NOT NULL,
	packages_purchased_array	int[],
	batch_number				int,
	CONSTRAINT PK_sale_id PRIMARY KEY (sale_id)
);

CREATE SEQUENCE sales_id_seq START 200001 OWNED BY sales.sale_id;

ALTER TABLE sales ALTER COLUMN sale_id SET DEFAULT nextval('sales_id_seq');

CREATE TABLE transactions
(
	transaction_id				serial NOT NULL,
	client_id					int NOT NULL,
	sale_id						int NOT NULL,
	payment_type				varchar(50) NOT NULL,
	payment_amount				decimal(13,2) NOT NULL,
	gift_code					varchar(13),
	CONSTRAINT PK_transaction_id PRIMARY KEY (transaction_id),
	CONSTRAINT FK_transaction_sale_id FOREIGN KEY (sale_id) REFERENCES sales(sale_id)
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
	code VARCHAR(12) NOT NULL,
	amount decimal(13, 2),
	client_id int,
	CONSTRAINT PK_gift_card PRIMARY KEY (code)
);
	

COMMIT TRANSACTION;


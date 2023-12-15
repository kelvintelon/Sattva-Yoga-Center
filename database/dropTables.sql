BEGIN TRANSACTION;

DROP TABLE IF EXISTS package_purchase,sales,transactions CASCADE;

DELETE FROM client_event;

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

COMMIT TRANSACTION;


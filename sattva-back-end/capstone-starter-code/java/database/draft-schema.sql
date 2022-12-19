CREATE TABLE class_attendance
    class_attendance_id serial PRIMARY KEY
    class_id int (foreign key)
    package_id int (foreign key?)
    purchase_id int
    client_id int (foreign key)
drop in boolean
new_client boolean
drop_in_fee numeric
mat_use_fee numeric
guest boolean
count int
private boolean
unlimited boolean

CREATE TABLE class_details
    class_id serial PRIMARY KEY
    teacher_id int (foreign key)
    date_of_class date
    class_time time
    duration int
    paid boolean
    description text

CREATE TABLE client_info
    client_id serial PRIMARY KEY
    last_name varchar(30)
    first_name varchar(30)
    is_client_active boolean
    constant_contact boolean
    street_address varchar(30)
    city varchar(30)
    state_abbreviation varchar(2)
    zip_code varchar(10)
    phone_number varchar(15)
    email_list boolean
    email varchar(30)
    record_of_liability boolean
    date_of_entry date

CREATE TABLE teacher_details
    teacher_id serial PRIMARY KEY
    last_name varchar(30)
    first_name varchar(30)
    is_teacher_active boolean



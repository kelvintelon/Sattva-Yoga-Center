BEGIN TRANSACTION;

INSERT INTO teacher_details (last_name,first_name,is_teacher_active) VALUES ('Mallur','Chuck',true);
INSERT INTO teacher_details (last_name,first_name,is_teacher_active) VALUES ('Green','Margaret',true);

INSERT INTO class_details (teacher_id, is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (1,true,'06:00 AM','{Mon,Wed,Fri}',60,true,'All Levels');
INSERT INTO class_details (teacher_id, is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (1,true,'06:00 AM','{Tue,Thu}',60,true,'Intermediate');
INSERT INTO class_details (teacher_id, is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (1,true,'08:00 AM','{Sat,Sun}',60,true,'All Levels');
INSERT INTO class_details (teacher_id, is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (1,true,'09:30 AM','{Mon,Tue,Wed,Thu,Fri,Sat,Sun}',60,true,'All Levels');
INSERT INTO class_details (teacher_id, is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (1,true,'11:00 AM','{Mon,Tue,Wed,Thu,Fri}',60,true,'All Levels');
INSERT INTO class_details (teacher_id, is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (1,true,'11:00 AM','{Sat,Sun}',60,true,'Beginner/Basic');
INSERT INTO class_details (teacher_id, is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (1,true,'04:30 PM','{Tue,Thu}',60,true,'All Levels');
INSERT INTO class_details (teacher_id, is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (1,true,'05:45 PM','{Mon,Tue,Wed,Thu,Fri}',60,true,'All Levels');
INSERT INTO class_details (teacher_id, is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (1,true,'07:15 PM','{Mon,Tue,Wed,Thu}',60,true,'All Levels');

INSERT INTO package_details (description,package_cost,classes_amount,subscription_duration,is_subscription,is_visible_online) VALUES ('New Client First Class',10,1,0,false,true);
INSERT INTO package_details (description,package_cost,classes_amount,subscription_duration,is_subscription,is_visible_online) VALUES ('New Client First Month',40,0,1,true,true);
INSERT INTO package_details (description,package_cost,classes_amount,subscription_duration,is_subscription,is_visible_online) VALUES ('1 Class',14,1,0,false,true);
INSERT INTO package_details (description,package_cost,classes_amount,subscription_duration,is_subscription,is_visible_online) VALUES ('10 Class Package',120,10,0,false,true);
INSERT INTO package_details (description,package_cost,classes_amount,subscription_duration,is_subscription,is_visible_online) VALUES ('20 Class Package',220,20,0,false,true);
INSERT INTO package_details (description,package_cost,classes_amount,subscription_duration,is_subscription,is_visible_online) VALUES ('One Month Package',95,0,1,true,true);
INSERT INTO package_details (description,package_cost,classes_amount,subscription_duration,is_subscription,is_visible_online) VALUES ('Six Month Package',540,0,6,true,true);

INSERT INTO website_descriptions(location_name, description) VALUES ('News and Events', 'New Website Coming Soon 2023!');
INSERT INTO website_descriptions(location_name, description) VALUES ('Class Schedule', 'Schedule Updates: Effective Monday, April 10th, 2023

 

1.  Wednesday: 12:30 p.m., Beginner/Basic Class added (60 minutes).

 

2.  Tuesday and Thursday: 4:30 p.m., All Levels Class cancelled.

​
Current Protocols:

1.  No reservation needed.

2.  No face-masks required (Clients can still wear them based on their comfort level).

3.  No capacity limitations.

4.  No distancing requirements (however, since we have a large space, we encourage everyone to spread out as much as possible).

5.  Clients are required to bring their own yoga mats; also, we are not bringing props back yet.

 

We hope to see you soon in a class at the studio!');

COMMIT TRANSACTION;

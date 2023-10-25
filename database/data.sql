BEGIN TRANSACTION;

-- INSERT INTO teacher_details (last_name,first_name,is_teacher_active) VALUES ('Mallur','Chuck',true);
-- INSERT INTO teacher_details (last_name,first_name,is_teacher_active) VALUES ('Green','Margaret',true);

INSERT INTO class_details (is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (true,'06:00 AM','{Mon,Wed,Fri}',60,false,'All Levels');
INSERT INTO class_details (is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (true,'06:00 AM','{Tue,Thu}',60,false,'Intermediate');
INSERT INTO class_details (is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (true,'08:00 AM','{Sat,Sun}',60,false,'All Levels');
INSERT INTO class_details (is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (true,'09:30 AM','{Mon,Tue,Wed,Thu,Fri,Sat,Sun}',60,false,'All Levels');
INSERT INTO class_details (is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (true,'11:00 AM','{Mon,Tue,Wed,Thu,Fri}',60,false,'All Levels');
INSERT INTO class_details (is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (true,'11:00 AM','{Sat,Sun}',60,false,'Beginner/Basic');
INSERT INTO class_details (is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (true,'04:30 PM','{Tue,Thu}',60,false,'All Levels');
INSERT INTO class_details (is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (true,'05:45 PM','{Mon,Tue,Wed,Thu,Fri}',60,false,'All Levels');
INSERT INTO class_details (is_repeating, start_time, date_range, class_duration, is_paid, class_description) VALUES (true,'07:15 PM','{Mon,Tue,Wed,Thu}',60,false,'All Levels');

INSERT INTO package_details (description,package_cost,classes_amount,subscription_duration,is_subscription,is_visible_online,is_recurring) VALUES ('New Client First Class',10,1,0,false,true,false);
INSERT INTO package_details (description,package_cost,classes_amount,subscription_duration,is_subscription,is_visible_online,is_recurring) VALUES ('New Client First Month Package',40,0,1,true,true,false);
INSERT INTO package_details (description,package_cost,classes_amount,subscription_duration,is_subscription,is_visible_online,is_recurring) VALUES ('1 Class',14,1,0,false,true,false);
INSERT INTO package_details (description,package_cost,classes_amount,subscription_duration,is_subscription,is_visible_online,is_recurring) VALUES ('10 Class Package',120,10,0,false,true,false);
INSERT INTO package_details (description,package_cost,classes_amount,subscription_duration,is_subscription,is_visible_online,is_recurring) VALUES ('20 Class Package',220,20,0,false,true,false);
-- INSERT INTO package_details (description,package_cost,classes_amount,subscription_duration,is_subscription,is_visible_online,is_recurring) VALUES ('One Month Subscription',95,0,1,true,true,true);
-- INSERT INTO package_details (description,package_cost,classes_amount,subscription_duration,is_subscription,is_visible_online,is_recurring) VALUES ('Six Month Subscription',540,0,6,true,true,true);
INSERT INTO package_details (description,package_cost,classes_amount,subscription_duration,is_subscription,is_visible_online,is_recurring) VALUES ('One Month Unlimited',95,0,1,true,true,false);
INSERT INTO package_details (description,package_cost,classes_amount,subscription_duration,is_subscription,is_visible_online,is_recurring) VALUES ('Six Month Unlimited',540,0,6,true,true,false);
INSERT INTO package_details (description,package_cost,classes_amount,subscription_duration,is_subscription,is_visible_online,is_recurring) VALUES ('Gift Card',10,0,0,false,true,false);


INSERT INTO website_descriptions(location_name, description) VALUES ('News and Events', 'New Website Coming Soon 2023!');
INSERT INTO website_descriptions(location_name, description) VALUES ('Class Schedule', 'Schedule Updates: Effective Monday, April 10th, 2023

1.  Wednesday: 12:30 p.m., Beginner/Basic Class added (60 minutes).

2.  Tuesday and Thursday: 4:30 p.m., All Levels Class cancelled.

Current Protocols:

1.  No reservation needed.

2.  No face-masks required (Clients can still wear them based on their comfort level).

3.  No capacity limitations.

4.  No distancing requirements (however, since we have a large space, we encourage everyone to spread out as much as possible).

5.  Clients are required to bring their own yoga mats; also, we are not bringing props back yet.

We hope to see you soon in a class at the studio!');

COMMIT TRANSACTION;

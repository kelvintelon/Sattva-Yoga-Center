package com.sattvayoga;


import com.sattvayoga.dao.EventDao;
import com.sattvayoga.dao.JdbcEventDao;

import com.sattvayoga.controller.EventController;

import com.sattvayoga.dao.PackageDetailsDao;
import com.sattvayoga.model.ClassDetails;
import com.sattvayoga.model.Event;
import com.sattvayoga.spring.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import javax.swing.text.View;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@SpringBootApplication
@ComponentScan(basePackages = {"com.sattvayoga"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        TimerTrigger.main(args);
    }

}


class TimerTrigger {

    public static void main(String[] args) {

        new WorkerThread().start();

//        try {
//            Thread.sleep(7500);
//        } catch (InterruptedException e) {
//            // handle here exception
//        }

//        System.out.println("Main Thread ending") ;
    }

}

//@RestController
class WorkerThread extends Thread {

//    @Autowired
//    private EventDao eventDao;

    // try an AppConfig file next then work with a Data Source set up.


//    public WorkerThread(EventDao eventDao) {
//        this.eventDao = eventDao;
//    }

//    private JdbcTemplate jdbcTemplate;
//
//    public void setDataSource(DataSource dataSource) {
//        this.jdbcTemplate = new JdbcTemplate(dataSource);
//    }

//    @Autowired
//    private EventDao eventDao;

//    public WorkerThread() {
//        // When false, (i.e. when it's a non daemon thread),
//        // the WorkerThread continues to run.
//        // When true, (i.e. when it's a daemon thread),
//        // the WorkerThread terminates when the main
//        // thread or/and user defined thread(non daemon) terminates.
//
//        // don't set Daemon
//        setDaemon(false);
//    }

//    @Autowired
//    private EventDao eventDao;

//    private JdbcEventDao eventDao;


    public void run() {
        int count = 0;

        while (true) {
            // TODO: logic goes here

            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
            EventDao eventDao = context.getBean(EventDao.class);


            eventDao.createEvent();


            System.out.println("Hello from Worker " + count++);

            // 86400000 ms in a day
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                // handle exception here


                //            EventDao eventDao = new JdbcEventDao();         }

////            JdbcEventDao eventDao = new JdbcEventDao();


//            ApplicationContext context =
//                    new ClassPathXmlApplicationContext("classpath*:/META-INF/spring/applicationContext*.xml");

//            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("application.properties");
//            EventDao eventDao = context.getBean(JdbcEventDao.class);
//
//            eventDao.createEvent();


//            Date date = new Date();
//            Timestamp timestamp2 = new Timestamp(date.getTime());
//            String sql = "INSERT INTO events (class_id, event_name, start_time, " +
//                    "end_time, color, timed) VALUES (?, ?, ?, ?, ?, ?)";
//            jdbcTemplate.update(sql, 4,
//                    "event_test", timestamp2,
//                    timestamp2, "blue", false);


            }
        }
    }
}
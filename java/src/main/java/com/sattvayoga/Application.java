package com.sattvayoga;

import com.sattvayoga.dao.EventDao;
import com.sattvayoga.spring.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.sattvayoga"})
@EnableCaching
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        // uncomment next line to start trigger
        TimerTrigger.main(args);
    }

}


class TimerTrigger {

    public static void main(String[] args) {
        new WorkerThread().start();
    }

}

class WorkerThread extends Thread {


    public void run() {
        int count = 0;

        while (true) {

            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
            EventDao eventDao = context.getBean(EventDao.class);

            try {
                eventDao.updateEventServerTask();
            } catch (Exception e) {
                System.out.println("ERROR ON EVENTS UPDATE THREAD");;
            }

            try {
                eventDao.updateAllClientsByLookingAtEvents();
            } catch (Exception e) {
                System.out.println("ERROR ON CLIENTS UPDATE THREAD");
            }

            System.out.println("Events Up To Date. Clients Up To Date. Thread Initialized at count:  " + ++count);

            // 86400000 ms in a day
            // 604800000 in a week
            try {
                sleep(604800000);
            } catch (InterruptedException e) {
                // handle exception here
                System.out.println("Thread error");


            }
        }
    }
}
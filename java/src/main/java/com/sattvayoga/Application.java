package com.sattvayoga;

import com.sattvayoga.dao.EventDao;
import com.sattvayoga.spring.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.sattvayoga"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        // uncomment next line to start trigger
//        TimerTrigger.main(args);
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


            eventDao.createEvent();


            System.out.println("Hello from Worker " + count++);

            // 86400000 ms in a day
            try {
                sleep(30000);
            } catch (InterruptedException e) {
                // handle exception here
                System.out.println("Thread error");


            }
        }
    }
}
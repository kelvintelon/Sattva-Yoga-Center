package com.sattvayoga.controller;

import com.sattvayoga.dao.ClassPurchaseDao;
import com.sattvayoga.model.ClassPurchase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class ClassPurchaseController {

    private ClassPurchaseDao classPurchaseDao;

    public ClassPurchaseController(ClassPurchaseDao classPurchaseDao) {
        this.classPurchaseDao = classPurchaseDao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/createClassPurchase", method = RequestMethod.POST)
    public void createClassPurchase(@RequestBody ClassPurchase classPurchase) {

        classPurchaseDao.createClassPurchase(classPurchase);
    }
}

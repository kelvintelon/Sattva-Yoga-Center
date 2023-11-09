package com.sattvayoga.controller;

import com.sattvayoga.dao.*;
import com.sattvayoga.model.PackageDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@PreAuthorize("isAuthenticated()")
@RestController
@CrossOrigin
public class DatabaseUploadController {

    @Autowired
    ClientDetailsDao clientDetailsDao;

    @Autowired
    TeacherDetailsDao teacherDetailsDao;

    @Autowired
    EventDao eventDao;

    @Autowired
    PackageDetailsDao packageDetailsDao;

    @Autowired
    PackagePurchaseDao packagePurchaseDao;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/uploadClients")
    public void uploadClients(@RequestParam("file")MultipartFile multipartFile) {

        clientDetailsDao.uploadClientCsv(multipartFile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/uploadTeachers")
    public void uploadTeachers(@RequestParam("file")MultipartFile multipartFile) {

        teacherDetailsDao.uploadTeacherCsv(multipartFile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/uploadEvents")
    public void uploadEvents(@RequestParam("file")MultipartFile multipartFile) {

        eventDao.uploadEventCsv(multipartFile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/uploadPackages")
    public void uploadPackages(@RequestParam("file")MultipartFile multipartFile) {


    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/uploadSales")
    public void uploadSales(@RequestParam("file")MultipartFile multipartFile) {


    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/uploadGiftCardReport")
    public void uploadGiftCardReport(@RequestParam("file")MultipartFile multipartFile) {


    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/uploadAttendance")
    public void uploadAttendance(@RequestParam("file")MultipartFile multipartFile) {


    }
}

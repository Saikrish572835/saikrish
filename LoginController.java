/*
 * Copyright (c) 2016 Cognizant Technology Solutions.
 * 
 * This software belongs to Cognizant Technology Solutions. 
 * Any replication or reuse requires permission from Cognizant 
 * Technology Solutions.
 * 
 */

/**
 * This class represents a Authentication and Authorization related data. Help in validating user 
 * at login /storing information of Administrator by providing signup facility/it also implement
 * the logic to recover forgotten password.
 */

package com.cts.lch.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    private static Logger LOGGER = Logger.getLogger(LoginController.class);

    /**
     * checks whether the user is present or not to provide admin signup.
     */
    @RequestMapping(value = "/Login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout) {
        LOGGER.info("Start of login()");
        ModelAndView model = new ModelAndView("AppAdminHome");
        if (error != null) {
            model.addObject("errorMessage", "Invalid username and password!");
        }

        if (logout != null) {
            model.addObject("message", "You've been logged out successfully.");
        }
        model.setViewName("Login");

        LOGGER.info("End of login()");
        return model;
    }

}

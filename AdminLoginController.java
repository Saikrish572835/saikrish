package com.cts.lch.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.cts.lch.form.ChangePasswordForm;
import com.cts.lch.form.LoginForm;
import com.cts.lch.marketplace.model.Admin;
import com.cts.lch.marketplace.service.AdminService;

@Controller
@SessionAttributes("admin")
public class AdminLoginController {
    
    private static final Logger LOGGER = Logger.getLogger(AdminLoginController.class);
    
    private AdminService adminService;

    @Autowired(required=true)
    @Qualifier(value="adminService")
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }
    
    @RequestMapping(value = "/ShowLogin", method = RequestMethod.GET)
    public String showLogin() {
        LOGGER.info("Start");
        return "ShowLogin";
    }

    @RequestMapping(value = "/ShowChangePassword", method = RequestMethod.GET)
    public String showChangePassword() {
        LOGGER.info("Start");
        return "ShowChangePassword";
    }

    @RequestMapping(value = "/AdminLogin", method=RequestMethod.POST)
    public ModelAndView login(@ModelAttribute LoginForm loginForm) {
        LOGGER.info("Start");
        ModelAndView model = new ModelAndView();
        
        int associateId = loginForm.getAssociateId();
        LOGGER.debug(associateId);
        Admin admin = adminService.authenticate(associateId, loginForm.getPassword());
        if (admin != null) {
            model.addObject("admin", admin);
            if (admin.getAdTempPwdExpires() != null) {
                LOGGER.debug("Change default password.");
                model.setViewName("ShowChangePassword");
                model.addObject("associateId", associateId);
            } else {
                model.setViewName(resolveView(admin.getAdRole()));
            }
            LOGGER.debug("Login successful.");
        } else {
            model.addObject("errorMessage", "Invalid Associate ID or Password");
            model.setViewName("ShowLogin");
            LOGGER.debug("Login failed.");
        }
        
        //model.setViewName("ShowLogin");
        LOGGER.info("End");
        return model;
    }
    
    @RequestMapping(value = "/ChangePassword", method=RequestMethod.POST)
    public ModelAndView changePassword(@ModelAttribute ChangePasswordForm changePasswordForm) {
        LOGGER.info("Start");
        ModelAndView model = new ModelAndView();
        
        int associateId = changePasswordForm.getAssociateId();
        if (!changePasswordForm.getNewPassword().equals(changePasswordForm.getConfirmPassword())) {
            model.addObject("errorMessage", "New Password and Confirm Password does not match");
            model.addObject("associateId", associateId);
            model.setViewName("ShowChangePassword");
            return model;
        }
        LOGGER.debug("associateId: " + associateId);
        Admin admin = adminService.authenticate(associateId, changePasswordForm.getCurrentPassword());
        if (admin == null) {
            model.addObject("errorMessage", "Invalid Associate Id or Password.");
            model.addObject("associateId", associateId);
            model.setViewName("ShowChangePassword");
            return model;
        }
        admin.setAdPassword(changePasswordForm.getNewPassword());
        admin.setAdTempPwdExpires(null);
        adminService.updateAdmin(admin);
        model.addObject("message", "Password changed successfully. Please login with new password.");
        model.setViewName("ShowLogin");
        LOGGER.info("End");
        return model;
    }

    private String resolveView(String role) {
        LOGGER.info("Start");
        LOGGER.debug("Role: " + role);
        String view = null;
        switch (role) {
            case "AD":
                view = "AppAdminHome";
                break;
            case "PL":
                view = "BuAdminHome";
                break;
            case "SOL":
                view = "SolutionTeamHome";
                break;
            case "GWFM":
                view = "GWFMHome";
                break;
            case "VMO":
                view = "VmoUserHome";
                break;
        }
        LOGGER.debug("View: " + view);
        return view;
    }
}

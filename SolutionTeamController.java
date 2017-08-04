/*
 * Copyright (c) 2016 Cognizant Technology Solutions.
 * 
 * This software belongs to Cognizant Technology Solutions. 
 * Any replication or reuse requires permission from Cognizant 
 * Technology Solutions.
 * 
 */

package com.cts.lch.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cts.lch.marketplace.model.CourseFamily;
import com.cts.lch.marketplace.model.CourseFamilyTrack;
import com.cts.lch.marketplace.model.KnowledgePartner;
import com.cts.lch.marketplace.model.Track;
import com.cts.lch.marketplace.service.CourseFamilyService;
import com.cts.lch.marketplace.service.CourseFamilyTrackService;
import com.cts.lch.marketplace.service.KnowledgePartnerService;
import com.cts.lch.marketplace.service.TrackService;

/**
 * This class represents the controller of Application Administrator for the
 * class Education Organization and manages the functionalities of the Education
 * Organization entity.
 */
@Controller
public class SolutionTeamController {

	private static final Logger LOGGER = Logger.getLogger(SolutionTeamController.class);
	KnowledgePartnerService knowledgePartnerService;
	CourseFamilyService CourseFamilyService;
	CourseFamilyTrackService courseFamilyTrackService;
	TrackService trackService; 
	
	/**
	 * This method is used to return the view based on its @RequestMapping
	 * annotation.
	 * 
	 * @return This method will return a string AppAdminHome.
	 */
    @Autowired(required=true)
    @Qualifier(value="knowledgePartnerService")
    public void setKnowledgePartnerService(KnowledgePartnerService knowledgePartnerService){
        this.knowledgePartnerService = knowledgePartnerService;
    }
	

    @Autowired(required=true)
    @Qualifier(value="courseFamilyTrackService")
    public void setcourseFamilyTrackService(CourseFamilyTrackService CourseFamilyTrackService){
        this.courseFamilyTrackService = CourseFamilyTrackService;
    }
    
    @Autowired(required=true)
    @Qualifier(value="trackService")
    public void setTrackService(TrackService TrackService){
        this.trackService = TrackService;
    }
    
    @Autowired(required=true)
    @Qualifier(value="courseFamilyService")
    public void setcourseFamilyService(CourseFamilyService CourseFamilyService){
        this.CourseFamilyService = CourseFamilyService;
    }
    
    @RequestMapping(value = "/SolutionTeamHome", method = RequestMethod.GET)
    public String showSolutionTeamHome() {

        LOGGER.info("Start of showSolutionTeamHome in Controller");
        LOGGER.info("End of showSolutionTeamHome in Controller");
        return "SolutionTeamHome";
    }
    
    @RequestMapping(value = "/ShowAddCourse", method = RequestMethod.GET)
    public ModelAndView showAddCourse() {

        LOGGER.info("Start of addcourse in Controller");
        ModelAndView model = new ModelAndView("AddCourse");
        List<KnowledgePartner> knowledgePartners = knowledgePartnerService.getAllKnowledgePartners();
        List<Track> tracks=new ArrayList<Track>();
        tracks = trackService.getAllTrack();
        System.out.println();
        System.out.println("testing " + tracks);
        model.addObject("knowledgePartners",knowledgePartners);
        model.addObject("list",tracks);
        LOGGER.info("End of addcourse in Controller");
        return model;
    }
    
    @RequestMapping(value = "/AddCourse", method = RequestMethod.POST)
    public ModelAndView addCourse(HttpServletRequest request) {

        LOGGER.info("Start of addcourse in Controller");
        ModelAndView model = new ModelAndView("AddCourse");
      if(request.getParameter("kpId") != null && request.getParameterValues("check") != null)
      {
        int knowledgePartnerId=Integer.parseInt(request.getParameter("kpId"));
        KnowledgePartner knowledgePartner = new KnowledgePartner();
        knowledgePartner.setKpId(knowledgePartnerId);
        String cfName=request.getParameter("cfName");
        String cfUrl=request.getParameter("cfUrl");
        CourseFamily courseFamily=new CourseFamily();
        courseFamily.setKnowledgePartner(knowledgePartner);
      
        
        courseFamily.setCfName(cfName);
        courseFamily.setCfUrl(cfUrl);
        int cfId = CourseFamilyService.addCourseFamily(courseFamily);
        CourseFamily course = new CourseFamily();
		course.setCfId(cfId);
        String[] check=request.getParameterValues("check");
        if(check != null)
        {
        for(String id : check)
        {
        	Track t = new Track();
        	t.setTrId(Integer.parseInt(id));
        	System.out.println("tesing " + id);
        	CourseFamilyTrack cf = new CourseFamilyTrack();
    		cf.setTrack(t);
    		cf.setCourseFamily(course);
    		courseFamilyTrackService.addCourseFamilyTrack(cf);
        }
        }
      
        LOGGER.debug(courseFamily.getCfId());
       
        model.addObject("addSuccess", "added");
        model.addObject("message", "Course Added Successfully.");
      }
      else
      {
    	  model.addObject("addSuccess", "notadded");
    	  model.addObject("message", "Mandatory to fill all details.");
      }
    	  List<KnowledgePartner> knowledgePartners = knowledgePartnerService.getAllKnowledgePartners();
      List<Track>tracks=new ArrayList<Track>();
      tracks = trackService.getAllTrack();
      model.addObject("list",tracks);
      model.addObject("knowledgePartners", knowledgePartners);
      LOGGER.info("End of addcourse in Controller");
        
      return model;
    	  
    }
    @RequestMapping(value = "/AddCertificationForCourse", method = RequestMethod.GET)
    public ModelAndView AddCertificationForCourse(HttpServletRequest req) {

        LOGGER.info("Start of AddCertificationForCourse in Controller");
       KnowledgePartner partner = new KnowledgePartner();
              ModelAndView obj = new ModelAndView("AddCertificationForCourse");
              List<KnowledgePartner> cmp = knowledgePartnerService.getAllKnowledgePartners();

              String kp = req.getParameter("kp");
              String kp1=req.getParameter("buttonid");
              obj.addObject("list", cmp);
              obj.addObject("test", kp1);

        LOGGER.info("End of addcourse in Controller");
        return obj;
    }

    
    
    
    

}

package com.cts.lch.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cts.lch.marketplace.exception.LchBusinessException;
import com.cts.lch.marketplace.model.Company;
import com.cts.lch.marketplace.model.CourseFamily;
import com.cts.lch.marketplace.model.Demand;
import com.cts.lch.marketplace.model.Location;
import com.cts.lch.marketplace.service.CandidateService;
import com.cts.lch.marketplace.service.CompanyService;
import com.cts.lch.marketplace.service.CompanyServiceImpl;
import com.cts.lch.marketplace.service.CourseFamilyService;
import com.cts.lch.marketplace.service.DemandService;
import com.cts.lch.marketplace.service.DemandServiceImpl;
import com.cts.lch.marketplace.service.LocationService;
import javax.servlet.http.HttpServletRequest;
import freemarker.ext.beans.MapModel;

@Controller
public class BuAdminController 
{
	CompanyService companyservice;
	@Autowired(required = true)
	@Qualifier(value = "companyService")
	public void setCompanyService(CompanyService companyservice) 
	{
		this.companyservice = companyservice;
	}

	LocationService locationservice;
	@Autowired(required = true)
	@Qualifier(value = "locationService")
	public void setLocationService(LocationService locationservice) 
	{
		this.locationservice = locationservice;
	}

	CourseFamilyService coursefamilyservice;
	@Autowired(required = true)
	@Qualifier(value = "courseFamilyService")
	public void setCourseFamilyService(CourseFamilyService coursefamilyservice) 
	{
		this.coursefamilyservice = coursefamilyservice;
	}

	DemandService demandService;
	@Autowired(required = true)
	@Qualifier(value = "demandService")
	public void setDemandService(DemandService demandService) 
	{
		this.demandService = demandService;
	}
  
	
	CandidateService candidateService;
	@Autowired(required = true)
	@Qualifier(value = "candidateService")
	public void setCandidateService(CandidateService candidateService) 
	{
		this.candidateService = candidateService;
	}

	private static final Logger LOGGER = Logger.getLogger(AppAdminController.class);
	

	@RequestMapping(value = "/BuAdminHome", method = RequestMethod.GET)
	public String appAdminHome() 
	{
		LOGGER.info("Start of buadminHome");
		LOGGER.info("End of buadminHome in Controller");
		System.out.println("Inside Conroler");
		return "BuAdminHome";
	}	

	@RequestMapping(value = "/Demand", method = RequestMethod.GET)
	public ModelAndView demand() 
	{
		LOGGER.info("Start of AppAdminAdd in Controller");
		ModelAndView model = new ModelAndView("Demand");
		model.addObject("CompanyService", companyservice.getAllCompanies());
		model.addObject("LocationService", locationservice.getAllLocations());
		model.addObject("CourseFamilyService", coursefamilyservice.getCourseFamily());
		LOGGER.info("End of AppAdminAdd in Controller");
		return model;
	}
	
	@RequestMapping(value = "/DemandAddSuccess", method = RequestMethod.POST)
	public ModelAndView demandAddSuccess(
			@RequestParam("comment") String comment, 
			@RequestParam("date") String date,
			@RequestParam("intake") int count, 
			@RequestParam("company") int coId, 
			@RequestParam("location") int loId,
			@RequestParam("coursefamily") int cfId) throws ParseException 
		{
			LOGGER.info("Start of AppAdminAdd in Controller");	
			Date dated=newdateformat(date);
			Company company = new Company();
			company.setCoId(coId);
			Location location = new Location();
			location.setLoId(loId);
			CourseFamily coursefamily = new CourseFamily();
			coursefamily.setCfId(cfId);
			Demand demand = new Demand();
			demand.setCompany(company);
			demand.setCourseFamily(coursefamily);
			demand.setLocation(location);
			demand.setDePeriod(dated);
			demand.setDeComments(comment);
			demand.setDeCount(count);
			demand.setDeFulfilledCount(0);
			demandService.addDemand(demand);
			ModelAndView model = new ModelAndView("Demand");
			model.addObject("addsuccessfully","Added");
			model.addObject("CompanyService", companyservice.getAllCompanies());
			model.addObject("LocationService", locationservice.getAllLocations());
			model.addObject("CourseFamilyService", coursefamilyservice.getCourseFamily());
			LOGGER.info("End of AppAdminAdd in Controller");
			return model;
		}	
/*
	@RequestMapping(value = "/DemandAddSuccess", method = RequestMethod.POST)
    public ModelAndView demandAddSuccess(
            @RequestParam("comment") String comment, 
            @RequestParam("date") Date date,
            @RequestParam("intake") int count, 
            @RequestParam("company") int coId, 
            @RequestParam("location") int loId,
            @RequestParam("coursefamily") int cfId) throws ParseException 
        {
            LOGGER.info("Start of AppAdminAdd in Controller");
            Company company = new Company();
            company.setCoId(coId);
            Location location = new Location();
            location.setLoId(loId);
            CourseFamily coursefamily = new CourseFamily();
            coursefamily.setCfId(cfId);
            Demand demand = new Demand();
            demand.setCompany(company);
            demand.setCourseFamily(coursefamily);
            demand.setLocation(location);
            demand.setDePeriod(date);
            demand.setDeComments(comment);
            demand.setDeCount(count);
            demand.setDeFulfilledCount(4);
            demandService.addDemand(demand);
            ModelAndView model = new ModelAndView("Demand");
            model.addObject("addsuccessfully","Added");
            model.addObject("CompanyService", companyservice.getAllCompanies());
            model.addObject("LocationService", locationservice.getAllLocations());
            model.addObject("CourseFamilyService", coursefamilyservice.getCourseFamily());
            LOGGER.info("End of AppAdminAdd in Controller");
            return model;
        }
*/
	@RequestMapping(value = "/UpdateDemand", method = RequestMethod.GET)
	public ModelAndView updatedemand() 
	{
		LOGGER.info("Start of AppAdminAdd in Controller");
		ModelAndView model = new ModelAndView("UpdateDemand");
		model.addObject("CompanyService", companyservice.getAllCompanies());
		model.addObject("LocationService", locationservice.getAllLocations());
		model.addObject("CourseFamilyService", coursefamilyservice.getCourseFamily());
		LOGGER.info("End of AppAdminAdd in Controller");
		return model;
	}	
	
	@RequestMapping(value = "/ViewDemand", method = RequestMethod.POST)
	public ModelAndView demandsearch(@RequestParam("company") int companyid,
										@RequestParam("date") Date doj,
										@RequestParam("location") int locationid,
										@RequestParam("coursefamily") int courseid)
			
	  {
		LOGGER.info("Start of BuDemand in Controller");	
		ModelAndView model = new ModelAndView("UpdateDemand");
		model.addObject("addsuccessfully","Added");
		model.addObject("searchdemand",companyservice.disableDemandSearch(companyid, locationid, courseid, doj));
		model.addObject("CompanyService", companyservice.getAllCompanies());
		model.addObject("LocationService", locationservice.getAllLocations());
		model.addObject("CourseFamilyService", coursefamilyservice.getCourseFamily());
		LOGGER.info("End of BuDemand in Controller");
		return model;
	}
	
	@RequestMapping(value = "/ModifyDemand", method = RequestMethod.GET)
	public ModelAndView modifyDemand(HttpServletRequest req) 
	{
		LOGGER.info("Start of AppAdminAdd in Controller");
		ModelAndView model = new ModelAndView("ModifyDemand");
		try {			
			int demandid=Integer.parseInt(req.getParameter("id"));
			model.addObject("demandService", demandService.getDemand(demandid));
		} 
		catch (LchBusinessException e) 
		{
			System.out.println(e.getMessage());
		}
		LOGGER.info("End of AppAdminAdd in Controller");
		return model;
	}
	
	@RequestMapping(value = "/DemandModifySuccess", method = RequestMethod.POST)
	public ModelAndView changedemand(
			@RequestParam("demandid") int demandid,
			@RequestParam("dateupdate") String dateupdate,
			@RequestParam("demandcompanyid") int demandcompanyid,
			@RequestParam("demandlocationid") int demandlocationid, 
			@RequestParam("demandcourseid") int demandcourseid,
			@RequestParam("demandcount") int demandcount,
			@RequestParam("demandfulfilled") int demandfulfilled,
			@RequestParam("demandcomments") String demandcomment) throws ParseException
	  {
		LOGGER.info("Start of BuDemand in Controller");	
		
		Date dated=newdateformat(dateupdate);
		Company company = new Company();
		company.setCoId(demandcompanyid);
		Location location = new Location();
		location.setLoId(demandlocationid);
		CourseFamily coursefamily = new CourseFamily();
		coursefamily.setCfId(demandcourseid);
		Demand demand = new Demand();
		demand.setDeId(demandid);
		demand.setCompany(company);
		demand.setCourseFamily(coursefamily);
		demand.setLocation(location);		
		demand.setDePeriod(dated);
		demand.setDeComments(demandcomment);
		demand.setDeCount(demandcount);
		demand.setDeFulfilledCount(demandfulfilled);		
		demandService.modifyDemand(demand);
		ModelAndView model = new ModelAndView("DemandModifySuccess");
		LOGGER.info("End of BuDemand in Controller");
		return model;
	}

	
	@RequestMapping(value = "/ChangeDemand", method = RequestMethod.GET)
	public ModelAndView changeDemand(HttpServletRequest req) 
	{
		LOGGER.info("Start of AppAdminAdd in Controller");		
		ModelAndView model = new ModelAndView("ChangeDemand");	
		int demandid=Integer.parseInt(req.getParameter("id"));	
		model.addObject("CompanyService", companyservice.getAllCompanies());
		model.addObject("LocationService", locationservice.getAllLocations());
		model.addObject("CourseFamilyService", coursefamilyservice.getCourseFamily());
		model.addObject("demandid",demandid);		
		LOGGER.info("End of AppAdminAdd in Controller");
		return model;
	}	
	
	@RequestMapping(value = "/ChangeDemandSuccess", method = RequestMethod.POST)
	public ModelAndView demandModifySuccess(
			@RequestParam("demandid") int demandid,
			@RequestParam("date") String date,
			@RequestParam("demandcompanyid") int demandcompanyid,
			@RequestParam("demandlocationid") int demandlocationid, 
			@RequestParam("demandcourseid") int demandcourseid,
			@RequestParam("demandcount") int demandcount, 
			@RequestParam("demandcomments") String demandcomment) throws ParseException
	  {
		LOGGER.info("Start of BuDemand in Controller");		
		Company company = new Company();
		company.setCoId(demandcompanyid);
		Location location = new Location();
		location.setLoId(demandlocationid);
		CourseFamily coursefamily = new CourseFamily();
		coursefamily.setCfId(demandcourseid);
		Demand demand = new Demand();
		demand.setDeId(demandid);
		demand.setCompany(company);
		demand.setCourseFamily(coursefamily);
		demand.setLocation(location);
		Date dated=newdateformat(date);
		demand.setDePeriod(dated);
		demand.setDeComments(demandcomment);
		demand.setDeCount(demandcount);
		demand.setDeFulfilledCount(0);		
		demandService.modifyDemand(demand);
		ModelAndView model = new ModelAndView("DemandModifySuccess");
		LOGGER.info("End of BuDemand in Controller");
		return model;
	}	
	public Date newdateformat(String date) throws ParseException
	{
		SimpleDateFormat simpleddateformat1=new SimpleDateFormat("dd/MM/yyyy");
		Date dated1=simpleddateformat1.parse(date);
		SimpleDateFormat simpleddateformat2=new SimpleDateFormat("yyyy-MM-dd");
		String datestring=simpleddateformat2.format(dated1);
		Date dated=simpleddateformat2.parse(datestring);
		return dated;
	}
}
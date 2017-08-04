/*
 * Copyright (c) 2016 Cognizant Technology Solutions.
 * 
 * This software belongs to Cognizant Technology Solutions. 
 * Any replication or reuse requires permission from Cognizant 
 * Technology Solutions.
 * 
 */

package com.cts.lch.controller;


import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**.
 * @author      CHN16FJ001-Team2
 * @version     
 */


import java.util.Random;
import java.util.Set;

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

import com.cts.lch.marketplace.exception.LchBusinessException;
import com.cts.lch.marketplace.model.Admin;
import com.cts.lch.marketplace.model.Candidate;
import com.cts.lch.marketplace.model.CandidatePoc;
import com.cts.lch.marketplace.model.CertFacility;
import com.cts.lch.marketplace.model.CertFacilityHoliday;
import com.cts.lch.marketplace.model.CertPartner;
import com.cts.lch.marketplace.model.Certification;
import com.cts.lch.marketplace.model.Company;
import com.cts.lch.marketplace.model.CourseFamily;
import com.cts.lch.marketplace.model.Demand;
import com.cts.lch.marketplace.model.KnowledgePartner;
import com.cts.lch.marketplace.model.Location;
import com.cts.lch.marketplace.service.AdminService;
import com.cts.lch.marketplace.service.CandidatePocService;
import com.cts.lch.marketplace.service.CandidateService;
import com.cts.lch.marketplace.service.CertFacilityHolidayService;
import com.cts.lch.marketplace.service.CertFacilityService;
import com.cts.lch.marketplace.service.CertPartnerService;
import com.cts.lch.marketplace.service.CompanyService;
import com.cts.lch.marketplace.service.CourseFamilyService;
import com.cts.lch.marketplace.service.KnowledgePartnerService;
import com.cts.lch.marketplace.service.LocationService;



/**
 * This class represents the controller of Application Administrator for the
 * class Education Organization and manages the functionalities of the Education
 * Organization entity.
 */
@Controller
public class AppAdminController  {

    private static final Logger LOGGER = Logger.getLogger(AppAdminController.class);
   // AppAdminController ac = new AppAdminController();
    private AdminService adminService;
    private CompanyService companyService; 
    private LocationService locationService; 
    private CourseFamilyService courseFamilyService; 
    private CandidateService candidateService;
	private CandidatePocService candidatePocService;
	private KnowledgePartnerService knowledgePartnerService;
    private static final Random RANDOM = new SecureRandom();
    public static final int PASSWORD_LENGTH = 8;
    List<Demand> demres = new ArrayList<Demand>();
    private String partner;
    private String address1;
    private String address2;
    private String locality;
    private String country;
    private String city;
    private String state;
    private String pincode;
    private String[] checkbox;
    private String stime;
    private String etime;
    private String[] dateArray;
    private String[] dateDesc;
    private String work="";
    private String holiday="";
    private String ch="";
    private Set<Certification> certifications = new HashSet<Certification>(0);
	private CertPartnerService certPartnerService;

    

   
    /**
     * This method is used to return the view based on its @RequestMapping
     * annotation.
     * 
     * @return This method will return a string AppAdminHome.
     */
    @Autowired(required=true)
    @Qualifier(value="adminService")
    public void setAdminService(AdminService adminService){
        this.adminService = adminService;
    } 

    @Autowired(required = true)
    @Qualifier(value = "companyService")
    public void setCompanyService(CompanyService companyService) {
           this.companyService = companyService;
    }
    @Autowired(required = true)
    @Qualifier(value = "locationService")
    public void setLocationService(LocationService locationService) {
           this.locationService = locationService;
    }
    @Autowired(required = true)
    @Qualifier(value = "courseFamilyService")
    public void setCourseFamilyService(CourseFamilyService courseFamilyService) {
           this.courseFamilyService = courseFamilyService;
    }
    @Autowired(required = true)
	@Qualifier(value = "candidateService")
	public void setCandidateService(CandidateService candidateService) {
		this.candidateService = candidateService;
	}

	@Autowired(required = true)
	@Qualifier(value = "candidatePocService")
	public void setCandidatePocService(CandidatePocService candidatePocService) {
		this.candidatePocService = candidatePocService;
	}

	@Autowired(required = true)
	@Qualifier(value = "knowledgePartnerService")
	public void setKnowledgePartnerService(KnowledgePartnerService knowledgePartnerService) {
		this.knowledgePartnerService = knowledgePartnerService;
	}
    @Autowired(required=true)
    @Qualifier(value="certPartnerService")
    public void setcertPartnerService(CertPartnerService certPartnerService){
      this.certPartnerService=certPartnerService;
      }
   
 @Autowired(required = true)
 @Qualifier(value = "certFacilityService")
 public void setCertFacilityService(CertFacilityService  certFacilityService) {
     this.certFacilityService = certFacilityService;
 }
 @Autowired(required = true)
 @Qualifier(value = "certFacilityHolidayService")
 public void setCertFacilityHolidayService(CertFacilityHolidayService  certFacilityHolidayService) {
     this.certFacilityHolidayService = certFacilityHolidayService;
 }
 private CertFacilityService certFacilityService;
 private CertFacilityHolidayService certFacilityHolidayService;
 


    public static String generateRandomPassword() {
        String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789+@";

        String pw = "";
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = (int) (RANDOM.nextDouble() * letters.length());
            pw += letters.substring(index, index + 1);
        }
        return pw;
    }    
    
    @RequestMapping(value = "/AppAdminHome", method = RequestMethod.GET)
    public String appAdminHome() {
        LOGGER.info("Start of appadminHome");
        LOGGER.info("End of appadminHome in Controller");
        return "AppAdminHome";
    }
  
    
    @RequestMapping(value = "/DeactivateCandidate", method = RequestMethod.GET)
	public ModelAndView deactivateCandidate(HttpServletRequest request) {
		LOGGER.info("Start of deactivateCandidate in Controller");
		ModelAndView modelAndView = new ModelAndView("DeactivateCandidate");
		List<Location> locationList = locationService.getAllLocations();
		modelAndView.addObject("location", locationList);
		LOGGER.info("End of deactivateCandidate in Controller");
		return modelAndView;
	}

	@RequestMapping(value = "/SearchCandidate", method = RequestMethod.POST)
	public ModelAndView searchCandidate(HttpServletRequest request) throws ParseException {
		LOGGER.info("Start of searchCandidate in Controller");

		ModelAndView model = new ModelAndView("DeactivateCandidate");
		List<Location> locationList = locationService.getAllLocations();
		String check = request.getParameter("check");
		SimpleDateFormat sdfSource = new SimpleDateFormat("dd/MM/yyyy");
		Integer location = !request.getParameter("locationDrop").equals("")
				? Integer.parseInt(request.getParameter("locationDrop")) : null;
		String candidateId = !request.getParameter("candidate").equals("") ? request.getParameter("candidate") : null;
		Date doj = !request.getParameter("doj").equals("") ? sdfSource.parse(request.getParameter("doj")) : null;

		LOGGER.debug("****location:" + location + ";candidateId" + candidateId + ";doj" + doj);

		List<Candidate> candidateList = adminService.disableCandidateSearch(candidateId, location, doj);

		LOGGER.debug("***Candidate" + candidateList.size());
		model.addObject("location", locationList);
		model.addObject("selected", location);
		model.addObject("id", candidateId);
		model.addObject("doj", doj);
		model.addObject("candidate", candidateList);
		model.addObject("check", check);
		LOGGER.info("End of searchCandidate in Controller");

		return model;
	}

	@RequestMapping(value = "/DisableCandidate", method = RequestMethod.POST)
	public ModelAndView disableCandidate(HttpServletRequest request) {
		LOGGER.info("Start of disableCandidate in Controller");
		ModelAndView modelAndView = new ModelAndView("DeactivateCandidate");
		List<Candidate> candidateList = new ArrayList<Candidate>();
		Candidate candidate = new Candidate();
		int candidateId = 0;
		String[] id = request.getParameterValues("check");
		if(id != null){
		for(String s : id)
		{
			if(!s.equals(""))
			{
		candidateId = Integer.parseInt(s);
		candidate.setCaId(candidateId);
		candidateList.add(candidate);		
		}	
		adminService.disableCandidate(candidateList);
		}
		}
		
		LOGGER.info("End of disableCandidate in Controller");
		modelAndView.addObject("checked",id);
		return modelAndView;
	}

   /* @RequestMapping(value = "/ResendPassword", method = RequestMethod.GET)
    public ModelAndView ResendPassword(HttpServletRequest request) {
           LOGGER.info("Start of ResendPassword");
           String id = request.getParameter("associateid");
           ModelAndView modelAndView = new ModelAndView("ResendPassword");
           modelAndView.addObject("id",id);
           LOGGER.info("End of ResendPassword in Controller");
           return modelAndView;
    }*/


   @RequestMapping(value = "/ResendPasswordSuccess", method = RequestMethod.POST)
    public ModelAndView  ResendPasswordSuccess( @ModelAttribute("associateid")int id){
        LOGGER.info("Start of ResendPasswordSucess");
        ModelAndView modelAndView = new ModelAndView("ResendPasswordSuccess");
      LOGGER.info("End of ResendPasswordSucess in Controller");
      return modelAndView;
       }

	

   @RequestMapping(value = "/DisableDemand", method = RequestMethod.GET)
	public ModelAndView DisableDemand(HttpServletRequest request) throws ParseException {
		LOGGER.info("Start of DisableDemand");
		ModelAndView modelAndView = new ModelAndView("DisableDemand");
		String val = request.getParameter("bu");
		List<Company> companies = companyService.listCompanies();
		List<Location> locations = companyService.listLocation();
		List<CourseFamily> courses = companyService.listCourseFamily();
		modelAndView.addObject("courses", courses);
		modelAndView.addObject("companies", companies);
		modelAndView.addObject("locations", locations);
		modelAndView.addObject("enable", val);
		modelAndView.addObject("selected", val);
		modelAndView.addObject("norecord", null);
		LOGGER.info("End of DisableDemand in Controller");
		return modelAndView;
	}

	@RequestMapping(value = "/SearchDemand", method = RequestMethod.POST)
	public ModelAndView SearchDemand(HttpServletRequest request) throws ParseException {
		LOGGER.info("Start of Search Demand");
		ModelAndView modelAndView = new ModelAndView("DisableDemand");
		List<Company> cmp = companyService.listCompanies();
		List<Location> loc = companyService.listLocation();
		List<CourseFamily> cou = companyService.listCourseFamily();
		String check = request.getParameter("selectedExport");
		modelAndView.addObject("check", check);
		Integer buId = null, locationId = null, courseId = null;
		if (!request.getParameter("bu").equals("")) {
			buId = Integer.parseInt(request.getParameter("bu"));
		}
		if (!request.getParameter("location").equals("")) {
			locationId = Integer.parseInt(request.getParameter("location"));
		}
		if (!request.getParameter("course").equals("")) {
			courseId = Integer.parseInt(request.getParameter("course"));
		}
		String date = (request.getParameter("date"));
		Date doj = null;
		if (date == "") {
			doj = null;
		} else {
			if (date != null) {
				SimpleDateFormat sdfSource = new SimpleDateFormat("dd/MM/yyyy");
				doj = sdfSource.parse(date);
			}
		}
	    demres = companyService.disableDemandSearch(buId, locationId, courseId, doj);
		System.out.println(demres);
		if(demres.isEmpty()){
			modelAndView.addObject("norecords", "yes");
		}
		else{
		    modelAndView.addObject("resultdemand", demres);
		}
		modelAndView.addObject("selected", buId);
		modelAndView.addObject("courses", cou);
		modelAndView.addObject("companies", cmp);
		modelAndView.addObject("locations", loc);
		modelAndView.addObject("date", date);
		LOGGER.info("End of Search Demand in Controller");
		return modelAndView;
	}

	@RequestMapping(value = "/DisableDemandResult", method = RequestMethod.POST)
	public ModelAndView DisableDemandResult(HttpServletRequest request) throws ParseException {
		LOGGER.info("Start of DisableDemandResult");
		ModelAndView modelAndView = new ModelAndView("DisableDemand");
		String check = request.getParameter("selectedExport");
		modelAndView.addObject("check", check);
		List<Demand> delete = new ArrayList<Demand>();
		String[] checkbox = request.getParameterValues("selectedExport");
		if (checkbox != null) {
			for (String checked : checkbox) {
				if(!checked.equals(""))
				{
				Demand demand = new Demand();
				demand.setDeId(Integer.parseInt(checked));
				delete.add(demand);
				}
			}
			companyService.disableDemand(delete);
		}
		else
		{
			 modelAndView.addObject("resultdemand", demres);
			 modelAndView.addObject("notselected", "yes");
		}
		List<Company> companies = companyService.listCompanies();
		List<Location> locations = companyService.listLocation();
		List<CourseFamily> courses = companyService.listCourseFamily();
		modelAndView.addObject("courses", courses);
		modelAndView.addObject("companies", companies);
		modelAndView.addObject("locations", locations);
		LOGGER.info("End of DisableDemandResult in Controller");
		return modelAndView;
	}


	@RequestMapping(value = "/ModifyAdminAccessSuccess", method = RequestMethod.GET)
	public ModelAndView ModifyAdminAccessSuccess() {
		LOGGER.info("Start of DisableDemandSuccess");
		ModelAndView model = new ModelAndView("ModifyAdminAccessSuccess");
		model.addObject("message", "No Operation Performed");
		LOGGER.info("End of DisableDemandSuccess in Controller");
		return model;
	}

	@RequestMapping(value = "/EnableOrDisableAdmin", method = RequestMethod.POST)
	public ModelAndView enableOrDisableAdmin(HttpServletRequest req) throws LchBusinessException {
		LOGGER.info("Start of enableOrDisableAdmin");
		String operation = req.getParameter("operation");

		int associateId = Integer.parseInt(req.getParameter("associate_id"));
		ModelAndView model = new ModelAndView("ModifyAdminAccess");
		if (operation.equals("disable")) {
			adminService.disableAdmin(associateId);
			model.addObject("message", "Admin Successfully Disabled");
		} else if (operation.equals("enable")) {
			adminService.enableAdmin(associateId);
			model.addObject("message", "Admin Successfully Enabled");
		}
		LOGGER.info("End of enableOrDisableAdmin in Controller");
		return model;
	}

	@RequestMapping(value = "/ShowEnableDisableAdmin", method = RequestMethod.GET)
	public String modifyAdminAccess(HttpServletRequest req) {
		LOGGER.info("Start of ModifyAdminAccess");
		LOGGER.info("End of ModifyAdminAccess in Controller");
		return "ModifyAdminAccess";
	}

	@RequestMapping(value = "/SearchAdmin", method = RequestMethod.POST)
	public ModelAndView searchAdmin(HttpServletRequest req) {
		LOGGER.info("Start of searchAdmin");
		ModelAndView model = new ModelAndView("ModifyAdminAccess");
		try {
			int associateId = Integer.parseInt(req.getParameter("associate_id"));
			LOGGER.debug("associateId: " + associateId);
			Admin admin = adminService.getAdmin(associateId);
			model.addObject("associate_details", admin);
		} catch (LchBusinessException exception) {
			model.addObject("errorMessage", exception.getMessage());
			return model;
		} catch (NumberFormatException numberFormatException)
		{
			model.addObject("errorMessage","Inavlid Input");
		}
		LOGGER.info("End of searchAdmin in Controller");
		return model;
	}
	
	@RequestMapping(value = "/AddCertification", method = RequestMethod.GET)
    public ModelAndView AddCertification(HttpServletRequest req) {
           LOGGER.info("Start of appadminHome");

           ModelAndView modelAndView = new ModelAndView("AddCertification");
           List<CertPartner> certPartners = certPartnerService.getAllCertPartners();
           for (CertPartner cp : certPartners) {
                  System.out.println(cp.getCpName());
           }
           List<Location> locations = companyService.listLocation();
           for(Location lc : locations)
           {
                  System.out.println(lc.getLoCountry());
           }
           if (req.getParameter("partner") != null) {
                  String partner = req.getParameter("partner");
                  CertFacility cf = new CertFacility();
                  CertPartner cp = new CertPartner();
                  cp = certPartnerService.getByCertPartnerId(Integer.parseInt(partner));
                  cf.setCertPartner(cp);
                  cf.setCyAddr1(req.getParameter("address1"));
                  cf.setCyAddr2(req.getParameter("address2"));
                  cf.setCyLocality(req.getParameter("locality"));
                  cf.setCyCountry(req.getParameter("country"));
                  cf.setCyCity(req.getParameter("cities"));
                  cf.setCyState(req.getParameter("state"));
                  cf.setCyPincode(req.getParameter("pincode"));
                  adminService.addCertFacility(cf);
                  modelAndView.addObject("addsuccessfully", "Added");
           }
           modelAndView.addObject("locations", locations);
          modelAndView.addObject("certPartners", certPartners);
           LOGGER.info("End of appadminHome in Controller");
           return modelAndView;
    }

   
   

    


	  
	  

 
    @RequestMapping(value = "/ShowAdminRegister", method = RequestMethod.GET)
    public String showAdminRegister(HttpServletRequest req) {
           return "AdminRegister";
    }
    
    
    @RequestMapping(value = "/SaveAdmin", method = RequestMethod.POST)
    public ModelAndView saveAdmin(HttpServletRequest req) {
           LOGGER.info("Start");
           ModelAndView model = new ModelAndView("AdminRegister");
           Admin admin = new Admin();
           String name = req.getParameter("First_Name");
           admin.setAdAssociateId(Integer.parseInt(req.getParameter("Associate_Id")));
           admin.setAdActive('Y');
           admin.setAdFirstname(req.getParameter("First_Name"));
           admin.setAdLastname(req.getParameter("Last_Name"));
           admin.setAdEmailId(req.getParameter("Email_Id"));
           admin.setAdRole(req.getParameter("selectRole"));
           admin.setAdTempPwdExpires(getDatePlus24Hrs());
           String s1 = AppAdminController.generateRandomPassword();
           admin.setAdPassword(s1);

           try {
                  adminService.addAdmin(admin);
                  StringBuffer successMsg = new StringBuffer();
                  successMsg.append(name.toUpperCase());
                  successMsg.append(" ");
                  successMsg.append("added Successfully.");
                  successMsg.append("\n");
                  successMsg.append("An e-mail notification is sent to the respective mail");
                  successMsg.append(" ");
                  successMsg.append("with temporary password that expires in a day.");
                  model.addObject("message", successMsg);
           } catch (LchBusinessException e) {
                  LOGGER.error(e);
                  model.addObject("errorMessage", e.getMessage());
           }
           LOGGER.info("End");
           return model;
    }
    
    private Date getDatePlus24Hrs() {
           Date d1 = new Date();
           Date d2;
           Calendar cl = Calendar.getInstance();
           cl.setTime(d1);
           cl.add(Calendar.HOUR, 24);
           d2 = cl.getTime();
           return d2;
    }
   @RequestMapping(value = "/ModifySlotDate", method = RequestMethod.GET)
   public ModelAndView modifySlotDate() {
       LOGGER.info("Start of ModifySlotDate");
       ModelAndView model = new ModelAndView("ModifySlotDate");
       LOGGER.info("End of ModifySlotDate in Controller");
       return model;
   }
    
   @RequestMapping(value = "/ShowCertificationHolidays", method = RequestMethod.GET)
   public ModelAndView showCertificationHolidays() {
       LOGGER.info("Start");
       List<CertFacility>certInfo=new ArrayList<CertFacility>(certFacilityService.getAllCertFacility());         
       ModelAndView model = new ModelAndView();
       //model.setViewName("ShowCertificationHolidays");       
       model.addObject("certInfo", certInfo);
       return model;
   }
   @RequestMapping(value = "/ShowCertificationHolidays", method = RequestMethod.POST)
   public ModelAndView showGetHolidays(HttpServletRequest req) {
      LOGGER.info("Start");
      int cyId=Integer.parseInt(req.getParameter("myselect"));    
      //String selectedValue = req.getParameter("selectedValue");
      //LOGGER.info(selectedValue);
      List<CertFacility>certInfo=new ArrayList<CertFacility>(certFacilityService.getAllCertFacility());
      if(cyId>0)
      {                    
             List<CertFacilityHoliday> holidays = new ArrayList<CertFacilityHoliday>(certFacilityHolidayService.getAllCertFacilityHolidays(cyId));            
           ModelAndView model = new ModelAndView();
           model.setViewName("ShowCertificationHolidays");
           model.addObject("holidays", holidays);
           model.addObject("certInfo", certInfo);
           model.addObject("cyId",cyId);
             return model;
      }else
      {             
             ModelAndView model = new ModelAndView();
             model.setViewName("ShowCertificationHolidays");
             model.addObject("holidays", null);
             model.addObject("certInfo", certInfo);
             return model;
      }
   }
   @RequestMapping(value = "/ModifyCertificationHolidays", method = RequestMethod.GET)
   public ModelAndView getModifyHolidays(HttpServletRequest req) {
      ModelAndView model = new ModelAndView();
      List<CertFacility>certInfo=new ArrayList<CertFacility>(certFacilityService.getAllCertFacility());
      model.setViewName("ShowCertificationHolidays");
      model.addObject("certInfo", certInfo);
      return model;
   }
   @RequestMapping(value = "/ModifyCertificationHolidays", method = RequestMethod.POST)
   public ModelAndView modifyHolidays(HttpServletRequest req) throws ParseException {
      LOGGER.info("Start of Modify Certification Holidays ");
      ModelAndView model = new ModelAndView();
      int cyId=Integer.parseInt(req.getParameter("cyId"));          
      String holiday=req.getParameter("eventname");
      SimpleDateFormat sdfSource = new SimpleDateFormat("dd/MM/yyyy");
      Date holidayDate = !req.getParameter("event-start-date").equals("") ? sdfSource.parse(req.getParameter("event-start-date")) : null;
      List<CertFacility>certInfo=new ArrayList<CertFacility>(certFacilityService.getAllCertFacility()); 
      if(req.getParameter("save")!=null)
      {
             if(req.getParameter("event-index")!="")
             {   
                    int chId=Integer.parseInt(req.getParameter("event-index"));
                    certFacilityHolidayService.UpdateHoliday(chId, holiday);
                    LOGGER.info("UPDATED");
             }
             else 
             {                    
                    CertFacility certFacility=new CertFacility();          
                    CertFacilityHoliday certHoliday=new CertFacilityHoliday();           
                    certHoliday.setChName(holiday);
                    certFacility.setCyId(cyId);   
                    certHoliday.setCertFacility(certFacility); 
                    certHoliday.setChDate(holidayDate);
                    certFacilityHolidayService.AddHoliday(certHoliday);
                    LOGGER.info("INSERTED");
             }
             
      }
      else if(req.getParameter("remove").equals("remove") && req.getParameter("event-index")!="")
      {
             int chId=Integer.parseInt(req.getParameter("event-index"));
             certFacilityHolidayService.deleteHoliday(chId);
             LOGGER.info("REMOVED");
      }
      List<CertFacilityHoliday> holidays = new ArrayList<CertFacilityHoliday>(certFacilityHolidayService.getAllCertFacilityHolidays(cyId));
      model.setViewName("ShowCertificationHolidays");
      model.addObject("certInfo", certInfo);
      model.addObject("certInfo", certInfo);
      model.addObject("holidays", holidays);
      model.addObject("cyId",cyId);
      return model;
   }

	@RequestMapping(value = "/AddCandidate", method = RequestMethod.GET)
	public ModelAndView addCandidate(HttpServletRequest request) {

		LOGGER.info("Start of SuccessfulAdd in Controller");
		ModelAndView modelAndView = new ModelAndView("AddCandidate");
		List<CandidatePoc> poc = candidatePocService.getAllPOC();
		List<KnowledgePartner> kp = knowledgePartnerService.getAllKnowledgePartners();
		List<Location> loc = locationService.getAllLocations();
		List<CourseFamily> cf = courseFamilyService.getCourseFamily();
		modelAndView.addObject("poc", poc);
		modelAndView.addObject("kp", kp);
		modelAndView.addObject("loc", loc);
		modelAndView.addObject("cf", cf);
		LOGGER.info("End of SuccessfulAdd in Controller");
		return modelAndView;
	}

	@RequestMapping(value = "/AddCandidates", method = RequestMethod.POST)
	public ModelAndView AddCandidate(HttpServletRequest req) throws ParseException {
		System.out.println("Inside Conroler");
		Candidate candidate = new Candidate();
		CourseFamily coursefamily = new CourseFamily();
		Location location = new Location();
		CandidatePoc candidatepoc = new CandidatePoc();
		ModelAndView obj = new ModelAndView("AddCandidate");
		String cid = req.getParameter("candidate");
		String cfname = req.getParameter("fname");
		String clname = req.getParameter("lname");
		String dob = req.getParameter("date");
		Integer poc = Integer.parseInt(req.getParameter("poc"));
		Character active = !req.getParameter("activity").equals("") ? req.getParameter("activity").charAt(0) : null;
		Integer kp = Integer.parseInt(req.getParameter("kp"));
		Integer cour = Integer.parseInt(req.getParameter("cou"));
		Integer loc = Integer.parseInt(req.getParameter("loc"));
		String mobile = req.getParameter("phone");
		String eid = req.getParameter("mailid");
		String doj = req.getParameter("doj");
		String profilelink = req.getParameter("link");
		Character mail = !req.getParameter("email").equals("") ? req.getParameter("email").charAt(0) : null;
		Date dob1 = null;
		if (dob != null) {
			SimpleDateFormat sdfSource = new SimpleDateFormat("dd/MM/yyyy");
			dob1 = sdfSource.parse(dob);
		}

		Date doj1 = null; 
		if (doj != null) {
			SimpleDateFormat sdfSource = new SimpleDateFormat("dd/MM/yyyy");
			doj1 = sdfSource.parse(doj);
		}

		if (cid != null) {
			candidatepoc.setPoId(poc);
			coursefamily.setCfId(cour);
			location.setLoId(loc);
			candidate.setCaCandidateId(cid);
			candidate.setCaFirstName(cfname);
			candidate.setCaLastName(clname);
			candidate.setCaDob(dob1);
			candidate.setCandidatePoc(candidatepoc);
			candidate.setCaActive(active);
			candidate.setCaMobile(mobile);
			candidate.setCaEmail(eid);
			candidate.setCaDoj(doj1);
			candidate.setCaProfileUrl(profilelink);
			candidate.setCaNotificationSent(mail);
			candidate.setCourseFamily(coursefamily);
			candidate.setLocation(location);
			candidateService.addCandidate(candidate);
		}
       obj.addObject("cid",cid);
		return obj;
	}

	@RequestMapping(value = "/AddCandidateSuccess", method = RequestMethod.GET)
	public String addCandidateSuccess(HttpServletRequest request) {

		LOGGER.info("Start of SuccessfulAdd in Controller");
		ModelAndView modelAndView = new ModelAndView();
		LOGGER.info("End of SuccessfulAdd in Controller");
		HttpSession session = request.getSession();
		return "AddCandidateSuccess";
	}
   
   
}





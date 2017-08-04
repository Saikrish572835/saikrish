package com.cts.lch.controller;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cts.lch.marketplace.exception.LchUploadException;
import com.cts.lch.marketplace.model.Candidate;
import com.cts.lch.marketplace.model.CourseFamily;
import com.cts.lch.marketplace.model.Location;
import com.cts.lch.marketplace.service.AdminService;
import com.cts.lch.marketplace.service.CandidatePocService;
import com.cts.lch.marketplace.service.CandidateService;
import com.cts.lch.marketplace.service.CompanyService;
import com.cts.lch.marketplace.service.CourseFamilyService;
import com.cts.lch.marketplace.service.EmailService;
import com.cts.lch.marketplace.service.KnowledgePartnerService;
import com.cts.lch.marketplace.service.LocationService;
import com.cts.lch.view.CsvFileError;
import com.cts.lch.view.UploadCandidate;

@Controller
public class GWFMAdminController {
	private static final Logger LOGGER = Logger.getLogger(GWFMAdminController.class);

	private LocationService locationService;
	private CourseFamilyService courseFamilyService;
	private CandidateService candidateService;
	private EmailService emailService;
	private CompanyService companyService;
	private CandidatePocService candidatePocService;
	private KnowledgePartnerService knowledgePartnerService;
	private AdminService adminService;

	@Autowired(required = true)
	@Qualifier(value = "adminService")
	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}

	@Autowired(required = true)
	@Qualifier(value = "companyService")
	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}

	@Autowired(required = true)
	@Qualifier(value = "emailService")
	public void setEmailservice(EmailService emailService) {
		this.emailService = emailService;
	}

	@Autowired(required = true)
	@Qualifier(value = "locationService")
	public void setAllLocation(LocationService locationService) {
		this.locationService = locationService;
	}

	@Autowired(required = true)
	@Qualifier(value = "courseFamilyService")
	public void setAllCourseFamily(CourseFamilyService courseFamilyService) {
		this.courseFamilyService = courseFamilyService;
	}

	@Autowired(required = true)
	@Qualifier(value = "candidateService")
	public void setAllCandidateService(CandidateService candidateService) {
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

	@RequestMapping(value = "/GWFMHome", method = RequestMethod.GET)
	public String VmoUserHome() {
		LOGGER.info("Start of buadminHome");
		LOGGER.info("End of buadminHome in Controller");
		System.out.println("Inside Conroler");
		return "GWFMHome";

	}

	@RequestMapping(value = "/DownloadCSV")
	public void downloadCSV(HttpServletResponse response) throws IOException {
		response.setContentType("text/csv");
		String reportName = "Candidate_Upload_Template.csv";
		response.setHeader("Content-disposition", "attachment;filename=" + reportName);
		response.getOutputStream().print("Candidate_ID,Firstname,Lastname,Email,Mobile");
		response.getOutputStream().flush();
	}

	@RequestMapping(value = "/ShowUploadPreview")
	public ModelAndView showUploadPreview(HttpServletRequest request) throws ParseException {
		LOGGER.info("Start of showUploadPreview");
		ModelAndView modelAndView = new ModelAndView("UploadCandidate");
		List<Location> locationList = locationService.getAllLocations();
		List<CourseFamily> courseFamilyList = courseFamilyService.getCourseFamily();
		modelAndView.addObject("location", locationList);
		modelAndView.addObject("coursefamily", courseFamilyList);

		Integer location = !request.getParameter("locationDrop").equals("")
				? Integer.parseInt(request.getParameter("locationDrop")) : null;
		Integer course = !request.getParameter("courseDrop").equals("")
				? Integer.parseInt(request.getParameter("courseDrop")) : null;
		SimpleDateFormat sdfSource = new SimpleDateFormat("dd/MM/yyyy");
		Date date = !request.getParameter("date").equals("") ? sdfSource.parse(request.getParameter("date")) : null;

		String locationName = null;
		if (location != null) {
			locationName = getLocationName(locationList, Integer.parseInt(request.getParameter("locationDrop")));
		}

		String courseName = null;
		if (course != null) {
			courseName = getCourseName(courseFamilyList, Integer.parseInt(request.getParameter("courseDrop")));
		}

		modelAndView.addObject("selcou", course);
		modelAndView.addObject("selloc", location);
		modelAndView.addObject("loc", locationName);
		modelAndView.addObject("course", courseName);
		modelAndView.addObject("date", date);

		return modelAndView;
	}

	@RequestMapping(value = "/UploadCandidate")
	public ModelAndView uploadCandidate(HttpServletRequest request, HttpSession session) throws ParseException {
		LOGGER.info("Start of UploadCandidate");

		ModelAndView modelAndView = new ModelAndView("UploadCandidate");
		List<Location> locationList = locationService.getAllLocations();
		List<CourseFamily> courseFamilyList = courseFamilyService.getCourseFamily();
		modelAndView.addObject("location", locationList);
		modelAndView.addObject("coursefamily", courseFamilyList);

		LOGGER.info("End of UploadCandidate in Controller");
		return modelAndView;
	}

	@RequestMapping(value = "/ConfirmUpload")
    public ModelAndView confirmUpload(HttpServletRequest request, HttpSession session) throws ParseException {
        LOGGER.info("Start of confirmUpload");
        ModelAndView modelAndView = new ModelAndView("UploadCandidate");
        Integer locationId = !request.getParameter("locationId").equals("")
                ? Integer.parseInt(request.getParameter("locationId")) : null;
        Integer courseId = !request.getParameter("courseId").equals("")
                ? Integer.parseInt(request.getParameter("courseId")) : null;
        SimpleDateFormat sdfSource = new SimpleDateFormat("dd/MM/yyyy");
        Date date = !request.getParameter("doj").equals("") ? sdfSource.parse(request.getParameter("doj")) : null;
        
        List<Location> locationList = locationService.getAllLocations();
        List<CourseFamily> courseFamilyList = courseFamilyService.getCourseFamily();
        modelAndView.addObject("location", locationList);
        modelAndView.addObject("coursefamily", courseFamilyList);
        ArrayList<UploadCandidate> canlist = (ArrayList<UploadCandidate>) session.getAttribute("canlist");
        ArrayList<Candidate> candidates = new ArrayList<Candidate>();
        if (canlist != null) {
            for (UploadCandidate uc : canlist) {
                Candidate candidate = new Candidate();
                candidate.setCaCandidateId(uc.getCaCandidateId());
                candidate.setCaFirstName(uc.getCaFirstName());
                candidate.setCaLastName(uc.getCaLastName());
                candidate.setCaEmail(uc.getCaEmail());
                candidate.setCaMobile(uc.getCaMobile());
                candidate.setCaActive('Y');
                candidate.setCaNotificationSent('N');
                candidate.setCaRegistered('N');
                candidate.setCaPasswordChanged('N');
                candidate.setCaPassword(generateRandomPassword());
                LOGGER.debug("Send Mail: " + request.getParameter("sendMail"));
                if (request.getParameter("sendMail") != null) {
                    candidate.setCaNotificationSent('Y');
                } else {
                    candidate.setCaNotificationSent('N');
                }
                

                if (locationId != null) {
                    Location location = new Location();
                    location.setLoId(locationId);
                    candidate.setLocation(location);
                }

                if (courseId != null) {
                    CourseFamily courseFamily = new CourseFamily();
                    courseFamily.setCfId(courseId);
                    candidate.setCourseFamily(courseFamily);
                }
                candidate.setCaDoj(date);
                candidates.add(candidate);
            }
        }
        
        Set<String> recordpresents = candidateService.bulkUploadCandidate(candidates);
        System.out.println("details present already "+recordpresents);
        if(recordpresents.isEmpty())
        {
              System.out.println("tresting srdgr");
              modelAndView.addObject("message", "Candidates uploaded successfully.");    
              modelAndView.addObject("recordpresent", "null");
              request.getSession().setAttribute("errors", null);
        }
        else
        {
              ArrayList<CsvFileError> list = new ArrayList<CsvFileError>();
              for(String record : recordpresents)
              {
                     CsvFileError obj = new CsvFileError(record);
                     list.add(obj);
              }
              modelAndView.addObject("message", null);
              modelAndView.addObject("recordpresent", "Record already Uploaded");
              request.getSession().setAttribute("errors", list);
        }
      
        request.getSession().setAttribute("canlist", null);
        
        LOGGER.info("End of confirmUpload");
        return modelAndView;
    }


	private String getLocationName(List<Location> locations, Integer locationId) {
		String locationName = null;
		for (Location location : locations) {
			if (locationId.equals(location.getLoId())) {
				locationName = location.getLoName();
			}
		}
		return locationName;
	}

	private String getCourseName(List<CourseFamily> courseFamilies, Integer courseId) {
		String courseName = null;
		for (CourseFamily courseFamily : courseFamilies) {
			if (courseId.equals(courseFamily.getCfId())) {
				courseName = courseFamily.getCfName();
			}
		}
		return courseName;
	}

	@RequestMapping(value = "/ShowSearchCandidates", method = RequestMethod.GET)
	public ModelAndView showSearchCandidates(HttpServletRequest request) {
		LOGGER.info("Start of showSearchCandidates in Controller");
		ModelAndView model = new ModelAndView("FilterCandidates");
		List<Location> locationList = locationService.getAllLocations();
		List<CourseFamily> courseFamilyList = courseFamilyService.getCourseFamily();
		model.addObject("location", locationList);
		model.addObject("coursefamily", courseFamilyList);
		LOGGER.info("End of showSearchCandidates in Controller");
		return model;
	}

	@RequestMapping(value = "/FilterCandidates", method = RequestMethod.POST)
	public ModelAndView filterCandidates(HttpServletRequest request) throws ParseException {
		LOGGER.info("Start of filterCandidates in Controller");
		ModelAndView model = new ModelAndView("FilterCandidates");
		List<Location> locationList = locationService.getAllLocations();
		List<CourseFamily> courseFamilyList = courseFamilyService.getCourseFamily();

		SimpleDateFormat sdfSource = new SimpleDateFormat("dd/MM/yyyy");
		Integer location = !request.getParameter("locationDrop").equals("")
				? Integer.parseInt(request.getParameter("locationDrop")) : null;
		Integer course = !request.getParameter("courseDrop").equals("")
				? Integer.parseInt(request.getParameter("courseDrop")) : null;
		Character active = !request.getParameter("activity").equals("B") ? request.getParameter("activity").charAt(0)
				: null;
		Character mail = !request.getParameter("email").equals("B") ? request.getParameter("email").charAt(0) : null;
		String candidateId = !request.getParameter("candidate").equals("") ? request.getParameter("candidate") : null;
		Date doj = !request.getParameter("doj").equals("") ? sdfSource.parse(request.getParameter("doj")) : null;

		LOGGER.debug("****location:" + location + ";course" + course + ";active" + active + ";mail" + mail
				+ ";candidateId" + candidateId + ";doj" + doj);

		List<Candidate> candidateList = candidateService.filter(candidateId, location, course, doj, active, mail);

		LOGGER.debug("***Candidate" + candidateList.size());
		model.addObject("location", locationList);
		model.addObject("selected", location);
		model.addObject("selectedcourse", course);
		model.addObject("active", active);
		model.addObject("mail", mail);
		model.addObject("id", candidateId);
		model.addObject("doj", doj);
		model.addObject("candidate", candidateList);
		model.addObject("coursefamily", courseFamilyList);
		LOGGER.info("End of filterCandidates in Controller");
		return model;
	}

	private String generateRandomPassword() {
		Random random = new SecureRandom();
		String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789+@";

		String password = "";
		for (int i = 0; i < 8; i++) {
			int index = (int) (random.nextDouble() * letters.length());
			password += letters.substring(index, index + 1);
		}
		return password;
	}

	@RequestMapping(value = "/DeactivateCandidates", method = RequestMethod.GET)
	public ModelAndView deactivateCandidate(HttpServletRequest request) {
		LOGGER.info("Start of deactivateCandidate in Controller");
		ModelAndView modelAndView = new ModelAndView("DeactivateCandidates");
		List<Location> locationList = locationService.getAllLocations();
		modelAndView.addObject("location", locationList);
		LOGGER.info("End of deactivateCandidate in Controller");
		return modelAndView;
	}

	@RequestMapping(value = "/SearchCandidates", method = RequestMethod.POST)
	public ModelAndView searchCandidate(HttpServletRequest request) throws ParseException {
		LOGGER.info("Start of searchCandidate in Controller");

		ModelAndView model = new ModelAndView("DeactivateCandidates");
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

	@RequestMapping(value = "/DisableCandidates", method = RequestMethod.POST)
	public ModelAndView disableCandidate(HttpServletRequest request) {
		LOGGER.info("Start of disableCandidate in Controller");
		ModelAndView modelAndView = new ModelAndView("DeactivateCandidates");
		List<Candidate> candidateList = new ArrayList<Candidate>();
		Candidate candidate = new Candidate();
		int candidateId = 0;
		String[] id = request.getParameterValues("check");
		if (id != null) {
			for (String s : id) {
				if (!s.equals("")) {
					candidateId = Integer.parseInt(s);
					candidate.setCaId(candidateId);
					candidateList.add(candidate);
				}
				adminService.disableCandidate(candidateList);
			}
		}

		LOGGER.info("End of disableCandidate in Controller");
		modelAndView.addObject("checked", id);
		return modelAndView;
	}

}

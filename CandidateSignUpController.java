package com.cts.lch.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.cts.lch.marketplace.exception.LchBusinessException;
import com.cts.lch.marketplace.model.Candidate;
import com.cts.lch.marketplace.model.CertAttempt;
import com.cts.lch.marketplace.model.Demand;
import com.cts.lch.marketplace.service.CandidatePocService;
import com.cts.lch.marketplace.service.CandidateService;
import com.cts.lch.marketplace.service.CertAttemptService;
import com.cts.lch.marketplace.service.DemandService;

@Controller
@SessionAttributes({"candidateId","id"})
public class CandidateSignUpController {
	private static final Logger LOGGER = Logger.getLogger(AppAdminController.class);
	CandidateService candidateService;
	CertAttemptService certAttemptService;
	CandidatePocService candidatePocService;
	DemandService demandService;
	
	@Autowired(required = true)
	@Qualifier(value = "demandService")
	public void setDemandService(DemandService demandService) {
		this.demandService = demandService;
	}
	
	@Autowired(required = true)
	@Qualifier(value = "candidateService")
	public void setCandidateService(CandidateService candidateService) {
		this.candidateService = candidateService;
	}

	@Autowired(required = true)
	@Qualifier(value = "certAttemptService")
	public void setCertAttemptService(CertAttemptService certAttemptService) {
		this.certAttemptService = certAttemptService;
	}

	@Autowired(required = true)
	@Qualifier(value = "candidatePocService")
	public void setCandidatePocService(CandidatePocService candidatePocService) {
		this.candidatePocService = candidatePocService;
	}

	@RequestMapping(value = "/CourseSelection", method = RequestMethod.GET)
	public String courseSelection() {
		LOGGER.info("Start of courseSelection in Controller");
		
		return "CourseSelection";
	}

	@RequestMapping(value = "/GetStarted", method = RequestMethod.GET)
	public ModelAndView candidateGetStarted(HttpServletRequest req) {
		LOGGER.info("Start of candidateSignUp in Controller");
		ModelAndView model = new ModelAndView("CandidateGetStarted");
		LOGGER.info("End of candidateSignUp in Controller");
		return model;
	}

	/*@RequestMapping(value = "/CandidateHome", method = RequestMethod.GET)
	public ModelAndView changePassword(@ModelAttribute("id") String id, HttpServletRequest request) {

		LOGGER.info("Start of changePassword in Controller");
		ModelAndView model = new ModelAndView("CandidateHome");
		int success = 0;
		String status = new String();
		String old = request.getParameter("password");
		String change = request.getParameter("password1");
		String confirm = request.getParameter("password2");
		model.addObject("id",id);
		LOGGER.info("End of changePassword in Controller");
		return model;
	}

	@RequestMapping(value = "/GetPassword", method = RequestMethod.POST)
	public ModelAndView getPassword(HttpServletRequest request, HttpServletResponse response)
			throws LchBusinessException, ServletException, IOException {

		LOGGER.info("Start of resetPassword in Controller");
		ModelAndView model = new ModelAndView("CandidateHome");
		HttpSession session = request.getSession();
		int success = 0;
		int match = 0;
		int ex = 0;
		String status = null;
		String message = null;
		String old = request.getParameter("password");
		String change = request.getParameter("password1");
		String confirm = request.getParameter("password2");
		session.setAttribute("candidateId", "572794");
		String candidateId = (String) session.getAttribute("candidateId");
		System.out.println("OLD " + old + " NEW " + change + " CONFIRM " + confirm + "ID " + candidateId);
		try {
			if (change.equals(confirm)) {
				candidateService.changePassword(candidateId, old, change);
				status = "yes";
			}

		} catch (LchBusinessException exception) {
				message = "yes";
			
		}
		model.addObject("changed", status);
		model.addObject("old", old);
		model.addObject("change", change);
		model.addObject("confirm", confirm);
		model.addObject("match", message);
		LOGGER.info("End of resetPassword in Controller");
		return model;
	}*/
	
	@RequestMapping(value = "/CandidateHomePage/{demandId}/{courseFamilyId}/{joiningDate}", method = RequestMethod.GET)
	public ModelAndView ViewcandidateDashBoard(@PathVariable @DateTimeFormat(pattern="dd-MMM-yyyy") Date joiningDate,@PathVariable Integer courseFamilyId, @PathVariable Integer demandId, @ModelAttribute("candidateId") Integer id) throws ParseException, LchBusinessException {
		LOGGER.info("Start of candidateHome in Controller");
		ModelAndView model = new ModelAndView("CandidateHomePage");	
		LOGGER.debug("**CANDIDATE**"+id);
		LOGGER.debug("COURSEFAMILYID--"+courseFamilyId);
		// int caId=Integer.parseInt(req.getParameter("caId"));
		candidateService.updateCandidateCourseFamily(id, courseFamilyId, joiningDate);
		Candidate candidate = (Candidate) candidateService.getCandidateWithPoc(id);
		System.out.println(candidate.getCaDoj());
		Demand demand = demandService.getDemand(demandId);
		Integer filledCount = demand.getDeFulfilledCount();
		demand.setDeFulfilledCount(filledCount+1);
		demandService.modifyDemand(demand);
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String cadoj = dateFormat.format(candidate.getCaDoj());
		String curdate = dateFormat.format(date);
		Date d = dateFormat.parse(cadoj);
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		long d1 = c.getTimeInMillis();
		d = dateFormat.parse(curdate);
		c.setTime(d);
		long d2 = c.getTimeInMillis();
		Integer n = Math.abs((int) ((d1 - d2) / (1000 * 3600 * 24)));
		ArrayList<CertAttempt> certAttempt = (ArrayList<CertAttempt>) certAttemptService
				.getAllCertAttemptByCandidateId(id);
		DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
		String cdate = formatter.format(candidate.getCaDoj());
		Date cDojdate = (Date) formatter.parse(cdate);

		Calendar cal = Calendar.getInstance();
		cal.setTime(cDojdate);
		String formatedDate = "";
		if (cal.get(Calendar.DAY_OF_MONTH) < 10)
			formatedDate += "(0" + cal.get(Calendar.DAY_OF_MONTH) + "/";
		else
			formatedDate += "(" + cal.get(Calendar.DAY_OF_MONTH) + "/";
		if (cal.get(Calendar.MONTH) + 1 < 10)
			formatedDate += "0" + (cal.get(Calendar.MONTH) + 1) + "/";
		else
			formatedDate += (cal.get(Calendar.MONTH) + 1) + "/";
		formatedDate += cal.get(Calendar.YEAR) + ")";

		model.addObject("certAttempt", certAttempt);
		model.addObject("numberOfDays", n);
		model.addObject("candidate", candidate);
		model.addObject("formatedDate", formatedDate);
		model.addObject("course",candidate.getCourseFamily().getCourses()); 
		return model;
	}
	
	private ModelAndView getCandidateDetails(Integer candidateId) throws ParseException, LchBusinessException {
		LOGGER.info("Start");
		ModelAndView model = new ModelAndView("CandidateHomePage");	
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Candidate candidate = (Candidate) candidateService.getCandidateWithPoc(candidateId);
		String cadoj = dateFormat.format(candidate.getCaDoj());
		String curdate = dateFormat.format(date);
		Date d = dateFormat.parse(cadoj);
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		long d1 = c.getTimeInMillis();
		d = dateFormat.parse(curdate);
		c.setTime(d);
		long d2 = c.getTimeInMillis();
		Integer n = Math.abs((int) ((d1 - d2) / (1000 * 3600 * 24)));
		ArrayList<CertAttempt> certAttempt = (ArrayList<CertAttempt>) certAttemptService
				.getAllCertAttemptByCandidateId(candidateId);
		DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
		String cdate = formatter.format(candidate.getCaDoj());
		Date cDojdate = (Date) formatter.parse(cdate);

		Calendar cal = Calendar.getInstance();
		cal.setTime(cDojdate);
		String formatedDate = "";
		if (cal.get(Calendar.DAY_OF_MONTH) < 10)
			formatedDate += "(0" + cal.get(Calendar.DAY_OF_MONTH) + "/";
		else
			formatedDate += "(" + cal.get(Calendar.DAY_OF_MONTH) + "/";
		if (cal.get(Calendar.MONTH) + 1 < 10)
			formatedDate += "0" + (cal.get(Calendar.MONTH) + 1) + "/";
		else
			formatedDate += (cal.get(Calendar.MONTH) + 1) + "/";
		formatedDate += cal.get(Calendar.YEAR) + ")";

		model.addObject("certAttempt", certAttempt);
		model.addObject("numberOfDays", n);
		model.addObject("candidate", candidate);
		model.addObject("formatedDate", formatedDate);
		model.addObject("course",candidate.getCourseFamily().getCourses()); 
		return model;
	}


	@RequestMapping(value = "/LearningDashboard", method = RequestMethod.POST)
	public ModelAndView ChangeCandidateDoj(@RequestParam("ChangeDoj") String newDoj,@ModelAttribute("candidateId") Integer id)
			throws ParseException, LchBusinessException {
		LOGGER.info("Start of LearningDashboard in Controller");

		// int caId=Integer.parseInt(req.getParameter("caId"));

		StringTokenizer st = new StringTokenizer(newDoj, "/");
		String day = null;
		String month = null;
		String year = null;
		while (st.hasMoreTokens()) {
			day = st.nextToken();
			month = st.nextToken();
			year = st.nextToken();

		}
		StringBuffer sb = new StringBuffer();
		sb.append(year).append("-").append(month).append("-").append(day);
		newDoj = sb.toString();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Candidate candidate = (Candidate) candidateService.getCandidateWithPoc(id);
		Date date = new Date();
		Date d3 = dateFormat.parse(newDoj);
		candidate.setCaDoj(d3);
		candidateService.updateCandidate(candidate);
		// Candidate
		// updatedCandidate=(Candidate)candidateService.getCandidateById(1);
		String cadoj = dateFormat.format(candidate.getCaDoj());
		String curdate = dateFormat.format(date);
		Date d = dateFormat.parse(cadoj);
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		long d1 = c.getTimeInMillis();
		d = dateFormat.parse(curdate);
		c.setTime(d);
		long d2 = c.getTimeInMillis();
		Integer n = Math.abs((int) ((d1 - d2) / (1000 * 3600 * 24)));

		System.out.println(candidate.getCaDoj());

		ArrayList<CertAttempt> certAttempt = (ArrayList<CertAttempt>) certAttemptService
				.getAllCertAttemptByCandidateId(id);
		DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
		String cdate = formatter.format(candidate.getCaDoj());
		Date cDojdate = (Date) formatter.parse(cdate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(cDojdate);
		String formatedDate = "";
		if (cal.get(Calendar.DAY_OF_MONTH) < 10)
			formatedDate += "(0" + cal.get(Calendar.DAY_OF_MONTH) + "/";
		else
			formatedDate += "(" + cal.get(Calendar.DAY_OF_MONTH) + "/";
		if (cal.get(Calendar.MONTH) + 1 < 10)
			formatedDate += "0" + (cal.get(Calendar.MONTH) + 1) + "/";
		else
			formatedDate += (cal.get(Calendar.MONTH) + 1) + "/";
		formatedDate += cal.get(Calendar.YEAR) + ")";

		ModelAndView model = new ModelAndView("CandidateHomePage");
		model.addObject("certAttempt", certAttempt);
		model.addObject("numberOfDays", n);
		model.addObject("candidate", candidate);
		model.addObject("formatedDate", formatedDate);
		model.addObject("course",candidate.getCourseFamily().getCourses()); 
		LOGGER.info("End of LearningDashboard in Controller");
		return model;
	}

	@RequestMapping(value = "/CandidateHomePage", method = RequestMethod.POST)
	public ModelAndView cancelBookedSlot(@RequestParam("CancelCertId") int ctId,@ModelAttribute("candidateId") Integer id)
			throws LchBusinessException, ParseException {
		Candidate candidate = (Candidate) candidateService.getCandidateWithPoc(id);

		ModelAndView model = new ModelAndView("CandidateHomePage");
		System.out.println(candidate.getCaDoj());
		// String candidateDoj=candidate.getCaDoj();
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String cadoj = dateFormat.format(candidate.getCaDoj());
		String curdate = dateFormat.format(date);
		Date d = dateFormat.parse(cadoj);
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		long d1 = c.getTimeInMillis();
		d = dateFormat.parse(curdate);
		c.setTime(d);
		long d2 = c.getTimeInMillis();
		Integer n = Math.abs((int) ((d1 - d2) / (1000 * 3600 * 24)));

		CertAttempt certAttempt1 = certAttemptService.getCertAttemptById(ctId);

		certAttemptService.modifyCertAttempt(certAttempt1);

		ArrayList<CertAttempt> certAttempt = (ArrayList<CertAttempt>) certAttemptService
				.getAllCertAttemptByCandidateId(id);

		DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
		String cdate = formatter.format(candidate.getCaDoj());
		Date cDojdate = (Date) formatter.parse(cdate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(cDojdate);
		String formatedDate = "";
		if (cal.get(Calendar.DAY_OF_MONTH) < 10)
			formatedDate += "(0" + cal.get(Calendar.DAY_OF_MONTH) + "/";
		else
			formatedDate += "(" + (cal.get(Calendar.DAY_OF_MONTH) + 1) + "/";
		if (cal.get(Calendar.MONTH) + 1 < 10)
			formatedDate += "0" + (cal.get(Calendar.MONTH) + 1) + "/";
		else
			formatedDate += (cal.get(Calendar.MONTH) + 1) + "/";
		formatedDate += cal.get(Calendar.YEAR) + ")";
		// String formatedDate = cal.get(Calendar.DAY_OF_MONTH) + "/" +
		// (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);

		model.addObject("certAttempt", certAttempt);
		model.addObject("numberOfDays", n);
		model.addObject("candidate", candidate);

		model.addObject("formatedDate", formatedDate);
		model.addObject("course",candidate.getCourseFamily().getCourses()); 
		LOGGER.info("End of Cancel Booked Slot in Controller");
		return model;

	}

	@RequestMapping(value = "/CandidateSignUp", method = RequestMethod.GET)
	public ModelAndView candidateSignUpGet(HttpServletRequest req) {
		LOGGER.info("Start of candidateSignUp in Controller");
		ModelAndView model = new ModelAndView("CandidateSignUp");
		LOGGER.info("End of candidateSignUp in Controller");
		return model;
	}

	@RequestMapping(value = "/CandidateSign", method = RequestMethod.POST)
	public ModelAndView candidateSignUpPost(@RequestParam("email") String email,
			@RequestParam("password") String password) throws LchBusinessException {
		LOGGER.info("Start of candidateSignUp in Controller");
		ModelAndView modelView = new ModelAndView("CandidateSignUp");
		try {
			Candidate candidate = candidateService.authenticateCandidate(email, password);
			ModelAndView model = new ModelAndView("CandidateRegister");
			String candidateId = candidate.getCaCandidateId();
			String firstName = candidate.getCaFirstName();
			String lastName = candidate.getCaLastName();
			Date dob = candidate.getCaDob();
			LOGGER.info("Date of birth" + " " + dob);
			String emailId = candidate.getCaEmail();
			String mobile = candidate.getCaMobile();
			model.addObject("id", candidateId);
			model.addObject("fname", firstName);
			model.addObject("lname", lastName);
			model.addObject("dob", dob);
			model.addObject("email", emailId);
			model.addObject("mobile", mobile);
			model.addObject("candidate", candidate);
			return model;
		} catch (LchBusinessException e) {
			ModelAndView model = new ModelAndView("CandidateSignUp");
			LOGGER.info("Catch block candidateRegister in Controller" + " " + e.getMessage());
			model.addObject("message", e.getMessage());
			return model;
		}
	}

	@RequestMapping(value = "/CandidateRegister", method = RequestMethod.GET)
	public ModelAndView candidateRegisterGet(HttpServletRequest req) throws ParseException, LchBusinessException {
		LOGGER.info("Start of CandidateRegister in Controller");
		ModelAndView model = new ModelAndView("CandidateRegister");
		SimpleDateFormat sdfSource = new SimpleDateFormat("dd/MM/yyyy");
		String candidateId = req.getParameter("candidateId");
		int id = candidateService.getIdByName(candidateId);
		Candidate candidate = candidateService.getCandidateById(id);
		String firstName = req.getParameter("firstName");
		String lastName = req.getParameter("lastName");
		Date dob = !req.getParameter("caDob").equals("") ? sdfSource.parse(req.getParameter("caDob")) : null;
		LOGGER.info("Date of birth" + " " + dob);
		String email = req.getParameter("email");
		System.out.println("EMAIL" + email);
		String mobile = req.getParameter("mobile");
		model.addObject("id", candidateId);
		model.addObject("fname", firstName);
		model.addObject("lname", lastName);
		model.addObject("dob", dob);
		model.addObject("email", email);
		model.addObject("mobile", mobile);
		LOGGER.info("End of CandidateRegister in Controller");
		return model;
	}

	@RequestMapping(value = "/CandidateReg", method = RequestMethod.POST)
    public ModelAndView candidateRegisterPost(HttpServletRequest req) throws ParseException {
           LOGGER.info("Start of CandidateRegister in Controller");
           ModelAndView model = new ModelAndView("CandidateRegister");
           SimpleDateFormat sdfSource = new SimpleDateFormat("dd/MM/yyyy");
           int caId = Integer.parseInt(req.getParameter("caId"));
           Candidate candidate1 = candidateService.getCandidateById(caId);
           String candidateId = req.getParameter("candidateId");
           String firstName = req.getParameter("firstName");
           String lastName = req.getParameter("lastName");
           String email = req.getParameter("email");
           String mobile = req.getParameter("mobile");
           candidate1.setCaId(caId);
           candidate1.setCaCandidateId(candidateId);
           candidate1.setCaFirstName(firstName);
           candidate1.setCaLastName(lastName);
           candidate1.setCaDob(candidate1.getCaDob());
           candidate1.setCaEmail(email);
           candidate1.setCaMobile(mobile);
           candidate1.setCaProfileUrl(candidate1.getCaProfileUrl());
           candidate1.setCaPassword(candidate1.getCaPassword());
           candidate1.setCaDoj(candidate1.getCaDoj());
           candidate1.setCaNotificationSent(candidate1.getCaNotificationSent());
           candidate1.setCaActive(candidate1.getCaActive());
           candidate1.setCaRegistered('Y');
           candidateService.updateCandidate(candidate1);
           String check = null;
           check = "yes";
           model.addObject("check", check);
           model.addObject("candidate", candidate1);
           LOGGER.info("End of CandidateRegister in Controller");
           return model;
    }


	@RequestMapping(value = "/ResetPassword", method = RequestMethod.GET)
	public ModelAndView resetpassword(HttpServletRequest req) throws LchBusinessException {
		LOGGER.info("Start of ResetPassword in Controller");
		ModelAndView model = new ModelAndView("ResetPassword");
		int id = 0;
		String candidate = req.getParameter("id");
		String mail = req.getParameter("candidatemail");
		String old = req.getParameter("oldpassword");
		String newpassword = req.getParameter("newpassword");
		String confirm = req.getParameter("confirmpassword");
		model.addObject("mail", mail);
		model.addObject("id", candidate);
		LOGGER.info("End of ResetPassword in Controller");
		return model;
	}

	@RequestMapping(value = "/ResetSuccess", method = RequestMethod.POST)
	public ModelAndView resetsuccess(HttpServletRequest req) throws ParseException, LchBusinessException {
		LOGGER.info("Start of resetsuccess in Controller");
		ModelAndView model = new ModelAndView("ResetPassword");
		int success = 0;
		int match = 0;
		int ex = 0;
		String status = null;
		String message = null;
		int id = 0;
		Integer cf = null;
		String candidateid = req.getParameter("id");
		String mail = req.getParameter("candidatemail");
		String oldpassword = req.getParameter("oldpassword");
		String newpassword = req.getParameter("newpassword");
		String confirm = req.getParameter("confirmpassword");
		id = candidateService.getIdByName(candidateid);
		Candidate samecandidate = candidateService.getCandidateById(id);
		if (samecandidate.getCourseFamily() != null) {
			cf = samecandidate.getCourseFamily().getCfId();
		} else {
			cf = null;
		}
		try {
			candidateService.changePassword(candidateid, oldpassword, newpassword);
			Candidate c = candidateService.getCandidateById(id);
			String update = c.getCaPassword();
			success = 1;
			if (success > 0) {
				status = "yes";
			}
			samecandidate.setCaPassword(update);
			samecandidate.setCaPasswordChanged('Y');
			candidateService.updateCandidate(samecandidate);
			if(success>0)
			{
				ModelAndView modelView = new ModelAndView("CandidateLogin");
				modelView.addObject("changed", status);
				return modelView;
			}
		} catch (LchBusinessException exception) {
			if (success != 1) {
				message = "yes";
			}
		}

		LOGGER.info("End of resetsuccess in Controller");
		model.addObject("mail", mail);
		model.addObject("id", candidateid);
		model.addObject("old", oldpassword);
		model.addObject("newpassword", newpassword);
		model.addObject("confirm", confirm);	
		model.addObject("match", message);
		return model;
	}

	@RequestMapping(value = "/CandidateLogin", method = RequestMethod.GET)
	public ModelAndView candidateLogin(HttpServletRequest req) {
		LOGGER.info("Start of candidateLogin in Controller");
		ModelAndView model = new ModelAndView("CandidateLogin");
		String id = null;
		String pwd = null;
		id = req.getParameter("CandidateId");
		pwd = req.getParameter("password");
		LOGGER.info("End of candidateLogin in Controller");
		return model;
	}

	@RequestMapping(value = "/CandidateLoginPost", method = RequestMethod.POST)
	public ModelAndView candidateLoginPost(HttpServletRequest req,Model models) throws LchBusinessException, ParseException {
		LOGGER.info("Start of candidateLogin in Controller");
		ModelAndView model = new ModelAndView("CandidateLogin");
		String id = null;
		String pwd = null;
		String mail = null;
		String changed = null;
		String message = null;
		String register = null;
		Character pwdChange = null;
		Character reg = null;
		Integer cf = null;
		Integer caId = null;
		id = req.getParameter("CandidateId");
		pwd = req.getParameter("password");
		try {
			Candidate candidateList = candidateService.authenticateByIdPassword(id, pwd);
			pwdChange = candidateList.getCaPasswordChanged();
			caId = candidateList.getCaId();
			LOGGER.debug("CANDIDATE ID"+caId);
			models.addAttribute("candidateId",caId);
			models.addAttribute("id",id);
			if (candidateList.getCourseFamily() != null) {
				cf = candidateList.getCourseFamily().getCfId();
			} else {
				cf = null;
			}
			mail = candidateList.getCaEmail();
			if (pwdChange == 'Y') {
				changed = "yes";
					ModelAndView model1 = new ModelAndView("CandidateHome");
					return model1;		

			} else {
				ModelAndView model2 = new ModelAndView("ResetPassword");
				model2.addObject("id", id);
				model2.addObject("mail", mail);
				return model2;
			}

		} catch (LchBusinessException exception) {
			LOGGER.info("Catch block candidateLogin in Controller" + " " + exception.getMessage());
			model.addObject("message", exception.getMessage());
		}
		model.addObject("changed", changed);
		model.addObject("reg", register);
		LOGGER.info("End of candidateLogin in Controller");
		return model;
	}
}

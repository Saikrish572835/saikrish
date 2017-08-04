/*
 * Copyright (c) 2016 Cognizant Technology Solutions.
 * 
 * This software belongs to Cognizant Technology Solutions. 
 * Any replication or reuse requires permission from Cognizant 
 * Technology Solutions.
 * 
 */

package com.cts.lch.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cts.lch.marketplace.exception.LchBusinessException;
import com.cts.lch.marketplace.model.Candidate;
import com.cts.lch.marketplace.model.CertFacility;
import com.cts.lch.marketplace.model.Certification;
import com.cts.lch.marketplace.model.CourseFamilyTrack;
import com.cts.lch.marketplace.model.Demand;
import com.cts.lch.marketplace.model.Location;
import com.cts.lch.marketplace.model.Track;
import com.cts.lch.marketplace.service.CandidateService;
import com.cts.lch.marketplace.service.CertFacilityService;
import com.cts.lch.marketplace.service.CertificationService;
import com.cts.lch.marketplace.service.DemandService;
import com.cts.lch.marketplace.service.LocationService;
import com.cts.lch.marketplace.service.TrackService;

/**
 * This class represents the controller of Application Administrator for the
 * class Education Organization and manages the functionalities of the Education
 * Organization entity.
 */
@Controller
public class CandidateController  {

	private static final Logger LOGGER = Logger.getLogger(CandidateController.class);

	private CandidateService candidateService;
	CertificationService certificationService;
    Candidate candidate=new Candidate();
	LocationService locationService;
	DemandService demandService;
	TrackService trackService;
	CertFacilityService certFacilityService;
	@Autowired(required = true)
	@Qualifier(value = "certificationService")
	public void setCertificationService(CertificationService certificationService) {
		this.certificationService = certificationService;
	}

	@Autowired(required = true)
	@Qualifier(value = "locationService")
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	@Autowired(required = true)
	@Qualifier(value = "demandService")
	public void setDemandService(DemandService demandService) {
		this.demandService = demandService;
	}
	
	@Autowired(required = true)
	@Qualifier(value = "trackService")
	public void setTrackService(TrackService trackService) {
		this.trackService = trackService;
	}
	
	@Autowired(required = true)
	@Qualifier(value = "candidateService")
	public void setAllCandidateService(CandidateService candidateService) {
		this.candidateService = candidateService;
	}
	
	@Autowired(required = true)
	@Qualifier(value = "certFacilityService")
	public void setCertFacilityService(CertFacilityService certFacilityService) {
		this.certFacilityService = certFacilityService;
	}

	@RequestMapping(value = "/CertificationSlot", method = RequestMethod.GET)
    public ModelAndView certificationSlot(HttpServletRequest req, HttpServletResponse response) throws IOException {

        LOGGER.info("Start of CertificationSlot in Controller");     
        ModelAndView model = new ModelAndView("CertificationSlot");
        String city = req.getParameter("city");
        String branch = req.getParameter("branch");
        List<CertFacility> certList = certFacilityService.getAllCertFacility();
        List<CertFacility> cityList =new ArrayList<CertFacility>();
        List<String> monthList = new ArrayList<String>();
		Format formatter = new SimpleDateFormat("MMM"); 
		DateFormat df = new SimpleDateFormat("yy");
		Date now = new Date();
		String currentMonth = formatter.format(now);
		String currentYear = df.format(now);
		String newDate = currentMonth.concat("'"+currentYear);
		LOGGER.info("MONTH"+newDate);
		monthList.add(newDate);
		Calendar myCal = Calendar.getInstance();
		for(int i=0;i<5;i++)
		{
		Date date = now;
	    myCal.setTime(date);    
		myCal.add(Calendar.DATE, +31); 
		date = myCal.getTime();
		now = date;
		String month = formatter.format(now);
		String year = df.format(now);
	    String newDat = month.concat("'"+year);
	    monthList.add(newDat);
		LOGGER.info("MONTH "+month);
		}
		for (String temp : monthList) {
			System.out.println("LIST VALUES"+temp);
		}

        for(CertFacility cert:certList)
        {
        	cityList.add(cert); 
        }
        
        if(city!=null)
        {
        	List<CertFacility> loc = certFacilityService.getLocation(city);
        	List<String> str = new ArrayList<String>();
        	  LOGGER.debug("---------LOCSIZE----------" +loc.size());
        	  for(CertFacility l:loc)
        	  {
        		  str.add(l.getCyName());
        	  }
      	 
        }
        removeDuplicate(cityList);
        LOGGER.debug("---------SIZE----------" +cityList.size());
        model.addObject("city",cityList);
        model.addObject("cert",certList);
        model.addObject("monthList",monthList);
        LOGGER.info("End of CertificationSlot in Controller");
        return model;
	}
	
	@RequestMapping(value = "/Branch", method = RequestMethod.GET)
    public void branch(HttpServletResponse response,HttpServletRequest req) throws LchBusinessException, ParseException, IOException {
           String city = req.getParameter("city");
           String branch = req.getParameter("branch");
           LOGGER.debug("---------City----------" +city);
           LOGGER.debug("---------BRANCH----------" +branch);
           String s="";
           String prefix = "";
           StringBuffer sBuffer = new StringBuffer(15);
           List<CertFacility> certList = certFacilityService.getAllCertFacility();
           List<CertFacility> cityList =new ArrayList<CertFacility>();
           response.setContentType("text/html");
          PrintWriter out = response.getWriter();
           for(CertFacility cert:certList)
           {
           	cityList.add(cert); 
           }
           
           if(city!=null)
           {
           	List<CertFacility> loc = certFacilityService.getLocation(city);
           	HashMap<Integer, String> location = new HashMap<Integer, String>();
           	List<String> str = new ArrayList<String>();
           	List<Integer> str1 = new ArrayList<Integer>();
           	  LOGGER.debug("---------LOCSIZE----------" +loc.size());
           	  for(CertFacility l:loc)
           	  {
           		  location.put(l.getCyId(), l.getCyName());
           		  str.add(l.getCyName());
           		  str1.add(l.getCyId());
           	  }
           	  Iterator it = location.entrySet().iterator();
           	sBuffer.append("["); 
    while (it.hasNext()) {
        Map.Entry pair = (Map.Entry)it.next();
        System.out.println(pair.getKey().toString()+ " = " + pair.getValue().toString());
        String check  = pair.getKey().toString();
        String check1 = pair.getValue().toString();
     	sBuffer.append("{");
        sBuffer.append("\"id\":");
        
        prefix=",";
        sBuffer.append("\""+check+"\"");
        sBuffer.append(prefix);
        sBuffer.append("\"name\":");
        sBuffer.append("\""+check1+"\"");
        sBuffer.append("}");
        sBuffer.append(prefix);
        //it.remove(); // avoids a ConcurrentModificationException
    }
                 sBuffer.append("}");
                 s = sBuffer.toString();
                 String newString = s.substring(0, s.length() - 1);
               newString = newString.substring(0, newString.length() - 1);
               StringBuffer sb=new StringBuffer(newString);
               sb.append("]");
               newString = sb.toString();
              out.println(newString);
              out.flush();

   LOGGER.debug(newString+"-------STRING BUFFER-------");
           	  LOGGER.debug("---------STRLOCSIZE----------" +str.size());
           }
            
	}
	
	@RequestMapping(value = "/Calendar", method = RequestMethod.GET)
    public void calendar(HttpServletResponse response,HttpServletRequest req){
	 String cal = req.getParameter("month");
	 String month = cal.substring(0, 3);
	 String years = cal.substring(4);
	 String leap = null;
	 years = "20"+years;
	 int days = 0;
	 int year = Integer.parseInt(years);
	 System.out.println("SELECTED MONTH "+month +" Year "+year);
	 if((year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0))){
			leap="yes";
		} else {
			leap = "no";
		}
	 if(month.equals("Jan")||month.equals("Mar")||month.equals("May")||month.equals("Jul")||month.equals("Aug")||month.equals("Oct")||month.equals("Dec"))
	 {
		 LOGGER.debug("Inside IF");
		 days = 31;
	 }
	 else if(month.equals("Apr")||month.equals("Jun")||month.equals("Sep")||month.equals("Nov"))
	 {
		 days = 30;
	 }
	 else if(month.equals("Feb")&&leap.equals("yes"))
	 {
		 days = 29;
	 }
	 else if(month.equals("Feb")&&leap.equals("no"))
	 {
		 days = 28;
	 }
	 LOGGER.debug("Month "+month+" Year "+year+" Days "+days);
	}
	
	private void removeDuplicate(List<CertFacility> list)
	{
	    int count = list.size();

	    for (int i = 0; i < count; i++) 
	    {
	        for (int j = i + 1; j < count; j++) 
	        {
	            if (list.get(i).getCyCity().equals(list.get(j).getCyCity()))
	            {
	                list.remove(j--);
	                count--;
	            }
	        }
	    }
	}
	
    @RequestMapping(value = "/ShowCertification", method = RequestMethod.GET)
    public ModelAndView showCertification() {

        LOGGER.info("Start of showSolutionTeamHome in Controller");
        ModelAndView model = new ModelAndView("Certification");
        
        /*
        Certification certification=new Certification();
        certification.setCeActive('Y');
        certification.setCeDurationMinutes(120);
        certification.setCeExtRefId("12");
        certification.setCeExtRefUrl("www.pluralsight.com");
        certification.setCeId(1);
        certification.setCeName("Advanced Java");
        certification.setCePassPercent(80);
        */
        
		List <Certification> certList = new ArrayList<Certification>();
		certList.addAll(certificationService.getCertificationsByCandidateId(1));
		
		//for(int i=0;i<4;i++)
		//	certList.add(certification);
		
		model.addObject("certList", certList);
		
        LOGGER.info("End of showSolutionTeamHome in Controller");
        return model;
    }
    @RequestMapping(value = "/ChangePassword", method = RequestMethod.GET)
	public ModelAndView changePassword(HttpServletRequest request) {

		LOGGER.info("Start of changePassword in Controller");
		ModelAndView model = new ModelAndView("ChangePassword");
		int success = 0;
		String status = new String();
		String candidateId = request.getParameter("candidate");
		String old = request.getParameter("password");
		String change = request.getParameter("password1");
		String confirm = request.getParameter("password2");
		System.out.println("OLD " + old + " NEW " + change + " CONFIRM " + confirm);
		LOGGER.info("End of changePassword in Controller");
		return model;
	}

	
	@RequestMapping(value = "/ForgottenPassword", method = RequestMethod.GET)
	public ModelAndView forgottenPassword(HttpServletRequest request) throws ParseException {
		LOGGER.info("Start of forgetPassword in Controller");
		ModelAndView model = new ModelAndView("ForgottenPassword");
		SimpleDateFormat sdfSource = new SimpleDateFormat("dd/MM/yyyy");
		String email = request.getParameter("email");
		String dob = request.getParameter("dob");
		// Date date=sdfSource.parse(dob);
		LOGGER.info("End of forgetPassword in Controller");
		return model;
	}
	@RequestMapping(value = "/CertificationsCandidate", method = RequestMethod.GET)
	public ModelAndView CertificationsCandidate(HttpServletRequest request) throws ParseException {
		LOGGER.info("Start of CertificationsCandidate in Controller");
		ModelAndView model = new ModelAndView("CertificationsCandidate");
		LOGGER.info("End of CertificationsCandidate in Controller");
		return model;
	}
	

    
    @RequestMapping(value = "/CandidateDashboard", method = RequestMethod.GET)
  	public ModelAndView candidateDashboard() 
  	{
  		LOGGER.info("Start of AppAdminAdd in Controller");
  		ModelAndView model = new ModelAndView("CandidateDashboard");	  		
  		LOGGER.info("End of AppAdminAdd in Controller");
  		return model;
  	} 
    
    @RequestMapping(value = "/BookSlot", method = RequestMethod.GET)
   	public ModelAndView bookslot1() {
   		LOGGER.info("Start of CandidateRegister in Controller");
   		ModelAndView model = new ModelAndView("BookSlot");	
   
   		LOGGER.info("End of CandidateRegister in Controller");
   		return model;
   	}
    
    @RequestMapping(value = "/BookSlot", method = RequestMethod.POST)
	public ModelAndView bookslot() {
		LOGGER.info("Start of CandidateRegister in Controller");
		ModelAndView model = new ModelAndView("BookSlot");	
		model.addObject("addsuccessfully","Added");
		LOGGER.info("End of CandidateRegister in Controller");
		return model;
	}
    
    
    

	@RequestMapping(value = "/UpdatePassword", method = RequestMethod.POST)
	public ModelAndView updatePassword(HttpServletRequest request, HttpServletResponse response)
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
		session.setAttribute("candidateId", "572713");
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
	}

	/*@RequestMapping(value = "/GetDemandData")
    public void locationformat(HttpServletResponse response) throws LchBusinessException, ParseException, IOException {
           response.setContentType("text/html");

           DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
           DateFormat dateFormating = new SimpleDateFormat("dd/MM/yyyy");
           DateFormat dateFormating1 = new SimpleDateFormat("MMM-yyyy");
           Date sdate, edate;
           int mon;
           int demandcheck = 0;
           StringBuffer sBuffer = new StringBuffer(15);

           List<Location> l = locationService.getAllLocations();
           List<Track> tracklist = trackService.getAllTrack();
           System.out.println("inside location service" + locationService.getAllLocations());

           Date date = new Date();
           String StartDatemonth = null, EndDatemonth = null;
           String reportDate = dateFormat.format(date);

           System.out.println("current date" + reportDate);

           String[] parts = reportDate.split("/");
           String part1 = parts[0];
           String part2 = parts[1];
           String part3 = parts[2];
           int sday = Integer.parseInt(part3);
           int m1 = Integer.parseInt(part2);
           int year = Integer.parseInt(part1);
           System.out.println("M1" + m1);
           m1 = m1 - 1;
           Calendar calendar = Calendar.getInstance();
           calendar.set(year, m1, sday);
           calendar.add(Calendar.MONTH, 1);
           sdate = calendar.getTime();

           System.out.println("Start date calender" + sdate);

           Calendar calendar1 = Calendar.getInstance();
           calendar1.set(year, m1, sday);
           calendar.add(Calendar.MONTH, 2);
           edate = calendar.getTime();

           System.out.println("End date calender" + edate);

           List<String> location = new ArrayList<String>();
           sBuffer.append("{\"filters\":[");
           sBuffer.append("{\"title\":\"Locations\",");
           sBuffer.append("\n");
           sBuffer.append("\"criteria\":\"location\",");
           sBuffer.append("\n");
           sBuffer.append("\"values\":[");
           for (Location loc : l) {
                  location.add(loc.getLoName());
           }

           int a = location.size() - 1;
           for (int j = 0; j < location.size(); j++) {
                  sBuffer.append("\"");
                  sBuffer.append(location.get(j));
                  if (j != a)
                        sBuffer.append("\",");
                  else {
                        sBuffer.append("\"]");
                        sBuffer.append("},");
                  }
           }

           List<String> track = new ArrayList<String>();
           sBuffer.append("\n");
           sBuffer.append("{\"title\":\"Tracks\",");
           sBuffer.append("\n");
           sBuffer.append("\"criteria\":\"tracks\",");
           sBuffer.append("\n");
           sBuffer.append("\"values\":[");

           for (Track t : tracklist) {
                  track.add(t.getTrName());
           }

           int a1 = track.size() - 1;
           for (int j = 0; j < track.size(); j++) {
                  sBuffer.append("\"");
                  sBuffer.append(track.get(j));
                  if (j != a1)
                        sBuffer.append("\",");
                  else {
                        sBuffer.append("\"]");
                        sBuffer.append("\n");
                        sBuffer.append("},");
                  }
           }

           sBuffer.append("\n");
           sBuffer.append("{\"title\":\"Date of Joining\",");
           sBuffer.append("\n");
           sBuffer.append("\"criteria\":\"month\",");
           sBuffer.append("\n");
           sBuffer.append("\"values\":[");

           Calendar calendar2 = Calendar.getInstance();
           calendar2.set(year, m1, sday);
           for (int i = 0; i <= 2; i++) {
                  calendar2.add(Calendar.MONTH, 1);
                  Date date1 = calendar2.getTime();

                  sBuffer.append("\"");
                  sBuffer.append(dateFormating1.format(date1));
                  if (i != 2) {
                        sBuffer.append("\",");
                  } else {
                        sBuffer.append("\"]");
                  }
           }

           sBuffer.append("\n");
           sBuffer.append("}],");

           sBuffer.append("\n");
           sBuffer.append("\"demands\":[");
           System.out.println("SIZE OF DEMAND LIST");
           List<Demand> l1 = demandService.getAllDemandsByDate(sdate, edate);
           int listlength = l1.size();
           System.out.println("SIZE OF DEMAND LIST***********" + listlength);
           int initial = 1;
           removeDuplicates(l1);

           for (Demand d : l1) 
           {
                  //if (demandcheck != d.getDeId()) {
                        System.out.println("hi in for each loop"+d.getDeId());
                        sBuffer.append("\n");
                        sBuffer.append("{");
                        sBuffer.append("\n");
                        sBuffer.append("\"id\":\"");
                        sBuffer.append(d.getDeId());
                        sBuffer.append("\",");
                        sBuffer.append("\n");
                        sBuffer.append("\"courseLink\":\"");
                        sBuffer.append(d.getCourseFamily().getCfUrl());
                        sBuffer.append("\",");
                        sBuffer.append("\n");
                        sBuffer.append("\"tracks\":[\"");
                        Set<CourseFamilyTrack> s = d.getCourseFamily().getCourseFamilyTracks();
                        for (CourseFamilyTrack t : s) {
                               sBuffer.append(t.getTrack().getTrName());
                               sBuffer.append("\",\"");
                        }
                        sBuffer.append("\"],");
                        sBuffer.append("\n");
                        sBuffer.append("\"fullDate\":\"");

                        sBuffer.append(dateFormating.format(d.getDePeriod()));
                        System.out.println("de period date" + d.getDePeriod());

                        sBuffer.append("\",");
                        sBuffer.append("\n");
                        sBuffer.append("\"courseTitle\":\"");
                        sBuffer.append(d.getCourseFamily().getCfName());
                        sBuffer.append("\",");                        
                        sBuffer.append("\n");
                        
                        sBuffer.append("\"courseId\":\"");
                        sBuffer.append(d.getCourseFamily().getCfId());
                        sBuffer.append("\",");
                        sBuffer.append("\n");
                        
                        sBuffer.append("\"location\":\"");
                        sBuffer.append(d.getLocation().getLoName());

                        sBuffer.append("\",");
                        sBuffer.append("\n");
                        sBuffer.append("\"date\":\"");
                        String day = dateFormat.format(d.getDePeriod());
                        String[] daypart = day.split("/");
                        sBuffer.append(daypart[2]);

                        sBuffer.append("\",");
                        sBuffer.append("\n");
                        sBuffer.append("\"month\":\"");

                        sBuffer.append(dateFormating1.format(d.getDePeriod()));

                        sBuffer.append("\",");
                        sBuffer.append("\n");
                        sBuffer.append("\"courseDescription\":\"");
                        sBuffer.append(d.getDeComments());

                        sBuffer.append("\",");
                        sBuffer.append("\n");
                        sBuffer.append("\"availableSeats\":");
                        int count = d.getDeCount() - d.getDeFulfilledCount();
                        sBuffer.append(count);

                        //if (initial < (listlength-1)) {
                               sBuffer.append("},");
                               System.out.println("list length value initial"+initial+" "+listlength);
                        //}

                  //}
                               
                  initial++;
                  demandcheck = d.getDeId();
           }
           String buffer = sBuffer.toString();
           sBuffer = new StringBuffer(buffer.substring(0,buffer.length()-1));
           System.out.println(sBuffer);
           //initial = initial - 1;
           System.out.println("list length value initial after for loop"+initial);
           //if (initial == listlength) {
                  //sBuffer.append("\n");
                  //sBuffer.append("}");
                  sBuffer.append("\n");
                  sBuffer.append("]");
                  sBuffer.append("\n");
                  sBuffer.append("}");
           //}

           String output = sBuffer.toString();
           response.getOutputStream().print(output);
           response.getOutputStream().flush();
           System.out.println("testing the data");
    }
	
	
	private void removeDuplicates(List<Demand> list)
	{
	    int count = list.size();

	    for (int i = 0; i < count; i++) 
	    {
	        for (int j = i + 1; j < count; j++) 
	        {
	            if (list.get(i).getDeId().equals(list.get(j).getDeId()))
	            {
	                list.remove(j--);
	                count--;
	            }
	        }
	    }
	}*/
}
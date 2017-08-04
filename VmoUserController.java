package com.cts.lch.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cts.lch.marketplace.exception.LchBusinessException;
import com.cts.lch.marketplace.model.KnowledgePartner;
import com.cts.lch.marketplace.model.KnowledgerPartnerContract;
import com.cts.lch.marketplace.service.KnowledgePartnerService;
import com.cts.lch.marketplace.service.KnowledgerPartnerContractService;

@Controller
public class VmoUserController {
	private static final Logger LOGGER = Logger.getLogger(AppAdminController.class);
	private KnowledgePartnerService knowledgePartnerService;
	private KnowledgerPartnerContractService knowledgerPartnerContractService;
	private int kpid;

	@Autowired(required = true)
	@Qualifier(value = "knowledgePartnerService")
	public void setKnowledgePartnerService(KnowledgePartnerService knowledgePartnerService) {
		this.knowledgePartnerService = knowledgePartnerService;
	}

	@Autowired(required = true)
	@Qualifier(value = "knowledgerPartnerContractService")
	public void setKnowledgePartnerContractService(KnowledgerPartnerContractService knowledgerPartnerContractService) {
		this.knowledgerPartnerContractService = knowledgerPartnerContractService;
	}

	@RequestMapping(value = "/VmoUserHome", method = RequestMethod.GET)
	public String VmoUserHome() {
		LOGGER.info("Start of buadminHome");
		LOGGER.info("End of buadminHome in Controller");
		System.out.println("Inside Controller");
		KnowledgePartner partner = new KnowledgePartner();

		return "VmoUserHome";

	}

	@RequestMapping(value = "/AddKnowledgePartner", method = RequestMethod.GET)
	public String AddKnowledgePartner() {
		LOGGER.info("Start of buadminHome");
		LOGGER.info("End of buadminHome in Controller");
		System.out.println("Inside Controller");
		KnowledgePartner partner = new KnowledgePartner();

		return "AddKnowledgePartner";

	}

	@RequestMapping(value = "/AddKnowledgePartner", method = RequestMethod.POST)
	public ModelAndView addknowledgepartner(HttpServletRequest req) {
		LOGGER.info("Start of buadminHome");
		LOGGER.info("End of buadminHome in Controller");
		System.out.println("Inside Conroler");
		KnowledgePartner partner = new KnowledgePartner();
		ModelAndView obj = new ModelAndView("AddKnowledgePartner");
		String name = req.getParameter("name");
		String url = req.getParameter("url");
		String email = req.getParameter("email");
		String pocmobile = req.getParameter("pocmobile");
		String pocname = req.getParameter("pocname");
		System.out.println(pocname);
		if (pocname != null) {
			partner.setKpName(req.getParameter("name"));
			partner.setKpUrl(req.getParameter("url"));
			partner.setKpPocName(req.getParameter("pocname"));
			partner.setKpPocEmail(req.getParameter("email"));
			partner.setKpPocMobile(req.getParameter("pocmobile"));
			knowledgePartnerService.addKnowledgePartner(partner);
		}
		obj.addObject("check", pocname);
		obj.addObject("name", name);
		obj.addObject("url", url);
		obj.addObject("mobile", pocmobile);
		obj.addObject("email", email);

		return obj;
	}

	@RequestMapping(value = "/ViewContract", method = RequestMethod.GET)
	public ModelAndView ViewContract(HttpServletRequest req) {
		LOGGER.info("Start of buadminHome");
		LOGGER.info("End of buadminHome in Controller");
		System.out.println("Inside Conroler");
		KnowledgePartner partner = new KnowledgePartner();
		ModelAndView obj = new ModelAndView("ViewContract");
		List<KnowledgePartner> cmp = knowledgePartnerService.getAllKnowledgePartners();

		String kp = req.getParameter("kp");

		obj.addObject("list", cmp);

		return obj;
	}

	@RequestMapping(value = "/SearchContract", method = RequestMethod.GET)
	public ModelAndView SearchContract(HttpServletRequest req) throws LchBusinessException {
		LOGGER.info("Start of buadminHome");
		LOGGER.info("End of buadminHome in Controller");
		System.out.println("Inside Conroler");
		int id = 0;
		KnowledgePartner partner = new KnowledgePartner();
		ModelAndView obj = new ModelAndView("ViewContract");
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		List<KnowledgePartner> cmp = knowledgePartnerService.getAllKnowledgePartners();
		String kp = req.getParameter("kp");
		int a = Integer.parseInt(kp);
		kpid = a;

		obj.addObject("enable", kp);
		obj.addObject("selected", kp);
		obj.addObject("list", cmp);
		for (KnowledgePartner d : cmp) {
			if (a == d.getKpId()) {
				id = d.getKpId();
				System.out.println(id);

			}
		}

		List<KnowledgerPartnerContract> cdetail = knowledgerPartnerContractService
				.getKnowledgePartnerContractByKnowledgePartnerId(id);

		Date currentenddate = this.knowledgerPartnerContractService.getEndDateOfActiveContract(kpid);
		String testDateString = df.format(currentenddate);

		obj.addObject("currentenddate", testDateString);
		obj.addObject("cdetail", cdetail);
		obj.addObject("list", cmp);
		return obj;
	}

	@RequestMapping(value = "/AddContract", method = RequestMethod.GET)
	public ModelAndView AddContract(HttpServletRequest req) throws ParseException {
		LOGGER.info("Start of buadminHome");
		LOGGER.info("End of buadminHome in Controller");
		System.out.println("Inside Conroler");
		KnowledgePartner partner = new KnowledgePartner();

		KnowledgerPartnerContract contract = new KnowledgerPartnerContract();
		partner.setKpId(kpid);
		contract.setKnowledgePartner(partner);
		List<KnowledgePartner> cmp = knowledgePartnerService.getAllKnowledgePartners();
		ModelAndView obj = new ModelAndView("ViewContract");

		String d1 = (req.getParameter("date"));
		Date doj1 = null;
		if (d1 != null) {
			SimpleDateFormat sdfSource = new SimpleDateFormat("dd/MM/yyyy");
			doj1 = sdfSource.parse(d1);
			contract.setKcStartDate(doj1);
		}
		String d2 = (req.getParameter("date2"));
		Date doj2 = null;
		if (d2 != null) {
			SimpleDateFormat sdfSource = new SimpleDateFormat("dd/MM/yyyy");
			doj2 = sdfSource.parse(d2);
			System.out.println(doj2 + "date");
			contract.setKcEndDate(doj2);
		}
		if (d1 != null && d2 != null) {
			contract.setKcActive('Y');
		}
		obj.addObject("check1", d1);
		obj.addObject("check3", d2);
		obj.addObject("list", cmp);

		this.knowledgerPartnerContractService.addKnowledgePartnerContract(contract);
		return obj;
	}

	@RequestMapping(value = "/ExtendContract", method = RequestMethod.GET)
	public ModelAndView ExtendContract(HttpServletRequest req) throws ParseException, LchBusinessException {
		LOGGER.info("Start of buadminHome");
		LOGGER.info("End of buadminHome in Controller");
		System.out.println("Inside Conroler");
		Date newenddate = new Date();
		KnowledgePartner partner = new KnowledgePartner();
		KnowledgerPartnerContract contract = new KnowledgerPartnerContract();
		partner.setKpId(kpid);
		contract.setKnowledgePartner(partner);
		ModelAndView obj = new ModelAndView("ViewContract");
		List<KnowledgePartner> cmp = knowledgePartnerService.getAllKnowledgePartners();
		List<KnowledgerPartnerContract> cdetail = knowledgerPartnerContractService
				.getKnowledgePartnerContractByKnowledgePartnerId(kpid);
		for (KnowledgerPartnerContract d1 : cdetail) {
			newenddate = d1.getKcEndDate();

			break;
		}
		String enddate = (req.getParameter("enddate"));

		Date end = null;
		if (enddate != null) {

			SimpleDateFormat sdfSource = new SimpleDateFormat("dd/MM/yyyy");
			end = sdfSource.parse(enddate);
			for (KnowledgerPartnerContract d1 : cdetail) {

				d1.setKcEndDate(end);
				this.knowledgerPartnerContractService.extendKnowledgePartnerContract(d1);

				break;
			}
		}

		contract.setKcEndDate(end);

		obj.addObject("check2", end);
		obj.addObject("list", cmp);
		return obj;
	}

	@RequestMapping(value = "/DeleteContract", method = RequestMethod.GET)
	public ModelAndView DeleteContract(HttpServletRequest req) {
		LOGGER.info("Start of buadminHome");
		LOGGER.info("End of buadminHome in Controller");
		System.out.println("Inside Controller");
		String status = new String();
		Set<KnowledgerPartnerContract> set = new HashSet<KnowledgerPartnerContract>();
		KnowledgePartner partner = new KnowledgePartner();
		KnowledgerPartnerContract contract = new KnowledgerPartnerContract();
		partner.setKpId(kpid);
		contract.setKnowledgePartner(partner);
		int cid = Integer.parseInt(req.getParameter("value"));
		ModelAndView obj = new ModelAndView("ViewContract");

		List<KnowledgePartner> cmp = knowledgePartnerService.getAllKnowledgePartners();

		set = partner.getKnowledgerPartnerContracts();
		for (KnowledgerPartnerContract s : set) {
			if (contract.getKnowledgePartner().getKpId() == kpid) {
				System.out.println("contract id****");
				System.out.println(s.getKcId());
			}
		}
		if (cid >= 0) {
			status = "yes";
		}
		this.knowledgerPartnerContractService.deleteKnowledgePartnerContact(cid);

		obj.addObject("checking", status);
		obj.addObject("list", cmp);

		return obj;
	}

}

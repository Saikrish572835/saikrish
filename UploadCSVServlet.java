package com.cts.lch.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import com.cts.lch.exception.FieldLengthExceededException;
import com.cts.lch.exception.IncorrectNumberOfFieldsException;
import com.cts.lch.exception.UploadedCsvReadException;
import com.cts.lch.view.CsvFileError;
import com.cts.lch.view.UploadCandidateView;
import com.cts.lch.view.UploadCandidate;

/**
 * Servlet implementation class UploadCSVServlet
 */
@WebServlet(name = "UploadCSVServlet", urlPatterns = { "/upload" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
		maxFileSize = 1024 * 1024 * 10, // 10MB
		maxRequestSize = 1024 * 1024 * 50) // 50MB

public class UploadCSVServlet extends HttpServlet {

	private static final Logger LOGGER = Logger.getLogger(UploadCSVServlet.class);

	private static final long serialVersionUID = 5022171108662372354L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		// ArrayList<String> test=new ArrayList<String>();
		// session.setAttribute("test", test);
		response.setContentType("text/html;charset=UTF-8");
		InputStream stream = null;
		BufferedReader reader = null;
		stream = request.getPart("fileName").getInputStream();

		reader = new BufferedReader(new InputStreamReader(stream));

		UploadCandidateView uploadcandidate = null;
		try {
			uploadcandidate = convertAndValidate(reader);
		} catch (UploadedCsvReadException exception) {
			exception.printStackTrace();
			ArrayList<CsvFileError> errors = new ArrayList<CsvFileError>(1);
			CsvFileError error = new CsvFileError();
			error.setLine("");
			error.setReason("Network Error. Retry file Upload");
			error.setEntry("");
			errors.add(error);
		}
		ArrayList<CsvFileError> errors = uploadcandidate.getErrors();
		ArrayList<UploadCandidate> canlist = uploadcandidate.getCanList();
		//System.out.println("calling " + errors.get(0).getReason());
		if (errors != null) {
			if (!errors.isEmpty()) {
				if (errors.get(0).getReason() == null) {
					session.setAttribute("displaymessage", "Provide record to upload.");
					errors.clear();
				} else if (errors.get(0).getReason().equals("Invalid file")) {
					session.setAttribute("displaymessage", "Please Uploaded CSV file.");
					errors.clear();
				} else if (errors.get(0).getLine() == null)
					session.setAttribute("displaymessage", null);
				else
					session.setAttribute("displaymessage",
							"Please check invalid details are present in Uploaded file.");
			}
		}
		session.setAttribute("errors", errors);
		session.setAttribute("canlist", canlist);

		RequestDispatcher rd = null;
		rd = request.getRequestDispatcher("ShowUploadPreview");

		rd.forward(request, response);
		doGet(request, response);

	}

	public UploadCandidateView convertAndValidate(BufferedReader reader) throws UploadedCsvReadException {
		int lineCounter = 1;
		ArrayList<UploadCandidate> canlist = new ArrayList<UploadCandidate>();
		ArrayList<CsvFileError> errors = new ArrayList<CsvFileError>();
		// HashSet<UploadCandidate> set = new HashSet<UploadCandidate>();
		LinkedHashSet<UploadCandidate> set = new LinkedHashSet<UploadCandidate>();
		UploadCandidateView view = new UploadCandidateView();
		try {
			String line = "";
			while ((line = reader.readLine()) != null) {
				UploadCandidate uploadCandidate = null;
				try {
					// System.out.println(line);
					uploadCandidate = new UploadCandidate(line);
				} catch (IncorrectNumberOfFieldsException e) {
					CsvFileError error = new CsvFileError();
					error.setLine(Integer.toString(lineCounter));
					if (!e.getMessage().equals("Invalid file"))
						error.setReason("Incorrect # of Fields in Uploaded file.");
					error.setEntry(line);
					errors.add(error);
					break;
				} catch (FieldLengthExceededException e) {
					CsvFileError error = new CsvFileError();
					error.setLine(Integer.toString(lineCounter));
					error.setReason("Field length exceeded and invalid");
					error.setEntry(e.getMessage());
					lineCounter++;
					errors.add(error);
					continue;
				}
				if (uploadCandidate.isHeader() == false) {
					if (set.add(uploadCandidate) == false) {
						System.out.println("testing inside if condition");
						CsvFileError error = new CsvFileError();
						error.setLine(Integer.toString(lineCounter));
						error.setReason("Duplicate Canditate Id or mailId");
						error.setEntry(line + " Code - " + uploadCandidate.getCaCandidateId());
						errors.add(error);
					} else {
						if (lineCounter != 1) {
							canlist.add(uploadCandidate);
						}
					}
				}
				lineCounter++;
			}
		} catch (IOException e) {
			throw new UploadedCsvReadException("Exception when reading uploaded CSV file." + e.getMessage());
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				throw new UploadedCsvReadException("Exception when reading uploaded CSV file." + e.getMessage());
			}
		}
		if (errors.isEmpty())
			view.setCanList(canlist);
		else
			view.setErrors(errors);
		return view;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

}

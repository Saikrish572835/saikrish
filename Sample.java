package com.cts.lch.controller;
import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import com.google.gson.Gson;
@WebServlet("/ResendPassword")
public class Sample  extends HttpServlet {

        private static final long serialVersionUID = 1L;

        protected void doGet(HttpServletRequest request,
                HttpServletResponse response) throws ServletException, IOException {

                String sportsName = request.getParameter("sportsName");
                System.out.println(sportsName+"NAME-----------");
                List<String> list = new ArrayList<String>();
                String json = null;
if(!sportsName.equals(null)){
                if (sportsName.equals("Football")) {
                        list.add("Lionel Messi");
                        list.add("Cristiano Ronaldo");
                        list.add("David Beckham");
                        list.add("Diego Maradona");
                } else if (sportsName.equals("Cricket")) {
                        list.add("Sourav Ganguly");
                        list.add("Sachin Tendulkar");
                        list.add("Lance Klusener");
                        list.add("Michael Bevan");
                } else if (sportsName.equals("Select Sports")) {
                        list.add("Select Player");
                }
}
                Iterator<String> itr = list.iterator();
                while(itr.hasNext())
                {
                       Object test = itr.next();
                       String check  = test.toString();
                      json += check +",";
                      System.out.println("JSON"+json);
                }
                //json = new Gson().toJson(list);
                response.setContentType("application/json");
                response.getWriter().write(json);
        }
}
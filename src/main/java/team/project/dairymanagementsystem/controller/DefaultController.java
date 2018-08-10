package team.project.dairymanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import team.project.dairymanagementsystem.model.TenderInfo;
import team.project.dairymanagementsystem.service.TenderInfoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Controller
public class DefaultController {
    @Autowired
    private TenderInfoService tenderInfoService;

    //Home page
    @GetMapping(value = {"/"})
    public ModelAndView defaultHomePage(ModelAndView modelAndView, HttpServletRequest request) throws ParseException {
        //get current date
        TimeZone local = TimeZone.getTimeZone("Africa/Nairobi");
        Calendar now = Calendar.getInstance(local); //get time in the current time zone
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(local);
        String date = formatter.format(now.getTime());
        Date currentDate = formatter.parse(date);

        TenderInfo latestTenderInfo = tenderInfoService.findLatestTender();
        boolean tender = false;
        if (latestTenderInfo != null) {
            Date deadline = latestTenderInfo.getDeadline();
            //check whether deadline has passed
            if (currentDate.compareTo(deadline) > 0) {
                //deadline has passed
                latestTenderInfo.setStatus(false);
                tenderInfoService.addTenderInfo(latestTenderInfo);
            } else if (currentDate.compareTo(deadline) < 0) {
                tender = true; //a tender is available
                modelAndView.addObject("tenderInfo", latestTenderInfo);
            } else {
                System.out.println("How did we get here?");
            }
        }
        //check whether user is logged in to determine whether to show a logout or login button
        if(request.getSession().getAttribute("loggedIn") != null){
            modelAndView.addObject("loggedIn", true);
            //check if the user is an admin
            if(request.getSession().getAttribute("admin") != null){
                modelAndView.addObject("admin", true);
            }
        }else{
            modelAndView.addObject("loggedIn", false);
        }
        modelAndView.addObject("tender", tender);
        modelAndView.setViewName("welcome");
        return modelAndView;
    }

    //Login page
    @GetMapping(value = "/login")
    public ModelAndView loginPage(ModelAndView modelAndView) {
        modelAndView.setViewName("login/login");
        return modelAndView;
    }

}

package com.leavemgmt.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.leavemgmt.dao.AgendaDAO;
import com.leavemgmt.model.AgendaItem;
import com.leavemgmt.model.User;
import com.leavemgmt.util.DateUtil;
import com.leavemgmt.util.SessionHelper;
import com.leavemgmt.util.StringUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class AgendaServlet extends HttpServlet {
    
    private AgendaDAO agendaDAO;
    
    @Override
    public void init() throws ServletException {
        agendaDAO = new AgendaDAO();
        System.out.println("AgendaServlet initialized");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        User currentUser = SessionHelper.getCurrentUser(request);
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get date range parameters (default: this week)
        String fromDateStr = request.getParameter("fromDate");
        String toDateStr = request.getParameter("toDate");
        
        LocalDate fromDate;
        LocalDate toDate;
        
        if (StringUtil.isEmpty(fromDateStr) || StringUtil.isEmpty(toDateStr)) {
            // Default to current week (Monday to Sunday)
            LocalDate today = LocalDate.now();
            fromDate = today.minusDays(today.getDayOfWeek().getValue() - 1);
            toDate = fromDate.plusDays(6);
        } else {
            fromDate = DateUtil.parseDate(fromDateStr);
            toDate = DateUtil.parseDate(toDateStr);
            
            if (fromDate == null || toDate == null) {
                fromDate = LocalDate.now();
                toDate = fromDate.plusDays(6);
            }
        }
        
        try {
            // Get agenda data (calls sp_GetDivisionAgenda)
            List<AgendaItem> agendaItems = agendaDAO.getDivisionAgenda(
                currentUser.getDivisionId(),
                DateUtil.toSqlDate(fromDate),
                DateUtil.toSqlDate(toDate)
            );
            
            // Organize data by user and date
            Map<Integer, Map<LocalDate, AgendaItem>> agendaMap = new LinkedHashMap<>();
            Map<Integer, String> userNames = new LinkedHashMap<>();
            Set<LocalDate> dates = new TreeSet<>();
            
            for (AgendaItem item : agendaItems) {
                int userId = item.getUserId();
                LocalDate workDate = item.getWorkDate();
                
                userNames.putIfAbsent(userId, item.getFullName());
                dates.add(workDate);
                
                agendaMap.putIfAbsent(userId, new HashMap<>());
                agendaMap.get(userId).put(workDate, item);
            }
            
            request.setAttribute("agendaMap", agendaMap);
            request.setAttribute("userNames", userNames);
            request.setAttribute("dates", dates);
            request.setAttribute("fromDate", DateUtil.formatDate(fromDate));
            request.setAttribute("toDate", DateUtil.formatDate(toDate));
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi tải agenda: " + e.getMessage());
        }
        
        // Forward to agenda page
        request.getRequestDispatcher("/agenda.jsp").forward(request, response);
    }
}
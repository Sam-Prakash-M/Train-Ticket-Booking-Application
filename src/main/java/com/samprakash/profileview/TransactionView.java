package com.samprakash.profileview;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import com.google.gson.Gson; // Requires GSON library dependency
import com.samprakash.profilemodel.TransactionData;
import com.samprakash.profileviewmodel.TransactionViewModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/TransactionList")
public class TransactionView extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 10; // Number of rows per page

    // 1. Initial Page Load
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_name") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        // Forward to the JSP view. Data will be fetched asynchronously.
        request.getRequestDispatcher("TransactionView.jsp").forward(request, response);
    }

    // 2. AJAX Data Handler
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) return; // Handle session timeout appropriately in prod

        String userName = (String) session.getAttribute("user_name");
        
        // Parse Pagination Parameters
        int page = 1;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) { 
            // Default to page 1 on error
        }

        int offset = (page - 1) * PAGE_SIZE;

        // Fetch paginated data from ViewModel
        // Note: You must update TransactionViewModel.getUserTransaction to support (userName, offset, limit)
        List<TransactionData> transactions = TransactionViewModel.getUserTransaction(userName, offset, PAGE_SIZE);
        
        // Return JSON Response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String json = new Gson().toJson(transactions);
        out.print(json);
        out.flush();
    }
}
package controller;

import dao.DashboardDAO;
import java.util.Map;

public class DashboardController {

    private final DashboardDAO dashboardDAO;

    public DashboardController() {
        dashboardDAO = new DashboardDAO();
    }

    public Map<String, Integer> getRingkasanDashboard() {
        return dashboardDAO.getRingkasanDashboard();
    }
}
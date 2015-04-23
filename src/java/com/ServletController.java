/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import config.Parameters;
import engine.Controller;
import engine.Path;
import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import learning.IterationInformation;
import problem.CLSDatum;
import problem.ProblemController;
import softwareDesign.CLSClass;
import softwareDesign.EleganceDesign;

/**
 *
 * @author kieran
 */
public class ServletController extends HttpServlet {

    protected String dataFolder;
    Map<String, String> parameters = new LinkedHashMap<String, String>();
    private Path path;
    private String designName;
    private SortedMap< String, List< CLSDatum>> useTable;
    private List< CLSClass> freezeList;
    private int iteration;
    private int interactionCounter;
    private IterationInformation information;
    private List< EleganceDesign> archive;
    private String clientDataFolder;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        this.dataFolder = servletConfig.getInitParameter("dataFolder");
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet FileUploadController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Controller at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Create a session object if it is not already created.
        HttpSession session = request.getSession(true);
        // Get session creation time.
        Date createTime = new Date(session.getCreationTime());
        // Get last access time of this web page.
        Date lastAccessTime
                = new Date(session.getLastAccessedTime());

        Integer visitCount = new Integer(0);
        String visitCountKey = new String("visitCount");
        String userIDKey = new String("userID");
        String userID = new String("ABCD");

        // Check if this is new comer on your web page.
        if (session.isNew()) {
            session.setAttribute(userIDKey, userID);
        } else {
            visitCount = (Integer) session.getAttribute(visitCountKey);
            visitCount = visitCount + 1;
            userID = (String) session.getAttribute(userIDKey);
        }
        session.setAttribute(visitCountKey, visitCount);
        // Set up initialisation parameters
        String paramName;
        String paramValue;
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            paramValue = paramValues[0]; // values should have only one element
            parameters.put(paramValue, paramName);
            switch (paramName) {
                case "Designer":
                    Parameters.designer = Integer.parseInt(paramValue);
                    System.out.println(paramName + ": " + paramValue);
                    assert Parameters.designer >= 0;
                    assert Parameters.designer <= Parameters.NUMBER_OF_DESIGNERS;
                    break;
                case "DesignProblem":
                    Parameters.problemNumber = Integer.parseInt(paramValue);
                    System.out.println(paramName + ": " + paramValue);
                    break;
                case "EpisodeNumber":
                    Parameters.episodeNumber = Integer.parseInt(paramValue);
                    System.out.println(paramName + ": " + paramValue);
                    break;
                case "Hints":
                    Parameters.freezing = Boolean.parseBoolean(paramValue);
                    System.out.println(paramName + ": " + paramValue);
                    break;
                case "ColourMetaphor":
                    if (paramValue.equalsIgnoreCase("TrafficLights")) {
                        Parameters.colourMetaphor = Parameters.TRAFFIC_LIGHTS;
                        System.out.println(paramName + ": " + paramValue);
                    } else if (paramValue.equalsIgnoreCase("WaterTap")) {
                        Parameters.colourMetaphor = Parameters.WATER_TAP;
                        System.out.println(paramName + ": " + paramValue);
                    } else {
                        System.out.println("Error processing ColourMetaphor in Controller Servlet.");
                    }
                    break;
                case "SelectPath":
                    Parameters.outputFilePath = clientDataFolder = dataFolder + paramValue + "\\";
                    System.out.println(paramName + ": " + dataFolder + paramValue + "\\");
                    break;
                default:
                    throw new AssertionError();
            }
        }

        // Run model - taken from ACOFrame.doInteractiveACO( )
        ProblemController problemController = setUpProblemController();
        Controller controller = new Controller(problemController, engine.Controller.Mode.interactive);
        controller.run(0, this); // parse this instance to fetch data results - engine.Controller.performInteraction()

        

        // Attach model results to HttpSession
        assert this.path != null;
        session.setAttribute("Path", this.path);
        assert this.designName != null;
        session.setAttribute("DesignName", this.designName);
        assert this.useTable != null;
        session.setAttribute("UseTable", this.useTable);
        assert this.freezeList != null;
        session.setAttribute("FreezeList", this.freezeList);
        assert this.information != null;
        session.setAttribute("Information", this.information);
        assert this.archive != null;
        session.setAttribute("Archive", this.archive);
        session.setAttribute("Iteration", this.iteration);
        session.setAttribute("IterationCounter", this.interactionCounter);
        session.setAttribute("ClientDataFolder", this.clientDataFolder);
 
        // Minipulate Variables
        // TODO: Minipulate Variables
        
        // Return worked variables by AJAX for rendering in the view if needed
//        String json = new Gson().toJson(parameters);
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        PrintWriter PW = response.getWriter();
//        PW.write(json);
//        PW.flush();
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "This Servlet instantiates an iACO ProblemController and Controller which have circumvented"
                + "calls back to this class, this is in order to process user parameters which are then forwarded to"
                + "the Results.jsp for viewing by the Client.";
    }// </editor-fold>

    private ProblemController setUpProblemController() {
        ProblemController problemController = new ProblemController();
        if (Parameters.problemNumber == 0) {    // test
            problemController.createDesignProblem8(); // randomised
            problemController.setNumberOfClasses(7);
            problemController.generateUseMatrix();
        } else if (Parameters.problemNumber == 1) { // Cinema Booking System
            problemController.createDesignProblem5();
            problemController.setNumberOfClasses(5);
            problemController.generateUseMatrix();
//            problemController.showActionsAndData( );
        } else if (Parameters.problemNumber == 2) { // GDP
            problemController.createDesignProblem7();
            problemController.setNumberOfClasses(5);
            problemController.generateUseMatrix();
        } else if (Parameters.problemNumber == 3) { // SC

            problemController.createDesignProblem6();
            problemController.setNumberOfClasses(16);
            problemController.generateUseMatrix();
        } else {
            String message = "impossible design problem!!";
            message += " problem number is : ";
            message += Integer.toString(Parameters.problemNumber);
            assert false : message;
        }
        return problemController;
    }

    private Color getWaterTapDisplayColour(CLSClass c, final double maxCOM) {
        assert maxCOM > 0.0;
        Color result = Color.WHITE;
        final int numberOfColours = 5;
        final double increment = maxCOM / numberOfColours;
        final double COM = c.getCOMFitness();
        if (COM < increment) {
            result = Color.CYAN;
        } else if (COM < (increment * 2)) {
            result = Color.LIGHT_GRAY;
        } else if (COM < (increment * 3)) {
            result = Color.YELLOW;
        } else if (COM < (increment * 4)) {
            result = Color.PINK;
        } //  25 July 2007 make number 5.1 to handle any potential rounding errors
        else if (COM <= (increment * 5.1)) {
            result = Color.RED;
        } else {
            assert false : "impossible colour";
        }
        return result;
    }

    private Color getTrafficLightDisplayColour(CLSClass c, final double maxCOM) {
        assert maxCOM > 0.0;
        Color result = Color.WHITE;
        final int numberOfColours = 3;
        final double increment = maxCOM / numberOfColours;
        final double COM = c.getCOMFitness();
        assert COM >= 0.0;
        if (COM < increment) {
            result = Color.RED;
        } else if (COM < (increment * 2)) {
            result = Color.ORANGE;
        } else { // COM <= ( increment * 3 ) )
            result = Color.GREEN;
        }
        return result;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setDesignName(String designName) {
        this.designName = designName;
    }

    public void setUseTable(SortedMap<String, List<CLSDatum>> useTable) {
        this.useTable = useTable;
    }

    public void setFreezeList(List<CLSClass> freezeList) {
        this.freezeList = freezeList;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public void setInteractionCounter(int interactionCounter) {
        this.interactionCounter = interactionCounter;
    }

    public void setInformation(IterationInformation information) {
        this.information = information;
    }

    public void setArchive(List<EleganceDesign> archive) {
        this.archive = archive;
    }
}

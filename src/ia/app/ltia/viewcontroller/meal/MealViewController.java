package ia.app.ltia.viewcontroller.meal;

import ia.app.ltia.viewcontroller.health.HealthViewController;
import ia.app.ltia.LifeTrackerConstants;
import ia.app.ltia.LifeTrackerHelper;
import ia.app.ltia.model.Database;
import ia.app.ltia.model.Meal;
import ia.app.ltia.model.MealEvent;
import ia.app.ltia.model.Member;
import ia.app.ltia.model.algo.NutrientReport;
import ia.app.ltia.model.algo.WeightChallenge;
import ia.app.ltia.viewcontroller.FrameDragListener;
import java.awt.Color;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author
 */
public class MealViewController extends javax.swing.JFrame {
    
    private static final Logger logger = LogManager.getLogger(MealViewController.class);
    
    private Member member;
    WeightChallenge weightChallenge;
    private ArrayList<Meal> allMeals;
    private NutrientReport nutrientReport;
    private Date nutrientDate;
    private boolean isValidPortion = false;
    
    /**
     * Creates new form MealViewController
     */
    public MealViewController() {
        initComponents();
        initUndecoratedFrame();
        this.setTitle(LifeTrackerConstants.TITLE_MEAL);
        initComponentsCustomStyling();
        initAllMealsComboBox();
        this.nutrientDate = this.jXDatePickerPieChart.getDate();
        addListener();  
    }
    
    private void initComponentsCustomStyling() {        
        //jFrame background
        this.getContentPane().setBackground(LifeTrackerConstants.GUI_BACKGROUND_COLOR_2);
        
        //Top panel background
        this.jTopPanel.setBackground(LifeTrackerConstants.GUI_COLOR_3);
        
        //Main Meal button
        this.buttonMeal.setBackground(LifeTrackerConstants.GUI_BUTTON_BACKGROUND_COLOR_1);
        this.buttonMeal.setForeground(Color.WHITE);
        this.buttonMeal.setRolloverEnabled(false);
        
        //Main Health button
        this.buttonHealth.setBackground(LifeTrackerConstants.GUI_BUTTON_BACKGROUND_COLOR_1);
        this.buttonHealth.setForeground(Color.WHITE);        
        this.buttonHealth.setRolloverEnabled(false);
        
        
        //Add Meal panel
        this.jPanelAddMeal.setBackground(LifeTrackerConstants.GUI_FOREGROUND_COLOR_2);        
        this.textAreaMealPortion.setBackground(LifeTrackerConstants.GUI_FIELD_BACKGROUND_COLOR_2);
        this.textAreaMealPortion.setBorder(BorderFactory.createEmptyBorder());                
        this.jScrollPane1.setBorder(BorderFactory.createEmptyBorder());        
        this.jTextAreaNutritionInfo.setBackground(LifeTrackerConstants.GUI_BACKGROUND_COLOR_2);
        
        //Nutrient info panel
        jTextAreaNutritionInfo.setBackground(LifeTrackerConstants.GUI_BACKGROUND_COLOR_2);                
        jPanelNutrientReport.setBackground(LifeTrackerConstants.GUI_FOREGROUND_COLOR_2);
        jTextAreaNutrientReport.setBackground(LifeTrackerConstants.GUI_FOREGROUND_COLOR_2);        
        jTextAreaNutrientReport.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane3.setBorder(BorderFactory.createEmptyBorder());     
        
        //buttons
        this.buttonMealEventSubmit.setBackground(LifeTrackerConstants.GUI_BUTTON_COLOR);
        this.buttonAddMeal.setBackground(LifeTrackerConstants.GUI_BUTTON_COLOR);
        
        jPanelCaloriePieChart.setBackground(Color.WHITE);
        
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);        
    }
    
    private void initUndecoratedFrame(){
        
        //To remove title, must set undecorated to true.
        //But dont do that in here, it must be in initComponents so 
        //you must do it in the JFrame properties.
        //this.setUndecorated(true);
        
        //Drag the window from by selecting any part of the frame
        FrameDragListener frameDragListener = new FrameDragListener((java.awt.Window)this);
        this.addMouseListener(frameDragListener);
        this.addMouseMotionListener(frameDragListener);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }    
    
    private void initAllMealsComboBox(){                 
        Database db = new Database(LifeTrackerConstants.DATABASE_NAME);
        allMeals = db.getAllMeals();
        
        logger.debug("Got all meals, found " + allMeals.size() + " meals");
        
        jXComboBoxMeal.setModel(new DefaultComboBoxModel(allMeals.toArray()));
        AutoCompleteDecorator.decorate(jXComboBoxMeal, new MealConverter());
    }    
    
    public void setMember(Member member){
        this.member = member;
        this.weightChallenge = new WeightChallenge(this.member);        
        updateUserStatus();
        
        logger.debug("Setting member to: " + member.getUsername());
    }
    
    private void updateUserStatus() {
        String part1 = "Welcome " + this.member.getUsername() + ". Today is " + LifeTrackerConstants.GUI_DETAILED_DATE_FORMAT.format(new Date());        
        String part2 = "\n\n" + this.weightChallenge.getDayOfChallengeStatement();
        String part3 = "\n" + this.weightChallenge.getChallengeBriefStatement();
        String part4 = "\n" + this.weightChallenge.getCurrentWeightStatement();
        
        displayUserJXLabel.setText(part1 + part2 + part3 + part4);
    }    
    
    private void displayNutrientBreakdown(){        
        logger.debug("Getting nutrient report for date: " + this.nutrientDate.toString());
        
        nutrientReport = new NutrientReport(this.member, this.nutrientDate);
        
        logger.debug("Got NutrientReport: " + this.nutrientReport.toString());
        
        displayNutritionStatements(this.nutrientDate);
        displayNutritionPieChart(this.nutrientDate);        
    }
    
    private void displayNutritionStatements(Date date) {        
        String part1 = this.nutrientReport.getHighestNutrient();
        String part2 = this.nutrientReport.getHighestFood();
        String part3 = this.nutrientReport.getRecommendedFood();
        
        String nutrientReportStatement = part1 + "\n\n" + part2 + "\n\n" + part3;
        
        this.jTextAreaNutrientReport.setText(nutrientReportStatement);            
    }
    
    private void displayNutritionPieChart(Date date) {
        jPanelCaloriePieChart.removeAll();
        jPanelCaloriePieChart.revalidate();
        
        JFreeChart pieChart = getChart();
        
        chartPanel = new ChartPanel(pieChart);
        chartPanel.setSize(jPanelCaloriePieChart.getWidth(), 450);
  
        jPanelCaloriePieChart.add(chartPanel);        
        
        jPanelCaloriePieChart.getParent().validate();
        jPanelCaloriePieChart.repaint();
        jPanelCaloriePieChart.revalidate();        
        
        
    }
    
    private JFreeChart getChart() {        
        String title = LifeTrackerConstants.PIECHART_NUTRIENT_BREAKDOWN + " on " + 
                LifeTrackerConstants.GUI_DATE_FORMAT.format(this.nutrientDate);
        
        PieDataset dataset = getDataset();
        
        JFreeChart chart = ChartFactory.createPieChart(      
            title,   // chart title 
            dataset, // data    
            true,    // include legend   
            true,    // configure chart to generate tool tips?
            false);  // configure chart to generate URLs?
        
        PiePlot plot = (PiePlot)chart.getPlot();
        plot.setNoDataMessage(LifeTrackerConstants.PIECHART_NO_DATA_FOUND);
        
        StandardPieSectionLabelGenerator pieChartLabelGenerator = new StandardPieSectionLabelGenerator("{0} {1} {2}");
        plot.setLabelGenerator(pieChartLabelGenerator);
        plot.setLegendLabelGenerator(pieChartLabelGenerator);
        
        return chart;
    }
    
    private PieDataset getDataset() {        
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        double carbs = nutrientReport.getSumOfCarbs();
        double fat = nutrientReport.getSumOfFat();
        double protein = nutrientReport.getSumOfProtein();
        double fibre = nutrientReport.getSumOfFibre();
        
        dataset.setValue(LifeTrackerConstants.CARBS_LABEL, carbs);
        dataset.setValue(LifeTrackerConstants.FAT_LABEL, fat);
        dataset.setValue(LifeTrackerConstants.PROTEIN_LABEL, protein);
        dataset.setValue(LifeTrackerConstants.FIBRE_LABEL, fibre);       
        
        return dataset;        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelServingSizeUnit = new javax.swing.JLabel();
        jPanelAddMeal = new javax.swing.JPanel();
        textAreaMealPortion = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaNutritionInfo = new javax.swing.JTextArea();
        titleUpdateHealth = new javax.swing.JLabel();
        labelMealDate = new javax.swing.JLabel();
        labelMeal = new javax.swing.JLabel();
        jXComboBoxMeal = new org.jdesktop.swingx.JXComboBox();
        buttonAddMeal = new javax.swing.JButton();
        mealEventStatusLabel = new javax.swing.JLabel();
        jXDatePickerMealEvent = new org.jdesktop.swingx.JXDatePicker();
        labelMealPortion = new javax.swing.JLabel();
        validPortion = new javax.swing.JLabel();
        buttonMealEventSubmit = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jPanelNutrientReport = new javax.swing.JPanel();
        jXDatePickerPieChart = new org.jdesktop.swingx.JXDatePicker();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaNutrientReport = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jTopPanel = new javax.swing.JPanel();
        displayUserJXLabel = new org.jdesktop.swingx.JXLabel();
        buttonHealth = new javax.swing.JButton();
        buttonMeal = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jButtonCloseScreen = new javax.swing.JButton();
        jPanelCaloriePieChart = new javax.swing.JPanel();
        chartPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1285, 624));
        setMinimumSize(new java.awt.Dimension(1285, 624));
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(1285, 624));
        setResizable(false);
        setSize(new java.awt.Dimension(1285, 624));

        jLabelServingSizeUnit.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N

        textAreaMealPortion.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        textAreaMealPortion.setPreferredSize(new java.awt.Dimension(8, 24));
        textAreaMealPortion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textAreaMealPortionFocusGained(evt);
            }
        });
        textAreaMealPortion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textAreaMealPortionActionPerformed(evt);
            }
        });
        textAreaMealPortion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textAreaMealPortionKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textAreaMealPortionKeyTyped(evt);
            }
        });

        jTextAreaNutritionInfo.setBackground(new java.awt.Color(240, 240, 240));
        jTextAreaNutritionInfo.setColumns(10);
        jTextAreaNutritionInfo.setRows(5);
        jTextAreaNutritionInfo.setWrapStyleWord(true);
        jTextAreaNutritionInfo.setBorder(null);
        jScrollPane1.setViewportView(jTextAreaNutritionInfo);

        titleUpdateHealth.setFont(new java.awt.Font("Keep Calm Med", 0, 14)); // NOI18N
        titleUpdateHealth.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        titleUpdateHealth.setText("Add Meal");

        labelMealDate.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        labelMealDate.setText("Date");

        labelMeal.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        labelMeal.setText(LifeTrackerConstants.MEAL_ADD_LABEL);

        jXComboBoxMeal.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N

        buttonAddMeal.setFont(new java.awt.Font("Keep Calm Med", 0, 11)); // NOI18N
        buttonAddMeal.setText("New");
        buttonAddMeal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddMealActionPerformed(evt);
            }
        });

        mealEventStatusLabel.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N

        jXDatePickerMealEvent.setDate(Calendar.getInstance().getTime());

        labelMealPortion.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        labelMealPortion.setText(LifeTrackerConstants.MEAL_PORTION_LABEL);

        validPortion.setFont(new java.awt.Font("Tahoma", 2, 13)); // NOI18N

        buttonMealEventSubmit.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        buttonMealEventSubmit.setText("Submit Meal");
        buttonMealEventSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMealEventSubmitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelAddMealLayout = new javax.swing.GroupLayout(jPanelAddMeal);
        jPanelAddMeal.setLayout(jPanelAddMealLayout);
        jPanelAddMealLayout.setHorizontalGroup(
            jPanelAddMealLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAddMealLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelAddMealLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelAddMealLayout.createSequentialGroup()
                        .addGroup(jPanelAddMealLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelMealDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanelAddMealLayout.createSequentialGroup()
                                .addGroup(jPanelAddMealLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelAddMealLayout.createSequentialGroup()
                                        .addComponent(labelMeal, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanelAddMealLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelAddMealLayout.createSequentialGroup()
                                                .addGroup(jPanelAddMealLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jXDatePickerMealEvent, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(jPanelAddMealLayout.createSequentialGroup()
                                                        .addGroup(jPanelAddMealLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(textAreaMealPortion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(validPortion, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(buttonAddMeal, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jXComboBoxMeal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(labelMealPortion, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 10, Short.MAX_VALUE)))
                        .addGap(26, 26, 26))
                    .addGroup(jPanelAddMealLayout.createSequentialGroup()
                        .addGroup(jPanelAddMealLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(titleUpdateHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanelAddMealLayout.createSequentialGroup()
                        .addGroup(jPanelAddMealLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buttonMealEventSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mealEventStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanelAddMealLayout.setVerticalGroup(
            jPanelAddMealLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAddMealLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleUpdateHealth)
                .addGroup(jPanelAddMealLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelAddMealLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(labelMeal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelMealDate))
                    .addGroup(jPanelAddMealLayout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(labelMealPortion)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelAddMealLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelAddMealLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelAddMealLayout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(buttonAddMeal, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelAddMealLayout.createSequentialGroup()
                                .addComponent(jXComboBoxMeal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(36, 36, 36)
                                .addGroup(jPanelAddMealLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(textAreaMealPortion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(validPortion, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                                .addComponent(jXDatePickerMealEvent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(38, 38, 38)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(buttonMealEventSubmit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(mealEventStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jXDatePickerPieChart.setDate(Calendar.getInstance().getTime());
        jXDatePickerPieChart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXDatePickerPieChartActionPerformed(evt);
            }
        });

        jTextAreaNutrientReport.setEditable(false);
        jTextAreaNutrientReport.setColumns(20);
        jTextAreaNutrientReport.setLineWrap(true);
        jTextAreaNutrientReport.setRows(3);
        jTextAreaNutrientReport.setWrapStyleWord(true);
        jTextAreaNutrientReport.setMaximumSize(new java.awt.Dimension(164, 94));
        jTextAreaNutrientReport.setMinimumSize(new java.awt.Dimension(164, 94));
        jScrollPane3.setViewportView(jTextAreaNutrientReport);

        jLabel1.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        jLabel1.setText("Select a date:");

        javax.swing.GroupLayout jPanelNutrientReportLayout = new javax.swing.GroupLayout(jPanelNutrientReport);
        jPanelNutrientReport.setLayout(jPanelNutrientReportLayout);
        jPanelNutrientReportLayout.setHorizontalGroup(
            jPanelNutrientReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelNutrientReportLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelNutrientReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelNutrientReportLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanelNutrientReportLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jXDatePickerPieChart, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24))))
        );
        jPanelNutrientReportLayout.setVerticalGroup(
            jPanelNutrientReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelNutrientReportLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelNutrientReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXDatePickerPieChart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTopPanel.setPreferredSize(new java.awt.Dimension(1285, 91));

        displayUserJXLabel.setBackground(new java.awt.Color(204, 255, 255));
        displayUserJXLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        displayUserJXLabel.setForeground(new java.awt.Color(255, 255, 255));
        displayUserJXLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        displayUserJXLabel.setText("Welcome ...");
        displayUserJXLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        displayUserJXLabel.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        displayUserJXLabel.setLineWrap(true);

        buttonHealth.setFont(new java.awt.Font("Keep Calm Med", 0, 40)); // NOI18N
        buttonHealth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ia/app/ltia/images/health-button-white.png"))); // NOI18N
        buttonHealth.setText("Health");
        buttonHealth.setBorderPainted(false);
        buttonHealth.setFocusPainted(false);
        buttonHealth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonHealthActionPerformed(evt);
            }
        });

        buttonMeal.setFont(new java.awt.Font("Keep Calm Med", 0, 40)); // NOI18N
        buttonMeal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ia/app/ltia/images/meal-button-white.png"))); // NOI18N
        buttonMeal.setText("Meal");
        buttonMeal.setBorderPainted(false);
        buttonMeal.setFocusPainted(false);
        buttonMeal.setPreferredSize(new java.awt.Dimension(227, 56));
        buttonMeal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMealActionPerformed(evt);
            }
        });

        jSeparator3.setMinimumSize(new java.awt.Dimension(50, 5));
        jSeparator3.setPreferredSize(new java.awt.Dimension(50, 5));

        jButtonCloseScreen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ia/app/ltia/images/exit-icon2-normal.png"))); // NOI18N
        jButtonCloseScreen.setAlignmentY(0.0F);
        jButtonCloseScreen.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 8, 12));
        jButtonCloseScreen.setBorderPainted(false);
        jButtonCloseScreen.setContentAreaFilled(false);
        jButtonCloseScreen.setFocusPainted(false);
        jButtonCloseScreen.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButtonCloseScreen.setIconTextGap(0);
        jButtonCloseScreen.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonCloseScreen.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/ia/app/ltia/images/exit-icon-highlighted.png"))); // NOI18N
        jButtonCloseScreen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonCloseScreenMouseClicked(evt);
            }
        });
        jButtonCloseScreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseScreenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jTopPanelLayout = new javax.swing.GroupLayout(jTopPanel);
        jTopPanel.setLayout(jTopPanelLayout);
        jTopPanelLayout.setHorizontalGroup(
            jTopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTopPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(displayUserJXLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jTopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jTopPanelLayout.createSequentialGroup()
                        .addComponent(buttonHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23)
                        .addComponent(buttonMeal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jTopPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 246, Short.MAX_VALUE)
                .addComponent(jButtonCloseScreen))
        );
        jTopPanelLayout.setVerticalGroup(
            jTopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTopPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jTopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jTopPanelLayout.createSequentialGroup()
                        .addGroup(jTopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonHealth)
                            .addComponent(buttonMeal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonCloseScreen)
                    .addComponent(displayUserJXLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTopPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonHealth, buttonMeal});

        chartPanel.setMinimumSize(new java.awt.Dimension(202, 214));

        javax.swing.GroupLayout chartPanelLayout = new javax.swing.GroupLayout(chartPanel);
        chartPanel.setLayout(chartPanelLayout);
        chartPanelLayout.setHorizontalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        chartPanelLayout.setVerticalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelCaloriePieChartLayout = new javax.swing.GroupLayout(jPanelCaloriePieChart);
        jPanelCaloriePieChart.setLayout(jPanelCaloriePieChartLayout);
        jPanelCaloriePieChartLayout.setHorizontalGroup(
            jPanelCaloriePieChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCaloriePieChartLayout.createSequentialGroup()
                .addComponent(chartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelCaloriePieChartLayout.setVerticalGroup(
            jPanelCaloriePieChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCaloriePieChartLayout.createSequentialGroup()
                .addComponent(chartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabelServingSizeUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanelAddMeal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelCaloriePieChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelNutrientReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jTopPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1300, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTopPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanelAddMeal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanelNutrientReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelServingSizeUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelCaloriePieChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonMealActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMealActionPerformed
        // TODO add your handling code here:\EventTimetableViewController eventScreen = new EventTimetableViewController();
    }//GEN-LAST:event_buttonMealActionPerformed

    private void buttonHealthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonHealthActionPerformed
        // TODO add your handling code here:
        HealthViewController healthForm = new HealthViewController();
        healthForm.setVisible(true);
        healthForm.setMember(this.member);
        this.dispose();
    }//GEN-LAST:event_buttonHealthActionPerformed

    private void jButtonCloseScreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseScreenActionPerformed
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_jButtonCloseScreenActionPerformed

    private void jButtonCloseScreenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonCloseScreenMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonCloseScreenMouseClicked

    private void buttonAddMealActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddMealActionPerformed
        // TODO add your handling code here
        AddMealViewController addmealform = new AddMealViewController(this, true);
        addmealform.setVisible(true);

        //we come here when the addmealform is closed
        if(addmealform.getParentMustReloadExercises()) {
            initAllMealsComboBox();
        }
    }//GEN-LAST:event_buttonAddMealActionPerformed

    private void buttonMealEventSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMealEventSubmitActionPerformed

        String servingSizeAsString = textAreaMealPortion.getText();

        if ( LifeTrackerHelper.isValidDouble(servingSizeAsString) ) {

            Meal meal = (Meal)jXComboBoxMeal.getModel().getSelectedItem();
            Date mealDate = jXDatePickerMealEvent.getDate();
            double servingSize = Double.parseDouble(servingSizeAsString);
            double caloriesGained = LifeTrackerHelper.getCaloriesGainedForMealEvent(meal, servingSize);

            //Add a new meal event to the database
            Database db = new Database(LifeTrackerConstants.DATABASE_NAME);
            if(db.addNewMealEvent(this.member.getUserId(), meal.getMealId(), mealDate, caloriesGained, servingSize)) {
                //Also add it to the member as a mealEvent object
                this.member.addMealEvent(new MealEvent(meal, mealDate, servingSize, caloriesGained));
                mealEventStatusLabel.setText("Meal event saved. You gained " + String.format("%.0f", caloriesGained) + " calories!");
                mealEventStatusLabel.setForeground(Color.BLACK);

                Date today = LifeTrackerHelper.getToday();
                if(nutrientDate.equals(today)) {
                    logger.debug("Refreshing the pie chart after new meal event was saved for today");
                    displayNutrientBreakdown();
                }
                else
                logger.debug("Not refreshing the pie chart as it is not today");

            }
            else {
                mealEventStatusLabel.setText(LifeTrackerConstants.DATABASE_ERROR_MESSAGE);
                mealEventStatusLabel.setForeground(Color.RED);
            }
        }
    }//GEN-LAST:event_buttonMealEventSubmitActionPerformed

    private void textAreaMealPortionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textAreaMealPortionKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_textAreaMealPortionKeyTyped

    private void textAreaMealPortionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textAreaMealPortionKeyReleased
        String checkInt = textAreaMealPortion.getText();

        if(LifeTrackerHelper.checkNumber(checkInt)){
            isValidPortion = true;
            validPortion.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.TICK_ICON)));
        }
        else{
            isValidPortion = false;
            if(textAreaMealPortion.getText().trim().isEmpty()){
                validPortion.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.EMPTY_ICON)));
            }
            else{
                validPortion.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.CROSS_ICON)));
            }
        }

        //logger.debug("isValidPortion after checkNumber = " + isValidWeight);
    }//GEN-LAST:event_textAreaMealPortionKeyReleased

    private void textAreaMealPortionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textAreaMealPortionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textAreaMealPortionActionPerformed

    private void textAreaMealPortionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textAreaMealPortionFocusGained
        Meal meal = (Meal)jXComboBoxMeal.getModel().getSelectedItem();
        jLabelServingSizeUnit.setText(meal.getServiceSizeUnit());

        String nutritionInfo = "Nutrition Information:\n" +
        "Serving Size " + meal.getServingSize() + " " + meal.getServiceSizeUnit() + "\n" +
        "Calories: " + meal.getCalories() + "\n" +
        "Protein: " + meal.getProtein() + "\n" +
        "Carbs: " + meal.getCarbs() + "\n" +
        "Fat: " + meal.getFat() + "\n" +
        "Fiber: " + meal.getFiber();

        jTextAreaNutritionInfo.setText(nutritionInfo);
    }//GEN-LAST:event_textAreaMealPortionFocusGained

    private void jXDatePickerPieChartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXDatePickerPieChartActionPerformed

        this.nutrientDate = this.jXDatePickerPieChart.getDate();
        displayNutrientBreakdown();

    }//GEN-LAST:event_jXDatePickerPieChartActionPerformed

    public void addListener() {
            addWindowListener(
                new java.awt.event.WindowAdapter() {
                public void windowOpened(WindowEvent e) {                    
                    logger.debug("Opened MealViewController");

                    displayNutrientBreakdown();
                }
                public void windowClosing(WindowEvent e) {
                    logger.debug("Invoking WindowClosing from JDialog");
                    dispose();
                }
        });
    }        
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MealViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MealViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MealViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MealViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MealViewController().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddMeal;
    private javax.swing.JButton buttonHealth;
    private javax.swing.JButton buttonMeal;
    private javax.swing.JButton buttonMealEventSubmit;
    private javax.swing.JPanel chartPanel;
    private org.jdesktop.swingx.JXLabel displayUserJXLabel;
    private javax.swing.JButton jButtonCloseScreen;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelServingSizeUnit;
    private javax.swing.JPanel jPanelAddMeal;
    private javax.swing.JPanel jPanelCaloriePieChart;
    private javax.swing.JPanel jPanelNutrientReport;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextArea jTextAreaNutrientReport;
    private javax.swing.JTextArea jTextAreaNutritionInfo;
    private javax.swing.JPanel jTopPanel;
    private org.jdesktop.swingx.JXComboBox jXComboBoxMeal;
    private org.jdesktop.swingx.JXDatePicker jXDatePickerMealEvent;
    private org.jdesktop.swingx.JXDatePicker jXDatePickerPieChart;
    private javax.swing.JLabel labelMeal;
    private javax.swing.JLabel labelMealDate;
    private javax.swing.JLabel labelMealPortion;
    private javax.swing.JLabel mealEventStatusLabel;
    private javax.swing.JTextField textAreaMealPortion;
    private javax.swing.JLabel titleUpdateHealth;
    private javax.swing.JLabel validPortion;
    // End of variables declaration//GEN-END:variables
}

package ia.app.ltia.viewcontroller.health;

import ia.app.ltia.LifeTrackerConstants;
import ia.app.ltia.LifeTrackerHelper;
import ia.app.ltia.model.Database;
import ia.app.ltia.model.Exercise;
import ia.app.ltia.model.Member;
import ia.app.ltia.model.HealthProgress;
import ia.app.ltia.model.algo.BMICalculator;
import ia.app.ltia.model.algo.WeightChallenge;
import ia.app.ltia.viewcontroller.FrameDragListener;
import ia.app.ltia.viewcontroller.HomeViewController;
import ia.app.ltia.viewcontroller.meal.MealViewController;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;


/**
 *
 * @author
 */
public class HealthViewController extends javax.swing.JFrame {
    
    private static final Logger logger = LogManager.getLogger(HealthViewController.class);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFuture;
    
    private Member member;
    WeightChallenge weightChallenge;
    private ArrayList<Exercise> allExercises;
    private boolean isValidHeight = false;
    private boolean isValidWeight = false;
    private boolean isValidExerciseDuration = false;
    private BMICalculator bmi;
     
    /**
     * Creates new form HomePage
     */
    public HealthViewController() {
        initComponents();        
        initUndecoratedFrame();
        initComponentsCustomStyling();
        setTitle(LifeTrackerConstants.TITLE_HEALTH);
        initAllExercisesComboBox();
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
        
        //Update Weight and Height panel
        this.jPanelUpdateWeightAndHeight.setBackground(LifeTrackerConstants.GUI_FOREGROUND_COLOR_2);
        this.jTextFieldInsertUpdateWeight.setBackground(LifeTrackerConstants.GUI_FIELD_BACKGROUND_COLOR_2);
        this.jTextFieldInsertUpdateWeight.setBorder(BorderFactory.createEmptyBorder());
        //this.jTextFieldInsertUpdateWeight.setOpaque(true);
        this.jTextFieldInsertUpdateHeight.setBackground(LifeTrackerConstants.GUI_FIELD_BACKGROUND_COLOR_2);
        this.jTextFieldInsertUpdateHeight.setBorder(BorderFactory.createEmptyBorder());        
        this.jScrollPane1.setBorder(BorderFactory.createEmptyBorder());        
        this.jTextAreaWeightChallenge.setBackground(LifeTrackerConstants.GUI_FOREGROUND_COLOR_2);
               
        //Add Exercise panel
        this.jPanelAddExerciseEvent.setBackground(LifeTrackerConstants.GUI_FOREGROUND_COLOR_2);
        this.textAreaExerciseDuration.setBackground(LifeTrackerConstants.GUI_FIELD_BACKGROUND_COLOR_2);
        this.textAreaExerciseDuration.setBorder(BorderFactory.createEmptyBorder());
        
        //BMI Panel
        this.jPanelBMI.setBackground(LifeTrackerConstants.GUI_FOREGROUND_COLOR_2);    
        
        //Buttons
        buttonExerciseEventSubmit.setBackground(LifeTrackerConstants.GUI_BUTTON_COLOR);
        buttonWeightHeightUpdateSubmit.setBackground(LifeTrackerConstants.GUI_BUTTON_COLOR);
        buttonDailyStats.setBackground(LifeTrackerConstants.GUI_BUTTON_COLOR);
        buttonWeightChangeGraph.setBackground(LifeTrackerConstants.GUI_BUTTON_COLOR);
        buttonCalorieGraph.setBackground(LifeTrackerConstants.GUI_BUTTON_COLOR);
        jButtonGoHome.setBackground(LifeTrackerConstants.GUI_BUTTON_COLOR_2);
        jButtonGoHome.setForeground(LifeTrackerConstants.GUI_BUTTON_TEXT_COLOR_2);
        
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
    
    private void initAllExercisesComboBox(){                 
        Database db = new Database(LifeTrackerConstants.DATABASE_NAME);
        allExercises = db.getAllExercises();
        
        logger.debug("Got all exercises, found " + allExercises.size() + " exercises");
        
        jXComboBoxExercise.setModel(new DefaultComboBoxModel(allExercises.toArray()));
        AutoCompleteDecorator.decorate(jXComboBoxExercise, new ExerciseConverter());
    }
    
    public void setMember(Member member){   
        this.member = member;
        
        //logger.debug("Member is: " + this.member.toString());
        
        //we know the member, now get its member health and health progress
        Database db = new Database(LifeTrackerConstants.DATABASE_NAME);
        this.member.setMemberHealth(db.getMemberHealth(this.member.getUserId()));
        this.member.setHealthProgress(db.getHealthProgresses(this.member.getUserId()));  
        this.member.setCalorieRecords(db.getCalorieRecords(this.member.getUserId()));
        this.member.setExerciseEvents(db.getExerciseEvents(this.member.getUserId()));
        this.member.setMealEvents(db.getMealEvents(this.member.getUserId()));

        //we can only do this after we have got the member data
        this.weightChallenge = new WeightChallenge(this.member);
        
        updateUserStatus();
        updateWeightChallengeTextArea();        
        updateBMI();
        
        //also run the email scheduler
        //if already scheduled, cancel
        if(scheduledFuture != null)
            scheduledFuture.cancel(false);
        runDailyScheduler();
    }
    
    private void updateUserStatus() {
        String part1 = "Welcome " + this.member.getUsername() + ". Today is " + LifeTrackerConstants.GUI_DETAILED_DATE_FORMAT.format(new Date());        
        String part2 = "\n\n" + this.weightChallenge.getDayOfChallengeStatement();
        String part3 = "\n" + this.weightChallenge.getChallengeBriefStatement();
        String part4 = "\n" + this.weightChallenge.getCurrentWeightStatement();
        
        jXLabelUserStatus.setText(part1 + part2 + part3 + part4);
    }
    
    private void updateWeightChallengeTextArea() {        
        jTextAreaWeightChallenge.setText(this.weightChallenge.getChallengeProgressStatement());        
    }
    
    private void updateBMI() {
        bmi = new BMICalculator();
        double height = LifeTrackerHelper.getLatestHeight(member);
        double weight = LifeTrackerHelper.getLatestWeight(member);
        
        logger.debug("BMI calculation: Height = " + height + ", Weight = " + weight);
        
        bmi.setHeight(height);
        bmi.setWeight(weight);
        DecimalFormat df = new DecimalFormat("##.##");
        labelBMIHeightValue.setText(df.format(bmi.getHeight()));
        labelBMIWeightValue.setText(df.format(bmi.getWeight()));
        labelBMIScoreValue.setText(df.format(bmi.getBMI()));
        
        logger.debug("BMI calculation: BMI = " + df.format(bmi.getBMI()));
        
        labelBMIScoreStatement.setText("You are " + bmi.getBMIJudgement() + " according to BMI");
    }
    
    private void runDailyScheduler() {   
        //Calendar for now
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.setTimeZone(TimeZone.getTimeZone(LifeTrackerConstants.MY_TIME_ZONE));

        //Calendar for midnight (the time we want to run) ... actually i will do 23:59:59
        int scheduledHour = 23;
        int scheduledMinute = 59;
        int scheduledSecond = 59;        
        Calendar calendarForDailyRunTime = Calendar.getInstance();        
        calendarForDailyRunTime.set(Calendar.HOUR_OF_DAY, scheduledHour); 
        calendarForDailyRunTime.set(Calendar.MINUTE, scheduledMinute); 
        calendarForDailyRunTime.set(Calendar.SECOND, scheduledSecond); 
        calendarForDailyRunTime.setTimeZone(TimeZone.getTimeZone(LifeTrackerConstants.MY_TIME_ZONE));
        
        //If the scheduler time has already passed for today, then make the scheduled date tomorrow (add 1 day)
        if (calendarForDailyRunTime.getTimeInMillis() < calendarToday.getTimeInMillis() &&
            calendarForDailyRunTime.get(Calendar.DAY_OF_YEAR) == calendarToday.get(Calendar.DAY_OF_YEAR) &&
            calendarForDailyRunTime.get(Calendar.YEAR) == calendarToday.get(Calendar.YEAR)) {
            calendarForDailyRunTime.add(Calendar.DATE, 1);
        }
        
        logger.info("Screen refresh and Mail scheduler will run at " + 
                new SimpleDateFormat("yyyy-MM-dd k:mm:ss z").format(calendarForDailyRunTime.getTime()));
        logger.info("Time now is " + new SimpleDateFormat("yyyy-MM-dd k:mm:ss z").format(calendarToday.getTime()));
        
        //Calculate the time until midnight
        long emailRunTimeDelayInMilliseconds = 
                calendarForDailyRunTime.getTimeInMillis() - calendarToday.getTimeInMillis();
        logger.debug("Email scheduler run time delayed by " + emailRunTimeDelayInMilliseconds + " milliseconds");
        long emailRunTimeDelayInSeconds = emailRunTimeDelayInMilliseconds / 1000; //(60 * 60 * 1000); 

        //Now call the scheduler to run DailyScheduler at the right time and then every 24 hours
        DailyScheduler dailyScheduler = new DailyScheduler(this.member, this);
        scheduledFuture = 
            scheduler.scheduleAtFixedRate(dailyScheduler, 
                                          emailRunTimeDelayInSeconds, 
                                          LifeTrackerConstants.MAIL_SCHEDULER_FREQUENCY_IN_MINUTES*60, //*60 TO GIVE SECONDS
                                          TimeUnit.SECONDS);
        
        logger.info("Calling the mail scheduler to run in " + emailRunTimeDelayInSeconds + 
                " seconds and then every " + LifeTrackerConstants.MAIL_SCHEDULER_FREQUENCY_IN_MINUTES*60 + " seconds");
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        buttonWeightChangeGraph = new javax.swing.JButton();
        buttonCalorieGraph = new javax.swing.JButton();
        buttonDailyStats = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaWeightChallenge = new javax.swing.JTextArea();
        jButtonGoHome = new javax.swing.JButton();
        jPanelUpdateWeightAndHeight = new javax.swing.JPanel();
        labelUpdateWeight = new javax.swing.JLabel();
        jTextFieldInsertUpdateWeight = new javax.swing.JTextField();
        jLabelStatusOfUpdateWeightHeight = new javax.swing.JLabel();
        labelUpdateHeigh1 = new javax.swing.JLabel();
        jTextFieldInsertUpdateHeight = new javax.swing.JTextField();
        jXDatePickerWeightHeight = new org.jdesktop.swingx.JXDatePicker();
        buttonWeightHeightUpdateSubmit = new javax.swing.JButton();
        titleUpdateHealth1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        validWeight = new javax.swing.JLabel();
        validHeight = new javax.swing.JLabel();
        labelUpdateHeigh = new javax.swing.JLabel();
        jPanelAddExerciseEvent = new javax.swing.JPanel();
        exerciseEventStatusLabel = new javax.swing.JLabel();
        labelExerciseDuration1 = new javax.swing.JLabel();
        labelExercise = new javax.swing.JLabel();
        textAreaExerciseDuration = new javax.swing.JTextField();
        titleUpdateHealth = new javax.swing.JLabel();
        ADDEXERCISE = new javax.swing.JButton();
        labelExerciseDuration = new javax.swing.JLabel();
        jXDatePickerExerciseEvent = new org.jdesktop.swingx.JXDatePicker();
        jXComboBoxExercise = new org.jdesktop.swingx.JXComboBox();
        buttonExerciseEventSubmit = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        validExerciseDuration = new javax.swing.JLabel();
        jTopPanel = new javax.swing.JPanel();
        jXLabelUserStatus = new org.jdesktop.swingx.JXLabel();
        buttonMeal = new javax.swing.JButton();
        buttonHealth = new javax.swing.JButton();
        jButtonCloseScreen = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jPanelBMI = new javax.swing.JPanel();
        jLabelBMITitle = new javax.swing.JLabel();
        labelBMIScoreValue = new javax.swing.JLabel();
        labelBMIScoreLabel1 = new javax.swing.JLabel();
        labelBMIScoreStatement = new javax.swing.JLabel();
        labelBMIHeightLabel = new javax.swing.JLabel();
        labelBMIWeightLabel = new javax.swing.JLabel();
        labelBMIWeightValue = new javax.swing.JLabel();
        labelBMIHeightValue = new javax.swing.JLabel();
        labelBMIScoreLabel4 = new javax.swing.JLabel();
        labelBMIScoreLabel5 = new javax.swing.JLabel();

        jMenuItem1.setText("jMenuItem1");

        jMenuItem2.setText("jMenuItem2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1285, 624));
        setMinimumSize(new java.awt.Dimension(1285, 624));
        setUndecorated(true);
        setResizable(false);
        setSize(new java.awt.Dimension(1285, 624));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        buttonWeightChangeGraph.setBackground(new java.awt.Color(153, 204, 255));
        buttonWeightChangeGraph.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        buttonWeightChangeGraph.setText("Weight Change Graph");
        buttonWeightChangeGraph.setPreferredSize(new java.awt.Dimension(177, 15));
        buttonWeightChangeGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonWeightChangeGraphActionPerformed(evt);
            }
        });

        buttonCalorieGraph.setBackground(new java.awt.Color(50, 163, 245));
        buttonCalorieGraph.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        buttonCalorieGraph.setText("Calorie Change Graph");
        buttonCalorieGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCalorieGraphActionPerformed(evt);
            }
        });

        buttonDailyStats.setBackground(new java.awt.Color(50, 163, 245));
        buttonDailyStats.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        buttonDailyStats.setText("Daily Statistics");
        buttonDailyStats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDailyStatsActionPerformed(evt);
            }
        });

        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextAreaWeightChallenge.setColumns(20);
        jTextAreaWeightChallenge.setFont(new java.awt.Font("Keep Calm Med", 0, 20)); // NOI18N
        jTextAreaWeightChallenge.setLineWrap(true);
        jTextAreaWeightChallenge.setRows(3);
        jTextAreaWeightChallenge.setText("Goal is to ...");
        jTextAreaWeightChallenge.setWrapStyleWord(true);
        jTextAreaWeightChallenge.setAutoscrolls(false);
        jScrollPane1.setViewportView(jTextAreaWeightChallenge);

        jButtonGoHome.setBackground(new java.awt.Color(50, 163, 245));
        jButtonGoHome.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        jButtonGoHome.setText("<< Logout");
        jButtonGoHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGoHomeActionPerformed(evt);
            }
        });

        labelUpdateWeight.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        labelUpdateWeight.setText(LifeTrackerConstants.WEIGHT_LABEL);

        jTextFieldInsertUpdateWeight.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        jTextFieldInsertUpdateWeight.setToolTipText("Enter height");
        jTextFieldInsertUpdateWeight.setPreferredSize(new java.awt.Dimension(8, 21));
        jTextFieldInsertUpdateWeight.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldInsertUpdateWeightKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldInsertUpdateWeightKeyTyped(evt);
            }
        });

        jLabelStatusOfUpdateWeightHeight.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N

        labelUpdateHeigh1.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        labelUpdateHeigh1.setText("Date");

        jTextFieldInsertUpdateHeight.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        jTextFieldInsertUpdateHeight.setPreferredSize(new java.awt.Dimension(8, 21));
        jTextFieldInsertUpdateHeight.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldInsertUpdateHeightKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldInsertUpdateHeightKeyTyped(evt);
            }
        });

        jXDatePickerWeightHeight.setDate(Calendar.getInstance().getTime());
        jXDatePickerWeightHeight.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        jXDatePickerWeightHeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXDatePickerWeightHeightActionPerformed(evt);
            }
        });

        buttonWeightHeightUpdateSubmit.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        buttonWeightHeightUpdateSubmit.setText("Submit");
        buttonWeightHeightUpdateSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonWeightHeightUpdateSubmitActionPerformed(evt);
            }
        });

        titleUpdateHealth1.setFont(new java.awt.Font("Keep Calm Med", 0, 14)); // NOI18N
        titleUpdateHealth1.setText("Update Weight and Height");

        validWeight.setFont(new java.awt.Font("Tahoma", 2, 13)); // NOI18N

        validHeight.setFont(new java.awt.Font("Tahoma", 2, 13)); // NOI18N

        labelUpdateHeigh.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        labelUpdateHeigh.setText(LifeTrackerConstants.HEIGHT_LABEL);

        javax.swing.GroupLayout jPanelUpdateWeightAndHeightLayout = new javax.swing.GroupLayout(jPanelUpdateWeightAndHeight);
        jPanelUpdateWeightAndHeight.setLayout(jPanelUpdateWeightAndHeightLayout);
        jPanelUpdateWeightAndHeightLayout.setHorizontalGroup(
            jPanelUpdateWeightAndHeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelUpdateWeightAndHeightLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelUpdateWeightAndHeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelUpdateWeightAndHeightLayout.createSequentialGroup()
                        .addGroup(jPanelUpdateWeightAndHeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelUpdateHeigh1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelUpdateWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelUpdateHeigh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelUpdateWeightAndHeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelUpdateWeightAndHeightLayout.createSequentialGroup()
                                .addGroup(jPanelUpdateWeightAndHeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldInsertUpdateHeight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanelUpdateWeightAndHeightLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(validHeight, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelUpdateWeightAndHeightLayout.createSequentialGroup()
                                .addGroup(jPanelUpdateWeightAndHeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelUpdateWeightAndHeightLayout.createSequentialGroup()
                                        .addComponent(jTextFieldInsertUpdateWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(validWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jXDatePickerWeightHeight, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(197, 197, 197))
                    .addGroup(jPanelUpdateWeightAndHeightLayout.createSequentialGroup()
                        .addGroup(jPanelUpdateWeightAndHeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(titleUpdateHealth1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buttonWeightHeightUpdateSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(jPanelUpdateWeightAndHeightLayout.createSequentialGroup()
                        .addComponent(jLabelStatusOfUpdateWeightHeight, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanelUpdateWeightAndHeightLayout.setVerticalGroup(
            jPanelUpdateWeightAndHeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelUpdateWeightAndHeightLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleUpdateHealth1)
                .addGroup(jPanelUpdateWeightAndHeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelUpdateWeightAndHeightLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(labelUpdateWeight)
                        .addGap(24, 24, 24)
                        .addGroup(jPanelUpdateWeightAndHeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelUpdateHeigh)
                            .addComponent(jTextFieldInsertUpdateHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(labelUpdateHeigh1))
                    .addGroup(jPanelUpdateWeightAndHeightLayout.createSequentialGroup()
                        .addGroup(jPanelUpdateWeightAndHeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanelUpdateWeightAndHeightLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanelUpdateWeightAndHeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTextFieldInsertUpdateWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(validWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(5, 5, 5)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(49, 49, 49))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelUpdateWeightAndHeightLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(validHeight, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22)))
                        .addComponent(jXDatePickerWeightHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(buttonWeightHeightUpdateSubmit)
                .addGap(18, 18, 18)
                .addComponent(jLabelStatusOfUpdateWeightHeight, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        exerciseEventStatusLabel.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N

        labelExerciseDuration1.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        labelExerciseDuration1.setText(LifeTrackerConstants.EXERCISE_DURATION_LABEL);

        labelExercise.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        labelExercise.setText(LifeTrackerConstants.EXERCISE_ADD_LABEL);

        textAreaExerciseDuration.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        textAreaExerciseDuration.setPreferredSize(new java.awt.Dimension(8, 21));
        textAreaExerciseDuration.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textAreaExerciseDurationKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textAreaExerciseDurationKeyTyped(evt);
            }
        });

        titleUpdateHealth.setFont(new java.awt.Font("Keep Calm Med", 0, 14)); // NOI18N
        titleUpdateHealth.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        titleUpdateHealth.setText("Add Exercise Event");

        ADDEXERCISE.setFont(new java.awt.Font("Keep Calm Med", 0, 11)); // NOI18N
        ADDEXERCISE.setText("New");
        ADDEXERCISE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ADDEXERCISEActionPerformed(evt);
            }
        });

        labelExerciseDuration.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        labelExerciseDuration.setText("Date");

        jXDatePickerExerciseEvent.setDate(Calendar.getInstance().getTime());
        jXDatePickerExerciseEvent.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N

        jXComboBoxExercise.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        jXComboBoxExercise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXComboBoxExerciseActionPerformed(evt);
            }
        });

        buttonExerciseEventSubmit.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        buttonExerciseEventSubmit.setText("Submit");
        buttonExerciseEventSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExerciseEventSubmitActionPerformed(evt);
            }
        });

        validExerciseDuration.setFont(new java.awt.Font("Tahoma", 2, 13)); // NOI18N

        javax.swing.GroupLayout jPanelAddExerciseEventLayout = new javax.swing.GroupLayout(jPanelAddExerciseEvent);
        jPanelAddExerciseEvent.setLayout(jPanelAddExerciseEventLayout);
        jPanelAddExerciseEventLayout.setHorizontalGroup(
            jPanelAddExerciseEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAddExerciseEventLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelAddExerciseEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonExerciseEventSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelAddExerciseEventLayout.createSequentialGroup()
                        .addGroup(jPanelAddExerciseEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelExerciseDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelExerciseDuration1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelExercise, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelAddExerciseEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jXDatePickerExerciseEvent, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelAddExerciseEventLayout.createSequentialGroup()
                                .addComponent(jXComboBoxExercise, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ADDEXERCISE))
                            .addGroup(jPanelAddExerciseEventLayout.createSequentialGroup()
                                .addGroup(jPanelAddExerciseEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(textAreaExerciseDuration, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(validExerciseDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(titleUpdateHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exerciseEventStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanelAddExerciseEventLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {labelExercise, labelExerciseDuration});

        jPanelAddExerciseEventLayout.setVerticalGroup(
            jPanelAddExerciseEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAddExerciseEventLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelAddExerciseEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelAddExerciseEventLayout.createSequentialGroup()
                        .addComponent(titleUpdateHealth)
                        .addGap(18, 18, 18)
                        .addGroup(jPanelAddExerciseEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jXComboBoxExercise, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ADDEXERCISE)
                            .addComponent(labelExercise))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelAddExerciseEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(textAreaExerciseDuration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelExerciseDuration1)))
                    .addComponent(validExerciseDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanelAddExerciseEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelExerciseDuration)
                    .addComponent(jXDatePickerExerciseEvent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(buttonExerciseEventSubmit)
                .addGap(18, 18, 18)
                .addComponent(exerciseEventStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );

        jPanelAddExerciseEventLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelExercise, labelExerciseDuration});

        jXLabelUserStatus.setBackground(new java.awt.Color(204, 255, 255));
        jXLabelUserStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jXLabelUserStatus.setForeground(new java.awt.Color(255, 255, 255));
        jXLabelUserStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXLabelUserStatus.setText("Welcome ...");
        jXLabelUserStatus.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jXLabelUserStatus.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        jXLabelUserStatus.setLineWrap(true);

        buttonMeal.setFont(new java.awt.Font("Keep Calm Med", 0, 40)); // NOI18N
        buttonMeal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ia/app/ltia/images/meal-button-white.png"))); // NOI18N
        buttonMeal.setText("Meal");
        buttonMeal.setBorderPainted(false);
        buttonMeal.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        buttonMeal.setFocusPainted(false);
        buttonMeal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                buttonMealMouseEntered(evt);
            }
        });
        buttonMeal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMealActionPerformed(evt);
            }
        });

        buttonHealth.setFont(new java.awt.Font("Keep Calm Med", 0, 40)); // NOI18N
        buttonHealth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ia/app/ltia/images/health-button-white.png"))); // NOI18N
        buttonHealth.setText("Health");
        buttonHealth.setBorderPainted(false);
        buttonHealth.setFocusPainted(false);

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
        jButtonCloseScreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseScreenActionPerformed(evt);
            }
        });

        jSeparator3.setMinimumSize(new java.awt.Dimension(50, 5));
        jSeparator3.setPreferredSize(new java.awt.Dimension(50, 5));

        javax.swing.GroupLayout jTopPanelLayout = new javax.swing.GroupLayout(jTopPanel);
        jTopPanel.setLayout(jTopPanelLayout);
        jTopPanelLayout.setHorizontalGroup(
            jTopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTopPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jXLabelUserStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 496, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jTopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jTopPanelLayout.createSequentialGroup()
                        .addComponent(buttonHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jTopPanelLayout.createSequentialGroup()
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)))
                .addComponent(buttonMeal, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonCloseScreen))
        );
        jTopPanelLayout.setVerticalGroup(
            jTopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTopPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jTopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jXLabelUserStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jTopPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jTopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonCloseScreen)
                            .addGroup(jTopPanelLayout.createSequentialGroup()
                                .addGroup(jTopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(buttonMeal)
                                    .addComponent(buttonHealth))
                                .addGap(3, 3, 3)
                                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        jLabelBMITitle.setFont(new java.awt.Font("Keep Calm Med", 0, 14)); // NOI18N
        jLabelBMITitle.setText("BMI");

        labelBMIScoreValue.setFont(new java.awt.Font("Keep Calm Med", 0, 24)); // NOI18N
        labelBMIScoreValue.setText("0");

        labelBMIScoreLabel1.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        labelBMIScoreLabel1.setText("Your BMI score is:");

        labelBMIScoreStatement.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        labelBMIScoreStatement.setText("You are ...");

        labelBMIHeightLabel.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        labelBMIHeightLabel.setText("Your height is:");

        labelBMIWeightLabel.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        labelBMIWeightLabel.setText("Your weight is:");

        labelBMIWeightValue.setFont(new java.awt.Font("Keep Calm Med", 0, 24)); // NOI18N
        labelBMIWeightValue.setText("0");

        labelBMIHeightValue.setFont(new java.awt.Font("Keep Calm Med", 0, 24)); // NOI18N
        labelBMIHeightValue.setText("0");

        labelBMIScoreLabel4.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        labelBMIScoreLabel4.setText("Cm");

        labelBMIScoreLabel5.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        labelBMIScoreLabel5.setText("Kg");

        javax.swing.GroupLayout jPanelBMILayout = new javax.swing.GroupLayout(jPanelBMI);
        jPanelBMI.setLayout(jPanelBMILayout);
        jPanelBMILayout.setHorizontalGroup(
            jPanelBMILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBMILayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBMILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelBMIScoreStatement, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelBMILayout.createSequentialGroup()
                        .addGroup(jPanelBMILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelBMITitle, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelBMIWeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelBMILayout.createSequentialGroup()
                                .addGroup(jPanelBMILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelBMIScoreLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelBMIHeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanelBMILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelBMILayout.createSequentialGroup()
                                        .addComponent(labelBMIWeightValue, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(labelBMIScoreLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(labelBMIScoreValue, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanelBMILayout.createSequentialGroup()
                                        .addComponent(labelBMIHeightValue, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(labelBMIScoreLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 10, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelBMILayout.setVerticalGroup(
            jPanelBMILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBMILayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelBMITitle)
                .addGap(18, 18, 18)
                .addGroup(jPanelBMILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelBMIHeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelBMIHeightValue)
                    .addComponent(labelBMIScoreLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanelBMILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelBMILayout.createSequentialGroup()
                        .addGroup(jPanelBMILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelBMIWeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelBMIScoreLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35)
                        .addGroup(jPanelBMILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelBMIScoreLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelBMIScoreValue)))
                    .addComponent(labelBMIWeightValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelBMIScoreStatement)
                .addGap(33, 33, 33))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTopPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanelUpdateWeightAndHeight, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                        .addComponent(jPanelBMI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(58, 58, 58)
                        .addComponent(jPanelAddExerciseEvent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18))
            .addGroup(layout.createSequentialGroup()
                .addGap(125, 125, 125)
                .addComponent(buttonWeightChangeGraph, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(125, 125, 125)
                .addComponent(buttonDailyStats)
                .addGap(105, 105, 105)
                .addComponent(buttonCalorieGraph)
                .addGap(121, 121, 121)
                .addComponent(jButtonGoHome)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTopPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelUpdateWeightAndHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanelBMI, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanelAddExerciseEvent, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonDailyStats)
                    .addComponent(buttonCalorieGraph)
                    .addComponent(jButtonGoHome)
                    .addComponent(buttonWeightChangeGraph, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(54, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonCalorieGraph, buttonDailyStats, buttonWeightChangeGraph, jButtonGoHome});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonWeightChangeGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonWeightChangeGraphActionPerformed
        // TODO add your handling code here:
        WeightProgressGraphViewController weightForm = new WeightProgressGraphViewController(this, true);
        logger.debug("About to open weight graph form");
        weightForm.setMember(this.member);
        weightForm.setVisible(true);
        
    }//GEN-LAST:event_buttonWeightChangeGraphActionPerformed

    private void buttonCalorieGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCalorieGraphActionPerformed
        CalorieProgressGraphViewController calorieForm = new CalorieProgressGraphViewController(this, true);
        calorieForm.setMember(this.member);
        calorieForm.setVisible(true);
    }//GEN-LAST:event_buttonCalorieGraphActionPerformed

    private void buttonDailyStatsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDailyStatsActionPerformed
        // TODO add your handling code here:
        DailyStatisticsViewController statsForm = new DailyStatisticsViewController(this, true);
        statsForm.setMember(this.member);
        statsForm.setVisible(true);       
        
        
    }//GEN-LAST:event_buttonDailyStatsActionPerformed

    private void buttonMealActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMealActionPerformed
        // TODO add your handling code here:\EventTimetableViewController eventScreen = new EventTimetableViewController();
        MealViewController mealScreen = new MealViewController();
        mealScreen.setMember(this.member);
        mealScreen.setVisible(true);        
        this.dispose();
    }//GEN-LAST:event_buttonMealActionPerformed

    private void jButtonGoHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGoHomeActionPerformed
        HomeViewController homeform = new HomeViewController();
        homeform.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButtonGoHomeActionPerformed

    private void buttonMealMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonMealMouseEntered
        // TODO add your handling code here:
        
    }//GEN-LAST:event_buttonMealMouseEntered

    private void jButtonCloseScreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseScreenActionPerformed
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_jButtonCloseScreenActionPerformed

    private void buttonExerciseEventSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExerciseEventSubmitActionPerformed
        String durationInMinutesString = textAreaExerciseDuration.getText();

        if ( LifeTrackerHelper.isValidDouble(durationInMinutesString) ) {

            Exercise exercise = (Exercise)jXComboBoxExercise.getModel().getSelectedItem(); //static polymoprhism
            Date exerciseDate = jXDatePickerExerciseEvent.getDate();
            double durationInMinutes = Double.parseDouble(durationInMinutesString);
            double caloriesBurned = LifeTrackerHelper.getCaloriesBurnedForExerciseEvent(exercise, durationInMinutes);

            //Add a new exercise event
            Database db = new Database(LifeTrackerConstants.DATABASE_NAME);
            if(db.addNewExerciseEvent(this.member.getUserId(), exercise.getExerciseId(), exerciseDate, caloriesBurned, durationInMinutes)) {
                exerciseEventStatusLabel.setText("Exercise event saved. You burned " + String.format("%.0f", caloriesBurned) + " calories!");
                exerciseEventStatusLabel.setForeground(Color.BLACK);
                exerciseEventStatusLabel.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.TICK_ICON)));  
                //do this to refresh all the member data that is used on screen, in charts etc..
                setMember(this.member);
            }
            else {
                exerciseEventStatusLabel.setText(LifeTrackerConstants.DATABASE_ERROR_MESSAGE);
                exerciseEventStatusLabel.setForeground(Color.RED);
                exerciseEventStatusLabel.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.CROSS_ICON)));  
            }
        }
    }//GEN-LAST:event_buttonExerciseEventSubmitActionPerformed

    private void jXComboBoxExerciseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXComboBoxExerciseActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jXComboBoxExerciseActionPerformed

    private void ADDEXERCISEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ADDEXERCISEActionPerformed
        // TODO add your handling code here:
        AddExerciseViewController addexerciseform = new AddExerciseViewController(this, true);
        addexerciseform.setVisible(true);

        //we come here when the addexerciseform is closed
        if(addexerciseform.getParentMustReloadExercises()) {
            initAllExercisesComboBox();
        }
    }//GEN-LAST:event_ADDEXERCISEActionPerformed

    private void textAreaExerciseDurationKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textAreaExerciseDurationKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_textAreaExerciseDurationKeyTyped

    private void buttonWeightHeightUpdateSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonWeightHeightUpdateSubmitActionPerformed
        //Empty the status
        jLabelStatusOfUpdateWeightHeight.setText("");
        jLabelStatusOfUpdateWeightHeight.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.EMPTY_ICON)));     
        

        if(isValidHeight && isValidWeight){

            int weight = Integer.parseInt(jTextFieldInsertUpdateWeight.getText());
            int height = Integer.parseInt(jTextFieldInsertUpdateHeight.getText());
            Date date = jXDatePickerWeightHeight.getDate();

            logger.info("Updating member: " + member.getUsername() + " -> new Weight: " + weight + " Height: " + 
                    height + " for date: " + LifeTrackerConstants.DB_DATE_FORMAT.format(date));

            HealthProgress updateHealth = new HealthProgress(weight, height, date);
            this.member.addHealthProgress(updateHealth);

            jLabelStatusOfUpdateWeightHeight.setText("Saved");
            jLabelStatusOfUpdateWeightHeight.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.TICK_ICON)));     

            jTextFieldInsertUpdateWeight.setText("");
            jTextFieldInsertUpdateHeight.setText("");
            this.jXDatePickerWeightHeight.setDate(Calendar.getInstance().getTime());

            validHeight.setIcon(new ImageIcon(getClass().getResource(""))); 
            validWeight.setIcon(new ImageIcon(getClass().getResource(""))); 
            //this will refresh the screen
            setMember(member);

        }
        else{
            jLabelStatusOfUpdateWeightHeight.setText("Invalid values. Please correct and retry");
            jLabelStatusOfUpdateWeightHeight.setForeground(Color.RED);
            jLabelStatusOfUpdateWeightHeight.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.CROSS_ICON)));     
        }
    }//GEN-LAST:event_buttonWeightHeightUpdateSubmitActionPerformed

    private void jXDatePickerWeightHeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXDatePickerWeightHeightActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jXDatePickerWeightHeightActionPerformed

    private void jTextFieldInsertUpdateHeightKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldInsertUpdateHeightKeyTyped
    }//GEN-LAST:event_jTextFieldInsertUpdateHeightKeyTyped

    private void jTextFieldInsertUpdateWeightKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldInsertUpdateWeightKeyTyped

    }//GEN-LAST:event_jTextFieldInsertUpdateWeightKeyTyped

    private void jTextFieldInsertUpdateWeightKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldInsertUpdateWeightKeyReleased
        resetWeightHeightChanged();

        String checkInt = jTextFieldInsertUpdateWeight.getText();
        
        if(LifeTrackerHelper.checkNumber(checkInt)){
            isValidWeight = true;
            validWeight.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.TICK_ICON)));            
        }
        else{
            isValidWeight = false;
            if(jTextFieldInsertUpdateWeight.getText().trim().isEmpty()){
                validWeight.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.EMPTY_ICON)));                        
            }
            else{
                validWeight.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.CROSS_ICON)));                            
            }
        }
        
        logger.debug("isValidWeight after checkNumber = " + isValidWeight);
    }//GEN-LAST:event_jTextFieldInsertUpdateWeightKeyReleased

    private void jTextFieldInsertUpdateHeightKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldInsertUpdateHeightKeyReleased
        
        resetWeightHeightChanged();

        String checkInt = jTextFieldInsertUpdateHeight.getText();
        if(LifeTrackerHelper.checkNumber(checkInt)){
            isValidHeight = true;
            validHeight.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.TICK_ICON)));
        }
        else{
            isValidHeight = false;
            if(jTextFieldInsertUpdateWeight.getText().trim().isEmpty()){
                validHeight.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.EMPTY_ICON)));           
            }
            else{
                validHeight.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.CROSS_ICON)));
            }            
        }
    }//GEN-LAST:event_jTextFieldInsertUpdateHeightKeyReleased

    private void textAreaExerciseDurationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textAreaExerciseDurationKeyReleased
        String checkInt = textAreaExerciseDuration.getText();
        if(LifeTrackerHelper.checkNumber(checkInt)){
            isValidExerciseDuration = true;
            validExerciseDuration.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.TICK_ICON)));
        }
        else{
            isValidExerciseDuration = false;
            if(textAreaExerciseDuration.getText().trim().isEmpty()){
                validExerciseDuration.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.EMPTY_ICON)));           
            }
            else{
                validExerciseDuration.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.CROSS_ICON)));
            }            
        }        
    }//GEN-LAST:event_textAreaExerciseDurationKeyReleased

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseClicked

    private void resetWeightHeightChanged() {        
        //Since the inputs of update weight and height have changed, empty the status display        
        jLabelStatusOfUpdateWeightHeight.setText("");
        jLabelStatusOfUpdateWeightHeight.setIcon(new ImageIcon(getClass().getResource(LifeTrackerConstants.EMPTY_ICON)));        
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
            java.util.logging.Logger.getLogger(HealthViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HealthViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HealthViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HealthViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HealthViewController().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ADDEXERCISE;
    private javax.swing.JButton buttonCalorieGraph;
    private javax.swing.JButton buttonDailyStats;
    private javax.swing.JButton buttonExerciseEventSubmit;
    private javax.swing.JButton buttonHealth;
    private javax.swing.JButton buttonMeal;
    private javax.swing.JButton buttonWeightChangeGraph;
    private javax.swing.JButton buttonWeightHeightUpdateSubmit;
    private javax.swing.JLabel exerciseEventStatusLabel;
    private javax.swing.JButton jButtonCloseScreen;
    private javax.swing.JButton jButtonGoHome;
    private javax.swing.JLabel jLabelBMITitle;
    private javax.swing.JLabel jLabelStatusOfUpdateWeightHeight;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanelAddExerciseEvent;
    private javax.swing.JPanel jPanelBMI;
    private javax.swing.JPanel jPanelUpdateWeightAndHeight;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTextArea jTextAreaWeightChallenge;
    private javax.swing.JTextField jTextFieldInsertUpdateHeight;
    private javax.swing.JTextField jTextFieldInsertUpdateWeight;
    private javax.swing.JPanel jTopPanel;
    private org.jdesktop.swingx.JXComboBox jXComboBoxExercise;
    private org.jdesktop.swingx.JXDatePicker jXDatePickerExerciseEvent;
    private org.jdesktop.swingx.JXDatePicker jXDatePickerWeightHeight;
    private org.jdesktop.swingx.JXLabel jXLabelUserStatus;
    private javax.swing.JLabel labelBMIHeightLabel;
    private javax.swing.JLabel labelBMIHeightValue;
    private javax.swing.JLabel labelBMIScoreLabel1;
    private javax.swing.JLabel labelBMIScoreLabel4;
    private javax.swing.JLabel labelBMIScoreLabel5;
    private javax.swing.JLabel labelBMIScoreStatement;
    private javax.swing.JLabel labelBMIScoreValue;
    private javax.swing.JLabel labelBMIWeightLabel;
    private javax.swing.JLabel labelBMIWeightValue;
    private javax.swing.JLabel labelExercise;
    private javax.swing.JLabel labelExerciseDuration;
    private javax.swing.JLabel labelExerciseDuration1;
    private javax.swing.JLabel labelUpdateHeigh;
    private javax.swing.JLabel labelUpdateHeigh1;
    private javax.swing.JLabel labelUpdateWeight;
    private javax.swing.JTextField textAreaExerciseDuration;
    private javax.swing.JLabel titleUpdateHealth;
    private javax.swing.JLabel titleUpdateHealth1;
    private javax.swing.JLabel validExerciseDuration;
    private javax.swing.JLabel validHeight;
    private javax.swing.JLabel validWeight;
    // End of variables declaration//GEN-END:variables
}

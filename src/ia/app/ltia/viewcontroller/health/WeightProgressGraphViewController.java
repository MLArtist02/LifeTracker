/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ia.app.ltia.viewcontroller.health;

import ia.app.ltia.LifeTrackerConstants;
import ia.app.ltia.model.HealthProgress;
import ia.app.ltia.model.Member;
import ia.app.ltia.viewcontroller.FrameDragListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.swing.JDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author
 */
public class WeightProgressGraphViewController extends javax.swing.JDialog {
    
    private static final Logger logger = LogManager.getLogger(WeightProgressGraphViewController.class);    
    private Member member;
    
    /**
     * Creates new form WeightProgressGraphViewController
     */
    public WeightProgressGraphViewController(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        addListener();
        initUndecoratedFrame();
        initComponentsCustomStyling();                
    }
    
    public void setMember(Member member){
        this.member = member;
        logger.debug("Settng member in weight progress graph form to: " + this.member.toString());
    }
    
    private void initUndecoratedFrame(){
        
        //To remove title, must set undecorated to true.
        //But dont do that in here, it must be in initComponents so 
        //you must do it in the JDialog properties.
        //this.setUndecorated(true);
        
        //Drag the window from by selecting any part of the frame        
        
        FrameDragListener frameDragListener = new FrameDragListener((java.awt.Window)this);
        this.addMouseListener(frameDragListener);
        this.addMouseMotionListener(frameDragListener);        

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);        
    }    
    
    private void initComponentsCustomStyling() {        
        //Background
        this.getContentPane().setBackground(LifeTrackerConstants.GUI_BACKGROUND_COLOR_2);               
    }         
    
    public void addListener() {
            addWindowListener(
                new java.awt.event.WindowAdapter() {
                public void windowOpened(WindowEvent e) {
                    logger.debug("Invoking windowOpened from JDialog");
                    displayWeightProgressChart();
                }
                public void windowClosing(WindowEvent e) {
                    logger.debug("Invoking WindowClosing from JDialog");
                    dispose();
                }
        });
    }
    
    private void displayWeightProgressChart() {        
        displayChart(LifeTrackerConstants.WEIGHT_PROGRESS_CHART_TITLE, createWeightProgressDataset());        
    }            
    
    private void displayWeightTargetChart() {        
        displayChart(LifeTrackerConstants.WEIGHT_TARGET_CHART_TITLE, createTargetWeightDataset());        
    }    
    
    private void displayCombinedChart() {    
        jPanel1.removeAll();
        jPanel1.revalidate();        
        
        XYDataset dataset1 = createWeightProgressDataset();
        XYDataset dataset2 = createTargetWeightDataset();
        String title = "Weight Target and Progress";
        
        JFreeChart timeSeriesChart = getCombinedChart(title, dataset1, dataset2);
        
        chartPanel = new ChartPanel(timeSeriesChart);
        chartPanel.setSize(jPanel1.getSize());
  
        jPanel1.add(chartPanel);
        jPanel1.getParent().validate();
        jPanel1.repaint();
        jPanel1.revalidate();    
    }       
    
    private TimeSeries getWeightProgressTimeSeries() {

        TimeSeries timeSeries = new TimeSeries(LifeTrackerConstants.WEIGHT_PROGRESS_CHART_TITLE);
        
        logger.debug("Getting weight data set for member: " + this.member.toString());
        
        //first get the start date weight
        int firstWeightRecord = this.member.getMemberHealth().getWeight();
        Date firstDateRecord = this.member.getMemberHealth().getStartDate();

        timeSeries.add(new Day(firstDateRecord), firstWeightRecord);
        
        //now get the health progress weights        
        ArrayList<HealthProgress> healthProgresses = this.member.getHealthProgresses();
        Iterator i = healthProgresses.iterator();
        
        while(i.hasNext()) {
            HealthProgress healthProgress = (HealthProgress)i.next();           
            timeSeries.add(new Day(healthProgress.getDate()), healthProgress.getWeight());
        }
        
        return timeSeries;        
    }
        
    private XYDataset createWeightProgressDataset() {        
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(getWeightProgressTimeSeries());
        
        return dataset;
    } 
    
    private TimeSeries getTargetWeightTimeSeries() {
        TimeSeries timeSeries = new TimeSeries(LifeTrackerConstants.WEIGHT_TARGET_CHART_TITLE);
        
        logger.debug("Getting weight data set for member: " + this.member.toString());
        
        //first get the start date and weight
        int startWeightRecord = this.member.getMemberHealth().getWeight();
        Date startDateRecord = this.member.getMemberHealth().getStartDate();

        timeSeries.add(new Day(startDateRecord), startWeightRecord);
        
        //now get the target date and weight
        int targetWeightRecord = this.member.getMemberHealth().getTargetWeight();
        Date targetDateRecord = this.member.getMemberHealth().getTargetDate();

        timeSeries.add(new Day(targetDateRecord), targetWeightRecord);
        
        return timeSeries;
    }
        
    private XYDataset createTargetWeightDataset( ) {           
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(getTargetWeightTimeSeries());
        
        return dataset;
    }     

    private void displayChart(String title, XYDataset dataSet) {        
        jPanel1.removeAll();
        jPanel1.revalidate();        
        
        JFreeChart timeSeriesChart = getChart(title, dataSet);
        
        chartPanel = new ChartPanel(timeSeriesChart);
        chartPanel.setSize(jPanel1.getSize());
  
        jPanel1.add(chartPanel);
        jPanel1.getParent().validate();
        jPanel1.repaint();
        jPanel1.revalidate();        
    }
    
    private JFreeChart getChart(String title, XYDataset dataSet) {        
        logger.debug("Chart title = " + title);
        logger.debug("Chart data row count = " + dataSet.getSeriesCount());
        
        String chartTitle = title; 
        
        JFreeChart timeSeriesChart = ChartFactory.createTimeSeriesChart(
            chartTitle,
            "Date",
            "Weight (Kg)",
            dataSet,
            true,
            true,
            false);
        
        return timeSeriesChart;
    }    
    
    private JFreeChart getCombinedChart(String title, XYDataset dataSet1, XYDataset dataSet2) {        
        logger.debug("Chart title = " + title);
        String chartTitle = title; 
        
        JFreeChart timeSeriesChart = ChartFactory.createTimeSeriesChart(
            chartTitle,
            "Date",
            "Weight (Kg)",
            dataSet1,
            true,
            true,
            false);
        
        //https://stackoverflow.com/questions/12173306/jfreechart-timeseries-and-candlestick-on-the-same-chart
        
        XYPlot plot = (XYPlot)timeSeriesChart.getXYPlot();
        
        int secondTimeSeriesIndex = 1; //2nd will be = 2 - 1 = 0 (as 1st = 0)
        plot.setDataset(secondTimeSeriesIndex, dataSet2);
        plot.mapDatasetToRangeAxis(secondTimeSeriesIndex, 0);
        
        XYItemRenderer renderer2 = new XYLineAndShapeRenderer();
        plot.setRenderer(secondTimeSeriesIndex, renderer2);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        
        return timeSeriesChart;        
    }        

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonClose = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        chartPanel = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        buttonClose.setFont(new java.awt.Font("Keep Calm Med", 0, 11)); // NOI18N
        buttonClose.setText("Close");
        buttonClose.setPreferredSize(new java.awt.Dimension(69, 35));
        buttonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCloseActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout chartPanelLayout = new javax.swing.GroupLayout(chartPanel);
        chartPanel.setLayout(chartPanelLayout);
        chartPanelLayout.setHorizontalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        chartPanelLayout.setVerticalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(1123, Short.MAX_VALUE)
                .addComponent(chartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(405, Short.MAX_VALUE))
        );

        jToggleButton1.setFont(new java.awt.Font("Keep Calm Med", 0, 11)); // NOI18N
        jToggleButton1.setText(LifeTrackerConstants.WEIGHT_TARGET_CHART_TOGGLE_BUTTON);
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Keep Calm Med", 0, 11)); // NOI18N
        jButton1.setText("Show Weight Target and Progress");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(buttonClose, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButton1)
                    .addComponent(jButton1))
                .addGap(25, 25, 25))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonClose, jButton1, jToggleButton1});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCloseActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_buttonCloseActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        // TODO add your handling code here:
        if(jToggleButton1.isSelected()){
            jToggleButton1.setText(LifeTrackerConstants.WEIGHT_TARGET_CHART_TOGGLE_BUTTON);
            displayWeightTargetChart();
        }
        else {
            jToggleButton1.setText(LifeTrackerConstants.WEIGHT_PROGRESS_CHART_TOGGLE_BUTTON);
            displayWeightProgressChart();            
        }
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        displayCombinedChart();
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(WeightProgressGraphViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(WeightProgressGraphViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(WeightProgressGraphViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WeightProgressGraphViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                WeightProgressGraphViewController dialog = new WeightProgressGraphViewController(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClose;
    private javax.swing.JPanel chartPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables
}

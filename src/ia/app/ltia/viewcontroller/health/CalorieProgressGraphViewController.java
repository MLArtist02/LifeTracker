package ia.app.ltia.viewcontroller.health;

import ia.app.ltia.LifeTrackerConstants;
import ia.app.ltia.model.CalorieRecord;
import ia.app.ltia.model.Member;
import ia.app.ltia.viewcontroller.FrameDragListener;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.JDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;


/**
 *
 * @author
 */
public class CalorieProgressGraphViewController extends javax.swing.JDialog {

    private static final Logger logger = LogManager.getLogger(CalorieProgressGraphViewController.class);
    private Member member;

    /**
     * Creates new form CalorieProgressGraphViewController
     */
    public CalorieProgressGraphViewController(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        addListener();       
        initUndecoratedFrame();
        initComponentsCustomStyling();        
    }
    
    public void setMember(Member member) {
        this.member = member;
        logger.debug("Setting member in weight progress graph form to: " + this.member.toString());
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
    
    private void displayChart() {        
        String title = "Calorie Progress";
        
        jPanel1.removeAll();
        jPanel1.revalidate();        
        
        JFreeChart barChart = getChart(title);
        
        chartPanel = new ChartPanel(barChart);
        chartPanel.setSize(jPanel1.getSize());               
  
        jPanel1.add(chartPanel);
        jPanel1.getParent().validate();
        jPanel1.repaint();
        jPanel1.revalidate();        
    }    
    
    private JFreeChart getChart(String title) {        
        logger.debug("Chart title = " + title);
        
        JFreeChart barChart = ChartFactory.createBarChart(
            title,           
            "Date",            
            "Calories",            
            createDataset(),          
            PlotOrientation.VERTICAL,
            true, 
            true, 
            false);        
        
        return barChart;
    }    
    
    private CategoryDataset createDataset() {        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset( );  
        
        logger.debug("Getting calorie records for member: " + this.member.toString());
        
        //now get the calorie records and add to the data set        
        ArrayList<CalorieRecord> calorieRecords = this.member.getCalorieRecords();
        
        //make sure the calorie records are sorted
        Collections.sort(calorieRecords);        
        Iterator i = calorieRecords.iterator();
        
        while(i.hasNext()) {
            CalorieRecord calorieRecord = (CalorieRecord)i.next();           
            dataset.addValue(calorieRecord.getCalories(), "Calories", new SimpleDateFormat("dd/MM/yy").format(calorieRecord.getDate()));
        }
       
        return dataset;        
    }
    
    
    public void addListener() {
            addWindowListener(
                new java.awt.event.WindowAdapter() {
                public void windowOpened(WindowEvent e) {
                    logger.debug("Invoking windowOpened from JDialog");
                    displayChart();
                }
                public void windowClosing(WindowEvent e) {
                    logger.debug("Invoking WindowClosing from JDialog");
                    dispose();
                }
        });
    }    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonBack = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        chartPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setSize(new java.awt.Dimension(1235, 450));

        buttonBack.setFont(new java.awt.Font("Keep Calm Med", 0, 11)); // NOI18N
        buttonBack.setText("Close");
        buttonBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBackActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout chartPanelLayout = new javax.swing.GroupLayout(chartPanel);
        chartPanel.setLayout(chartPanelLayout);
        chartPanelLayout.setHorizontalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        chartPanelLayout.setVerticalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(1137, Short.MAX_VALUE)
                .addComponent(chartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(278, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(1087, Short.MAX_VALUE)
                .addComponent(buttonBack, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(28, 28, 28)
                .addComponent(buttonBack, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBackActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_buttonBackActionPerformed

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
            java.util.logging.Logger.getLogger(CalorieProgressGraphViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CalorieProgressGraphViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CalorieProgressGraphViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CalorieProgressGraphViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CalorieProgressGraphViewController dialog = new CalorieProgressGraphViewController(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton buttonBack;
    private javax.swing.JPanel chartPanel;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
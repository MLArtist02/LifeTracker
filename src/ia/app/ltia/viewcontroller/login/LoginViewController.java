package ia.app.ltia.viewcontroller.login;

import ia.app.ltia.viewcontroller.health.HealthViewController;
import ia.app.ltia.LifeTrackerConstants;
import ia.app.ltia.model.Member;
import ia.app.ltia.model.Database;
import ia.app.ltia.viewcontroller.FrameDragListener;
import ia.app.ltia.viewcontroller.HomeViewController;
import java.awt.Color;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author
 */
public class LoginViewController extends javax.swing.JFrame {
    
    private Member member;
    
    
    /**
     * Creates new form LoginForm
     */
    public LoginViewController() {
        initComponents();
        CANCELLOGIN1.setBorder(null);
        this.setTitle(LifeTrackerConstants.TITLE_LOGIN);
        SwingUtilities.getRootPane(passwordField).setDefaultButton(SUBMITLOGIN1);
        initUndecoratedFrame();
        initComponentsCustomStyling();        
    }
    
    private void initComponentsCustomStyling() {
        
        //jFrame background
        this.getContentPane().setBackground(LifeTrackerConstants.GUI_BACKGROUND_COLOR_2);       
        
        //Top panel background
        this.jMainPanel.setBackground(LifeTrackerConstants.GUI_COLOR_3);
        
        //Text field backgrounds        
        this.passwordField.setBackground(LifeTrackerConstants.GUI_FIELD_BACKGROUND_COLOR_2);
        this.passwordField.setBorder(BorderFactory.createEmptyBorder());                
        
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
    
    public void setMember(Member member){
        this.member = member;
        jLabelWelcome.setText("Welcome " + this.member.getUsername());
        jLabelInstruction.setText("Please type your password");
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        CANCELLOGIN1 = new javax.swing.JButton();
        SUBMITLOGIN1 = new javax.swing.JButton();
        jMainPanel = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jLabelInstruction = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        passwordField = new javax.swing.JPasswordField();
        jLabelWelcome = new javax.swing.JLabel();

        jLabel3.setFont(new java.awt.Font("Keep Calm", 0, 30)); // NOI18N
        jLabel3.setText("Name");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        CANCELLOGIN1.setBackground(new java.awt.Color(204, 204, 204));
        CANCELLOGIN1.setFont(new java.awt.Font("Keep Calm Med", 0, 18)); // NOI18N
        CANCELLOGIN1.setText("Cancel");
        CANCELLOGIN1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        CANCELLOGIN1.setBorderPainted(false);
        CANCELLOGIN1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CANCELLOGIN1ActionPerformed(evt);
            }
        });

        SUBMITLOGIN1.setBackground(new java.awt.Color(204, 204, 204));
        SUBMITLOGIN1.setFont(new java.awt.Font("Keep Calm Med", 0, 18)); // NOI18N
        SUBMITLOGIN1.setText("Login");
        SUBMITLOGIN1.setBorder(null);
        SUBMITLOGIN1.setBorderPainted(false);
        SUBMITLOGIN1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SUBMITLOGIN1ActionPerformed(evt);
            }
        });

        jLabelTitle.setFont(new java.awt.Font("Keep Calm Med", 0, 36)); // NOI18N
        jLabelTitle.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelTitle.setText("Login");
        jLabelTitle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jLabelInstruction.setFont(new java.awt.Font("Keep Calm Med", 0, 18)); // NOI18N
        jLabelInstruction.setForeground(new java.awt.Color(255, 255, 255));
        jLabelInstruction.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        passwordField.setBackground(new java.awt.Color(235, 227, 190));
        passwordField.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        passwordField.setBorder(null);
        passwordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordFieldActionPerformed(evt);
            }
        });
        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                passwordFieldKeyPressed(evt);
            }
        });

        jLabelWelcome.setFont(new java.awt.Font("Keep Calm Med", 0, 18)); // NOI18N
        jLabelWelcome.setForeground(new java.awt.Color(255, 255, 255));
        jLabelWelcome.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout jMainPanelLayout = new javax.swing.GroupLayout(jMainPanel);
        jMainPanel.setLayout(jMainPanelLayout);
        jMainPanelLayout.setHorizontalGroup(
            jMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jMainPanelLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(jMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelInstruction, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jMainPanelLayout.createSequentialGroup()
                        .addGroup(jMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelWelcome, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                            .addComponent(jSeparator1))
                        .addGap(0, 81, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jMainPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jSeparator1, passwordField});

        jMainPanelLayout.setVerticalGroup(
            jMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jMainPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabelTitle)
                .addGap(37, 37, 37)
                .addComponent(jLabelWelcome, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelInstruction, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jMainPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(SUBMITLOGIN1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addComponent(CANCELLOGIN1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {CANCELLOGIN1, SUBMITLOGIN1});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jMainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CANCELLOGIN1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SUBMITLOGIN1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {CANCELLOGIN1, SUBMITLOGIN1});

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void passwordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passwordFieldActionPerformed

    private void CANCELLOGIN1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CANCELLOGIN1ActionPerformed
        // TODO add your handling code here:
        HomeViewController homeform = new HomeViewController();
        homeform.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_CANCELLOGIN1ActionPerformed

    private void SUBMITLOGIN1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SUBMITLOGIN1ActionPerformed
        // TODO add your handling code here:
        Database db = new Database(LifeTrackerConstants.DATABASE_NAME);
        if (db.authenticateLogin(this.member.getUsername(), String.valueOf(passwordField.getPassword()))){
            HealthViewController healthform = new HealthViewController();
            healthform.setMember(this.member);
            healthform.setVisible(true);
            this.dispose();
        }
        else{
            jLabelInstruction.setText("Incorrect Password, Please Try Again");
            jLabelInstruction.setForeground(Color.RED);
        }
    }//GEN-LAST:event_SUBMITLOGIN1ActionPerformed

    private void passwordFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passwordFieldKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            SUBMITLOGIN1.doClick();
        }
    }//GEN-LAST:event_passwordFieldKeyPressed

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
            java.util.logging.Logger.getLogger(LoginViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginViewController().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CANCELLOGIN1;
    private javax.swing.JButton SUBMITLOGIN1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelInstruction;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelWelcome;
    private javax.swing.JPanel jMainPanel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPasswordField passwordField;
    // End of variables declaration//GEN-END:variables


}

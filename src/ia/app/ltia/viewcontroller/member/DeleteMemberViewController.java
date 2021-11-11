package ia.app.ltia.viewcontroller.member;

import ia.app.ltia.LifeTrackerConstants;
import ia.app.ltia.model.Database;
import ia.app.ltia.model.Member;
import ia.app.ltia.viewcontroller.FrameDragListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeleteMemberViewController extends javax.swing.JDialog {
    
    private static final Logger logger = LogManager.getLogger(DeleteMemberViewController.class);
    private String[] members;
    private String defaultStatus = "Enter password and then press Delete Member to delete a member";
    private boolean memberWasDeleted = false;

    public DeleteMemberViewController(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();    
        this.setTitle(LifeTrackerConstants.TITLE_DELETE_MEMBER);
        initUndecoratedFrame();
        initComponentsCustomStyling();         
        this.jTextPaneDeleteStatus.setText(defaultStatus);
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
        
        this.jPasswordField1.setBackground(LifeTrackerConstants.GUI_FIELD_BACKGROUND_COLOR_2);
        this.jPasswordField1.setBorder(BorderFactory.createEmptyBorder());                        
        
        this.jButtonDeleteMember.setBackground(LifeTrackerConstants.GUI_DELETE_BUTTON_COLOR);                
               
    }          
    
    public void setAllMembers(Object[] members){
        for(int i=0; i < members.length; i++){
            Member m = (Member)members[i];
            String s = m.getUsername();
            jComboBoxMembers.addItem(s);
        }
    }    
   
    public boolean getMemberWasDeleted(){
        return this.memberWasDeleted;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelDeleteMemberTitle = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxMembers = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jButtonDeleteMember = new javax.swing.JButton();
        jButtonCloseCancel = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPaneDeleteStatus = new javax.swing.JTextPane();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        jLabelDeleteMemberTitle.setBackground(new java.awt.Color(255, 102, 102));
        jLabelDeleteMemberTitle.setFont(new java.awt.Font("Keep Calm Med", 0, 14)); // NOI18N
        jLabelDeleteMemberTitle.setForeground(new java.awt.Color(255, 0, 51));
        jLabelDeleteMemberTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDeleteMemberTitle.setText("Delete Member - please be careful!");

        jLabel1.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        jLabel1.setText("Enter member's password:");

        jComboBoxMembers.setFont(new java.awt.Font("Keep Calm Med", 0, 14)); // NOI18N
        jComboBoxMembers.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxMembersFocusGained(evt);
            }
        });
        jComboBoxMembers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxMembersActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Please select the user you would like to delete:");

        jPasswordField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPasswordField1FocusGained(evt);
            }
        });
        jPasswordField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPasswordField1KeyPressed(evt);
            }
        });

        jButtonDeleteMember.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        jButtonDeleteMember.setText("Delete Member");
        jButtonDeleteMember.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteMemberActionPerformed(evt);
            }
        });

        jButtonCloseCancel.setFont(new java.awt.Font("Keep Calm Med", 0, 12)); // NOI18N
        jButtonCloseCancel.setText("Cancel");
        jButtonCloseCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseCancelActionPerformed(evt);
            }
        });

        jTextPaneDeleteStatus.setBackground(new java.awt.Color(204, 204, 204));
        jTextPaneDeleteStatus.setBorder(null);
        jTextPaneDeleteStatus.setFont(new java.awt.Font("Keep Calm Med", 2, 11)); // NOI18N
        jScrollPane1.setViewportView(jTextPaneDeleteStatus);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jScrollPane1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonCloseCancel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDeleteMember, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelDeleteMemberTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 602, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxMembers, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(25, 25, 25))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jPasswordField1, jSeparator1});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelDeleteMemberTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxMembers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonDeleteMember, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonCloseCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonDeleteMemberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteMemberActionPerformed
        //Check the password
        Database db = new Database(LifeTrackerConstants.DATABASE_NAME);
        int memberToDeleteIndex = jComboBoxMembers.getSelectedIndex();
        String memberToDelete = jComboBoxMembers.getItemAt(memberToDeleteIndex);
        logger.debug("Going to delete: " + memberToDelete);
        if(db.authenticateLogin(memberToDelete, String.valueOf(jPasswordField1.getPassword()))){
            //If password is good, delete the member    
            logger.debug("Going to delete: " + memberToDelete + " as password authentication passed");
            String s = "Password authenticated.\nDeleting member ...";
            jTextPaneDeleteStatus.setText(s);
            if(db.deleteMember(memberToDelete)){                
                String s2 = s + "\nMember was deleted successfully.";
                jTextPaneDeleteStatus.setText(s2);                
                //reload the dropdown
                jComboBoxMembers.removeItemAt(memberToDeleteIndex);
                //re-label cancel button to close now            
                jButtonCloseCancel.setText("Close"); 
                //set boolean so parent knows to reload members
                this.memberWasDeleted = true;
            }
            else{
                logger.debug("Unable to delete: " + memberToDelete + " due to database error");
                String s2 = s + "\nThere was an error when deleting member." + "\n" + LifeTrackerConstants.DATABASE_ERROR_MESSAGE;
                jTextPaneDeleteStatus.setText(s2);
            }            
        }
        else{
            logger.debug("Not going to delete: " + memberToDelete + " due to password failure");
            String s = "Incorrect password provided.\nMember cannot be deleted.";
            jTextPaneDeleteStatus.setText(s);
        }       
    }//GEN-LAST:event_jButtonDeleteMemberActionPerformed

    private void jButtonCloseCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonCloseCancelActionPerformed

    private void jPasswordField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPasswordField1FocusGained
        //When ever user tries another password, empty the status
        jTextPaneDeleteStatus.setText(this.defaultStatus);
    }//GEN-LAST:event_jPasswordField1FocusGained

    private void jComboBoxMembersFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jComboBoxMembersFocusGained
        jTextPaneDeleteStatus.setText(this.defaultStatus);
    }//GEN-LAST:event_jComboBoxMembersFocusGained

    private void jPasswordField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordField1KeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            jButtonDeleteMember.doClick();
        }
    }//GEN-LAST:event_jPasswordField1KeyPressed

    private void jComboBoxMembersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxMembersActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxMembersActionPerformed

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
            java.util.logging.Logger.getLogger(DeleteMemberViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DeleteMemberViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DeleteMemberViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DeleteMemberViewController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DeleteMemberViewController dialog = new DeleteMemberViewController(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jButtonCloseCancel;
    private javax.swing.JButton jButtonDeleteMember;
    private javax.swing.JComboBox<String> jComboBoxMembers;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelDeleteMemberTitle;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextPane jTextPaneDeleteStatus;
    // End of variables declaration//GEN-END:variables
}

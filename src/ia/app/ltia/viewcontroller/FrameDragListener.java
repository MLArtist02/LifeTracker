/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ia.app.ltia.viewcontroller;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
//import javax.swing.JFrame;
import java.awt.Window;

/**
 *
 * @author
 */

//https://stackoverflow.com/questions/16046824/making-a-java-swing-frame-movable-and-setundecorated

public class FrameDragListener extends MouseAdapter {

    //Use Window so that any subclass can be passed (polymorphism)
    //e.g. JFrame and JDialog
    private final Window frame;
    
    private Point mouseDownCompCoords = null;

    public FrameDragListener(Window frame) {
        this.frame = frame;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDownCompCoords = null;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDownCompCoords = e.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point currCoords = e.getLocationOnScreen();
        frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
    }
    
}


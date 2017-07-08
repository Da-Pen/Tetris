/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Daniel Peng
 */
public class DrawingSurface extends JPanel implements ActionListener{
    
    Map map;
    Timer timer;
    public DrawingSurface(){
        
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.WHITE);

        map = new Map();
        timer = new Timer(10, this);
        timer.start();
    }
    
    //does the actual drawing
     private void doDrawing(Graphics g) {
        //the Graphics2D class is the class that handles all the drawing
        //must be casted from older Graphics class in order to have access to some newer methods
        Graphics2D g2d = (Graphics2D) g;
        map.draw(g);
        
    }
     private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            map.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            map.keyPressed(e);
        }
    }
    
    //overrides paintComponent in JPanel class
    //performs custom painting
    @Override
    public void paintComponent(Graphics g) {        
        super.paintComponent(g);//does the necessary work to prepare the panel for drawing        
        doDrawing(g);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        map.update();
        repaint();
        System.out.println(e);
    }
}

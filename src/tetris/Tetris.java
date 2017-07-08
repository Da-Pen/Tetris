/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author Daniel Peng
 */
public class Tetris extends JFrame{
    //constructor
    public Tetris() {
        //create the User interface
        initUI();
        
    }
    
    //create the custom JFrame
    private void initUI() {
        //set title of the JFrame
        setTitle("Tetris");
        //add a custom JPanel to draw on
        add(new DrawingSurface());
        //set the size of the window
        //30x10+16, 30x20+38
        setSize(317, 640); //inner frame is 600 x 600
        //tell the JFrame what to do when closed
        //this is important if our application has multiple windows
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        //make sure that all UI updates are concurrency safe (related to multi threading)
        //much more detailed http://www.javamex.com/tutorials/threads/invokelater.shtml
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                //instantiate the main window
                Tetris windowFrame = new Tetris();
                //make sure it can be seen
                windowFrame.setVisible(true);
            }
        });
    }
}

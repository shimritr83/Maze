package org.example;

import javax.swing.*;

public class Window extends JFrame {

    public Window(){
        this.setSize(Constants.WIDTH,Constants.HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        this.add(new MazePanel());
        setVisible(true);


    }

}

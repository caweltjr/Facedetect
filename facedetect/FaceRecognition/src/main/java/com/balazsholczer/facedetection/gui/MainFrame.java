package com.balazsholczer.facedetection.gui;

import com.balazsholczer.facedetection.constants.Constants;
import com.balazsholczer.facedetetion.algorithm.FaceDetection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private ImagePanel imagePanel;
    private JFileChooser fileChooser;
    private FaceDetection faceDetection;
    private File file;

    public MainFrame() {
        super(Constants.APPLICATION_NAME);

        setJMenuBar(createMenuBar());

        this.imagePanel = new ImagePanel();
        this.fileChooser = new JFileChooser();
        this.faceDetection = new FaceDetection();

        add(imagePanel, BorderLayout.CENTER);

        setSize(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(this);
    }

    private JMenuBar createMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem loadMenuItem = new JMenuItem("Load image");
        JMenuItem detectMenuItem = new JMenuItem("Detect faces");
        JMenuItem identifyMenuItem = new JMenuItem("Identify face");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        fileMenu.add(loadMenuItem);
        fileMenu.add(detectMenuItem);
        fileMenu.add(exitMenuItem);

        loadMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    MainFrame.this.file = fileChooser.getSelectedFile();
                    MainFrame.this.imagePanel.loadImage(MainFrame.this.file);
                }
            }
        });

        detectMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                MainFrame.this.faceDetection.detectFaces(MainFrame.this.file, MainFrame.this.imagePanel);
            }
        });
        identifyMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                MainFrame.this.faceDetection.detectFaces(MainFrame.this.file, MainFrame.this.imagePanel);
            }
        });
        JMenu aboutMenu = new JMenu("About");
        JMenu helpMenu = new JMenu("Help");

        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);
        menuBar.add(helpMenu);

        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {

                int action = JOptionPane.showConfirmDialog(MainFrame.this, Constants.EXIT_WARNING, "Warning", JOptionPane.YES_NO_OPTION);

                if (action == JOptionPane.OK_OPTION) {
                    System.gc();
                    System.exit(0);
                }
            }
        });

        return menuBar;
    }
}

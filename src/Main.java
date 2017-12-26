
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.FilterQuery;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.*;

import com.fazecast.jSerialComm.SerialPort;



/*
 Program that fetches tweets from GBF raids
 
 */

public class Main {

	static SerialPort serialPort;
	public static void main(String[] args) throws TwitterException, FileNotFoundException {
		
		
		
		
		
		final int WINDOW_WIDTH = 400, WINDOW_HEIGHT = 350;
		final JFrame window = new JFrame("GBF Raids");
		JPanel WelcomeP = new JPanel();
		window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		window.setResizable(false);
		final Container cp = window.getContentPane();
		
		WelcomeP.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		JLabel WelcomeLabel;
		JLabel SelectionLabel;
		JButton NextButton;
		JComboBox<String> portList;
		
		WelcomeLabel = new JLabel("GBF Raid Notifier");
		
		gbc.weighty = 0.5;
		gbc.gridy = 0;
		gbc.gridx = 2;
		gbc.gridwidth = 4;
		gbc.anchor = GridBagConstraints.PAGE_START;
		WelcomeP.add(WelcomeLabel,gbc);
		
		SelectionLabel = new JLabel("Select the port of that the Arduino is connected on");
		gbc.weighty = 0.5;
		gbc.gridx = 2;
		gbc.gridy = 1;			
		WelcomeP.add(SelectionLabel,gbc);
		
		portList = new JComboBox<String>();
		portList.setPrototypeDisplayValue("XXXXXXXXXXXXXX");
		portList.setMaximumRowCount(4);
		portList.addItem("NO PORT");
			//Load ports into drop down list
		SerialPort[] ports = SerialPort.getCommPorts();
		
		for(SerialPort port : ports)
			portList.addItem(port.getSystemPortName());
		
		gbc.weighty = 1.2;
		gbc.gridx = 2;
		gbc.gridy = 2;

		WelcomeP.add(portList,gbc);
		
		NextButton = new JButton("Next");
		NextButton.addActionListener(new ActionListener(){
			@Override
			 public void actionPerformed(ActionEvent e)
			{
				serialPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
				boolean noPort = false;
				if(portList.getSelectedItem().toString().equals("NO PORT"))
					noPort = true;
				if(noPort || serialPort.openPort())
				{
					if(noPort)
						serialPort = null;
					System.out.println("Port opened successfully.");
					cp.removeAll();
					 window.setSize(1200,800);
					 window.setLocationRelativeTo(null);
					 window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					cp.add(new MainMenu());
					cp.validate();					
				}
				else 
				{
					JOptionPane.showMessageDialog(null, "Port couldn't open");
					System.exit(5);
				}
			}
		});
		gbc.gridx = 2;
		gbc.gridy = 4;
		WelcomeP.add(NextButton,gbc);	
		
		
		cp.add(WelcomeP);

		
		//Put window in middle of screen
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}	

}



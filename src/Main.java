
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import com.fazecast.jSerialComm.SerialPort;

/*
 Program that fetches tweets for GBF raids
 
 */

public class Main {

	static SerialPort serialPort;	//serial port for Arduino 
	
	public static void main(String[] args){
		//Window
		final int WINDOW_WIDTH = 400, WINDOW_HEIGHT = 350;
		final JFrame window = new JFrame("GBF Raids");
		window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		window.setResizable(false);
		
		final Container container = window.getContentPane();
		
		// Welcome Panel
		JPanel welcomePanel = new JPanel();
		welcomePanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		JLabel welcomeLabel;
		JLabel selectionLabel;
		JButton nextButton;
		JComboBox<String> portList;
		
		welcomeLabel = new JLabel("GBF Raid Notifier");
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weighty = 0.5;
		gbc.gridwidth = 4;
		gbc.anchor = GridBagConstraints.PAGE_START;
		welcomePanel.add(welcomeLabel,gbc);
		
		selectionLabel = new JLabel("Select the port of that the Arduino is connected on");
		gbc.gridx = 2;
		gbc.gridy = 1;			
		welcomePanel.add(selectionLabel,gbc);
		
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
		welcomePanel.add(portList,gbc);
		
		nextButton = new JButton("Next");
		nextButton.addActionListener(new ActionListener()
		{
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
					container.removeAll();
					 window.setSize(1200,800);
					 window.setLocationRelativeTo(null);
					 window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					container.add(new MainMenu());
					container.validate();					
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
		welcomePanel.add(nextButton,gbc);	
		
		
		container.add(welcomePanel);
		
		//Put window in middle of screen
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}	

}



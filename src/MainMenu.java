import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import com.fazecast.jSerialComm.SerialPort;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MainMenu extends JPanel {
	
		GridBagConstraints gbc = new GridBagConstraints();
		JTable table;
	
		public MainMenu()
		{
			setLayout(new GridBagLayout());
			
			ConfigurationBuilder cf = new ConfigurationBuilder();
			cf.setDebugEnabled(true)
					.setOAuthConsumerKey("O3ZsRh6L7RlfBmRXtCWAmYND1")
					.setOAuthConsumerSecret("K5rJzYKQp3LZO3WcodzN2nN36n61fnbSUMJNwTycZKh0a1wzvi")		
					.setOAuthAccessToken("868806062-JBalQwdWZgaM2IyJb2GQhJDtp2iHI1L8zVQprMeX")
					.setOAuthAccessTokenSecret("6gmzZ8EHLSV0sCMgjjibusFpaYEGcgWHd5GDxdN2boSpt");
			
			SerialPort serialPort = Main.serialPort;
			JTable table = new JTable();
			table.setAutoCreateRowSorter(true);
			String[] columnNames = {"Level", "Boss", "Key"};
			DefaultTableModel dtm = new DefaultTableModel(0,0);
			
			dtm.setColumnIdentifiers(columnNames);
			table.setModel(dtm);
		
			JScrollPane scroll = new JScrollPane(table);
			scroll.setSize(700,270);
		
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weighty = 1;
			gbc.gridy = 0;
			gbc.gridx = 0;
			
			add(scroll,gbc);
			
			
			 
			 
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			serialPort.setComPortParameters(9600, 8, 1, SerialPort.NO_PARITY);
			serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);	
//			
//			Scanner d = new Scanner(System.in);
//			int chosenPort = d.nextInt();
//			
//			
//			
//			
//			
//			
//			serialPort.setComPortParameters(9600, 8, 1, SerialPort.NO_PARITY);
//			serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
//			
//			
//			
//		
//			 TwitterStream twitterStream = new TwitterStreamFactory(cf.build()).getInstance();
//			
//			 
//			 StatusListener listener = new StatusListener() {
//			    	
//				 	SerialPort t = serialPort;
//			    	public void onStatus(Status status) {
//			        	String test;
//			    		long bytestoWrite;
//			    		byte [] buffer;
//			    		
//			            System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText()+" "+ status.getCreatedAt());
//			            test = status.getText();
//			            buffer = test.getBytes();
//			            bytestoWrite = test.length();
//			           t.writeBytes(buffer, bytestoWrite);
//			              
//			        }
//
//			        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
//			            
//			        }
//
//			        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
//			           
//			        }
//
//			        public void onScrubGeo(long userId, long upToStatusId) {
//			            
//			        }
//
//			        public void onException(Exception ex) {
//			            
//			        }
//
//					@Override
//					public void onStallWarning(StallWarning arg0) {
//						// TODO Auto-generated method stub
//						
//					}
//			    };
//
//			    FilterQuery fq = new FilterQuery();
//			    String keywords[] = {":Battle ID"};
//			    
//
//			    fq.track(keywords);
//
//			    twitterStream.addListener(listener);
//			    twitterStream.filter(fq);      
		
		}
		
}

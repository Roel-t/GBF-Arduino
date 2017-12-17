
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.FilterQuery;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.*;

import com.fazecast.jSerialComm.SerialPort;


/*
 Program that fetches tweets from GBF raids
 
 */

public class Main {

	public static void main(String[] args) throws TwitterException, FileNotFoundException {
		
		ConfigurationBuilder cf = new ConfigurationBuilder();
		
		cf.setDebugEnabled(true)
				.setOAuthConsumerKey("O3ZsRh6L7RlfBmRXtCWAmYND1")
				.setOAuthConsumerSecret("K5rJzYKQp3LZO3WcodzN2nN36n61fnbSUMJNwTycZKh0a1wzvi")		
				.setOAuthAccessToken("868806062-JBalQwdWZgaM2IyJb2GQhJDtp2iHI1L8zVQprMeX")
				.setOAuthAccessTokenSecret("6gmzZ8EHLSV0sCMgjjibusFpaYEGcgWHd5GDxdN2boSpt");
		
		// Create and set Window Frame at middle of screen
		final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 600;
		JFrame window = new JFrame("GBF Raids");
		window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(null);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth()- window.getWidth())/2);
		int y = (int) ((dimension.getHeight()- window.getHeight())/2);
		window.setLocation(x,y);
		
		
		
		SerialPort[] ports = SerialPort.getCommPorts();
		System.out.println("Select a port:");
		int i = 1;
		for(SerialPort port : ports)
			System.out.println(i++ +  ": " + port.getSystemPortName());
		Scanner s = new Scanner(System.in);
		int chosenPort = s.nextInt();

		SerialPort serialPort = ports[chosenPort - 1];
		if(serialPort.openPort())
			System.out.println("Port opened successfully.");
		else {
			System.out.println("Unable to open the port.");
			return;
		}
		//serialPort.setComPortParameters(9600, 8, 1, SerialPort.NO_PARITY);
		serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
		
		
		
	
		 TwitterStream twitterStream = new TwitterStreamFactory(cf.build()).getInstance();
		
		 
		 StatusListener listener = new StatusListener() {
		    	
			 SerialPort t = ports[chosenPort-1];
		    	public void onStatus(Status status) {
		        	String test;
		    		long bytestoWrite;
		    		byte [] buffer;
		    		
		            System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText()+" "+ status.getCreatedAt());
		            test = status.getText();
		            buffer = test.getBytes();
		            bytestoWrite = test.length();
		            t.writeBytes(buffer, bytestoWrite);
		              
		        }

		        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		            
		        }

		        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		           
		        }

		        public void onScrubGeo(long userId, long upToStatusId) {
		            
		        }

		        public void onException(Exception ex) {
		            
		        }

				@Override
				public void onStallWarning(StallWarning arg0) {
					// TODO Auto-generated method stub
					
				}
		    };

		    FilterQuery fq = new FilterQuery();
		    String keywords[] = {":Battle ID"};
		    

		    fq.track(keywords);

		    twitterStream.addListener(listener);
		    twitterStream.filter(fq);      
		    window.setVisible(true);
		   
	}	

}

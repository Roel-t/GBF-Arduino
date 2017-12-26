import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
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
		List<String> Filter;
	
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
			String[] columnNames = {"Level", "Boss", "Key","Time"};
			DefaultTableModel dtm = new DefaultTableModel(0,0);
			
			dtm.setColumnIdentifiers(columnNames);
			table.setModel(dtm);
		
			JScrollPane scroll = new JScrollPane(table);
			
		
			gbc.gridx=0;
			gbc.gridy=0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.gridwidth = 2;
			JLabel title = new JLabel("RAIDS");
			title.setOpaque(true);
			title.setHorizontalAlignment(SwingConstants.CENTER);
			add(title, gbc);
			
		
			gbc.weightx = 0.5;
			gbc.gridwidth = 1;        
			gbc.gridx=1;
			gbc.gridy=1;  
			JLabel col2 = new JLabel("Filters");
			col2.setBackground(Color.lightGray);
			col2.setOpaque(true);
			col2.setHorizontalAlignment(SwingConstants.CENTER);
			add(col2, gbc);
			 
			
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx=0;
			gbc.gridy=2;
			gbc.weighty = 1.0;
			gbc.gridwidth = 1; 
			add(scroll,gbc);
			
			gbc.gridx=1;
			gbc.gridy=2;
			
			//ADD the filter section
			
			
			
			
			
			
			
			
			

			
			
			if(serialPort !=null)
			{
				serialPort.setComPortParameters(9600, 8, 1, SerialPort.NO_PARITY);
				serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
			}
			
			
			
		
			 TwitterStream twitterStream = new TwitterStreamFactory(cf.build()).getInstance();
			
			 
			 StatusListener listener = new StatusListener() {
			    	
				 	
			    	public void onStatus(Status status) {
			        	String test;
			    		long bytestoWrite;
			    		byte [] buffer;
			          
			            String level="";String key="";String boss="";
			            
			            String stat = status.getText();
			            Boolean lv = false;
			            int k=0;
			            for(int i=0;i<stat.length();i++)
			            {
			            	
			            	if(k<8)		    
			            		key += stat.charAt(i);			            	
			            	else if(stat.charAt(i)=='L')
			            	{
			            		i+=4;
			            		
			            		while(Character.isDigit(stat.charAt(i)))
			            		{
			            			level += stat.charAt(i);
			            			i++;	
			            		}
			            		lv = true;
			            	}
			         
			            	else if(lv)
			            	{
			            		if(stat.charAt(i) == '\n' )
			            		{
			            			if(stat.charAt(i+1) == 'h'&& stat.charAt(i+2) == 't' && stat.charAt(i+3) == 't')
			            				break;
			            		}
			            		boss += stat.charAt(i);
			            	}
			            	k++;
			            }
			            System.out.println("@" + status.getUser().getScreenName() + " - "+level+"-"+key+"-"+boss +"D: " + status.getCreatedAt());
			           
			            
			            DateFormat outputFormat = new SimpleDateFormat("hh:mm:ss a");
			            DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
			            inputFormat.setLenient(true);

			            Date date = null;
						try {
							date = inputFormat.parse(status.getCreatedAt().toString());
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			            String outputText = outputFormat.format(date);

			            System.out.println(outputText);
			            dtm.addRow(new Object[] {level,boss,key,outputText});
			          
			            
			            if(serialPort != null)
			            {
				            test = status.getText();
				            buffer = test.getBytes();
				            bytestoWrite = test.length();
				            serialPort.writeBytes(buffer, bytestoWrite);
			            }
			              
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
		
		}
		
}

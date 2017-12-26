import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.NumberFormat;
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
	
		GridBagConstraints gbc;
		SerialPort serialPort;
		
		//Table
		JTable table;
		DefaultTableModel dtm;
		JScrollPane scroll;
		
		//Panel
		JLabel title;
		JLabel col2;
		
		//Filter
		private int lowAmnt= 20;
		private int highAmnt=200;
		JPanel Filter;
		JButton Reset;
		JLabel LvlLabel;
		JLabel BossesLabel;
		JLabel LowLabel;
		JLabel HighLabel;
		JFormattedTextField LowLvlFormat;
		JFormattedTextField HighLvlFormat;
		NumberFormat amountFormat;
		  
		public MainMenu()
		{
			setLayout(new GridBagLayout());
			
			ConfigurationBuilder cf = new ConfigurationBuilder();
			cf.setDebugEnabled(true)
					.setOAuthConsumerKey("O3ZsRh6L7RlfBmRXtCWAmYND1")
					.setOAuthConsumerSecret("K5rJzYKQp3LZO3WcodzN2nN36n61fnbSUMJNwTycZKh0a1wzvi")		
					.setOAuthAccessToken("868806062-JBalQwdWZgaM2IyJb2GQhJDtp2iHI1L8zVQprMeX")
					.setOAuthAccessTokenSecret("6gmzZ8EHLSV0sCMgjjibusFpaYEGcgWHd5GDxdN2boSpt");
			
			gbc = new GridBagConstraints();
			serialPort = Main.serialPort;
			table = new JTable();
			table.setAutoCreateRowSorter(true);
			String[] columnNames = {"Level", "Boss", "Key","Time"};
			
			table.addMouseListener( new MouseAdapter()
			{
			    public void mouseReleased(MouseEvent e)
			    {
			        if (e.isPopupTrigger())
			        {
			            JTable source = (JTable)e.getSource();
			            int row = source.rowAtPoint( e.getPoint() );
			            int column = source.columnAtPoint( e.getPoint() );

			            if (! source.isRowSelected(row))
			                source.changeSelection(row, column, false, false);

			            int x =JOptionPane.showConfirmDialog(null, "Do you want to delete it?","Delete Confirmation",JOptionPane.YES_NO_OPTION);
			            if(x==0)
			            {
			            	((DefaultTableModel)table.getModel()).removeRow(row);
			            }
			        }
			    }
			});
			
			dtm = new DefaultTableModel()	//Sets table to not editable
			{

			    @Override
			    public boolean isCellEditable(int row, int column) {	
			       //all cells false
			       return false;
			    }
			};
			dtm.setColumnIdentifiers(columnNames);
			table.setModel(dtm);
			table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

			scroll = new JScrollPane(table);
			
		
			gbc.gridx=0;
			gbc.gridy=0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.gridwidth = 2;
			title = new JLabel("RAIDS");
			title.setOpaque(true);
			title.setHorizontalAlignment(SwingConstants.CENTER);
			add(title, gbc);
			
		
			gbc.weightx = 0.5;
			gbc.gridwidth = 1;        
			gbc.gridx=1;
			gbc.gridy=1;  
			col2 = new JLabel("Filters");
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
			
			
			
			
			//Filter section
			GridBagLayout lay = new GridBagLayout();
			Filter = new JPanel(lay);
//			  {
//
//		        @Override
//		        public void paint(Graphics g)
//		        {
//		            super.paint(g);
//		            int[][] dims = lay.getLayoutDimensions();
//		            g.setColor(Color.BLUE);
//		            int x = 0;
//		            for (int add : dims[0])
//		            {
//		                x += add;
//		                g.drawLine(x, 0, x, getHeight());
//		            }
//		            int y = 0;
//		            for (int add : dims[1])
//		            {
//		                y += add;
//		                g.drawLine(0, y, getWidth(), y);
//		            }
//		        }
//		    };
			
			
			gbc.gridx=1;
			gbc.gridy=2;
			add(Filter,gbc);
			
			gbc = new GridBagConstraints();
			
			
			
			LvlLabel = new JLabel("Level Range");
			gbc.gridx=1;
			gbc.gridy=0;
			
			
			Filter.add(LvlLabel,gbc);
			
			amountFormat = NumberFormat.getNumberInstance();
			LowLvlFormat = new JFormattedTextField(amountFormat); 
			LowLvlFormat.setValue(lowAmnt);
			HighLvlFormat = new JFormattedTextField(amountFormat);
			HighLvlFormat.setValue(highAmnt);
			
			gbc.gridx=0;
			gbc.gridy =1;
			
			Filter.add(LowLvlFormat,gbc);
			gbc.gridx =2;
			gbc.gridy =1;

			Filter.add(HighLvlFormat,gbc);
			
			LowLabel = new JLabel("Lowest Level");
			gbc.gridx=0;
			gbc.gridy =2;
			
			Filter.add(LowLabel,gbc);
			HighLabel = new JLabel("Highest Level");

			gbc.gridx =2;
			gbc.gridy =2;
			
			Filter.add(HighLabel,gbc);
			
			
			
			
			
			
			Reset = new JButton("Reset");
			Reset.addActionListener(new ActionListener(){
				@Override
				 public void actionPerformed(ActionEvent e)
				{
					lowAmnt= 20;
					highAmnt=200;
				}
			});
		 
			
			
			
			
			
			
			
			
			
			
			
			
			

			
			
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
			            	{
			            		if(stat.charAt(i) == ' ')	//handles if they put something in front of raid key
			            		{
			            			key = "";
			            			k=-1;
			            		}	
			            		else
			            			key += stat.charAt(i);	
			            	}
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

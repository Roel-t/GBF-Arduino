import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

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
		List<String> bossFormat = new ArrayList<String>();
		List<JCheckBox> checkboxes = new ArrayList<JCheckBox>();		  
		public MainMenu()
		{
			loadBosses();
			setLayout(new GridBagLayout());
			gbc = new GridBagConstraints();
			serialPort = Main.serialPort;
			
			//Connects to Twitter
			ConfigurationBuilder cf = new ConfigurationBuilder();
			cf.setDebugEnabled(true)
					.setOAuthConsumerKey("O3ZsRh6L7RlfBmRXtCWAmYND1")
					.setOAuthConsumerSecret("K5rJzYKQp3LZO3WcodzN2nN36n61fnbSUMJNwTycZKh0a1wzvi")		
					.setOAuthAccessToken("868806062-JBalQwdWZgaM2IyJb2GQhJDtp2iHI1L8zVQprMeX")
					.setOAuthAccessTokenSecret("6gmzZ8EHLSV0sCMgjjibusFpaYEGcgWHd5GDxdN2boSpt");
			
			
			table = new JTable();
			table.setAutoCreateRowSorter(true);
			String[] columnNames = {"Level", "Boss", "Key","Time"};
			
			//Right click to delete row listener
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
			//Sets table to not editable
			dtm = new DefaultTableModel()	
			{
				private static final long serialVersionUID = 1L;

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
			
			
			
			
			//Filter Section
			Filter = new JPanel(new GridBagLayout());
			
			gbc.gridx=1;
			gbc.gridy=2;
			add(Filter,gbc);
			
			
			gbc = new GridBagConstraints();
			LvlLabel = new JLabel("Level Range");
			Font f = LvlLabel.getFont();
			LvlLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
			
			gbc.gridx=0;
			gbc.gridy=0;
			gbc.weighty=0.01;
			gbc.weightx=0.33333;
			gbc.gridwidth = 6;
			gbc.insets = new Insets(20, 0, 0, 0);
			gbc.anchor = GridBagConstraints.PAGE_START;
			Filter.add(LvlLabel,gbc);
			
			
			
			LowLabel = new JLabel("Lowest Level");
			gbc.insets = new Insets(20, 0, 0, 0);

			LowLabel.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
			gbc.fill = GridBagConstraints.NONE;
			gbc.ipady = 0;
			gbc.weighty=0.001;
			gbc.weightx=0.33333;
			gbc.gridwidth = 1;
			gbc.gridx=0;
			gbc.gridy =1;
			Filter.add(LowLabel,gbc);
			
			
			HighLabel = new JLabel("Highest Level");
			HighLabel.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
			gbc.gridx =3;
			gbc.gridy =1;
			Filter.add(HighLabel,gbc);
			
			
		

			amountFormat = NumberFormat.getNumberInstance();
			LowLvlFormat = new JFormattedTextField(amountFormat); 
			LowLvlFormat.setValue(lowAmnt);
			LowLvlFormat.addPropertyChangeListener(new PropertyChangeListener()
					{
						@Override
						public void propertyChange(PropertyChangeEvent arg0) {
							lowAmnt = Integer.parseInt(LowLvlFormat.getText());
						}
					});
			HighLvlFormat = new JFormattedTextField(amountFormat);
			HighLvlFormat.setValue(highAmnt);
			HighLvlFormat.addPropertyChangeListener(new PropertyChangeListener()
			{
				@Override
				public void propertyChange(PropertyChangeEvent arg0) {
					highAmnt = Integer.parseInt(HighLvlFormat.getText());
				}
			});
	
			gbc.gridx=0;
			gbc.gridy=2;
			gbc.weighty=0.25f;
			gbc.weightx=0.33333;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(0, 50, 0, 50);
			
			gbc.fill = GridBagConstraints.HORIZONTAL;
			
			Filter.add(LowLvlFormat,gbc);
			gbc.gridx =3;
			gbc.gridy =2;

			Filter.add(HighLvlFormat,gbc);
			
			
			
			//Add checkbox list for filter
			BossesLabel = new JLabel("Bosses");
			BossesLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD)); 
			gbc.fill = GridBagConstraints.CENTER;

			gbc.gridx=0;
			gbc.gridy=3;
			gbc.weightx =.1;
			gbc.gridwidth = 4;
			gbc.insets = new Insets(0, 50, 0, 50);
			Filter.add(BossesLabel,gbc);
			
			
			gbc.fill = GridBagConstraints.HORIZONTAL;

			gbc.gridx =0;
			gbc.gridy=5;
			gbc.gridwidth = 10;
			gbc.gridheight = 1;
			gbc.insets = new Insets(0, 0, 0, 0);
			gbc.weighty = .1f;


			
			JPanel Bosses = new JPanel();
			Bosses.setLayout(new GridLayout(20,4));
			Filter.add(Bosses,gbc);

			
			    for (int i = 0; i <bossFormat.size(); i++) {
			        JCheckBox checkbox = new JCheckBox(bossFormat.get(i),true);
					checkbox.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));

			        checkboxes.add(checkbox); //for further use you add it to the list
			        Bosses.add(checkbox);
			    }
			JCheckBox SelectAll = new JCheckBox("Select All/Deselect All",true);
			SelectAll.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (JCheckBox cb : checkboxes) {
                        cb.setSelected(SelectAll.isSelected());
                     
                    }
                }
            });
			gbc.gridx =0;
			gbc.gridy=4;
			gbc.weighty=5;
			gbc.weighty = 0.1f;

			Filter.add(SelectAll,gbc);
			
			
			
			Reset = new JButton("Reset");
			Reset.addActionListener(new ActionListener(){
				@Override
				 public void actionPerformed(ActionEvent e)
				{
					TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
					table.setRowSorter(sorter);
					List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
					sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
					sorter.setSortKeys(sortKeys);
					
	            	((DefaultTableModel)table.getModel()).removeRow(0);
	            	
	            	sortKeys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
	            	sortKeys.remove(0);
					sorter.setSortKeys(sortKeys);
				}
//					lowAmnt= 20;
//					LowLvlFormat.setValue(lowAmnt);
//					highAmnt=200;
//					HighLvlFormat.setValue(highAmnt);
//					SelectAll.setSelected(true);
//					 for (JCheckBox cb : checkboxes) {
//	                        cb.setSelected(SelectAll.isSelected());
//	                     
//	                    }
//				}
			});
			gbc.gridx =1;
			gbc.gridy=6;
			gbc.gridwidth = 2;
			Filter.add(Reset,gbc);
		 			
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
			            for(int i=0;i<stat.length();i++)	//Parses the tweet
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

			           
			            if(bossFilter(boss) && levelFilter(level))		
			            	dtm.addRow(new Object[] {level,boss,key,outputText});
			            
			          
			            
			            if(serialPort != null)
			            {
				            test = status.getText();
				            buffer = test.getBytes();
				            bytestoWrite = test.length();
				            serialPort.writeBytes(buffer, bytestoWrite);
			            }
			              
			        }

			        private boolean levelFilter(String level) {
						int num = Integer.parseInt(level);
						if(num>=lowAmnt && num<=highAmnt)
							return true;
						else 
							return false;
					}

					private boolean bossFilter(String boss) {
						 for (JCheckBox cb : checkboxes) {
		                       if(cb.getText().equals(boss))
		                       {
		                    	   if(cb.isSelected())
		                    		   return true;
		                    	   else 
		                    		   return false;
		                       } 
		                    }
						 return false;
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

		private void loadBosses() {
			
			Scanner s1 = null;
			File f1= new File("Bosses.txt");
			try {
				s1 = new Scanner(f1);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while(s1.hasNext())
			{
				String temp = s1.nextLine();
				bossFormat.add(temp);
				System.out.println(temp);
			}
			s1.close();
		}
		
}

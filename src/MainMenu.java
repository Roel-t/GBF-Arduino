import java.awt.Color;
import java.awt.Font;
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
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
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
		int ID=0;
		
		//Panel
		JLabel title;
		JLabel col2;
		
		//Filter
		private int lowAmnt= 20;
		private int highAmnt=200;
		JPanel Filter;
		JButton reset;
		JLabel lvlLabel;
		JLabel bossesLabel;
		JLabel lowLabel;
		JLabel highLabel;
		JFormattedTextField lowLvlFormat;
		JFormattedTextField highLvlFormat;
		NumberFormat amountFormat;
		JPanel bossesPanel;
		List<String> bossFormat = new ArrayList<String>();
		List<JCheckBox> checkboxes = new ArrayList<JCheckBox>();
		TableRowSorter<TableModel> sorter;
		List <RowSorter.SortKey> sortKeys;
		
		//Twitter 
		TwitterStream twitterStream;
		StatusListener listener;
		
		//Timer
		final int rowsRemoved = 20;
		final int minimumRows = 30;
		final int delayBeforeStarting = 1;	//In minutes
		final int delayBetweenDeletions = 3;	//In minutes
		
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
			
			//ID Column used to uniquely identify each row (used for removal of individual rows)
			String[] columnNames = {"Level", "Boss", "Key","Time","ID"};
			
			table = new JTable();
			table.setAutoCreateRowSorter(true);
			
			//Sets table to not editable
			dtm = new DefaultTableModel()
			{
				private static final long serialVersionUID = 1L;

				@Override
			    public boolean isCellEditable(int row, int column) 
				{	
			       //all cells false
			       return false;
			    }
				//Makes columns into their respective format
				@Override
				public Class<?> getColumnClass(int arg0) 
				{
					 switch (arg0) 
					 {
	                    case 0:
	                        return Integer.class;
	                    case 1:
	                        return String.class;
	                    case 2:
	                        return String.class;
	                    case 3: 
	                    	return DateFormat.class;
	                    case 4:
	                    	return Integer.class;
	                    default:
	                        return String.class;
					 }	
				}
			
			};
			
			dtm.setColumnIdentifiers(columnNames);
		
			table.setModel(dtm);
			table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);	
			
			scroll = new JScrollPane(table);
			
			//Makes the Lvl Column anchor to the left
			DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
			leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
			table.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
			
			//Removes the ID column 
			table.removeColumn(table.getColumnModel().getColumn(4));
			
			sorter = new TableRowSorter<TableModel>(table.getModel());
			table.setRowSorter(sorter);
			sortKeys = new ArrayList<RowSorter.SortKey>();
			
			sortKeys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
			sorter.setSortKeys(sortKeys);
			
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
			            int rowView =table.convertRowIndexToModel(row);
			              
			            if (!source.isRowSelected(row))
			                source.changeSelection(row, column, false, false);
			            
			            int id = Integer.parseInt(dtm.getValueAt(rowView,4).toString());

			            int x =JOptionPane.showConfirmDialog(null, "Do you want to delete it?","Delete Confirmation",JOptionPane.YES_NO_OPTION);
			            
			            if(x==0)
			            {
			            	twitterStream.removeListener(listener);
			            	//Binary Search to delete row selected
			            	int low =0;
			            	int high = table.getModel().getRowCount();
			            	int mid;
			            	int value;  
			            	while(low<=high)
			            	{
			            		mid = (low+high)/2;
			            		value = Integer.parseInt(dtm.getValueAt(mid,4).toString());
			            		if(value == id)
			            		{
			            			dtm.removeRow(mid);
			            			break;
			            		}
			            		else if(value>id)		   
			            			high = mid-1;
			            		else
			            			low = mid+1;           		
			            	}
			            	twitterStream.addListener(listener);
			       
			            }
			        }
			    }
			});
			
			
			title = new JLabel("RAIDS");
			gbc.gridx=0;
			gbc.gridy=0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.gridwidth = 2;
			title.setOpaque(true);
			title.setHorizontalAlignment(SwingConstants.CENTER);
			add(title, gbc);
			
			
			col2 = new JLabel("Filters");
			gbc.gridx=1;
			gbc.gridy=1;
			gbc.weightx = 0.5;
			gbc.gridwidth = 1;        
			col2.setBackground(Color.lightGray);
			col2.setOpaque(true);
			col2.setHorizontalAlignment(SwingConstants.CENTER);
			add(col2, gbc);
			 
			//table
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
			
			
			lvlLabel = new JLabel("Level Range");
			Font f = lvlLabel.getFont();
			lvlLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
			gbc.gridx=0;
			gbc.gridy=0;
			gbc.weighty=0.01;
			gbc.weightx=0.33333;
			gbc.gridwidth = 6;
			gbc.insets = new Insets(20, 0, 0, 0);
			gbc.anchor = GridBagConstraints.PAGE_START;
			Filter.add(lvlLabel,gbc);
			
			
			lowLabel = new JLabel("Lowest Level");
			gbc.insets = new Insets(20, 0, 0, 0);
			lowLabel.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
			gbc.fill = GridBagConstraints.NONE;
			gbc.ipady = 0;
			gbc.weighty=0.001;
			gbc.weightx=0.33333;
			gbc.gridwidth = 1;
			gbc.gridx=0;
			gbc.gridy =1;
			Filter.add(lowLabel,gbc);
			
			
			highLabel = new JLabel("Highest Level");
			highLabel.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
			gbc.gridx =3;
			gbc.gridy =1;
			Filter.add(highLabel,gbc);
			

			amountFormat = NumberFormat.getNumberInstance();
			lowLvlFormat = new JFormattedTextField(amountFormat); 
			lowLvlFormat.setValue(lowAmnt);
			lowLvlFormat.addPropertyChangeListener(new PropertyChangeListener()
			{
				@Override
				public void propertyChange(PropertyChangeEvent arg0)
				{
					lowAmnt = Integer.parseInt(lowLvlFormat.getText());
				}
			});
			
			highLvlFormat = new JFormattedTextField(amountFormat);
			highLvlFormat.setValue(highAmnt);
			highLvlFormat.addPropertyChangeListener(new PropertyChangeListener()
			{
				@Override
				public void propertyChange(PropertyChangeEvent arg0) 
				{
					highAmnt = Integer.parseInt(highLvlFormat.getText());
				}
			});
	
			gbc.gridx=0;
			gbc.gridy=2;
			gbc.weighty=0.25f;
			gbc.weightx=0.33333;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(0, 50, 0, 50);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			Filter.add(lowLvlFormat,gbc);
			
			gbc.gridx =3;
			gbc.gridy =2;
			Filter.add(highLvlFormat,gbc);
			
			
			//Add check box list for filter
			bossesLabel = new JLabel("Bosses");
			bossesLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD)); 
			gbc.fill = GridBagConstraints.CENTER;
			gbc.gridx=0;
			gbc.gridy=3;
			gbc.weightx =.1;
			gbc.gridwidth = 4;
			gbc.insets = new Insets(0, 50, 0, 50);
			Filter.add(bossesLabel,gbc);
			
			
			bossesPanel = new JPanel();
			bossesPanel.setLayout(new GridLayout(20,4));
			gbc.gridx =0;
			gbc.gridy=5;
			gbc.gridwidth = 10;
			gbc.gridheight = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0, 0, 0, 0);
			gbc.weighty = .1f;
			Filter.add(bossesPanel,gbc);

			//Create check boxes for the bosses  
		    for (int i = 0; i <bossFormat.size(); i++) 
		    {
		        JCheckBox checkbox = new JCheckBox(bossFormat.get(i),true);
				checkbox.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));

		        checkboxes.add(checkbox);	//Used for to filter tweets faster
		        bossesPanel.add(checkbox);
		    }
			JCheckBox SelectAll = new JCheckBox("Select All/Deselect All",true);
			SelectAll.addActionListener(new ActionListener() 
			{
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
			
			//Resets the filter to default values
			reset = new JButton("Reset");
			reset.addActionListener(new ActionListener()
			{
				@Override
				 public void actionPerformed(ActionEvent e)
				{		
					lowAmnt= 20;
					lowLvlFormat.setValue(lowAmnt);
					highAmnt=200;
					highLvlFormat.setValue(highAmnt);
					SelectAll.setSelected(true);
					 for (JCheckBox cb : checkboxes) 
					 {
	                   cb.setSelected(SelectAll.isSelected());
	                 }
				}
			});
			gbc.gridx =1;
			gbc.gridy=6;
			gbc.gridwidth = 2;
			Filter.add(reset,gbc);
		 			
			if(serialPort !=null)	// if they didn't select a port of arduino
			{
				serialPort.setComPortParameters(9600, 8, 1, SerialPort.NO_PARITY);
				serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
			}
			
			
			Timer timer = new Timer();	
	        timer.schedule(new TimerTask() 
	        {
	            @Override
	            public void run() 
	            {
	            	deleteRow();	
	            }
				private void deleteRow() {
					twitterStream.removeListener(listener);
					
					if(table.getModel().getRowCount()>minimumRows)
					{
		            	SortOrder prevSorting = table.getRowSorter().getSortKeys().get(0).getSortOrder();
						int col = table.getRowSorter().getSortKeys().get(0).getColumn();
						
						sortKeys.clear();						
						sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
						sorter.setSortKeys(sortKeys);

						for(int i=0;i<rowsRemoved;i++)
							dtm.removeRow(0);
						

			        	sortKeys.clear();
			        	sortKeys.add(new RowSorter.SortKey(col, prevSorting));
						sorter.setSortKeys(sortKeys);
					}	
					twitterStream.addListener(listener);
				}
	        }, 1000*(60*delayBeforeStarting), 1000*(60*delayBetweenDeletions));// SET DELAY BETWEEN EACH DELETION
		    
		   
			 twitterStream = new TwitterStreamFactory(cf.build()).getInstance();
			
	
			 listener = new StatusListener() 
			 {
			    	public void onStatus(Status status) 
			    	{
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
			            		if(stat.charAt(i) == ' ' || (k==0 && stat.charAt(i+9)!=':'))	//handles if they put something in front of raid key
			            		{
			            			key = "";
			            			k=-1;
			            		}	
			            		else
			            			key += stat.charAt(i);	
			            	}
			            	else if(stat.charAt(i)=='L' && stat.charAt(i+1)=='v' && stat.charAt(i+2)=='l')
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
			            
			            System.out.println("@" + status.getUser().getScreenName() + " - "+level+"-"+key+"-"+boss +" D: " + status.getCreatedAt());
			           
			            int lvl = Integer.parseInt(level);
			            
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
			            String outputDate = outputFormat.format(date);

			            //Only adds if it passes thru filter
			            if(bossFilter(boss) && levelFilter(level))	
			            {
			            	dtm.addRow(new Object[] {lvl,boss,key,outputDate,ID});
			            	ID++;
			            }
			            
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
			}
			s1.close();
		}
		
		
}

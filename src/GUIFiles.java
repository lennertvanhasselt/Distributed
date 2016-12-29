import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

public class GUIFiles extends JPanel {
	SystemYGUI g;
	MainNode mainNode = GUILogin.getMainnode();
	String[] fileList;
	JComboBox fileListCombo;
	
	public GUIFiles(SystemYGUI menu) throws InterruptedException{
		g = menu;
		
		//Grootte instellen van het panel en een titel eraan geven.
		Dimension size = getPreferredSize();
		setPreferredSize(size);
		setBorder(BorderFactory.createTitledBorder("Filelist"));
		
		GUILogin.getThread().join();
		fillList();
		
		JButton logout = new JButton("Logout");
		JButton download = new JButton("Download");
		JButton delete = new JButton("Delete");
		JButton deleteLocal = new JButton("Delete Local");
		JButton refresh = new JButton("Refresh");
		fileListCombo = new JComboBox(fileList);
		
		logout.setPreferredSize(new Dimension(150,60));
		fileListCombo.setPreferredSize(new Dimension(400,50));
		download.setPreferredSize(new Dimension(150,60));
		delete.setPreferredSize(new Dimension(150,60));
		deleteLocal.setPreferredSize(new Dimension(150,60));
		refresh.setPreferredSize(new Dimension(150,60));
    
		//Swing componenten toewijzen op de GridBagLayout
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
    
		// 1ste Rij 
		gc.weightx = 1; // hoeveel plaats de knop inneemt
		gc.weighty = 1;
    
		gc.gridx = 0; 
    	gc.gridy = 1;
    	add(fileListCombo, gc);
    	
    	gc.gridx = 0; 
    	gc.gridy = 2;
    	add(download, gc);
    	
    	gc.gridx = 0; 
    	gc.gridy = 3;
    	add(delete, gc);
    	
    	gc.gridx = 0; 
    	gc.gridy = 4;
    	add(deleteLocal, gc);
		
    	gc.gridx = 0; 
    	gc.gridy = 5;
    	add(refresh, gc);
    	
		gc.gridx = 0; 
    	gc.gridy = 6;
    	add(logout, gc);
    
    
		// Actionlisteners
    	logout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
    			int selectedOption = JOptionPane.showConfirmDialog(null, 
                        "Are sure you want to logout? ", 
                        "Logout", 
                        JOptionPane.YES_NO_OPTION); 
    					if (selectedOption == JOptionPane.YES_OPTION) {
    						MainNode mainNode = GUILogin.getMainnode();
    						mainNode.choice(4);
    						System.exit(0);
    					}
    		}
    	});
    	
    	refresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
    			
    		}
    	});
    	
	}


	private void fillList() {
				if (!mainNode.node.totalFileList.isEmpty()) {
					int length = mainNode.node.totalFileList.size();
					fileList = new String[length];
					for(int i = 0;i<length;i++){
						fileList[i]=mainNode.node.totalFileList.get(i).getNameFile();
					}
				} else {
					fileList = new String[1];
					fileList[0]="No other Nodes";
				}
				fileListCombo = new JComboBox(fileList);
	}

}

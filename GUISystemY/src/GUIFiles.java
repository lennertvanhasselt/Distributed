import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

//Screen where a specific file can be selected to apply an action on it
public class GUIFiles extends JPanel {
	SystemYGUI g;
	
	public GUIFiles(SystemYGUI menu){
		g = menu;
		MainNode mainNode = GUILogin.getMainnode();
		
		Dimension size = getPreferredSize();
		setPreferredSize(size);
		setBorder(BorderFactory.createTitledBorder("Filelist"));
		
		int length = mainNode.node.totalFileList.size();
		String[] fileList = new String[length];
		for(int i = 0;i<length;i++){
			fileList[i]=mainNode.node.totalFileList.get(i);//.getNameFile();
		}
		
		JButton logout = new JButton("Logout");
		JButton download = new JButton("Download");
		JButton delete = new JButton("Delete");
		JButton deleteLocal = new JButton("Delete Local");
		JComboBox petList = new JComboBox(fileList);
		
		logout.setPreferredSize(new Dimension(150,60));
		petList.setPreferredSize(new Dimension(400,50));
		download.setPreferredSize(new Dimension(150,60));
		delete.setPreferredSize(new Dimension(150,60));
		deleteLocal.setPreferredSize(new Dimension(150,60));
 
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
    
		gc.weightx = 1; 
		gc.weighty = 1;
    
		gc.gridx = 0; 
    	gc.gridy = 1;
    	add(petList, gc);
    	
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
    	add(logout, gc);
    
    
		// Actionlistener
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
    	
	}

}

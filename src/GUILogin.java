import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;

/**
 * Aanmaken van het panel om in te loggen of om een gebruiker aan te maken.
 */
public class GUILogin extends JPanel {
	SystemYGUI g;
	static String name;
	static MainNode mainnode;
	static Thread thread;
	
	public GUILogin(SystemYGUI menu){
		g = menu;
		//Grootte instellen van het panel en een titel eraan geven.
		Dimension size = getPreferredSize();
		setPreferredSize(size);
		setBorder(BorderFactory.createTitledBorder("Login"));
		
		//Swing knoppen definiëren en de grootte er van instellen
		JTextField nodeName = new JTextField();
		nodeName.setColumns(10);
		JLabel nameRequest = new JLabel("Give the name for the node: ");
		JButton login = new JButton("login");
		JButton shutdown = new JButton("shutdown");
		shutdown.setPreferredSize(new Dimension(150, 30));
		login.setPreferredSize(new Dimension(150, 30));
    
		//Swing componenten toewijzen op de GridBagLayout
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
    
		gc.weightx = 1; // hoeveel plaats de knop inneemt
		gc.weighty = 1;		
		gc.gridx = 1; 
		gc.gridy = 1;
		add(nameRequest, gc);
		
		gc.gridx = 1; 
		gc.gridy = 2;
		add(nodeName, gc);
		
    	gc.gridx = 1;
    	gc.gridy = 3;
    	add(login, gc);
    	
    	gc.gridx = 1;
    	gc.gridy = 4;
    	add(shutdown, gc);
    	
		// Actionlisteners op knoppen
    	
    	//actionlistener die een waarschuwingsvenster geeft als je wil afsluiten
    	shutdown.addActionListener(new ActionListener(){
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			int selectedOption = JOptionPane.showConfirmDialog(null, 
                        "Are sure you want to close? ", 
                        "Shutdown", 
                        JOptionPane.YES_NO_OPTION); 
    					if (selectedOption == JOptionPane.YES_OPTION) {
    						System.exit(0);
    					}
    		}
    	});
    	
    	//
    	login.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				name = nodeName.getText();
				SystemYGUI.choice = 0;
				mainnode = new MainNode(name);
				thread = new Thread(mainnode);
				thread.start();
				try {
					g.ChoiceInMenu();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
    	});
	}
	
	public static MainNode getMainnode()
	{
		return mainnode;
	}
	
	public static Thread getThread()
	{
		return thread;
	}
}

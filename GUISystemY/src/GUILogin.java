import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;

// Login screen where user chooses his name
public class GUILogin extends JPanel {
	SystemYGUI g;
	static String name;
	static MainNode mainnode;
	
	public GUILogin(SystemYGUI menu){
		g = menu;
		//size and title of the panel
		Dimension size = getPreferredSize();
		setPreferredSize(size);
		setBorder(BorderFactory.createTitledBorder("Login"));
		
		//Defining the Swing buttons and it's size
		JTextField nodeName = new JTextField();
		nodeName.setColumns(10);
		JLabel nameRequest = new JLabel("Give the name for the node: ");
		JButton login = new JButton("login");
		JButton shutdown = new JButton("shutdown");
		shutdown.setPreferredSize(new Dimension(150, 30));
		login.setPreferredSize(new Dimension(150, 30));
    
		//Add components to GridBagLayout
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
    
		gc.weightx = 1;
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
    	
		// Actionlisteners on Buttons
    	
    	//Actionlistener for shutdown
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
    	
    	//Actionlistener for login
    	login.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				name = nodeName.getText();
				SystemYGUI.choice = 0;
				mainnode = new MainNode(name);
				new Thread(mainnode).start();
				g.ChoiceInMenu();
			}
    	});
	}
	
	// getfunction for the created mainNode
	public static MainNode getMainnode()
	{
		return mainnode;
	}
}

import java.awt.*;
import java.io.IOException;

import javax.swing.*;

// Main GUI control Class
public class SystemYGUI extends JFrame
{
	JPanel currentPanel;
	public static int choice = -1;
	GUILogin login;
	
	// Create a frame for the GUI
    public static void main(String[] args)
    {
    	SystemYGUI menu = new SystemYGUI();
    	menu.setSize(600,600);
        menu.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        menu.setVisible(true);
        menu.setTitle("System Y");
    }

    // Display panels
    public SystemYGUI()
    {
    	JPanel panel = new GUILogin(this);
    	setLayout(new BorderLayout());
        this.currentPanel = panel;
        Container c = getContentPane();
        c.add(panel, BorderLayout.CENTER);
        ChoiceInMenu();
    }
    
    // Depending on a choice the panel will be updated
    public void ChoiceInMenu()
    {
    	switch (choice)
		 {
    	case -1:   	this.remove(currentPanel);
					setCurrentPanel(login = new GUILogin(this));
					this.add(currentPanel);
					this.validate();
					this.repaint();
					this.requestFocus();
    	 break;
	     case 0: 	this.remove(currentPanel);
	     			setCurrentPanel(new GUIFiles(this));
	     			this.add(currentPanel);
	     			this.validate();
	                this.repaint();
	                this.requestFocus();
	    break;
	    default: 	System.out.println("Error");
	    break;
		}
    }
    
    // Used to update panel
    public void setCurrentPanel(JPanel currentPanel)
    {
        this.currentPanel = currentPanel;
    }
}

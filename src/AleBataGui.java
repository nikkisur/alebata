import java.awt.*;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JFrame;
 
public class AleBataGui {
 
    public static void addComponentsToPane(Container pane) {
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
	 
		JTextArea jta0 = new JTextArea("ITO ANG NANGYAYARI:\n");
		JScrollPane jsp0 = new JScrollPane(jta0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 0.25;
		c.weighty = 0.25;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 10;
		pane.add(jsp0, c);
	 
		JTextArea jta1 = new JTextArea("ITO ANG NABASA KO:\n");
		JScrollPane jsp1 = new JScrollPane(jta1);
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		c.gridx = 2;
		c.gridwidth = 1;
		pane.add(jsp1, c);
	 
		JTextField jtf = new JTextField("Ilagay rito ang pangalan ng file mo.");
		c.anchor = GridBagConstraints.LAST_LINE_START;
		c.gridx = 0;
		c.gridy = 10;
		c.gridwidth = 2;
		c.gridheight = 1;
		pane.add(jtf, c);
		
		JButton button = new JButton("ANDAR!");
		c.anchor = GridBagConstraints.LAST_LINE_END;
		c.gridx = 2;
		c.gridy = 10;
		c.gridwidth = 1;
		pane.add(button, c);
    }
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Ale, Bata!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
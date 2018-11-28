package engine.forms;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import engine.EvaluationManager;
import engine.ExternalStateEvaluator;

/*
 *
 * @author LUCA
 * 
 * Currently unused.
 * Todo if proper usage is wished:
 * 1. StateEvaluator Assignment interface
 * 2. StateEvaluator Deletion interface
 */
@SuppressWarnings("serial")
public class ExternalEvaluatorCreator extends javax.swing.JPanel {

	private JFrame frame;
	
    /**
     * Creates new form EvaluatorCreator
     */
    public ExternalEvaluatorCreator(JFrame parent) {
    	this.frame = parent;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */              
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel2 = new javax.swing.JPanel();
        applyButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        ipLabel = new javax.swing.JLabel();
        portLabel = new javax.swing.JLabel();
        ipScrollPane = new javax.swing.JScrollPane();
        ipEntry = new javax.swing.JTextPane();
        nameScrollPane = new javax.swing.JScrollPane();
        nameEntry = new javax.swing.JTextPane();
        instructionLabel = new javax.swing.JLabel();
        portScrollPane = new javax.swing.JScrollPane();
        portEntry = new javax.swing.JTextPane();
        nameLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setName("Create External Evaluator"); // NOI18N

        applyButton.setText("Apply");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });
        jPanel2.add(applyButton);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        jPanel2.add(closeButton);

        add(jPanel2, java.awt.BorderLayout.SOUTH);

        jPanel1.setMinimumSize(new java.awt.Dimension(250, 200));
        jPanel1.setPreferredSize(new java.awt.Dimension(250, 200));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        ipLabel.setText("IP:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(ipLabel, gridBagConstraints);

        portLabel.setText("Port:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(portLabel, gridBagConstraints);

        ipScrollPane.setMinimumSize(new java.awt.Dimension(150, 35));
        ipScrollPane.setName(""); // NOI18N
        ipScrollPane.setPreferredSize(new java.awt.Dimension(150, 35));
        ipScrollPane.setRequestFocusEnabled(false);

        ipEntry.setMinimumSize(new java.awt.Dimension(150, 35));
        ipEntry.setPreferredSize(new java.awt.Dimension(150, 35));
        ipScrollPane.setViewportView(ipEntry);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(ipScrollPane, gridBagConstraints);

        nameScrollPane.setMinimumSize(new java.awt.Dimension(150, 35));
        nameScrollPane.setPreferredSize(new java.awt.Dimension(150, 35));
        nameScrollPane.setRequestFocusEnabled(false);

        nameEntry.setMinimumSize(new java.awt.Dimension(150, 35));
        nameEntry.setPreferredSize(new java.awt.Dimension(150, 35));
        nameScrollPane.setViewportView(nameEntry);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(nameScrollPane, gridBagConstraints);

        instructionLabel.setText("Create new evaluator");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(instructionLabel, gridBagConstraints);

        portScrollPane.setMinimumSize(new java.awt.Dimension(150, 35));
        portScrollPane.setPreferredSize(new java.awt.Dimension(150, 35));
        portScrollPane.setRequestFocusEnabled(false);

        portEntry.setMinimumSize(new java.awt.Dimension(150, 35));
        portEntry.setPreferredSize(new java.awt.Dimension(150, 35));
        portScrollPane.setViewportView(portEntry);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(portScrollPane, gridBagConstraints);

        nameLabel.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(nameLabel, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }

    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {                                    
    	//create the external evaluator
    	String name = nameEntry.getText();
    	String ip = ipEntry.getText();
    	int port;
    	try{
    		port = Integer.parseInt(portEntry.getText());
    	}catch(NumberFormatException e) {
    		JOptionPane.showMessageDialog(frame, "Could not parse the port as an integer. Please enter a valid number in the port field.");
    		return;
    	}
    	//add the external evaluator
    	ExternalStateEvaluator eval = new ExternalStateEvaluator(port, ip);
    	EvaluationManager.addEvaluator(name, eval);
    	//finished
    	close();
    }                                           

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {        
    	close();
    }                   
    
    private void close() {                              
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }


    // Variables declaration - do not modify                     
    private javax.swing.JButton applyButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel instructionLabel;
    private javax.swing.JTextPane ipEntry;
    private javax.swing.JLabel ipLabel;
    private javax.swing.JScrollPane ipScrollPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextPane nameEntry;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JScrollPane nameScrollPane;
    private javax.swing.JTextPane portEntry;
    private javax.swing.JLabel portLabel;
    private javax.swing.JScrollPane portScrollPane;
    // End of variables declaration            
    
	
    public static void show(JFrame parentFrame) {
    	parentFrame.setEnabled(false);
    	JFrame frame = new JFrame("Create new Evaluator");
    	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	frame.addWindowListener(new WindowAdapter() {
    		@Override
    		public void windowClosed(WindowEvent e) {
    			parentFrame.setEnabled(true);
    		}
		});
    	frame.add(new ExternalEvaluatorCreator(frame));
    	frame.pack();
    	frame.setVisible(true);
    }
    
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new ExternalEvaluatorCreator(frame));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

}

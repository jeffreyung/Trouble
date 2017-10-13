package troublegame.client.panels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import troublegame.client.Interface;
import troublegame.client.SwingUI;

/**
 * 
 * The {@link StartPanel} class handles the display of the start screen of the client.
 * 
 * @author Jeffrey Ung and Tony Tran
 *
 */
public class StartPanel extends JPanel {
	
	/**
	 * Serial ID for object serialisation
	 */
	private static final long serialVersionUID = -3316901513367466105L;
	
	/**
	 * The swing user interface.
	 */
	private SwingUI swingUI;
	
	/**
	 * The constructor for the start panel.
	 * @param swingUI is the swing user interface.
	 */
	public StartPanel(SwingUI swingUI) {
		this.swingUI = swingUI;
		this.init();
	}
	
	/**
	 * Initializes the start panel.
	 */
	public void init() {
		JButton login = new JButton("Login");
		JButton rules = new JButton("How to play");
		JButton exit = new JButton("Exit");
		this.setLayout(new GridLayout(3, 0));
		login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				swingUI.setInterface(Interface.LOGIN);
			}
		});
		rules.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				swingUI.setInterface(Interface.RULES);
			}
		});
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		this.add(login);
		this.add(rules);
		this.add(exit);
	}

	/**
	 * @return the swing user interface.
	 */
	public SwingUI getSwingUI() {
		return swingUI;
	}

	/**
	 * Sets the swing user interface.
	 * @param swingUI is the swing user interface.
	 */
	public void setSwingUI(SwingUI swingUI) {
		this.swingUI = swingUI;
	}

}

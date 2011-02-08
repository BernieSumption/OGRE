package com.berniecode.ogre.demos.friendgraph.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.berniecode.ogre.demos.friendgraph.model.Person;

public class PersonView extends JPanel implements MouseMotionListener, MouseListener {

	public static final int BIO_IMAGE_WIDTH = 50;
	public static final int BIO_IMAGE_HEIGHT = 50;

	public static final int INITIAL_WIDTH = 100;
	public static final int INITIAL_HEIGHT = 150;
	private static final Border INITIAL_BORDER = BorderFactory.createLineBorder(Color.BLACK);
	
	private final Person person;
	private final FriendGraphView friendGraphView;
	
	private JLabel nameLabel;
	private int mouseXOffset;
	private int mouseYOffset;
	private JLabel photoLabel;

	/**
	 * Constructor
	 */
	public PersonView(final Person person, final FriendGraphView friendGraphView) {
		this.person = person;
		this.friendGraphView = friendGraphView;
		setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
		setBorder(INITIAL_BORDER);
		add(nameLabel = new JLabel());
		
		add(photoLabel = new JLabel());
		
		if (friendGraphView.isEditable()) {
			nameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			nameLabel.setToolTipText("Click to edit");
			nameLabel.addMouseListener(new MouseClickListener() {
				public void mouseClicked(MouseEvent e) {
					handleNameLabelClick();
				}
			});

	        setAutoscrolls(true);
	        addMouseMotionListener(this);
	        addMouseListener(this);
	        
	        setCursor(Cursors.MOVE_CURSOR);
	        
	        JButton delete = new JButton("delete");
	        delete.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					PersonView.this.friendGraphView.getEditEventListener().deletePerson(person);
				}
			});
			add(delete);
		}
		
		updateView();
	}

	public void updateView() {
		setLocation(person.getXPosition(), person.getYPosition());
		String name = person.getName();
		if (name == null || name.length() == 0) {
			name = " ";
		}
		nameLabel.setText(name);
		
		use more exact change reporting to only change image when the data has changed.
		
		PropertyChangeEvents?
		
		byte[] photo = person.getPhotoJpeg();
		if (photo == null) {
			photoLabel.setIcon(new ImageIcon(photo));
		}
	}

	protected void handleNameLabelClick() {
		String result = JOptionPane.showInputDialog("Enter a new name", person.getName());
		friendGraphView.getEditEventListener().setPersonName(person, result);
	}
	
    public void mouseDragged(MouseEvent e) {
		friendGraphView.handlePersonDragTo(person, e.getXOnScreen() - mouseXOffset, e.getYOnScreen() - mouseYOffset);
    }

	public void mousePressed(MouseEvent e) {
		mouseXOffset = (int) (e.getLocationOnScreen().getX() - person.getXPosition());
		mouseYOffset = (int) (e.getLocationOnScreen().getY() - person.getYPosition());
		getParent().setComponentZOrder(this, 0);
		getParent().repaint();
	}

    public void mouseMoved(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}

}

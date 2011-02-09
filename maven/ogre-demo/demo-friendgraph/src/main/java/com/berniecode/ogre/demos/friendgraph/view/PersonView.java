package com.berniecode.ogre.demos.friendgraph.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.berniecode.ogre.demos.friendgraph.Utils;
import com.berniecode.ogre.demos.friendgraph.model.Person;

public class PersonView extends JPanel implements MouseMotionListener, MouseListener {

	public static final int BIO_IMAGE_WIDTH = 50;
	public static final int BIO_IMAGE_HEIGHT = 50;

	public static final int INITIAL_WIDTH = 100;
	public static final int INITIAL_HEIGHT = 150;
	private static final Border INITIAL_BORDER = BorderFactory.createLineBorder(Color.BLACK);
	private static final ImageIcon NO_PHOTO_ICON = new ImageIcon(Utils.loadImageFromClasspath("default-photo.jpg"));
	
	private final Person person;
	private final FriendGraphView friendGraphView;
	
	private JLabel nameLabel;
	private int mouseXOffset;
	private int mouseYOffset;
	private JLabel photoLabel;
	private byte[] lastPhoto;
	

	/**
	 * Constructor
	 */
	public PersonView(final Person person, final FriendGraphView friendGraphView) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.person = person;
		this.friendGraphView = friendGraphView;
		setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
		setBorder(INITIAL_BORDER);
		setBackground(Color.WHITE);
		addNicely(nameLabel = new JLabel());
		
		addNicely(photoLabel = new JLabel());
		
		if (friendGraphView.isEditable()) {
			nameLabel.setCursor(Cursors.CLICK_CURSOR);
			nameLabel.setToolTipText("Click to edit");
			nameLabel.addMouseListener(new MouseClickListener() {
				public void mouseClicked(MouseEvent e) {
					handleNameLabelClick();
				}
			});

			photoLabel.setCursor(Cursors.CLICK_CURSOR);
			photoLabel.setToolTipText("Click to edit");
			photoLabel.addMouseListener(new MouseClickListener() {
				public void mouseClicked(MouseEvent e) {
					handlePhotoLabelClick();
				}
			});

	        setAutoscrolls(true);
	        addMouseMotionListener(this);
	        addMouseListener(this);
	        
	        setCursor(Cursors.MOVE_CURSOR);
	        
	        JButton delete = new JButton("delete");
	        delete.setCursor(Cursors.CLICK_CURSOR);
	        delete.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					PersonView.this.friendGraphView.getEditEventListener().deletePerson(person);
				}
			});
	        addNicely(delete);
		}
		
		updateView();
	}
	

    public void addNicely(JComponent comp) {
    	comp.setAlignmentX(CENTER_ALIGNMENT);
    	if (getComponentCount() == 0) {
    		add(Box.createVerticalStrut(5));
    	}
    	add(comp);
		add(Box.createVerticalStrut(5));
    }

	public void updateView() {
		setLocation(person.getXPosition(), person.getYPosition());
		String name = person.getName();
		if (name == null || name.length() == 0) {
			name = "                ";
		}
		nameLabel.setText(name);
		
		byte[] photo = person.getPhotoJpeg();
		if (photoLabel.getIcon() == null || !Arrays.equals(photo, lastPhoto)) {
			if (photo == null) {
				photoLabel.setIcon(NO_PHOTO_ICON);
			} else {
				photoLabel.setIcon(new ImageIcon(photo));
			}
		}
		lastPhoto = photo;
	}

	protected void handleNameLabelClick() {
		String result = JOptionPane.showInputDialog("Enter a new name", person.getName());
		friendGraphView.getEditEventListener().setPersonName(person, result);
	}
	
	private static File lastPhotoDirectory = null;

	protected void handlePhotoLabelClick() {
		JFileChooser chooser = new JFileChooser(lastPhotoDirectory);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG Images", "jpg");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				byte[] result = Utils.loadBytesFromFile(file);
				friendGraphView.getEditEventListener().setPersonPhotoJpeg(person, result);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Oh noes!", JOptionPane.ERROR_MESSAGE);
			}
		}
		lastPhotoDirectory = chooser.getCurrentDirectory();
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

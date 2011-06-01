/*
 * Copyright 2011 Bernie Sumption. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. THIS SOFTWARE IS PROVIDED ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

package com.berniecode.ogre.demos.friendgraph.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
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
import com.berniecode.ogre.demos.friendgraph.model.SocialNetwork;

public class PersonView extends JPanel implements MouseMotionListener, MouseListener {

	public static final int BIO_IMAGE_WIDTH = 50;
	public static final int BIO_IMAGE_HEIGHT = 50;

	public static final int INITIAL_WIDTH = 100;
	public static final int INITIAL_HEIGHT = 120;
	public static final int EDITING_HEIGHT = 140;
	private static final Border INITIAL_BORDER = BorderFactory.createLineBorder(Color.BLACK);

	private static final ImageIcon NO_PHOTO_ICON = new ImageIcon(Utils.loadImageFromClasspath("default-photo.jpg"));
	private static final ImageIcon DELETE_ICON = new ImageIcon(Utils.loadImageFromClasspath("delete.gif"));
	private static final ImageIcon SET_FRIENDS_ICON = new ImageIcon(Utils.loadImageFromClasspath("set-friends.gif"));
	private static final ImageIcon CANCEL_SET_FRIENDS_ICON = new ImageIcon(Utils.loadImageFromClasspath("cancel-set-friends.gif"));
	private static final ImageIcon ADD_FRIEND_ICON = new ImageIcon(Utils.loadImageFromClasspath("add-friend.gif"));
	private static final ImageIcon REMOVE_FRIEND_ICON = new ImageIcon(Utils.loadImageFromClasspath("remove-friend.gif"));

	private final Person person;
	private final FriendGraphView friendGraphView;

	private final JLabel nameLabel;
	private final JLabel photoLabel;
	private int mouseXOffset;
	private int mouseYOffset;
	private byte[] lastPhoto;
	private JButton setFriendsButton;
	private final SocialNetwork model;

	/**
	 * Constructor
	 */
	public PersonView(final Person person, final FriendGraphView friendGraphView, SocialNetwork model) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.person = person;
		this.friendGraphView = friendGraphView;
		this.model = model;
		setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
		setBorder(INITIAL_BORDER);
		setBackground(Color.WHITE);
		addNicely(nameLabel = new JLabel());
		addNicely(photoLabel = new JLabel());

		if (friendGraphView.isEditable()) {

			setSize(INITIAL_WIDTH, EDITING_HEIGHT);

			addMouseMotionListener(this);
			addMouseListener(this);
			setCursor(Cursors.MOVE_CURSOR);

			makeLabelClickable(nameLabel, new MouseClickListener() {
				public void mouseClicked(MouseEvent e) {
					handleNameLabelClick();
				}
			});

			makeLabelClickable(photoLabel, new MouseClickListener() {
				public void mouseClicked(MouseEvent e) {
					handlePhotoLabelClick();
				}
			});

			
			JButton deleteButton = makeButton(DELETE_ICON, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getEditEventListener().deletePerson(person);
				}
			});
			deleteButton.setToolTipText("delete this person");

			setFriendsButton = makeButton(null, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					handleSetFriendsClick();
				}
			});
			
			Box box = Box.createHorizontalBox();
			box.add(deleteButton);
			box.add(Box.createRigidArea(new Dimension(8, 1)));
			box.add(setFriendsButton);
			addNicely(box);

			
			setPersonForFriendshipEditing(null);
		}

		updateView();
	}

	private JButton makeButton(ImageIcon icon, ActionListener listener) {
		JButton button = new JButton(icon);
		button.setMargin(new Insets(5, 5, 5, 5));
		button.setCursor(Cursors.CLICK_CURSOR);
		button.addActionListener(listener);
		return button;
	}

	private void makeLabelClickable(JLabel nameLabel, MouseClickListener listener) {
		nameLabel.setCursor(Cursors.CLICK_CURSOR);
		nameLabel.setToolTipText("Click to edit");
		nameLabel.addMouseListener(listener);
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

	private EditEventListener getEditEventListener() {
		return friendGraphView.getEditEventListener();
	}

	protected void handleNameLabelClick() {
		String result = JOptionPane.showInputDialog("Enter a new name", person.getName());
		getEditEventListener().setPersonName(person, result);
	}

	private static File lastPhotoDirectory;
	static {
		lastPhotoDirectory = new File("src/main/resources/sample-photos");
		if (!lastPhotoDirectory.exists()) {
			lastPhotoDirectory = new File(".");
		}
	}

	protected void handlePhotoLabelClick() {
		JFileChooser chooser = new JFileChooser(lastPhotoDirectory);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG Images", "jpg");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				byte[] result = Utils.loadBytesFromFile(file);
				getEditEventListener().setPersonPhotoJpeg(person, result);
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

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	//
	// FRIENDSHIP EDITING
	//

	private Person currentSetFriendsPerson;

	private EditingOperation currentSetFriendsOperation;

	private static enum EditingOperation {
		ADD, REMOVE
	};

	/**
	 * Handle a click on the "set friends" button of THIS PersonView
	 */
	protected void handleSetFriendsClick() {
		if (currentSetFriendsPerson == null) {
			// enter editing mode
			friendGraphView.setPersonForFriendshipEditing(person);
		} else {
			// we are in editing mode, conclude the editing operation
			if (currentSetFriendsOperation == EditingOperation.ADD) {
				getEditEventListener().addFriendship(currentSetFriendsPerson, person);
			} else if (currentSetFriendsOperation == EditingOperation.REMOVE) {
				getEditEventListener().removeFriendship(currentSetFriendsPerson, person);
			}
			friendGraphView.setPersonForFriendshipEditing(null);
		}
	}

	/**
	 * Called when the "set friends" button has been clicked on ANY PersonView.
	 */
	public void setPersonForFriendshipEditing(Person editedPerson) {
		this.currentSetFriendsPerson = editedPerson;
		if (editedPerson == null) {
			// the operation has completed or has been cancelled
			setFriendsButton.setIcon(SET_FRIENDS_ICON);
			setFriendsButton.setToolTipText("change this person's friends");
			currentSetFriendsOperation = null;
		} else {
			if (editedPerson == person) {
				// the button was clicked on this PersonView
				setFriendsButton.setIcon(CANCEL_SET_FRIENDS_ICON);
				setFriendsButton.setToolTipText("cancel");
				currentSetFriendsOperation = null;
			} else if (model.getPersonLikesPerson(editedPerson, this.person)) {
				// the button was clicked on another PersonView that already likes this person
				setFriendsButton.setIcon(REMOVE_FRIEND_ICON);
				setFriendsButton.setToolTipText("stop being friends with " + editedPerson.getName());
				currentSetFriendsOperation = EditingOperation.REMOVE;
			} else {
				// the button was clicked on another PersonView that does not yet like this person
				setFriendsButton.setIcon(ADD_FRIEND_ICON);
				setFriendsButton.setToolTipText("start being friends with " + editedPerson.getName());
				currentSetFriendsOperation = EditingOperation.ADD;
			}
		}
	}

}

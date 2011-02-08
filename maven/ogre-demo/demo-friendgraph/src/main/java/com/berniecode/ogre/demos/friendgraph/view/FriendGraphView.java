package com.berniecode.ogre.demos.friendgraph.view;

import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import com.berniecode.ogre.demos.friendgraph.model.FriendGraphModel;
import com.berniecode.ogre.demos.friendgraph.model.Person;

public class FriendGraphView extends JFrame {

	private static final int INITIAL_WIDTH = 600;
	private static final int INITIAL_HEIGHT = 400;
	private static final int INITIAL_TOP = 100;
	private static final int INITIAL_LEFT = 200;
	private static final String INITIAL_TITLE = "Friend Graph Demo";
	
	private Map<Person, PersonView> personToView = new HashMap<Person, PersonView>();
	private final boolean editable;
	private EditEventListener editEventListener;

	/**
	 * Constructor
	 * 
	 * @param controller a controller to use to edit the model, or null to create a read-only view of the model
	 */
	public FriendGraphView(boolean editable) {
		this.editable = editable;
		setTitle(INITIAL_TITLE);
		setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
		setLocation(INITIAL_LEFT, INITIAL_TOP);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new AbsoluteLayout());
		
		if (editable) {
			setCursor(Cursors.NEW_CURSOR);
			addMouseListener(new MouseClickListener() {
				public void mouseClicked(MouseEvent e) {
					Person p = editEventListener.createNewPerson(e.getX() - getInsets().left, e.getY() - getInsets().top);
					handlePersonDragTo(p, p.getXPosition(), p.getYPosition()); // apply position validation
				}
			});
		}
	}

	public void updateFromModel(FriendGraphModel model) {
		for (Person person: model.getPeople()) {
			PersonView personView = personToView.get(person);
			if (personView == null) {
				personView = new PersonView(person, this);
				add(personView);
				personToView.put(person, personView);
			} else {
				personView.updateView();
			}
		}
		Set<Person> keySet = personToView.keySet();
		for (Person person: keySet) {
			if (!model.getPeople().contains(person)) {
				PersonView personView = personToView.get(person);
				remove(personView);
				keySet.remove(person);
			}
		}
		validate();
		repaint();
	}

	/**
	 * 
	 * @param editController
	 */
	public void setEditEventListener(EditEventListener listener) {
		this.editEventListener = listener;
	}

	public boolean isEditable() {
		return editable;
	}

	public void handlePersonDragTo(Person person, int x, int y) {
		// prevent dragging off edge of frame
		Insets insets = getInsets();
		PersonView personView = personToView.get(person);
		if (x < 0) {
			x = 0;
		}
		int maxX = getWidth() - personView.getWidth() - insets.left - insets.right;
		if (x > maxX) {
			x = maxX;
		}
		if (y < 0) {
			y = 0;
		}
		int maxY = getHeight() - personView.getHeight() - insets.bottom - insets.top;
		if (y > maxY) {
			y = maxY;
		}
		if (editEventListener != null) {
			editEventListener.setPersonLocation(person, x, y);
		}
	}

	public EditEventListener getEditEventListener() {
		return editEventListener;
	}

}

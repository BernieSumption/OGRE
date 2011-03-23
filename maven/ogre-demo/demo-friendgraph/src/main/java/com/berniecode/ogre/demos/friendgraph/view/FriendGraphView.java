package com.berniecode.ogre.demos.friendgraph.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Path2D.Double;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import com.berniecode.ogre.demos.friendgraph.model.Person;
import com.berniecode.ogre.demos.friendgraph.model.Relationship;
import com.berniecode.ogre.demos.friendgraph.model.SocialNetwork;

/**
 * This is the canvas on which people are created and arranged. This class is designed more or less
 * around the MVC pattern. The view never updates itself directly. Instead, all interactions result
 * in calls to the {@link EditEventListener} member, which in turn updates the model, causing the
 * view to be updated.
 * 
 * @author Bernie Sumption
 */
public class FriendGraphView extends JFrame {

	public static final int INITIAL_WIDTH = 600;
	public static final int INITIAL_HEIGHT = 400;
	private static final int INITIAL_TOP = 100;
	private static final int INITIAL_LEFT = 200;
	private static final String INITIAL_TITLE = "Friend Graph Demo";

	private Map<Person, PersonView> personToView = new HashMap<Person, PersonView>();
	private final boolean editable;
	private EditEventListener editEventListener;
	private FriendshipContainer contents;

	/**
	 * Constructor
	 * 
	 * @param controller a controller to use to edit the model, or null to create a read-only view
	 *            of the model
	 */
	public FriendGraphView(boolean editable) {
		this.editable = editable;
		setTitle(INITIAL_TITLE);
		setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
		setLocation(INITIAL_LEFT, INITIAL_TOP);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		contents = new FriendshipContainer(editable);
		add(contents);
		contents.setLayout(new AbsoluteLayout());

		if (editable) {
			setCursor(Cursors.NEW_CURSOR);
			addMouseListener(new MouseClickListener() {
				public void mouseClicked(MouseEvent e) {
					String name = JOptionPane.showInputDialog("Enter a name for the new person");
					if (name != null && name.length() != 0) {
						Person p = editEventListener.createNewPerson(name, e.getX() - getInsets().left, e.getY()
								- getInsets().top);
						handlePersonDragTo(p, p.getXPosition(), p.getYPosition()); // apply position
																					// validation
					}
				}
			});
		}
	}

	public void updateFromModel(SocialNetwork model) {
		for (Person person : model.getPeople()) {
			PersonView personView = personToView.get(person);
			if (personView == null) {
				personView = new PersonView(person, this);
				contents.add(personView);
				personToView.put(person, personView);
			} else {
				personView.updateView();
			}
		}
		contents.setRelationships(model.getLikesRelationships());
		Iterator<Person> keys = personToView.keySet().iterator();
		while (keys.hasNext()) {
			Person person = keys.next();
			if (!model.getPeople().contains(person)) {
				PersonView personView = personToView.get(person);
				contents.remove(personView);
				keys.remove();
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

	/**
	 * Handle a click on the "edit friends" button
	 */
	public void setPersonForFriendshipEditing(Person person) {
		for (PersonView personView : personToView.values()) {
			personView.setPersonForFriendshipEditing(person);
		}
	}

}

class FriendshipContainer extends JLayeredPane {

	private final static double ARROW_ANGLE = Math.toRadians(30); // equilateral triangle
	private final static int ARROW_SIZE = 15; // edge length
	private static final Color ARROW_COLOR = new Color(0x33AA33);

	private Collection<Relationship> relationships;
	private final int yOffset;
	private final int xOffset;
	private Double arrow;

	public FriendshipContainer(boolean editable) {
		yOffset = (editable ? PersonView.EDITING_HEIGHT : PersonView.INITIAL_HEIGHT) / 2;
		xOffset = PersonView.INITIAL_WIDTH / 2;
		arrow = createArrow();
	}

	@Override
	protected void paintComponent(java.awt.Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if (relationships == null) {
			return;
		}
		g2.setColor(ARROW_COLOR);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (Relationship relationship : relationships) {
			paintRelationship(g2, relationship);
		}
	}

	private void paintRelationship(Graphics2D g, Relationship relationship) {
		Person subject = relationship.getSubject();
		Person object = relationship.getObject();
		int subjectX = subject.getXPosition() + xOffset;
		int subjectY = subject.getYPosition() + yOffset;
		int objectX = object.getXPosition() + xOffset;
		int objectY = object.getYPosition() + yOffset;
		g.drawLine(subjectX, subjectY, objectX, objectY);
		int centerX = (subjectX + objectX) / 2;
		int centerY = (subjectY + objectY) / 2;
		AffineTransform at = AffineTransform.getTranslateInstance(centerX, centerY + 0.5);
		double tan = Math.atan((1.0 * (subjectY - objectY)) / (1.0 * (subjectX - objectX)));
		at.rotate(tan);
		if (objectX <= subjectX) {
			at.rotate(Math.PI);
		}
		Shape shape = at.createTransformedShape(arrow);
		g.fill(shape);
	}

	private Path2D.Double createArrow() {
		Path2D.Double path = new Path2D.Double();
		path.moveTo(0, 0);
		double x = -ARROW_SIZE * Math.cos(ARROW_ANGLE);
		double y = ARROW_SIZE * Math.sin(ARROW_ANGLE);
		path.lineTo(x, y);
		x = -ARROW_SIZE * Math.cos(-ARROW_ANGLE);
		y = ARROW_SIZE * Math.sin(-ARROW_ANGLE);
		path.lineTo(0 + x, y);
		path.lineTo(0, 0);
		return path;
	}

	public void setRelationships(Collection<Relationship> relationships) {
		this.relationships = relationships;
	}
}

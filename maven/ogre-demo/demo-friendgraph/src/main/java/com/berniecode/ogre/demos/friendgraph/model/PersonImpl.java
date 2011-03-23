package com.berniecode.ogre.demos.friendgraph.model;

public class PersonImpl implements Person {

	private String name;
	private byte[] photoJpeg;
	private int xPosition;
	private int yPosition;
	private final SocialNetwork socialNetwork;
	
	public PersonImpl(SocialNetwork socialNetwork, String name, byte[] photoJpeg, int xPosition, int yPosition) {
		super();
		this.socialNetwork = socialNetwork;
		this.name = name;
		this.photoJpeg = photoJpeg;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte[] getPhotoJpeg() {
		return photoJpeg;
	}
	public void setPhotoJpeg(byte[] photoJpeg) {
		this.photoJpeg = photoJpeg;
	}
	public int getXPosition() {
		return xPosition;
	}
	public void setXPosition(int xPosition) {
		this.xPosition = xPosition;
	}
	public int getYPosition() {
		return yPosition;
	}
	public void setYPosition(int yPosition) {
		this.yPosition = yPosition;
	}

	public SocialNetwork getSocialNetwork() {
		return socialNetwork;
	}
	
	@Override
	public String toString() {
		return name;
	}

}

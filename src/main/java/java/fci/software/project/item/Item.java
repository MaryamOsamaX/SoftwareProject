package fci.software.project.item;

import javax.persistence.*;
@Entity
public class Item {
	
	private String itemName , type ,color ,userId , itemId;
	@Lob
	private byte[] image;

	public String getItemName() {
		return itemName;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
   @Id
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

}

package fci.software.project.classes;

import java.io.Serializable;

import javax.persistence.*;
@Embeddable
class Key implements Serializable
{
	public String userId , itemId;
}
@Entity
public class BlockedItems {
	@EmbeddedId
 private Key itemKey; // primary key is object

	public Key getItemKey() {
		return itemKey;
	}

	public void setItemKey(Key itemKey) {
		this.itemKey = itemKey;
	}
 

}

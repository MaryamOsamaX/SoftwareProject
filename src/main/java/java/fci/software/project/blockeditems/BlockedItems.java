package fci.software.project.blockeditems;
import javax.persistence.*;

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

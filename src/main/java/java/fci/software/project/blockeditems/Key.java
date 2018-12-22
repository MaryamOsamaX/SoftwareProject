package fci.software.project.blockeditems;

import java.io.Serializable;

import javax.persistence.Embeddable;
@Embeddable
public class Key implements Serializable
{
	public String userId , itemId;
}
package com.bridgelabz.fundoo.note.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table
public class Collaborator implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long collabUserId;
	private long ownerId;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long collabId;
	private long noteId;

}

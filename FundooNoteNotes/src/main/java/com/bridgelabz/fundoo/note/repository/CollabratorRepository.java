package com.bridgelabz.fundoo.note.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgelabz.fundoo.note.model.Collaborator;
import com.bridgelabz.fundoo.note.model.Note;



public interface CollabratorRepository extends  JpaRepository<Collaborator,Long>{ 
	public List<Collaborator> findByCollabUserId(long userId);
	
	public Collaborator findByCollabUserIdAndNoteId(long userId,long noteId);
}

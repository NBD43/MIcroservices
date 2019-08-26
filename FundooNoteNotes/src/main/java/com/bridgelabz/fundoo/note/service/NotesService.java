package com.bridgelabz.fundoo.note.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bridgelabz.fundoo.note.dto.ColorDto;
import com.bridgelabz.fundoo.note.dto.LabelDto;
import com.bridgelabz.fundoo.note.dto.NotesDto;
import com.bridgelabz.fundoo.note.model.Note;
import com.bridgelabz.fundoo.response.Response;

@Service
public interface NotesService {

	public Response createNote(NotesDto notesDto, String token);
	
	public Response updateNote(NotesDto notesDto, String token, Long noteId);

	public Response delete(String token, Long noteId);

//	public List<Note> getAllNotes(String token);
//
//	public Response pinAndUnPin(String token, Long noteId);
//
	public Response archiveAndUnArchive(String token, Long noteId);
//
	public Response trashAndUnTrash(String token, Long noteId);
//
//	public Response deletePermanently(String token, Long noteId);
//	
	public Response colourNote( String token, ColorDto colorDto);

	Response deletePermanently(String token, Long noteId);

	Response pinAndUnPin(String token, Long noteId);
	
//	public Response reminderNote(String reminderDate, String token, Long noteId);
//	
//	public Response addCollabrator(String token,String email,Long noteId);
//	
//	public Response removeCollabrator(String token,String email,Long noteId);
//
	public List<Note> getAllArchive(String token);
//
	List<Note> getNotes(String token);
//
	List<Note> getTrashNotes(String token);
//
	List<Note> getPinnedNotes(String token);
//
	List<Note> getReminderNotes(String token);
//
	Response removeReminder(String token, Long noteId);
//
//	Response createNotewithLabel(NotesDto notesDto,LabelDto labeldto, String token);

	Response reminderNote(String reminderDate, String token, Long noteId);

	Response addCollaborator(String token, String emailId,long noteId);

	Response removeCollaborator(String token, String emailId, long noteId);

	

	

	
}

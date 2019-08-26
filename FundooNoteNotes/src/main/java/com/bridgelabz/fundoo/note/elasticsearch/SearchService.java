package com.bridgelabz.fundoo.note.elasticsearch;

import java.util.List;

import com.bridgelabz.fundoo.note.model.Note;
import com.bridgelabz.fundoo.response.Response;

public interface SearchService {

	Response createNote(Note note) throws Exception;

	String updateNote(Note note) throws Exception;

	List<Note> findAll() throws Exception;

	List<Note> searchByLabel(String label) throws Exception;

	Note findById(String id) throws Exception;

	String deleteNote(String id) throws Exception;

}

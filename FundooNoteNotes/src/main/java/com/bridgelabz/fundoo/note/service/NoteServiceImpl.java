package com.bridgelabz.fundoo.note.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bridgelabz.fundoo.exception.UserException;
import com.bridgelabz.fundoo.note.dto.ColorDto;
import com.bridgelabz.fundoo.note.dto.LabelDto;
import com.bridgelabz.fundoo.note.dto.NotesDto;
import com.bridgelabz.fundoo.note.elasticsearch.SearchService;
import com.bridgelabz.fundoo.note.model.Collaborator;
import com.bridgelabz.fundoo.note.model.Label;
import com.bridgelabz.fundoo.note.model.Note;
import com.bridgelabz.fundoo.note.repository.CollabratorRepository;
import com.bridgelabz.fundoo.note.repository.LabelRepository;
import com.bridgelabz.fundoo.note.repository.NotesRepository;
import com.bridgelabz.fundoo.response.Response;
//import com.bridgelabz.fundoo.user.model.User;
//import com.bridgelabz.fundoo.user.repository.UserRepo;
import com.bridgelabz.fundoo.utility.ResponseHelper;
import com.bridgelabz.fundoo.utility.TokenGenerator;
import com.bridgelabz.fundoo.utility.Utility;

@PropertySource("classpath:message.properties")
@Service("noteService")
public class NoteServiceImpl implements NotesService {
	Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);

	@Autowired
	private TokenGenerator userToken;
	
	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private ModelMapper modelMapper;

//	@Autowired
//	private UserRepo userRepository;

	@Autowired
	private NotesRepository notesRepository;

	@Autowired
	private Environment environment;
	
	@Autowired
	private SearchService searchService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private CollabratorRepository collabRepository;

	@Override
	public Response createNote(NotesDto notesDto, String token) {

		long id = userToken.decodeToken(token);
		logger.info(notesDto.toString());
		if (notesDto.getTitle().isEmpty() && notesDto.getDescription().isEmpty()) {

			throw new UserException(-5, "Title and description are empty");

		}
		Note notes = modelMapper.map(notesDto, Note.class);
	//	Optional<User> user = userRepository.findById(id);
		notes.setUserId(id);
		notes.setCreated(LocalDateTime.now());
		notes.setModified(LocalDateTime.now());
	//	user.get().getNotes().add(notes);
		notesRepository.save(notes);
	//	userRepository.save(user.get());
		try {
			searchService.createNote(notes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Response response=ResponseHelper.statusResponse(200, environment.getProperty("status.notes.createdSuccessfull"));
		return response;
		
		
		
	}


	@Override
	public Response updateNote(NotesDto notesDto, String token, Long noteId) {
		if(notesDto.getTitle().isEmpty() && notesDto.getDescription().isEmpty()) {
			throw new UserException(-5,"Title and discriptions are empty");
		}
		long id=userToken.decodeToken(token);
		Note notes=notesRepository.findBynoteIdAndUserId(noteId, id);
		notes.setTitle(notesDto.getTitle());
		notes.setDescription(notesDto.getDescription());
		notes.setModified(LocalDateTime.now());
		notesRepository.save(notes);
		try {
			searchService.updateNote(notes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response response=ResponseHelper.statusResponse(200, environment.getProperty("status.notes.updated"));
		return response;
	}

	@Override
	public Response delete(String token, Long noteId) {
		long id=userToken.decodeToken(token);
		Note notes=notesRepository.findBynoteIdAndUserId(noteId, id);
		if(notes==null) {
			throw new UserException(-5,"Invalid input");
		}
		if(notes.isTrash()==false) {
			notes.setTrash(true);
			notes.setModified(LocalDateTime.now());
			notesRepository.save(notes);
			Response response=ResponseHelper.statusResponse(200, environment.getProperty("status.note.trashed"));
		}
		Response response=ResponseHelper.statusResponse(100, environment.getProperty("status.note.trashError"));
		return response;
	}
	
	@Override
	public Response deletePermanently(String token, Long noteId) {
		long id =userToken.decodeToken(token);
		//Optional<User> user=userRepository.findById(id);
		Note note=notesRepository.findById(noteId).orElseThrow();
		System.out.println(note);
		if(note.isTrash()==true) {
			//user.get().getNotes().remove(note);
			//userRepository.save(user.get());
			notesRepository.delete(note);
			Response response=ResponseHelper.statusResponse(200, environment.getProperty("status.note.deleted"));
			return response;
		}else {
		Response response=ResponseHelper.statusResponse(100, environment.getProperty("status.note.notdeleted"));
		return response;
		}
	}

//	@Override
//	public List<Note> getAllNotes(String token) {
//		long id =userToken.decodeToken(token);
//	//	User user =userRepository.findById(id).get();
//		
////		List<NotesDto> listNotes=new ArrayList<NotesDto>();
////		for(Note userNotes:notes) {
////			NotesDto noteDto=modelMapper.map(userNotes,NotesDto.class);
////			if(userNotes.isArchived()==false && userNotes.isTrash()==false) {
////				listNotes.add(noteDto);
////			}
//
//		
//		return user.getNotes();
//	}

	@Override
	public Response pinAndUnPin(String token, Long noteId) {
		long id =userToken.decodeToken(token);
		Note notes=notesRepository.findBynoteIdAndUserId(noteId, id);
		if(notes==null) {
			throw new UserException(-5,"Invalid input");
		}
		if(notes.isPined()==false) {
			notes.setPined(true);
			notes.setModified(LocalDateTime.now());
			notesRepository.save(notes);
			try {
				searchService.updateNote(notes);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Response response=ResponseHelper.statusResponse(200, environment.getProperty("status.note.pinned"));
			return response;
		}else {
			notes.setPined(false);
			notes.setModified(LocalDateTime.now());
			notesRepository.save(notes);
			try {
				searchService.updateNote(notes);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Response response=ResponseHelper.statusResponse(200, environment.getProperty("status.note.unpinned"));
			return response;
		
		}
	}
           
	@Override
	public Response archiveAndUnArchive(String token, Long noteId) {
		long id =userToken.decodeToken(token);
		Note notes=notesRepository.findBynoteIdAndUserId(noteId, id);
		if(notes==null) {
			throw new UserException(-5,"Invalid input");
		}
		if(notes.isArchived()==false) {
			notes.setArchived(true);;
			notes.setModified(LocalDateTime.now());
			notesRepository.save(notes);
			try {
				searchService.updateNote(notes);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Response response=ResponseHelper.statusResponse(200, environment.getProperty("status.note.archieved"));
			return response;
		}else {
			notes.setArchived(false);
			notes.setModified(LocalDateTime.now());
			notesRepository.save(notes);
			try {
				searchService.updateNote(notes);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Response response=ResponseHelper.statusResponse(200, environment.getProperty("status.note.unarchieved"));
			return response;
		
		}
	}

	@Override
	public Response trashAndUnTrash(String token, Long noteId) {
		System.out.println(token+"......"+noteId);
		long id =userToken.decodeToken(token);
		Note notes=notesRepository.findBynoteIdAndUserId(noteId, id);
		if(notes==null) {
			throw new UserException(-5,"Invalid input");
		}
		if(notes.isTrash()==false) {
			notes.setTrash(true);
			notes.setModified(LocalDateTime.now());
			notesRepository.save(notes);
			try {
				searchService.updateNote(notes);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Response response=ResponseHelper.statusResponse(200, environment.getProperty("status.note.trashed"));
			return response;
		}else {
			notes.setTrash(false);
			notes.setModified(LocalDateTime.now());
			notesRepository.save(notes);
			try {
				searchService.updateNote(notes);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Response response=ResponseHelper.statusResponse(200, environment.getProperty("status.note.untrashed"));
			return response;
		
		}
	}


	@Override
	public Response colourNote(String token,ColorDto colorDto) {
		long id =userToken.decodeToken(token);
		Note notes=notesRepository.findBynoteIdAndUserId(colorDto.getNoteId(), id);
		notes.setColour(colorDto.getColor());
		notesRepository.save(notes);
		try {
			searchService.updateNote(notes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response response=ResponseHelper.statusResponse(200, environment.getProperty("status.notes.colour"));
		return response;
	}


	@Override
	public Response reminderNote(String reminderDate, String token, Long noteId) {
		long id =userToken.decodeToken(token);
		Note notes=notesRepository.findBynoteIdAndUserId(noteId, id);
		LocalDateTime today=LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		LocalDateTime remind=LocalDateTime.parse(reminderDate, formatter);
		if(remind.isBefore(today)) {
			throw new UserException(-6,"date is before the orignal time");
		}
		notes.setReminder(reminderDate);
		notesRepository.save(notes);
		try {
			searchService.updateNote(notes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response response=ResponseHelper.statusResponse(200, environment.getProperty("status.notes.setreminder"));
		return response;
	}
	
	@Override
	public Response removeReminder( String token, Long noteId) {
		long id =userToken.decodeToken(token);
		Note notes=notesRepository.findBynoteIdAndUserId(noteId, id);
//		LocalDateTime today=LocalDateTime.now();
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//		LocalDateTime remind=LocalDateTime.parse(reminderDate, formatter);
//		if(remind.isBefore(today)) {
//			throw new UserException(-6,"date is before the orignal time");
//		}
//		notes.setReminder(reminderDate);
		notes.setReminder(null);
		notesRepository.save(notes);
		try {
			searchService.updateNote(notes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response response=ResponseHelper.statusResponse(200, environment.getProperty("status.notes.remove.reminder"));
		return response;
	}




	


	
	  @Override 
	  public List<Note> getAllArchive(String token) { 
		  long userId =userToken.decodeToken(token);
	  
		  List<Note> user = notesRepository.findByUserId(userId);
		  List<Note> archieveNote = user.stream().filter(data -> (data.isArchived() == true && data.isTrash() == false) ) .collect(Collectors.toList());
	  
		  user.stream().filter(data1 -> (data1.isArchived() == true)).collect(Collectors.toList()) .forEach(System.out::println);
		  return  archieveNote; 
	  }
	 
	
	
	@Override
	public List<Note> getNotes(String token) {
		long userId = userToken.decodeToken(token);
		
		List<Note> user = notesRepository.findByUserId(userId);
		List<Collaborator> collaborator = collabRepository.findByCollabUserId(userId);
		
		List<Note> archieveNote = user.stream().filter(data -> (data.isArchived() == false && data.isTrash() == false && data.isPined()==false ) )
				.collect(Collectors.toList());

		user.stream().filter(data1 -> (data1.isArchived() == false)).collect(Collectors.toList())
				.forEach(System.out::println);
		for (Collaborator collaborator2 : collaborator) {
			long noteId= collaborator2.getNoteId();
			archieveNote.add(notesRepository.findById(noteId).get());
		}
		//archieveNote.addAll();
		return archieveNote;
	}
	
	
	
	  @Override public List<Note> getTrashNotes(String token) { long userId =
	  userToken.decodeToken(token);
	  
	  List<Note> user = notesRepository.findByUserId(userId);
	  List<Note> archieveNote = user.stream().filter(data -> (data.isTrash() == true) )
	  .collect(Collectors.toList());
	  
	  user.stream().filter(data1 -> (data1.isTrash() == true)).collect(Collectors.toList()) .forEach(System.out::println);
	  return  archieveNote;
	  }
	
	
	@Override
	public List<Note> getPinnedNotes(String token) {
		long userId = userToken.decodeToken(token);

		List<Note> user = notesRepository.findByUserId(userId);
		List<Note> archieveNote = user.stream().filter(data -> (data.isArchived() == false && data.isTrash() == false && data.isPined()==true) )
				.collect(Collectors.toList());

		user.stream().filter(data1 -> (data1.isPined()==true)).collect(Collectors.toList())
				.forEach(System.out::println);
		return archieveNote;
	}
	
	@Override
	public List<Note> getReminderNotes(String token) {
		long userId = userToken.decodeToken(token);

		List<Note> user = notesRepository.findByUserId(userId);
		List<Note> archieveNote = user.stream().filter(data -> ((data.getReminder()==null) == false) )
				.collect(Collectors.toList());

		user.stream().filter(data1 -> (data1.isTrash() == false && (data1.getReminder()==null) == false)).collect(Collectors.toList())
				.forEach(System.out::println);
		return archieveNote;
	}
	@Override
	public Response addCollaborator(String token,String emailId,long noteId) {
		long userId = userToken.decodeToken(token);
		String url="http://localhost:9092/user/"+emailId;
		long collabId=restTemplate.getForObject(url, long.class);
		if(collabId==0) {
			throw new UserException("Id not Found");
		}
		Note note= notesRepository.findBynoteIdAndUserId(noteId, userId);
		if(note==null) {
			throw new UserException("note is Not Present");
		}
		 Collaborator collab = new Collaborator();
		 collab.setCollabUserId(collabId);
		 collab.setNoteId(noteId);
		 collab.setOwnerId(userId);
		 collabRepository.save(collab);
		 
		
		
	
	Response response=ResponseHelper.statusResponse(200, environment.getProperty("status.notes.collaborator"));
	return response;
		
	}
	
	
	@Override
	public Response removeCollaborator(String token,String emailId,long noteId) {
		long userId = userToken.decodeToken(token);
		String url="http://localhost:9092/user/"+emailId;
		long collabId=restTemplate.getForObject(url, long.class);
		if(collabId==0) {
			throw new UserException("Id not Found");
		}
		
		Collaborator collabrator= collabRepository.findByCollabUserIdAndNoteId(collabId, noteId);
		
		
		 collabRepository.delete(collabrator);;
		 
		
		
	
	Response response=ResponseHelper.statusResponse(200, environment.getProperty("status.notes.collaborator.remove"));
	return response;
		
	}
	

}

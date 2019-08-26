package com.bridgelabz.fundoo.note.elasticsearch;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.fundoo.note.model.Note;
@RestController
@RequestMapping("/user/profile")
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class SearchController {
	
	private SearchServiceImpl service;
	

    @Autowired
    public SearchController(SearchServiceImpl service) {

        this.service = service;
    }
    
    @PostMapping
    public ResponseEntity createProfile(
        @RequestBody Note note) throws Exception {

        return 
            new ResponseEntity(service.createNote(note), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity updateProfile(@RequestBody Note note) throws Exception {

        return new ResponseEntity(service.updateNote(note), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public Note findById(@PathVariable String id) throws Exception {

        return service.findById(id);
    }
    
    @GetMapping
    public List<Note> findAll() throws Exception {

           return service.findAll();
    }
    
    @GetMapping(value = "/search")
    public List<Note> search(
                @RequestParam(value = "label") String label) 
                throws Exception {
    return service.searchByLabel(label);
    }
    
    @DeleteMapping("/{id}")
    public String deleteProfile(@PathVariable String id) 
         throws Exception {

         return service.deleteNote(id);

     }
    
    @GetMapping(value = "/searchnote")
    public List<Note> searchnote(
                @RequestParam(value = "label") String label) 
                throws Exception {
    return service.searchBytitle(label);
    }
}



package com.bridgelabz.fundoo.note.elasticsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundoo.note.model.Note;
import com.bridgelabz.fundoo.note.repository.LabelRepository;
import com.bridgelabz.fundoo.note.repository.NotesRepository;
import com.bridgelabz.fundoo.response.Response;

import com.bridgelabz.fundoo.utility.ResponseHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {
	
	 private static final String INDEX = "fundoodb";

	private static final String TYPE = "note";

	//private RestHighLevelClient client;

	@Autowired
	 private ObjectMapper objectMapper;
	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private ModelMapper modelMapper;

	

	@Autowired
	private NotesRepository notesRepository;

	
	 
	/*
	 * @Autowired public SearchServiceImpl(RestHighLevelClient client, ObjectMapper
	 * objectMapper) { this.client = client; this.objectMapper = objectMapper; }
	 */
	 @Autowired
	 private RestHighLevelClient client;
	 
	 @Autowired
	 private Environment environment;
	 
	 @Override
	 public Response createNote(Note note) throws Exception {

	        //UUID uuid = UUID.randomUUID();
	       // note.setNoteId(uuid.toString());

	       // Map<String, Object> documentMapper = objectMapper.convertValue(note, Map.class);
	        Map<String, Object> documentMapper = objectMapper.convertValue(note, Map.class);
	        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, String.valueOf(note.getNoteId()))
	                .source(documentMapper);
	        try {
				client.index(indexRequest, RequestOptions.DEFAULT);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

	        Response response = ResponseHelper.statusResponse(100, environment.getProperty("status.notes.created"));
			return response;
	    }
	
	 @Override
	  public String updateNote(Note note) throws Exception {
	  
	  Note resultDocument = findById(String.valueOf(note.getNoteId()));
	  
	  UpdateRequest updateRequest = new UpdateRequest( INDEX, TYPE,
	  String.valueOf(resultDocument.getNoteId()));
	  
	  Map<String, Object> documentMapper = objectMapper.convertValue(note,
	  Map.class);
	  
	  updateRequest.doc(documentMapper);
	  
	  UpdateResponse updateResponse = client.update(updateRequest,
	  RequestOptions.DEFAULT);
	  
	  return updateResponse .getResult() .name();
	  
	  }
	  
	 @Override
	   public List<Note> findAll() throws Exception {
	  
	  SearchRequest searchRequest = new SearchRequest(); SearchSourceBuilder
	  searchSourceBuilder = new SearchSourceBuilder();
	  searchSourceBuilder.query(QueryBuilders.matchAllQuery());
	  searchRequest.source(searchSourceBuilder);
	  
	  SearchResponse searchResponse = client.search(searchRequest,
	  RequestOptions.DEFAULT);
	  
	  return getSearchResult(searchResponse);
	  
	  
	  }
	  
	 
	  private List<Note> getSearchResult(SearchResponse response) {
		//  Client client = node.client();
	  
	  SearchHit[] searchHit = response.getHits().getHits();
	  
	  List<Note> notes = new ArrayList<>();
	  
	  if (searchHit.length > 0) {
	  
	  Arrays.stream(searchHit) .forEach(hit -> notes .add(objectMapper
	  .convertValue(hit.getSourceAsMap(), Note.class)) ); }
	  
	  return notes; }
	  
	  @Override
	  public List<Note> searchByLabel(String label) throws Exception {
	  
	  SearchRequest searchRequest = new SearchRequest();
	  SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//	  
//	  QueryBuilder queryBuilder = QueryBuilders .boolQuery() .must(QueryBuilders
//	  .matchQuery("Note.listLabel.labelName", label));
//	  System.out.println("hII");
//	  searchSourceBuilder.query(QueryBuilders .nestedQuery("Note.listLabel",
//	  queryBuilder, ScoreMode.Avg));
	 
	  
	  QueryBuilder queryBuilder = QueryBuilders .boolQuery() .must(QueryBuilders.queryStringQuery(label).analyzeWildcard(true).field("listLabel.labelName"));
	  searchSourceBuilder.query(queryBuilder);
	  System.out.println("hII");
	  searchRequest.source(searchSourceBuilder);
	  System.out.println(searchRequest);
	  SearchResponse response = client.search(searchRequest,
	  RequestOptions.DEFAULT);
	  System.out.println("hII");
	  return getSearchResult(response); 
	  }
	  
	  
	  
	  
	  @Override
	  public Note findById(String id) throws Exception {
	  
	  GetRequest getRequest = new GetRequest(INDEX, TYPE, id);
	  
	  GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
	  Map<String, Object> resultMap = getResponse.getSource();
	  
	  return objectMapper .convertValue(resultMap, Note.class);
	  
	  
	  }
	  
	  @Override
	  public String deleteNote(String id) throws Exception {

	        DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
	        DeleteResponse response =
	                client.delete(deleteRequest, RequestOptions.DEFAULT);

	        return response
	                .getResult()
	                .name();

	    }
	  //public List<Note>
	  public List<Note> searchBytitle(String label) throws Exception {
		  
		  SearchRequest searchRequest = new SearchRequest();
		  SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		  
//		  QueryBuilder queryBuilder = QueryBuilders .boolQuery() .must(QueryBuilders
//		  .matchQuery("Note.title", label));
		  QueryBuilder queryBuilder = QueryBuilders .boolQuery() 
				  .must(QueryBuilders.queryStringQuery(label).analyzeWildcard(true).field("description"));
		  System.out.println("hII");
		 
//		  searchSourceBuilder.query(QueryBuilders .nestedQuery("Note.title",
//		  queryBuilder, ScoreMode.Avg));
		  searchSourceBuilder.query(queryBuilder);
		  System.out.println("hII");
		  searchRequest.source(searchSourceBuilder);
		  System.out.println(searchRequest);
		  SearchResponse response = client.search(searchRequest,
		  RequestOptions.DEFAULT);
		  System.out.println("hII");
		  
		  return getSearchResult(response); 
		  }
	 

}

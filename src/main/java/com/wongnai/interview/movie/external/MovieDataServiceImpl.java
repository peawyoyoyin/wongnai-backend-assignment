package com.wongnai.interview.movie.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class MovieDataServiceImpl implements MovieDataService {
	public static final String MOVIE_DATA_URL
			= "https://raw.githubusercontent.com/prust/wikipedia-movie-data/master/movies.json";

	@Autowired
	private RestOperations restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public MoviesResponse fetchAll() {
		//TODO:
		// Step 1 => Implement this method to download data from MOVIE_DATA_URL and fix any error you may found.
		// Please noted that you must only read data remotely and only from given source,
		// do not download and use local file or put the file anywhere else.

		/*
			Additional explanation:
				The given URL (MOVIE_DATA_URL) responds with correctly formatted JSON text, but incorrect headers:
				the field Content-Type is text/plain instead of application/json.

				Here we have to manually add an HTTP message converter that converts JSON texts in to Spring Entities
				and modify the converter so that it also consumes responses with Context-Type text/plain as well.
		 */
		MoviesResponse response = null;
		try {
			response = restTemplate.getForObject(MOVIE_DATA_URL, MoviesResponse.class);
		} catch(RestClientException e) {
			MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
			converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON));
			((RestTemplate) restTemplate).getMessageConverters().add(0, converter);

			response = restTemplate.getForObject(MOVIE_DATA_URL, MoviesResponse.class);
		}
		return response;

	}
}

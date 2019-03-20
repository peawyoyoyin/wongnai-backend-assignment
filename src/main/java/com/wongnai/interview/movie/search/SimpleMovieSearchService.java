package com.wongnai.interview.movie.search;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.wongnai.interview.movie.external.MovieData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wongnai.interview.movie.Movie;
import com.wongnai.interview.movie.MovieSearchService;
import com.wongnai.interview.movie.external.MovieDataService;

@Component("simpleMovieSearchService")
public class SimpleMovieSearchService implements MovieSearchService {
	@Autowired
	private MovieDataService movieDataService;

	@Override
	public List<Movie> search(String queryText) {
		//TODO: Step 2 => Implement this method by using data from MovieDataService
		// All test in SimpleMovieSearchServiceIntegrationTest must pass.
		// Please do not change @Component annotation on this class

		/*
			Additional Explanation:
				Here I have to split the movie titles into list of Strings instead of using indexOf().
				I convert both titles and queryText into lower case to support case-insensitive searching.

				I have to write a simple function to convert MovieData from movieDataService.fetchAll()
				into Movie objects.
		 */

		List<MovieData> movies = movieDataService.fetchAll();

		return 	movies.stream()
				.filter(movieData -> {
					List<String> tokens = Arrays.asList(movieData.getTitle().toLowerCase().split(" "));
					return tokens.contains(queryText.toLowerCase());
				})
				.map(movieData -> {
					Movie m = new Movie(movieData.getTitle());
					m.getActors().addAll(movieData.getCast());
					return m;
				})
				.collect(Collectors.toList());
	}
}

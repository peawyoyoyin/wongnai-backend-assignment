package com.wongnai.interview.movie.sync;

import javax.transaction.Transactional;

import com.wongnai.interview.movie.Movie;
import com.wongnai.interview.movie.external.MovieData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wongnai.interview.movie.MovieRepository;
import com.wongnai.interview.movie.external.MovieDataService;

import java.util.stream.Collectors;

@Component
public class MovieDataSynchronizer {
	@Autowired
	private MovieDataService movieDataService;

	@Autowired
	private MovieRepository movieRepository;

	private Movie convertMovieDataToMovie(MovieData movieData) {
		Movie movie = new Movie(movieData.getTitle());
		movie.getActors().addAll(movieData.getCast());

		return movie;
	}

	@Transactional
	public void forceSync() {
		//TODO: implement this to sync movie into repository

		// delete all movies first to prevent duplicate entries
		movieRepository.deleteAll();
		movieDataService.fetchAll()
				.stream()
				.map(this::convertMovieDataToMovie)
				.forEach(movie -> movieRepository.save(movie));
	}
}

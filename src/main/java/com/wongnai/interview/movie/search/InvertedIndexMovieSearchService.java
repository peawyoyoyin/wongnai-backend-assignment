package com.wongnai.interview.movie.search;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.wongnai.interview.movie.Movie;
import com.wongnai.interview.movie.MovieRepository;
import com.wongnai.interview.movie.MovieSearchService;

@Component("invertedIndexMovieSearchService")
@DependsOn("movieDatabaseInitializer")
public class InvertedIndexMovieSearchService implements MovieSearchService {
	@Autowired
	private MovieRepository movieRepository;

	// the lookup table
	private Map<String, Set<Long>> invertedIndexLookupTable;

	// construct the inverted index lookup table
	private void constructInvertedIndexLookupTable() {
		invertedIndexLookupTable = new HashMap<>();
		movieRepository.findAll().forEach(movie -> {
			String[] keywords = movie.getName().split(" ");
			Long movieId = movie.getId();

			for (String keyword: keywords) {
				// we convert the keyword to lowercase to support case-insensitive search
				String keywordLowerCase = keyword.toLowerCase();

				if(invertedIndexLookupTable.containsKey(keywordLowerCase)) {
					invertedIndexLookupTable.get(keywordLowerCase).add(movieId);
				} else {
					Set<Long> s = new HashSet<>();
					s.add(movieId);
					invertedIndexLookupTable.put(keywordLowerCase, s);
				}
			}
		});
	}

	@Override
	public List<Movie> search(String queryText) {
		//TODO: Step 4 => Please implement in-memory inverted index to search movie by keyword.
		// You must find a way to build inverted index before you do an actual search.
		// Inverted index would looks like this:
		// -------------------------------
		// |  Term      | Movie Ids      |
		// -------------------------------
		// |  Star      |  5, 8, 1       |
		// |  War       |  5, 2          |
		// |  Trek      |  1, 8          |
		// -------------------------------
		// When you search with keyword "Star", you will know immediately, by looking at Term column, and see that
		// there are 3 movie ids contains this word -- 1,5,8. Then, you can use these ids to find full movie object from repository.
		// Another case is when you find with keyword "Star War", there are 2 terms, Star and War, then you lookup
		// from inverted index for Star and for War so that you get movie ids 1,5,8 for Star and 2,5 for War. The result that
		// you have to return can be union or intersection of those 2 sets of ids.
		// By the way, in this assignment, you must use intersection so that it left for just movie id 5.

		/*
			Additional Explanation:
				Here I used long, functional-style chaining to implement the above logic.

				I declared an additional method (constructInvertedIndexLookupTable) to construct the
				inverted index lookup table using Map and Sets.

				The return lines are longer than it should, to convert the result of movie.findAllById (Iterable), into
				List.
		 */

		// construct the inverted index lookup table if not exist.
		if(invertedIndexLookupTable == null) {
			constructInvertedIndexLookupTable();
		}

		String[] keywords = queryText.split(" ");
		Set<Long> ids = Arrays.stream(keywords)
				.map(String::toLowerCase) // convert keywords to lowercase to support case-insensitive search
				.filter(keyword -> invertedIndexLookupTable.containsKey(keyword))
				.map(keyword -> invertedIndexLookupTable.get(keyword))
				.reduce(Sets::intersection)
				.orElse(new HashSet<>());

		return StreamSupport.stream(movieRepository.findAllById(ids).spliterator(), false)
				.collect(Collectors.toList());
	}
}

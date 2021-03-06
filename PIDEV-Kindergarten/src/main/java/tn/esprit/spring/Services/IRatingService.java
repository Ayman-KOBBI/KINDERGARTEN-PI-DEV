package tn.esprit.spring.Services;

import java.util.List;

import tn.esprit.spring.entity.Publicity;
import tn.esprit.spring.entity.Rating;

public interface IRatingService {

	public String addRating(Rating r);

	public void deleteRating(int i);

	public int nbReview(int id);

	public List<Rating> retrieveAllReviews(int id);



	String updateRating(int id);

	public List<Rating> listLastReviews(int id);

}

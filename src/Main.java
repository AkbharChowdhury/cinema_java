import models.Movie;
import models.SearchMovies;
//git rm --cached .gitignore
void main() {
    var props = ENVManager.getENV();
//    var username = props.get("USERNAME");
//    System.out.println(username);
//    List<Movie> movies = new ArrayList<>();
//    movies.add(new Movie(123, "Apple", "Action| Horror"));
//    movies.add(new Movie(1233, "Grapes", "Fantasy"));
//    movies.add(new Movie(12233, "Blood", "Horror"));
//
//
//    final SearchMovies search = new SearchMovies(movies);
//    search.setGenre("fantasy");
////    search.setGenre("fantasy");
//    var results = search.filterResults.get();
//    results.forEach(System.out::println);

    var db = Database.getInstance();
//    List<String> genres = new ArrayList<>(db.getMovieGenres());
//    System.out.println(db.getAllGenres());
    System.out.println(db.getSelectedMovieGenres(2));


}

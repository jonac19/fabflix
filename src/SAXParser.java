public class SAXParser {
    public void run() {
        System.out.println("---Parsing Movie XML---");
        MovieSAXParser msp = new MovieSAXParser();
        msp.run();

        System.out.println("---Parsing Star XML---");
        StarSAXParser ssp = new StarSAXParser();
        ssp.run();

        System.out.println("---Parsing Cast XML---");
        CastSAXParser csp = new CastSAXParser();
        csp.run();

        System.out.println("+----------------------+");
        System.out.println("| Inconsistency Report |");
        System.out.println("+----------------------+");

        System.out.println("Inserted Movies Count: " + msp.getInsertedMoviesCount());
        System.out.println("Inserted Stars Count: " + ssp.getInsertStarsCount());
        System.out.println("Inserted Genres Count: " + msp.getInsertedGenresCount());
        System.out.println("Inserted Genres In Movies Count: " + msp.getInsertedGenresInMoviesCount());
        System.out.println("Inserted Stars In Movies Count: " + csp.getInsertedStarsInMoviesCount());
        System.out.println();

        System.out.println("Inconsistent Movies Count: " + msp.getInconsistentMoviesCount());
        System.out.println("Duplicate Movies Count: " + msp.getDuplicateMoviesCount());
        System.out.println("Nonexisting Movies Count: " + csp.getNonexistingMoviesCount());
        System.out.println("Duplicate Stars Count: " + ssp.getDuplicateStarsCount());
        System.out.println("Nonexisting Stars Count: " + csp.getNonexistingStarsCount());
        System.out.println("Linked Genres Count: " + msp.getLinkedGenresCount());
    }

    public static void main(String[] args) {
        SAXParser spe = new SAXParser();
        spe.run();
    }
}

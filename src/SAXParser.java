public class SAXParser {
    public void run() {
        MovieSAXParser msp = new MovieSAXParser();
        msp.run();

        StarSAXParser ssp = new StarSAXParser();
        ssp.run();

        CastSAXParser csp = new CastSAXParser();
        csp.run();
    }

    public static void main(String[] args) {
        SAXParser spe = new SAXParser();
        spe.run();
    }
}

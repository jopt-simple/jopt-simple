package joptsimple.examples;

import joptsimple.OptionParser;

public class ExceptionExample {
    public static void main( String[] args ) throws Exception {
        OptionParser parser = new OptionParser();

        parser.parse( "-x" );
    }
}

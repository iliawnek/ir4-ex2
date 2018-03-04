package uk.ac.gla.dcs.dsms;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.terrier.indexing.IndexTestUtils;
import org.terrier.structures.Index;
import org.terrier.structures.postings.BlockPosting;
import org.terrier.structures.postings.IterablePosting;
import org.terrier.tests.ApplicationSetupBasedTest;
import org.terrier.utility.ApplicationSetup;

/**
 * Tests the AverageMinimumDistanceDSM class, which implements the avg_min_dist proximity measure.
 * <p>
 * @author Ken Li
 */
public class TestAverageMinimumDistanceDSM extends ApplicationSetupBasedTest {
    @Test
    public void testTwoTermsOneOccurrence() throws Exception {

        // make an index with a single sample document
        ApplicationSetup.setProperty("termpipelines", "");
        Index index = IndexTestUtils.makeIndexBlocks(
                new String[]{"doc1"},
                new String[]{"The quick brown fox jumps over the lazy dog."});

        // get posting iterators for two terms 'fox' and 'jumps'
        IterablePosting[] ips = new IterablePosting[2];
        ips[0] = index.getInvertedIndex().getPostings(index.getLexicon().getLexiconEntry("fox"));
        ips[1] = index.getInvertedIndex().getPostings(index.getLexicon().getLexiconEntry("jumps"));
        ips[0].next();
        ips[1].next();
        assertEquals(0, ips[0].getId());
        assertEquals(0, ips[1].getId());
        System.out.println("Positions of term 'fox' = " + Arrays.toString(((BlockPosting) ips[0]).getPositions()));
        System.out.println("Positions of term 'jumps' = " + Arrays.toString(((BlockPosting) ips[1]).getPositions()));

        // compute score
        AverageMinimumDistanceDSM sample = new AverageMinimumDistanceDSM();
        double score = sample.calculateDependence(
                ips, // posting lists
                new boolean[] {true, true}, // is this posting list on the correct document?
                new double[] {1d, 1d}, false // doesn't matter
        );
        System.out.println("Score = " + score);
        assertEquals(1.0d, score, 0.0d);
    }

    @Test
    public void testTwoTermsSameNumberOfOccurrences() throws Exception {

        // make an index with a single sample document
        ApplicationSetup.setProperty("termpipelines", "");
        Index index = IndexTestUtils.makeIndexBlocks(
                new String[]{"doc1"},
                new String[]{"The quick fox and brown fox both jump over the quick dog."});

        // get posting iterators for two terms 'quick' and 'fox'
        IterablePosting[] ips = new IterablePosting[2];
        ips[0] = index.getInvertedIndex().getPostings(index.getLexicon().getLexiconEntry("quick"));
        ips[1] = index.getInvertedIndex().getPostings(index.getLexicon().getLexiconEntry("fox"));
        ips[0].next();
        ips[1].next();
        assertEquals(0, ips[0].getId());
        assertEquals(0, ips[1].getId());
        System.out.println("Positions of term 'quick' = " + Arrays.toString(((BlockPosting) ips[0]).getPositions()));
        System.out.println("Positions of term 'fox' = " + Arrays.toString(((BlockPosting) ips[1]).getPositions()));

        // compute score
        AverageMinimumDistanceDSM sample = new AverageMinimumDistanceDSM();
        double score = sample.calculateDependence(
                ips, // posting lists
                new boolean[] {true, true}, // is this posting list on the correct document?
                new double[] {1d, 1d}, false // doesn't matter
        );
        System.out.println("Score = " + score);
        final double avgMinDist = (1 + 5) / 2;
        assertEquals(avgMinDist, score, 0.0d);
    }

    @Test
    public void testTwoTermsDifferentNumberOfOccurrences() throws Exception {

        // make an index with a single sample document
        ApplicationSetup.setProperty("termpipelines", "");
        Index index = IndexTestUtils.makeIndexBlocks(
                new String[]{"doc1"},
                new String[]{"The quick fox and brown fox both jump over the quick fox."});

        // get posting iterators for two terms 'quick' and 'fox'
        IterablePosting[] ips = new IterablePosting[2];
        ips[0] = index.getInvertedIndex().getPostings(index.getLexicon().getLexiconEntry("quick"));
        ips[1] = index.getInvertedIndex().getPostings(index.getLexicon().getLexiconEntry("fox"));
        ips[0].next();
        ips[1].next();
        assertEquals(0, ips[0].getId());
        assertEquals(0, ips[1].getId());
        System.out.println("Positions of term 'quick' = " + Arrays.toString(((BlockPosting) ips[0]).getPositions()));
        System.out.println("Positions of term 'fox' = " + Arrays.toString(((BlockPosting) ips[1]).getPositions()));

        // compute score
        AverageMinimumDistanceDSM sample = new AverageMinimumDistanceDSM();
        double score = sample.calculateDependence(
                ips, // posting lists
                new boolean[] {true, true}, // is this posting list on the correct document?
                new double[] {1d, 1d}, false // doesn't matter
        );
        System.out.println("Score = " + score);
        final double avgMinDist = (1 + 1) / 2;
        assertEquals(avgMinDist, score, 0.0d);
    }

    @Test
    public void testManyTermsManyOccurrences() throws Exception {

        // make an index with a single sample document
        ApplicationSetup.setProperty("termpipelines", "");
        Index index = IndexTestUtils.makeIndexBlocks(
                new String[]{"doc1"},
                new String[]{"The quick fox and brown dog and fox dog all jump over the quick fox and brown fox and quick fox."});

        // get posting iterators for three terms 'quick', 'fox', and 'brown'
        IterablePosting[] ips = new IterablePosting[3];
        ips[0] = index.getInvertedIndex().getPostings(index.getLexicon().getLexiconEntry("quick"));
        ips[1] = index.getInvertedIndex().getPostings(index.getLexicon().getLexiconEntry("fox"));
        ips[2] = index.getInvertedIndex().getPostings(index.getLexicon().getLexiconEntry("brown"));
        ips[0].next();
        ips[1].next();
        ips[2].next();
        assertEquals(0, ips[0].getId());
        assertEquals(0, ips[1].getId());
        assertEquals(0, ips[2].getId());
        System.out.println("Positions of term 'quick' = " + Arrays.toString(((BlockPosting) ips[0]).getPositions()));
        System.out.println("Positions of term 'fox' = " + Arrays.toString(((BlockPosting) ips[1]).getPositions()));
        System.out.println("Positions of term 'brown' = " + Arrays.toString(((BlockPosting) ips[2]).getPositions()));

        // compute score
        AverageMinimumDistanceDSM sample = new AverageMinimumDistanceDSM();
        double score = sample.calculateDependence(
                ips, // posting lists
                new boolean[] {true, true, true}, // is this posting list on the correct document?
                new double[] {1d, 1d, 1d}, false // doesn't matter
        );
        System.out.println("Score = " + score);
        final double avgMinDistQuickFox = (1 + 1 + 1) / 3;
        final double avgMinDistQuickBrown = (3 + 3) / 2;
        final double avgMinDistFoxBrown = (2 + 1) / 2;
        final double avgMinDist = (
                avgMinDistQuickFox +
                avgMinDistQuickBrown +
                avgMinDistFoxBrown
                ) / 3;
        assertEquals(avgMinDist, score, 0.0d);
    }

    @Test
    public void testNoQueryTerms() throws Exception {

        // make an index with a single sample document
        ApplicationSetup.setProperty("termpipelines", "");
        Index index = IndexTestUtils.makeIndexBlocks(
                new String[]{"doc1"},
                new String[]{"The quick brown fox jumps over the lazy dog."});

        // get posting iterators for two terms 'fox' and 'jumps'
        IterablePosting[] ips = new IterablePosting[2];
        System.out.println("LEXICONENTRY = " + index.getLexicon().getLexiconEntry("fox"));
        ips[0] = index.getInvertedIndex().getPostings(index.getLexicon().getLexiconEntry("fox"));
        ips[1] = index.getInvertedIndex().getPostings(index.getLexicon().getLexiconEntry("jumps"));
        ips[0].next();
        ips[1].next();
        assertEquals(0, ips[0].getId());
        assertEquals(0, ips[1].getId());
        System.out.println("Positions of term 'fox' = " + Arrays.toString(((BlockPosting) ips[0]).getPositions()));
        System.out.println("Positions of term 'jumps' = " + Arrays.toString(((BlockPosting) ips[1]).getPositions()));

        // compute score
        AverageMinimumDistanceDSM sample = new AverageMinimumDistanceDSM();
        double score = sample.calculateDependence(
                ips, // posting lists
                new boolean[] {false, false}, // is this posting list on the correct document?
                new double[] {1d, 1d}, false // doesn't matter
        );
        System.out.println("Score = " + score);
        assertEquals(0.0d, score, 0.0d);
    }
}

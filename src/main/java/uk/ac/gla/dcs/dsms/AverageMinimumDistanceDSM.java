package uk.ac.gla.dcs.dsms;

import org.terrier.structures.postings.IterablePosting;
import org.terrier.structures.postings.BlockPosting;
import org.terrier.matching.dsms.DependenceScoreModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * You should use this sample class to implement a proximity feature in Exercise 2.
 * TODO: Describe the function that your class implements
 * <p>
 * You can add your feature into a learned model by appending DSM:uk.ac.gla.IRcourse.AveragePositionDifferenceDSM to the features.list file.
 *
 * @author TODO
 */
public class AverageMinimumDistanceDSM extends DependenceScoreModifier {

    /**
     * This class is passed the postings of the current document,
     * and should return a score to represent that document.
     */
    @Override
    protected double calculateDependence(
            IterablePosting[] ips, // posting lists
            boolean[] okToUse,  // is this posting list on the correct document?
            double[] phraseTermWeights, boolean SD // not needed
    ) {

        // get all position vectors for this document
        List<int[]> positionVectors = new ArrayList<>();
        for (int i = 0; i < okToUse.length; i++) {
            if (okToUse[i]) {
                int[] positionVector = ((BlockPosting) ips[i]).getPositions();
                positionVectors.add(positionVector);
            }
        }

        // return if not enough terms to calculate proximity
        if (positionVectors.size() < 2) return 0.0d;

        // compute average minimum distance between each pair
        List<Double> avgMinDistances = new ArrayList<>();
        for (int i = 0; i < positionVectors.size(); i++) {
            for (int j = i + 1; j < positionVectors.size(); j++) {
                // identify which position vector has fewer/more occurrences
                int[] pvi = positionVectors.get(i);
                int[] pvj = positionVectors.get(j);
                int[] pvFewer = pvi.length <= pvj.length ? pvi : pvj;
                int[] pvMore = pvi.length > pvj.length ? pvi : pvj;

                // compute average of minimum distances between each occurrence in pvFewer and any occurrence in pvMore
                int sum = 0;
                for (int posFewer : pvFewer) {
                    int min = Integer.MAX_VALUE;
                    for (int posMore : pvMore) {
                        int dist = Math.abs(posFewer - posMore);
                        if (dist < min) min = dist;
                    }
                    sum += min;
                }
                double avg = sum / pvFewer.length;
                avgMinDistances.add(avg);
            }
        }

        // compute average of average minimum distances across all pairs
        double sum = 0.0d;
        for (double d : avgMinDistances) sum += d;
        double avg = sum / avgMinDistances.size();
        return avg;
    }

    /**
     * You do NOT need to implement this method
     */
    @Override
    protected double scoreFDSD(int matchingNGrams, int docLength) {
        throw new UnsupportedOperationException();
    }


    @Override
    public String getName() {
        return "ProxFeatureDSM_AveragePositionDifference";
    }

}

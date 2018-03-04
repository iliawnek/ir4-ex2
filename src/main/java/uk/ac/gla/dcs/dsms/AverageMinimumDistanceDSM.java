package uk.ac.gla.dcs.dsms;

import org.terrier.structures.postings.IterablePosting;
import org.terrier.structures.postings.BlockPosting;
import org.terrier.matching.dsms.DependenceScoreModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * This DSM implements the avg_min_dist term proximity feature.
 * <p>
 * avg_min_dist(a, b, D) is defined as the average of the shortest distance between each occurrence of the least
 * frequently occurring term and any occurrence of the other term, where a and b are terms and D is the document.
 * <p>
 * Full dependence between query terms is assumed. If there are multiple query terms, the score is the average
 * avg_min_dist of every pair of query terms.
 * <p>
 * If there are fewer than two query terms such that proximity cannot be computed, the score is 0.
 *
 * @author Ken Li
 */
public class AverageMinimumDistanceDSM extends DependenceScoreModifier {

    @Override
    protected double calculateDependence(
            IterablePosting[] ips, // posting lists
            boolean[] okToUse, // is this posting list on the correct document?
            double[] phraseTermWeights,
            boolean SD
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

    @Override
    protected double scoreFDSD(int matchingNGrams, int docLength) {
        throw new UnsupportedOperationException();
    }


    @Override
    public String getName() {
        return "ProxFeatureDSM_AverageMinimumDistance";
    }

}

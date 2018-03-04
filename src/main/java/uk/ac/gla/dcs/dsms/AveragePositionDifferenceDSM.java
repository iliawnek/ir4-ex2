package uk.ac.gla.dcs.dsms;

import org.terrier.structures.postings.IterablePosting;
import org.terrier.structures.postings.BlockPosting;
import org.terrier.matching.dsms.DependenceScoreModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * This DSM implements the diff_avg_pos term proximity feature.
 * <p>
 * diff_avg_pos(a, b, D) is defined as the difference between the average positions of terms a and b in document D.
 * <p>
 * Full dependence between query terms is assumed. If there are multiple query terms, the score is the average
 * diff_avg_pos of every pair of query terms.
 * <p>
 * If there are fewer than two query terms such that proximity cannot be computed, the score is 0.
 *
 * @author Ken Li
 */
public class AveragePositionDifferenceDSM extends DependenceScoreModifier {

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

        // compute average position for each query term
        List<Double> averagePositions = new ArrayList<>();
        for (int[] positionVector : positionVectors) {
            double sum = 0.0d;
            for (int pos : positionVector) sum += pos;
            double avg = sum / positionVector.length;
            averagePositions.add(avg);
        }

        // compute difference between average positions for each pair
        List<Double> differences = new ArrayList<>();
        for (int i = 0; i < averagePositions.size(); i++) {
            for (int j = i + 1; j < averagePositions.size(); j++) {
                double difference = Math.abs(averagePositions.get(i) - averagePositions.get(j));
                differences.add(difference);
            }
        }

        // compute average across all differences (i.e. aggregate for entire query)
        double sum = 0.0d;
        for (double diff : differences) sum += diff;
        double avg = sum / differences.size();
        return avg;
    }

    @Override
    protected double scoreFDSD(int matchingNGrams, int docLength) {
        throw new UnsupportedOperationException();
    }


    @Override
    public String getName() {
        return "ProxFeatureDSM_AveragePositionDifference";
    }

}

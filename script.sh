#!/usr/bin/env bash

TREC_SETUP=./terrier-core-4.2/bin/trec_setup.sh
TREC_TERRIER=./terrier-core-4.2/bin/trec_terrier.sh
TREC_EVAL=./terrier-core-4.2/bin/trec_eval.sh
ANYCLASS=./terrier-core-4.2/bin/anyclass.sh
COLLECTION=./data/Dotgov_50pc/
INDEX=blocks_fields_stemming
TRAINING=./data/TopicsQrels/training
VALIDATION=./data/TopicsQrels/validation
TESTING=./data/TopicsQrels/HP04
FEATURES=features.list
RESULTS=./terrier-core-4.2/var/results
JFORESTS_PROPERTIES=./terrier-core-4.2/etc/jforests.properties
ENSEMBLE=./ensemble.txt

#echo Setting up collection...
#$TREC_SETUP $COLLECTION

####################################################################################################

#echo Performing PL2 retrieval...
#$TREC_TERRIER -r -Dterrier.index.path=$INDEX -Dtrec.model=PL2 -Dtrec.topics=$TESTING/topics

#echo Performing PL2 evaluation...
#$TREC_EVAL $TESTING/qrels $RESULTS/PL2c1.0_0.res

####################################################################################################

#echo Creating training sample for LTR using PL2...
#$TREC_TERRIER -r \
#-Dterrier.index.path=$INDEX \
#-Dtrec.model=PL2 \
#-Dtrec.topics=$TRAINING/topics \
#-Dtrec.matching=FatFeaturedScoringMatching,org.terrier.matching.daat.FatFull \
#-Dfat.featured.scoring.matching.features=FILE \
#-Dfat.featured.scoring.matching.features.file=$FEATURES \
#-Dtrec.querying.outputformat=Normalised2LETOROutputFormat \
#-Dquerying.postprocesses.order=QueryExpansion,org.terrier.learning.LabelDecorator \
#-Dquerying.postprocesses.controls=labels:org.terrier.learning.LabelDecorator,qe:QueryExpansion \
#-Dquerying.default.controls=labels:on \
#-Dlearning.labels.file=$TRAINING/qrels \
#-Dtrec.results.file=tr.letor \
#-Dproximity.dependency.type=SD

#echo Creating validation sample for LTR using PL2...
#$TREC_TERRIER -r \
#-Dterrier.index.path=$INDEX \
#-Dtrec.model=PL2 \
#-Dtrec.topics=$VALIDATION/topics \
#-Dtrec.matching=FatFeaturedScoringMatching,org.terrier.matching.daat.FatFull \
#-Dfat.featured.scoring.matching.features=FILE \
#-Dfat.featured.scoring.matching.features.file=$FEATURES \
#-Dtrec.querying.outputformat=Normalised2LETOROutputFormat \
#-Dquerying.postprocesses.order=QueryExpansion,org.terrier.learning.LabelDecorator \
#-Dquerying.postprocesses.controls=labels:org.terrier.learning.LabelDecorator,qe:QueryExpansion \
#-Dquerying.default.controls=labels:on \
#-Dlearning.labels.file=$VALIDATION/qrels \
#-Dtrec.results.file=va.letor \
#-Dproximity.dependency.type=SD

#echo Building learned model using Jforests...
#$ANYCLASS edu.uci.jforests.applications.Runner \
#--config-file $JFORESTS_PROPERTIES \
#--cmd=generate-bin \
#--ranking \
#--folder $RESULTS \
#--file tr.letor \
#--file va.letor
#$ANYCLASS \
#edu.uci.jforests.applications.Runner \
#--config-file $JFORESTS_PROPERTIES \
#--cmd=train \
#--ranking \
#--folder $RESULTS \
#--train-file $RESULTS/tr.bin \
#--validation-file $RESULTS/va.bin \
#--output-model ensemble.txt

#echo Applying learned model...
#$TREC_TERRIER -r \
#-Dterrier.index.path=$INDEX \
#-Dtrec.model=PL2 \
#-Dtrec.topics=$TESTING/topics \
#-Dtrec.matching=JforestsModelMatching,FatFeaturedScoringMatching,org.terrier.matching.daat.FatFull \
#-Dfat.featured.scoring.matching.features=FILE \
#-Dfat.featured.scoring.matching.features.file=$FEATURES \
#-Dtrec.results.file=te.res \
#-Dfat.matching.learned.jforest.model=$ENSEMBLE \
#-Dfat.matching.learned.jforest.statistics=$RESULTS/jforests-feature-stats.txt \
#-Dproximity.dependency.type=SD

#echo Performing LTR evaluation...
#$TREC_EVAL $TESTING/qrels $RESULTS/te.res

echo Done!

function [orderings]= getOrderings(rankings)
    [numInst,numLabels] = size(rankings);
    orderings = zeros(numInst,numLabels);
    for j=1:numInst
        orderings(j,:) = ranking2ordering(rankings(j,:));
    end
end
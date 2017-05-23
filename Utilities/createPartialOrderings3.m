% Creates partial orderings
%
% Input:  - A matrix consisting of (complete) orderings (one ordering at a row)
%         - p - probability of which a label will be kept
%         - minLen - sanity check, a minimum number of items an ordering
%         should have (default=2 to enable one pairwise comparison)
%
% Output: - partialOrderings, where objects at some positions are missing.
% These positions are indicated by -1.

% Note: Fct prevents empty orderings by ensuring a minimum number of labels/items to occur. 
%
% 2014-08-17 Dirk Schaefer
function [partialOrderings]=createPartialOrderings3(completeOrderings, p, minLen)
    [numInst,numLabels] = size(completeOrderings);

    incompleteOrders=completeOrderings;
    for i1=1:numInst
        %PR = randperm(numLabels);
        keepTrack = 0;
        for i2 = 1: numLabels
             %currentLabel = PR(i2);
             R = binornd(1,p);
             if (R == 0)
                incompleteOrders(i1,i2)=-1;
                keepTrack = keepTrack + 1;
                if (numLabels - minLen == keepTrack)
                    break;
                end
             end
        end
    end
    
    % Correct representation, s.t. missing labels are at the end of each
    % ordering
    partialOrderings=ones(numInst,numLabels)*-1;
    for j=1:numInst
        orderv1=incompleteOrders(j,:);
        labels_in_orderv1=find(orderv1~=-1);
        currentpos=1;
        for i=1:length(labels_in_orderv1)
            label_id=orderv1(labels_in_orderv1(i));
            partialOrderings(j,currentpos)=label_id;
            currentpos=currentpos+1;
        end
    end

    
end

function [orderedTensor] = createOrderedTensor(jfTensor, orderings)
%% createOrderedTensor - creates "an ordered joint-feature tensor"
% Input: jfTensor: nExamples x nAlternatives x dim
%        orderings: nExamples x nAlternatives
% Output: ordered jfTensor
%   
% Changelog
%   2017-01-21 - Support for incomplete orderings
%   2016-01 - Init
%
% (C) Dirk Schaefer

    [N,M,p] = size(jfTensor);
    orderedTensor = nan(N,M,p);
    for i1 = 1 : N
        ct_examples =  squeeze(jfTensor(i1,:,:));
        if (size(jfTensor,3) == 1) % fix for special case p=1
            orderedTensor(i1,1:sum(orderings(i1,:)~=-1),:) = ct_examples(orderings(i1,:));
        else
            orderedTensor(i1,1:sum(orderings(i1,:)~=-1),:) = ct_examples(orderings(i1,orderings(i1,:)~=-1),:);
        end
        
    end
end
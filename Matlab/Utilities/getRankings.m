function [rankings]= getRankings(orderings)
% GETRANKINGS converts a set of orderings into a set of rankings.
%   Input:
%       orderings - matrix of row vectors. A row corresponds to a single
%       ordering.
%  (C) D.S.
% Changelog:
%   2016-04 - doc improved
    [numInst,numLabels] = size(orderings);
    rankings = zeros(numInst,numLabels);
    for j=1:numInst
        rankings(j,:) = ordering2ranking(orderings(j,:));
    end
end
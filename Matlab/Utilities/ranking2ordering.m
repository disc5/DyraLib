function [ordering]=ranking2ordering(ranking)
% Converts a single ranking into a single ordering.
%
% A "ranking" representation is a vector in which each dimension
% corresponds to a label (item,object). And the value of the i-th dimension
% determines its rank position.
%
% An "ordering" representation is a vector in which the i-th dimension
% represents the i-th rank position. And its value determines the ID of the
% object.
%
% Example 1 (complete ranking of length 4)
%   R = (3,1,4,2) corresponds to O = (2,4,1,3)
%
% Example 2 (incomplete ranking)
%   R = (-1,1,3,2) corresponds to O = (2,4,3,-1)
%
% (C) Disc 
%
% Changelog:
% 2016-03: Adapted s.t. by default an ordering consists of -1 values to
% denote incompleteness. It also can deal now with incomplete rankings as
% inputs.
    len=length(ranking);
    ordering=ones(1,len).*-1;
    for i1=1:len
       r=ranking(i1); % (rank) position of i-th label
       if (r>-1)
        ordering(r)=i1;
       end
    end
end
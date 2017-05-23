function [ psize ] = get1FixedKPermutationSize( M, b, L, a )
%GET1FIXEDKPERMUTATIONSIZE Returns the size of the 1-fixed k-Permutations
%sets.
%   Calculates |{pi \in S_{M,b} | pi(L) = a}|, where S_{n,k} is the set of 
%   k-Permutations.
%
%   Purpose: sanity check for function get1FixedKPermutationSize
%
%   Inputs:
%       M - number of elements of the complete set
%       b - length of resulting partial permutations
%       L - position which is fixed (1<=L<=b)
%       a - the object ID which is fixed at the L-th position
%
%
% (C) 2016, D. Schaefer

    %% sanity check
    psize = factorial(M-1)/(factorial((M-1) - (b-1))); % OK

end


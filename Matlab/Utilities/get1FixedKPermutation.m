function [ partPerms ] = get1FixedKPermutation( M, b, L, a )
%GET1FIXEDKPERMUTATION Returns the set of 1-fixed k-Permutations
%   Calculates {pi \in S_{M,b} | pi(L) = a}, where S_{n,k} is the set of 
%   k-Permutations.
%
%   Inputs:
%       M - number of elements of the complete set
%       b - length of resulting partial permutations
%       L - position which is fixed (1<=L<=b)
%       a - the object ID which is fixed at the L-th position
%
%
% (C) 2016, D. Schaefer

    allP = perms(1:M);

    %% first, filter those which have a at the L-th position
    P2 = allP(allP(:,L)==a,:);
    
    %% Restrict length to b
    P3 = P2(:,1:b);
    
    %% take unique entries
    partPerms=unique(P3,'rows');
    
end


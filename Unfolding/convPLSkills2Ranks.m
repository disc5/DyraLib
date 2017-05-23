function [ Delta ] = convPLSkills2Ranks( Vmat )
%CONVSKILL2DISSIM Performs a rank-transformation of PL skills.
%   Each entry v_ij is mapped to a rank position. High valued skills
%   are mapped by that to front positions of a global ranking consisting of N*M elements. 
%
%   Note: The rank positions can be interpreted as dissimilarities. 
%   There is no conditioning on rows.
%   
%   Input
%       Vmat - N x M matrix of skills
%
%   Output
%       Delta - N x M matrix of dissimilarities
%
%   Can be used in conjunction with smacofSym. 
%
% (C) 2017 DIRK SCHAEFER

[N,M] = size(Vmat);
[~, skillOrdering] = sort(Vmat(:),'descend');
skillRanking = getRankings(skillOrdering');
cnt = 1;
Delta = zeros(N,M);
for l= 1 : M
    for ind = 1 : N
        Delta(ind,l) = skillRanking(cnt);
        cnt = cnt + 1;
    end
end

% experimental: scale to [0,1]
sr_min = min(Delta(:));
sr_max = max(Delta(:));
Dissim = (Delta-sr_min)/(sr_max - sr_min);
Delta = Dissim;

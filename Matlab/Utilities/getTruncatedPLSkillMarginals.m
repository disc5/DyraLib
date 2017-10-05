function [ SkillMarginals ] = getTruncatedPLSkillMarginals( skills, upToRank )
%GETPLSKILLMARGINALS Calculates a partial PLSkillMarginal Table up to rank
%   Calculates Marginals with Plackett-Luce skill parameters.
%   The empirical marginals are defined in Marden95 (2.10).
%
%   Purpose: For a large number of objects the calculuations for lower ranks (i.e.,
%   higher rank numbers) may become intractable. This is why this method
%   exists besides the method getPLSkillMarginals.m
%
%   Input
%       skills - 1xM vector of PL skills
%       upToRank - a value <=M
%
%   Output
%      SkillMarginals - matrix M x upToRank
%
% (C) 2016 D. Schaefer
    M = length(skills);
    SkillMarginals = zeros(M, upToRank);
    for objectID = 1 : M
        for rankID = 1 : upToRank
            [ Marginal ] = getPLSkillMarginal(skills, objectID, rankID );
            SkillMarginals(objectID, rankID) = Marginal;
        end
    end
end


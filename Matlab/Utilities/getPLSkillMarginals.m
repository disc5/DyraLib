function [ SkillMarginals ] = getPLSkillMarginals( skills )
%getPLSkillMarginals Calculates a PLSkillMarginal Table
%   Calculates Marginals with Plackett-Luce skill parameters.
%   The empirical marginals are defined in Marden95 (2.10).
%
%   Input
%       skills - 1xM vector of PL skills
%
%   Output
%      SkillMarginals - matrix M x M
%
% (C) 2016 D. Schaefer
    M = length(skills);
    SkillMarginals = zeros(M, M);
    for objectID = 1 : M
        for rankID = 1 : M
            [ Marginal ] = getPLSkillMarginal(skills, objectID, rankID );
            SkillMarginals(objectID, rankID) = Marginal;
        end
    end
end

